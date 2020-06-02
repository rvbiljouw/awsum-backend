package me.rvbiljouw.awsum.agent;

import me.rvbiljouw.awsum.model.UserGroup;

/**
 * @author rvbiljouw
 */
public class GroupHandler implements Runnable {
    private final UserGroup userGroup;

    public GroupHandler(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    @Override
    public void run() {

    }
}
