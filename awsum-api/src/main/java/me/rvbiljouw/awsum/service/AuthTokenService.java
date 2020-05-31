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
package me.rvbiljouw.awsum.service;

import me.rvbiljouw.awsum.model.AuthToken;
import me.rvbiljouw.awsum.model.UserAccount;
import me.rvbiljouw.awsum.repository.AuthTokenRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * A service for interacting with {@link AuthToken} objects
 *
 * @author rvbiljouw
 */
@Service
public class AuthTokenService {
    private final AuthTokenRepository authTokens;

    public AuthTokenService(AuthTokenRepository authTokens) {
        this.authTokens = authTokens;
    }

    /**
     * Creates a {@link AuthToken} for the supplied user account
     *
     * @param account account to create an {@link AuthToken} for
     * @return a newly created {@link AuthToken}
     */
    public AuthToken createAuthTokenFor(UserAccount account) {
        final AuthToken authToken = new AuthToken();
        authToken.setAccount(account);
        authToken.setToken(UUID.randomUUID().toString());
        authTokens.save(authToken);
        return authToken;
    }

}
