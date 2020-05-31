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
package me.rvbiljouw.awsum.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.time.LocalDateTime;

/**
 * User account model
 *
 * @author rvbiljouw
 */
@Entity
@NamedQueries(value = {
        @NamedQuery(name = "UserAccount.findBySpotifyId", query = "select acc from UserAccount acc where acc.spotifyId = :spotifyId")
})
public class UserAccount extends AbstractPersistable<Long> {
    @Column(nullable = false)
    private String displayName;
    private String spotifyId;
    private String spotifyAccessToken;
    private String spotifyRefreshToken;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public UserAccount() {
        this(null);
    }

    public UserAccount(Long id) {
        this.setId(id);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getSpotifyAccessToken() {
        return spotifyAccessToken;
    }

    public void setSpotifyAccessToken(String spotifyAccessToken) {
        this.spotifyAccessToken = spotifyAccessToken;
    }

    public String getSpotifyRefreshToken() {
        return spotifyRefreshToken;
    }

    public void setSpotifyRefreshToken(String spotifyRefreshToken) {
        this.spotifyRefreshToken = spotifyRefreshToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
