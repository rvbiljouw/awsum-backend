package me.rvbiljouw.awsum.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import me.rvbiljouw.awsum.ApiApplication;
import me.rvbiljouw.awsum.model.AuthToken;
import me.rvbiljouw.awsum.model.UserAccount;
import me.rvbiljouw.awsum.model.UserGroup;
import me.rvbiljouw.awsum.repository.AuthTokenRepository;
import me.rvbiljouw.awsum.repository.UserAccountRepository;
import me.rvbiljouw.awsum.repository.UserGroupRepository;
import me.rvbiljouw.awsum.request.CreateGroupRequest;
import me.rvbiljouw.awsum.response.PageResponse;
import me.rvbiljouw.awsum.response.SimpleUserAccountResponse;
import me.rvbiljouw.awsum.response.SimpleUserGroupResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author rvbiljouw
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {ApiApplication.class})
class GroupControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private AuthTokenRepository authTokenRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;

    private UserAccount otherAccount;
    private UserAccount ourAccount;
    private AuthToken ourAuthToken;
    private UserGroup ownerGroup;
    private UserGroup memberGroup;


    @BeforeEach
    void setup() {
        otherAccount = new UserAccount();
        otherAccount.setDisplayName("other-id");
        otherAccount.setSpotifyId("other-id");
        otherAccount.setSpotifyRefreshToken("test-refresh-token");
        otherAccount.setSpotifyAccessToken("test-access-token");
        userAccountRepository.save(otherAccount);

        ourAccount = new UserAccount();
        ourAccount.setDisplayName("our-id");
        ourAccount.setSpotifyId("our-id");
        ourAccount.setSpotifyRefreshToken("test-refresh-token");
        ourAccount.setSpotifyAccessToken("test-access-token");
        userAccountRepository.save(ourAccount);

        ourAuthToken = new AuthToken();
        ourAuthToken.setAccount(ourAccount);
        ourAuthToken.setToken("test-token");
        authTokenRepository.save(ourAuthToken);

        ownerGroup = new UserGroup();
        ownerGroup.setOwner(ourAccount);
        ownerGroup.setName("our-group");
        userGroupRepository.save(ownerGroup);

        memberGroup = new UserGroup();
        memberGroup.setOwner(otherAccount);
        memberGroup.setName("other-group");
        memberGroup.setMembers(singletonList(ourAccount));
        userGroupRepository.save(memberGroup);
    }

    @AfterEach
    void tearDown() {
        userGroupRepository.deleteAll();
        authTokenRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    void getOwnerGroups() throws Exception {
        final MvcResult result = mvc.perform(get("/api/v1/groups")
                .param("type", "OWNER")
                .header("Authorization", getAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        final String json = result.getResponse().getContentAsString();

        final PageResponse<SimpleUserGroupResponse> groups = messageConverter.getObjectMapper().readValue(json, createGroupPageTypeRef());
        assertTrue("Max records doesn't match expected.", groups.getMaxRecords() == 1);
        assertTrue("Items.size() doesn't match expected.", groups.getItems().size() == 1);

        final SimpleUserGroupResponse group = groups.getItems().get(0);
        assertEquals("The ID of the group doesn't match expected.", group.getId(), ownerGroup.getId());
        assertEquals("We are not the owner of this group.", ourAccount.getId(), group.getOwner().getId());
    }

    @Test
    void getAllGroups() throws Exception {
        final MvcResult result = mvc.perform(get("/api/v1/groups")
                .param("type", "ALL")
                .header("Authorization", getAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        final String json = result.getResponse().getContentAsString();

        final PageResponse<SimpleUserGroupResponse> groups = messageConverter.getObjectMapper().readValue(json, createGroupPageTypeRef());
        assertTrue("Max records doesn't match expected.", groups.getMaxRecords() == 2);
        assertTrue("Items.size() doesn't match expected.", groups.getItems().size() == 2);

        final Optional<SimpleUserGroupResponse> ourGroup = groups.getItems()
                .stream()
                .filter(g -> g.getOwner().getId().equals(ourAccount.getId()))
                .findFirst();
        assertTrue("None of the groups in the list were owned by us.", ourGroup.isPresent());
        ourGroup.ifPresent(g -> {
            assertEquals("The ID of the group doesn't match expected.", g.getId(), ownerGroup.getId());
        });

        final Optional<SimpleUserGroupResponse> theirGroup = groups.getItems()
                .stream()
                .filter(g -> !g.getOwner().getId().equals(ourAccount.getId()))
                .findFirst();
        assertTrue("None of the groups in the list were owned by us.", theirGroup.isPresent());
        theirGroup.ifPresent(g -> {
            assertEquals("The ID of the group doesn't match expected.", g.getId(), memberGroup.getId());
        });
    }

    @Test
    void getMemberGroups() throws Exception {
        final MvcResult result = mvc.perform(get("/api/v1/groups")
                .param("type", "MEMBER")
                .header("Authorization", getAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        final String json = result.getResponse().getContentAsString();

        final PageResponse<SimpleUserGroupResponse> groups = messageConverter.getObjectMapper().readValue(json, createGroupPageTypeRef());
        assertTrue("Max records doesn't match expected.", groups.getMaxRecords() == 1);
        assertTrue("Items.size() doesn't match expected.", groups.getItems().size() == 1);

        final SimpleUserGroupResponse group = groups.getItems().get(0);
        assertEquals("The ID of the group doesn't match expected.", group.getId(), memberGroup.getId());
    }

    @Test
    void createNewGroup() throws Exception {
        final CreateGroupRequest request = new CreateGroupRequest();
        request.setName("create-group");

        final MvcResult result = makeCreateNewGroupRequest(request)
                .andExpect(status().isOk())
                .andReturn();
        final String responseJson = result.getResponse().getContentAsString();

        final SimpleUserGroupResponse createdGroup = messageConverter.getObjectMapper().readValue(
                responseJson, SimpleUserGroupResponse.class);
        assertEquals("Created group doesn't match expected.", request.getName(), createdGroup.getName());
    }

    @Test
    void createGroupNameNull() throws Exception {
        final CreateGroupRequest request = new CreateGroupRequest();
        request.setName(null);

        makeCreateNewGroupRequest(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void createGroupNameEmpty() throws Exception {
        final CreateGroupRequest request = new CreateGroupRequest();
        request.setName("");

        makeCreateNewGroupRequest(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    @Test
    void createGroupNameTooShort() throws Exception {
        final CreateGroupRequest request = new CreateGroupRequest();
        request.setName("a");

        makeCreateNewGroupRequest(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void createGroupNameTooLong() throws Exception {
        final CreateGroupRequest request = new CreateGroupRequest();
        request.setName("sgygoVbVnYMbtkSnRHnJAcjNONEIqQc");

        makeCreateNewGroupRequest(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void createGroupNameDuplicate() throws Exception {
        final CreateGroupRequest request = new CreateGroupRequest();
        request.setName("duplicate-group");

        makeCreateNewGroupRequest(request).andExpect(status().isOk());
        makeCreateNewGroupRequest(request).andExpect(status().isBadRequest());
    }

    @Test
    void joinGroupWithoutCode() throws Exception {
        mvc.perform(get("/api/v1/groups/join")
                .header("Authorization", getAuthHeader()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    @Test
    void joinGroupWithInvalidCode() throws Exception {
        mvc.perform(get("/api/v1/groups/join")
                .param("code", "non-existing-code")
                .header("Authorization", getAuthHeader()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void joinGroupByCode() throws Exception {
        final UserGroup joinGroup = new UserGroup();
        joinGroup.setOwner(ourAccount);
        joinGroup.setName("join-group");
        joinGroup.setCode(UUID.randomUUID().toString());
        userGroupRepository.save(joinGroup);

        final MvcResult result = mvc.perform(get("/api/v1/groups/join")
                .param("code", joinGroup.getCode())
                .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andReturn();
        final String responseJson = result.getResponse().getContentAsString();
        final SimpleUserGroupResponse group = messageConverter.getObjectMapper().readValue(responseJson, SimpleUserGroupResponse.class);
        assertEquals("The joined group has a different id than expected", joinGroup.getId(), group.getId());
    }

    @Test
    void getMembersByGroupId() throws Exception {
        final MvcResult result = mvc.perform(get("/api/v1/groups/" + memberGroup.getId() + "/members")
                .header("Authorization", getAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        final String responseJson = result.getResponse().getContentAsString();

        final PageResponse<SimpleUserAccountResponse> members = messageConverter.getObjectMapper().readValue(
                responseJson, createUserAccountPageTypeRef());

        assertTrue("The number of members returned doesn't match expected.",
                memberGroup.getMembers().size() == members.getMaxRecords());
    }

    @Test
    void getMembersByInvalidGroupId() throws Exception {
        mvc.perform(get("/api/v1/groups/0/members")
                .header("Authorization", getAuthHeader()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void getMembersNoAccess() throws Exception {
        final UserGroup noAccessGroup = new UserGroup();
        noAccessGroup.setOwner(otherAccount);
        noAccessGroup.setName("no-access-group");
        noAccessGroup.setCode(UUID.randomUUID().toString());
        userGroupRepository.save(noAccessGroup);

        mvc.perform(get("/api/v1/groups/" + noAccessGroup.getId() + "/members")
                .header("Authorization", getAuthHeader()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private ResultActions makeCreateNewGroupRequest(CreateGroupRequest request) throws Exception {
        final String requestJson = messageConverter.getObjectMapper().writeValueAsString(request);
        return mvc.perform(post("/api/v1/groups")
                .header("Authorization", getAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));
    }

    private TypeReference<PageResponse<SimpleUserGroupResponse>> createGroupPageTypeRef() {
        return new TypeReference<PageResponse<SimpleUserGroupResponse>>() {
        };
    }

    private TypeReference<PageResponse<SimpleUserAccountResponse>> createUserAccountPageTypeRef() {
        return new TypeReference<PageResponse<SimpleUserAccountResponse>>() {
        };
    }

    private String getAuthHeader() {
        return "Bearer " + ourAuthToken.getToken();
    }

}
