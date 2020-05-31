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

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User group model
 *
 * @author rvbiljouw
 */
@NamedQueries({
        @NamedQuery(name = "UserGroup.findByOwnerId", query = "select ug from UserGroup ug where ug.owner.id = :ownerId"),
        @NamedQuery(name = "UserGroup.findByMemberId", query = "select ug from UserGroup ug join ug.members m where m.id = :memberId"),
        @NamedQuery(name = "UserGroup.findInvolvingAccountId", query = "select ug from UserGroup ug left join ug.members m where m.id = :accountId or ug.owner.id = :accountId"),
        @NamedQuery(name = "UserGroup.findByName", query = "select ug from UserGroup ug where ug.name = :name"),
        @NamedQuery(name = "UserGroup.findByCode", query = "select ug from UserGroup ug where ug.code = :code"),
        @NamedQuery(name = "UserGroup.findMembersByGroupId", query = "select ug.members from UserGroup ug where ug.id = :groupId")
})
@Entity
public class UserGroup extends AbstractPersistable<Long> {
    @ManyToOne
    private UserAccount owner;
    @ManyToMany
    private List<UserAccount> members;
    private String name;
    private String code;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public UserGroup(Long id) {
        this.setId(id);
    }

    public UserGroup() {
        this(null);
    }

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount admin) {
        this.owner = admin;
    }

    public List<UserAccount> getMembers() {
        return members;
    }

    public void setMembers(List<UserAccount> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String uuid) {
        this.code = uuid;
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
