package me.rvbiljouw.awsum.service;

import me.rvbiljouw.awsum.model.UserGroup;
import me.rvbiljouw.awsum.repository.UserGroupRepository;
import me.rvbiljouw.awsum.response.MessageWrapper;
import me.rvbiljouw.awsum.session.AwsumSession;
import me.rvbiljouw.awsum.session.ConnectionHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author rvbiljouw
 */
@Service
public class SessionService {
    private final UserGroupRepository userGroupRepository;
    private final ConnectionHandler connectionHandler;

    public SessionService(UserGroupRepository userGroupRepository, ConnectionHandler connectionHandler) {
        this.userGroupRepository = userGroupRepository;
        this.connectionHandler = connectionHandler;
    }

    public boolean setGroupForSession(String sessionId, long groupId) {
        final Optional<UserGroup> groupOpt = userGroupRepository.findById(groupId);
        final Optional<AwsumSession> sessionOpt = connectionHandler.getSession(sessionId);
        if (groupOpt.isPresent() && sessionOpt.isPresent()) {
            final UserGroup group = groupOpt.get();
            // TODO: Check access levels
            final AwsumSession session = sessionOpt.get();
            session.setGroupId(groupId);

            broadcastJoinAnnouncement(session.getAccount().getDisplayName(), groupId);
            return true;
        }
        return false;
    }

    private void broadcastJoinAnnouncement(String displayName, long groupId) {
        final String announcement = String.format("User %s has connected to the group.", displayName);
        final MessageWrapper<String> message = new MessageWrapper<>("Announcement", announcement);
        connectionHandler.broadcastMessageToGroup(groupId, message);
    }

}
