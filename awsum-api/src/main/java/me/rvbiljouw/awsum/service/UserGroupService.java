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
import me.rvbiljouw.awsum.model.UserGroup;
import me.rvbiljouw.awsum.repository.UserGroupRepository;
import me.rvbiljouw.awsum.response.PageResponse;
import me.rvbiljouw.awsum.response.SimpleUserAccountResponse;
import me.rvbiljouw.awsum.response.SimpleUserGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A service for interacting with {@link UserGroup} objects
 *
 * @author rvbiljouw
 */
@Service
public class UserGroupService {
    private final UserGroupRepository userGroups;

    public UserGroupService(UserGroupRepository userGroups) {
        this.userGroups = userGroups;
    }

    /**
     * Retrieves a page of groups that belong to the supplied account.
     *
     * @param accountId the account
     * @param limit     max no. of records to return
     * @param offset    no. of records to skip
     * @return a page of groups
     */
    public PageResponse<SimpleUserGroupResponse> getGroupsByOwnerId(long accountId, int limit, int offset) {
        final Page<UserGroup> ownedPage = userGroups.findByOwnerId(
                accountId, limitAndOffset(limit, offset));
        return pageToPageResponse(ownedPage);
    }

    /**
     * Retrieves a page of groups that the supplied account is a member of.
     *
     * @param memberAccountId the account
     * @param limit           max no. of records to return
     * @param offset          no. of records to skip
     * @return a page of groups
     */
    public PageResponse<SimpleUserGroupResponse> getGroupsByMemberId(long memberAccountId, int limit, int offset) {
        final Page<UserGroup> memberOfPage = userGroups.findByMemberId(
                memberAccountId, limitAndOffset(limit, offset));
        return pageToPageResponse(memberOfPage);
    }

    /**
     * Retrieves a page of groups that somehow involve the supplied account id.
     * This could be through being a member or owning the group.
     *
     * @param accountId the account
     * @param limit     max no. of records to return
     * @param offset    no. of records to skip
     * @return a page of groups
     */
    public PageResponse<SimpleUserGroupResponse> getGroupsInvolvingAccountId(long accountId, int limit, int offset) {
        final Page<UserGroup> memberOfPage = userGroups.findInvolvingAccountId(
                accountId, limitAndOffset(limit, offset));
        return pageToPageResponse(memberOfPage);
    }

    /**
     * Checks if the group name is available or already taken
     *
     * @param name a group name
     * @return true if free, false if taken
     */
    public boolean isGroupNameAvailable(String name) {
        return !userGroups.findByName(name).isPresent();
    }

    /**
     * Retrieves a page of members for the supplied group id.
     *
     * @param groupId the group id
     * @param limit   max no. of records to return
     * @param offset  no. of records to skip
     * @return a page of members
     */
    public PageResponse<SimpleUserAccountResponse> getMembersByGroup(long groupId, int limit, int offset) {
        final Page<UserAccount> members = userGroups.findMembersByGroupId(groupId, limitAndOffset(limit, offset));
        final List<SimpleUserAccountResponse> packed = members.getContent().stream()
                .map(SimpleUserAccountResponse::new)
                .collect(Collectors.toList());
        return new PageResponse<>(members.getTotalElements(), packed);
    }

    /**
     * Checks if the supplied account is a member of the supplied group.
     *
     * @param account the account
     * @param group   the group
     * @return true if a member, false if not.
     */
    public boolean checkMembership(UserAccount account, UserGroup group) {
        return Objects.equals(group.getOwner().getId(), account.getId()) || group.getMembers().contains(account);
    }

    /**
     * Creates a {@link PageRequest} object from a limit and offset combo
     *
     * @param limit  max no. of records per page
     * @param offset no. of records to skip
     * @return page request
     */
    private PageRequest limitAndOffset(int limit, int offset) {
        return PageRequest.of(offset / limit, limit);
    }

    /**
     * Maps a {@link Page} to a corresponding {@link PageResponse} object
     *
     * @param userGroupPage a page of groups
     * @return a page response
     */
    private PageResponse<SimpleUserGroupResponse> pageToPageResponse(Page<UserGroup> userGroupPage) {
        final List<SimpleUserGroupResponse> packed = userGroupPage.get()
                .map(SimpleUserGroupResponse::new)
                .collect(Collectors.toList());
        return new PageResponse<>(userGroupPage.getTotalElements(), packed);
    }

}
