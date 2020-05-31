/*
 * MIT License
 *
 * Copyright (c) 2020 Rick van Biljouw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.rvbiljouw.awsum.controller;

import me.rvbiljouw.awsum.auth.AuthenticatedUser;
import me.rvbiljouw.awsum.model.AuthToken;
import me.rvbiljouw.awsum.model.UserAccount;
import me.rvbiljouw.awsum.response.SimpleAuthTokenResponse;
import me.rvbiljouw.awsum.response.SimpleUserAccountResponse;
import me.rvbiljouw.awsum.service.AuthTokenService;
import me.rvbiljouw.awsum.service.UserAccountService;
import me.rvbiljouw.awsum.spotify.SpotifyClient;
import me.rvbiljouw.awsum.spotify.exception.SpotifyException;
import me.rvbiljouw.awsum.spotify.model.SpotifyToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for handling of authentication-related requests.
 *
 * @author rvbiljouw
 */
@RestController
public class AuthController {
    private final AuthTokenService authTokenService;
    private final UserAccountService userAccountService;
    private final SpotifyClient spotifyClient;

    public AuthController(
            AuthTokenService authTokenService,
            UserAccountService userAccountService,
            @Qualifier("serverSpotifyClient") SpotifyClient spotifyClient) {
        this.authTokenService = authTokenService;
        this.userAccountService = userAccountService;
        this.spotifyClient = spotifyClient;
    }

    @RequestMapping("/api/v1/user")
    public SimpleUserAccountResponse getCurrentAccount(AuthenticatedUser user) {
        final UserAccount account = (UserAccount) user.getPrincipal();
        return new SimpleUserAccountResponse(account);
    }

    @RequestMapping("/api/v1/token")
    public SimpleAuthTokenResponse getCurrentToken(AuthenticatedUser user) {
        final AuthToken token = (AuthToken) user.getCredentials();
        return new SimpleAuthTokenResponse(token);
    }

    /**
     * Creates a Spotify authorization URL and redirects the callee to the Spotify auth gateway.
     *
     * @param response response object
     * @throws IOException in case the redirection fails
     */
    @RequestMapping("/api/v1/auth")
    public void auth(HttpServletResponse response) throws IOException {
        final String authURI = spotifyClient.getAuthorizationURL();
        response.sendRedirect(authURI);
    }

    /**
     * Receives the callback from the Spotify auth gateway and then creates an account (if one doesn't already exist)
     * and authentication token for the end-user, after which they're redirected to the appropriate frontend URL.
     *
     * @param code Spotify authorization code
     * @return a simplified authentication token
     * @throws SpotifyException if any interaction with the Spotify API fails.
     */
    @RequestMapping("/api/v1/callback")
    public SimpleAuthTokenResponse callback(@RequestParam String code) throws SpotifyException {
        final SpotifyToken token = spotifyClient.getTokenForAuthorizationCode(code);
        final SpotifyClient scopedClient = spotifyClient.deriveUserScopedWithToken(token);
        final String currentUserId = scopedClient.getCurrentUserId();

        final UserAccount account = userAccountService.createOrUpdateAccountWith(token, currentUserId);
        final AuthToken authToken = authTokenService.createAuthTokenFor(account);
        return new SimpleAuthTokenResponse(authToken);
    }


}
