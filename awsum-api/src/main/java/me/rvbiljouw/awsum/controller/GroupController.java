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
import me.rvbiljouw.awsum.exception.ApiException;
import me.rvbiljouw.awsum.model.UserAccount;
import me.rvbiljouw.awsum.model.UserGroup;
import me.rvbiljouw.awsum.repository.UserGroupRepository;
import me.rvbiljouw.awsum.request.CreateGroupRequest;
import me.rvbiljouw.awsum.response.PageResponse;
import me.rvbiljouw.awsum.response.SimpleUserAccountResponse;
import me.rvbiljouw.awsum.response.SimpleUserGroupResponse;
import me.rvbiljouw.awsum.service.UserGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static me.rvbiljouw.awsum.util.BindingUtils.bindingResultToMap;

/**
 * Controller for {@link UserGroup}-related functionality
 *
 * @author rvbiljouw
 */
@RestController
public class GroupController {
    private final UserGroupService userGroupService;
    private final UserGroupRepository userGroupRepository;

    public GroupController(UserGroupService userGroupService, UserGroupRepository userGroupRepository) {
        this.userGroupService = userGroupService;
        this.userGroupRepository = userGroupRepository;
    }

    public enum GetGroupType {
        ALL, MEMBER, OWNER
    }

    /**
     * Retrieve a page of {@link UserGroup} objects
     *
     * @param user      the logged in user
     * @param type      the type of request
     * @param limitArg  max no. of records to return
     * @param offsetArg no. of records to skip
     * @return a page of groups
     * @throws ApiException if the parameter type doesn't match any known (should never occur!)
     */
    @RequestMapping("/api/v1/groups")
    public PageResponse<SimpleUserGroupResponse> getGroups(
            AuthenticatedUser user,
            @RequestParam GetGroupType type,
            @RequestParam("limit") Optional<Integer> limitArg,
            @RequestParam("offset") Optional<Integer> offsetArg) throws ApiException {
        final UserAccount account = (UserAccount) user.getPrincipal();
        final int limit = limitArg.orElse(25);
        final int offset = offsetArg.orElse(0);
        switch (type) {
            case ALL:
                return userGroupService.getGroupsInvolvingAccountId(account.getId(), limit, offset);
            case MEMBER:
                return userGroupService.getGroupsByMemberId(account.getId(), limit, offset);
            case OWNER:
                return userGroupService.getGroupsByOwnerId(account.getId(), limit, offset);
            default:
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid parameter: type");
        }
    }

    /**
     * Create a new {@link UserGroup} owned by the currently logged in {@link UserAccount}
     *
     * @param request       the create request body
     * @param bindingResult validator results
     * @param user          currently logged in user
     * @return a simple group object if successful.
     * @throws ApiException if the request body has errors.
     */
    @RequestMapping(value = "/api/v1/groups", method = {RequestMethod.POST})
    public SimpleUserGroupResponse createGroup(
            @Validated @RequestBody CreateGroupRequest request,
            BindingResult bindingResult,
            AuthenticatedUser user) throws ApiException {
        if (bindingResult.hasErrors()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, bindingResultToMap(bindingResult));
        }

        final UserAccount userAccount = (UserAccount) user.getPrincipal();
        final UserGroup userGroup = new UserGroup();
        userGroup.setOwner(userAccount);
        userGroup.setName(request.getName());
        userGroupRepository.save(userGroup);
        return new SimpleUserGroupResponse(userGroup);
    }

    /**
     * Joins a {@link UserGroup}
     *
     * @param code join code for the group
     * @param user currently logged in user
     * @return a simple group object if successful
     * @throws ApiException if the group doesn't exist
     */
    @RequestMapping(value = "/api/v1/groups/join")
    public SimpleUserGroupResponse joinGroupByCode(
            @RequestParam String code,
            AuthenticatedUser user) throws ApiException {
        final Optional<UserGroup> groupByCode = userGroupRepository.findByCode(code);
        if (!groupByCode.isPresent()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No group exists for code: " + code);
        }

        final UserAccount account = (UserAccount) user.getPrincipal();
        final UserGroup userGroup = groupByCode.get();
        userGroup.getMembers().add(account);
        userGroupRepository.save(userGroup);
        return new SimpleUserGroupResponse(userGroup);
    }

    /**
     * Retrieve a page of {@link UserAccount} objects that are part of this group
     *
     * @param id        the group ID
     * @param limitArg  max no. of records to return
     * @param offsetArg no. of records to skip
     * @param user      currently logged in user
     * @return a page of simple account objects
     * @throws ApiException if the group doesn't exist, or the user
     *                      doesn't have an existing relationship
     *                      with the group.
     */
    @RequestMapping(value = "/api/v1/groups/{id}/members")
    public PageResponse<SimpleUserAccountResponse> getMembersByGroup(
            @PathVariable(value = "id") Long id,
            @RequestParam("limit") Optional<Integer> limitArg,
            @RequestParam("offset") Optional<Integer> offsetArg,
            AuthenticatedUser user) throws ApiException {
        final Optional<UserGroup> groupById = userGroupRepository.findById(id);
        if (!groupById.isPresent()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No group exists for id: " + id);
        }

        final UserAccount account = (UserAccount) user.getPrincipal();
        if (!userGroupService.checkMembership(account, groupById.get())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You don't have access to this group.");
        }

        final int limit = limitArg.orElse(25);
        final int offset = offsetArg.orElse(0);
        return userGroupService.getMembersByGroup(id, limit, offset);
    }

}
