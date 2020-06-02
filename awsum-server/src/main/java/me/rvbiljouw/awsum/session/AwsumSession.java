package me.rvbiljouw.awsum.session;

import me.rvbiljouw.awsum.model.UserAccount;

/**
 * @author rvbiljouw
 */
public class AwsumSession {
    private final UserAccount account;
    private long groupId;

    public AwsumSession(UserAccount account) {
        this.account = account;
    }

    public UserAccount getAccount() {
        return account;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

}
