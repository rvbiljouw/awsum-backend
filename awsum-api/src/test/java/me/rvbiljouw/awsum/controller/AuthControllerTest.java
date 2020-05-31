package me.rvbiljouw.awsum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.rvbiljouw.awsum.ApiApplication;
import me.rvbiljouw.awsum.model.AuthToken;
import me.rvbiljouw.awsum.model.UserAccount;
import me.rvbiljouw.awsum.repository.AuthTokenRepository;
import me.rvbiljouw.awsum.repository.UserAccountRepository;
import me.rvbiljouw.awsum.response.SimpleAuthTokenResponse;
import me.rvbiljouw.awsum.response.SimpleUserAccountResponse;
import me.rvbiljouw.awsum.spotify.SpotifyClient;
import me.rvbiljouw.awsum.spotify.model.SpotifyToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = {ApiApplication.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    @Qualifier("serverSpotifyClient")
    private SpotifyClient spotifyClient;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;

    private UserAccount userAccount;
    private AuthToken authToken;

    @BeforeEach
    void setup() throws Exception {
        when(spotifyClient.getTokenForAuthorizationCode(any())).thenReturn(makeSpotifyToken());
        when(spotifyClient.getAuthorizationURL()).thenReturn("https://accounts.spotify.com:443/authorize");
        when(spotifyClient.deriveUserScopedWithToken(any())).thenReturn(spotifyClient);
        when(spotifyClient.getCurrentUserId()).thenReturn("user-id");

        userAccount = new UserAccount();
        userAccount.setDisplayName("test-id");
        userAccount.setSpotifyId("test-id");
        userAccount.setSpotifyRefreshToken("test-refresh-token");
        userAccount.setSpotifyAccessToken("test-access-token");
        userAccountRepository.save(userAccount);

        authToken = new AuthToken();
        authToken.setAccount(userAccount);
        authToken.setToken("test-token");
        authTokenRepository.save(authToken);
    }

    @AfterEach
    void tearDown() {
        authTokenRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    void getUserUnauthorized() throws Exception {
        mvc.perform(get("/api/v1/user"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserAuthorized() throws Exception {
        MvcResult result = mvc.perform(get("/api/v1/user")
                .header("Authorization", "Bearer " + authToken.getToken()
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SimpleUserAccountResponse response = responseToObject(
                result.getResponse().getContentAsString(),
                SimpleUserAccountResponse.class);
        boolean accountIsEqual = response.getId().equals(authToken.getAccount().getId());
        assertTrue("Returned user account doesn't match", accountIsEqual);
    }

    @Test
    void getTokenUnauthorized() throws Exception {
        mvc.perform(get("/api/v1/token"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getTokenAuthorized() throws Exception {
        MvcResult result = mvc.perform(get("/api/v1/token").header("Authorization", getAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SimpleAuthTokenResponse response = responseToObject(
                result.getResponse().getContentAsString(),
                SimpleAuthTokenResponse.class);
        boolean accountIsEqual = response.getId().equals(authToken.getId());
        assertTrue("Returned auth token doesn't match", accountIsEqual);
    }

    @Test
    void authRedirectsToSpotify() throws Exception {
        mvc.perform(get("/api/v1/auth"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://accounts.spotify.com:443/authorize*"));
    }

    @Test
    void callbackCreatesUser() throws Exception {
        MvcResult result = callCallback();
        assertTrue("Response does not contain a token.",
                result.getResponse().getContentAsString().contains("\"token\":"));
    }

    private MvcResult callCallback() throws Exception {
        return mvc.perform(get("/api/v1/callback").param("code", "auth-code"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void callbackUpdatesUserIfExists() throws Exception {
        // repeated calls should update the updatedAt timestamp in the bean!
        final String responseOne = callCallback().getResponse().getContentAsString();
        final SimpleAuthTokenResponse tokenOne = responseToObject(responseOne, SimpleAuthTokenResponse.class);

        final String responseTwo = callCallback().getResponse().getContentAsString();
        final SimpleAuthTokenResponse tokenTwo = responseToObject(responseTwo, SimpleAuthTokenResponse.class);

        assertEquals("Account was not updated after creation.",
                tokenTwo.getAccount().getId(),
                tokenOne.getAccount().getId());
    }

    private <T> T responseToObject(String responseString, Class<T> type) throws JsonProcessingException {
        return messageConverter.getObjectMapper().readValue(responseString, type);
    }

    private SpotifyToken makeSpotifyToken() {
        return new SpotifyToken("access-token", "refresh-token");
    }

    private String getAuthHeader() {
        return "Bearer " + authToken.getToken();
    }

}
