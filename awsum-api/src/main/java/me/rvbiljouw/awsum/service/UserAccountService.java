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

import me.rvbiljouw.awsum.model.UserAccount;
import me.rvbiljouw.awsum.repository.UserAccountRepository;
import me.rvbiljouw.awsum.spotify.model.SpotifyToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * A service for interacting with {@link UserAccount} objects
 *
 * @author rvbiljouw
 */
@Service
public class UserAccountService {
    private final UserAccountRepository userAccounts;

    public UserAccountService(UserAccountRepository userAccounts) {
        this.userAccounts = userAccounts;
    }

    /**
     * Creates or updates a {@link UserAccount} with data retrieved from the Spotify API
     *
     * @param token         the {@link SpotifyToken}
     * @param currentUserId the corresponding Spotify user ID.
     * @return an up-to-date {@link UserAccount}
     */
    @Transactional
    public UserAccount createOrUpdateAccountWith(SpotifyToken token, String currentUserId) {
        Optional<UserAccount> userAccount = userAccounts.findBySpotifyId(currentUserId);
        return userAccount
                .map(account -> updateAccountWith(token, account))
                .orElseGet(() -> createAccountWith(token, currentUserId));
    }

    /**
     * Updates an existing {@link UserAccount} with data from a {@link SpotifyToken}
     *
     * @param token       the {@link SpotifyToken}
     * @param userAccount the {@link UserAccount}
     * @return the updated {@link UserAccount}
     */
    private UserAccount updateAccountWith(SpotifyToken token, UserAccount userAccount) {
        userAccount.setSpotifyAccessToken(token.getAccessToken());
        userAccount.setSpotifyRefreshToken(token.getRefreshToken());
        userAccounts.save(userAccount);
        return userAccount;
    }

    /**
     * Creates a new account with data from the Spotify API
     *
     * @param token         the {@link SpotifyToken}
     * @param currentUserId the corresponding Spotify user ID.
     * @return a newly created {@link UserAccount}
     */
    private UserAccount createAccountWith(SpotifyToken token, String currentUserId) {
        UserAccount userAccount = new UserAccount();
        userAccount.setDisplayName(currentUserId);
        userAccount.setSpotifyAccessToken(token.getAccessToken());
        userAccount.setSpotifyRefreshToken(token.getRefreshToken());
        userAccount.setSpotifyId(currentUserId);
        userAccounts.save(userAccount);
        return userAccount;
    }
}
