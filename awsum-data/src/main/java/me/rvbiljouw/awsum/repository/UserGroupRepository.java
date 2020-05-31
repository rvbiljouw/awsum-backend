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
package me.rvbiljouw.awsum.repository;

import me.rvbiljouw.awsum.model.UserAccount;
import me.rvbiljouw.awsum.model.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author rvbiljouw
 */
@Repository
public interface UserGroupRepository extends CrudRepository<UserGroup, Long>, PagingAndSortingRepository<UserGroup, Long> {

    Page<UserGroup> findByOwnerId(@Param("ownerId") long ownerId, Pageable pageable);

    Page<UserGroup> findByMemberId(@Param("memberId") long memberId, Pageable pageable);

    Page<UserGroup> findInvolvingAccountId(@Param("accountId") long accountId, Pageable pageable);

    Page<UserAccount> findMembersByGroupId(@Param("groupId") long groupId, Pageable pageable);

    Optional<UserGroup> findByName(@Param("name") String name);

    Optional<UserGroup> findByCode(@Param("code") String code);

}
