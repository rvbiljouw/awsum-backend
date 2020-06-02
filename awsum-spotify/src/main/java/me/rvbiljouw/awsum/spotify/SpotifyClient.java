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
package me.rvbiljouw.awsum.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import me.rvbiljouw.awsum.spotify.api.SpotifySongApi;
import me.rvbiljouw.awsum.spotify.exception.SpotifyException;
import me.rvbiljouw.awsum.spotify.model.SpotifyToken;

import java.net.URI;

/**
 * @author rvbiljouw
 */
public class SpotifyClient {
    private final SpotifyApi api;
    private final SpotifySongApi songs;

    SpotifyClient(String clientId, String clientSecret, URI redirectURI, SpotifyToken token) {
        this.api = SpotifyApi.builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectURI)
                .build();
        if (token != null) {
            this.api.setAccessToken(token.getAccessToken());
            this.api.setRefreshToken(token.getRefreshToken());
        }
        this.songs = new SpotifySongApi(this, api);
    }

    public String getCurrentUserId() throws SpotifyException {
        if (!isUserScoped()) {
            throw new SpotifyException("This client is not user-scoped. " +
                    "Make sure to supply a valid token upon construction.");
        }

        try {
            User currentUser = this.api
                    .getCurrentUsersProfile()
                    .build()
                    .execute();
            return currentUser.getId();
        } catch (Throwable t) {
            throw new SpotifyException("Couldn't fetch current user ID.", t);
        }
    }

    public String getAuthorizationURL() {
        return this.api
                .authorizationCodeUri()
                .scope(SpotifyConstants.SCOPES)
                .build()
                .getUri()
                .toString();
    }

    public SpotifyToken getTokenForAuthorizationCode(String authorizationCode) throws SpotifyException {
        AuthorizationCodeRequest req = this.api
                .authorizationCode(authorizationCode)
                .build();
        try {
            AuthorizationCodeCredentials creds = req.execute();
            return new SpotifyToken(creds.getAccessToken(), creds.getRefreshToken());
        } catch (Throwable t) {
            throw new SpotifyException("Couldn't retrieve auth/refresh token.", t);
        }
    }

    public SpotifySongApi getSongs() {
        return songs;
    }

    private boolean isUserScoped() {
        return this.api.getAccessToken() != null && this.api.getRefreshToken() != null;
    }

    public SpotifyClient deriveUserScopedWithToken(SpotifyToken token) {
        return new SpotifyClient(
                this.api.getClientId(),
                this.api.getClientSecret(),
                this.api.getRedirectURI(),
                token);
    }

    public static class Builder {
        private String clientId;
        private String clientSecret;
        private URI redirectURI;
        private SpotifyToken token;

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder setRedirectURI(URI redirectURI) {
            this.redirectURI = redirectURI;
            return this;
        }

        public SpotifyToken getToken() {
            return token;
        }

        public Builder setToken(SpotifyToken token) {
            this.token = token;
            return this;
        }

        public SpotifyClient build() {
            return new SpotifyClient(clientId, clientSecret, redirectURI, token);
        }

    }

}
