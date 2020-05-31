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
package me.rvbiljouw.awsum.response;

import me.rvbiljouw.awsum.model.UserGroup;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author rvbiljouw
 */
public class SimpleUserGroupResponse {
    private Long id;
    private SimpleUserAccountResponse owner;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SimpleUserGroupResponse() {
    }

    public SimpleUserGroupResponse(UserGroup userGroup) {
        this.id = userGroup.getId();
        this.owner = new SimpleUserAccountResponse(userGroup.getOwner());
        this.name = userGroup.getName();
        this.createdAt = userGroup.getCreatedAt();
        this.updatedAt = userGroup.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public SimpleUserAccountResponse getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleUserGroupResponse that = (SimpleUserGroupResponse) o;
        return id.equals(that.id) &&
                owner.equals(that.owner) &&
                name.equals(that.name) &&
                createdAt.equals(that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, name, createdAt, updatedAt);
    }
}
