package me.rvbiljouw.awsum.session;

import com.google.common.eventbus.EventBus;
import me.rvbiljouw.awsum.auth.AuthenticatedUser;
import me.rvbiljouw.awsum.model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.messaging.simp.SimpAttributesContextHolder.currentAttributes;

/**
 * @author rvbiljouw
 */
@Component
public class ConnectionHandler {
    private final ConcurrentMap<String, AwsumSession> sessions = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SimpMessagingTemplate template;

    public ConnectionHandler(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        final AuthenticatedUser wrapper = (AuthenticatedUser) event.getUser();
        final UserAccount account = (UserAccount) wrapper.getPrincipal();
        final String sessionId = currentAttributes().getSessionId();
        sessions.put(sessionId, new AwsumSession(account));
        logger.info("Registered new session [#" + sessionId + "] for user " + account.getId());
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        final AwsumSession session = sessions.get(event.getSessionId());
        if (session != null) {
            sessions.remove(event.getSessionId());
            logger.info("De-registered session [#" + event.getSessionId() + "]");
        }
    }

    public void broadcastMessageToGroup(long groupId, Object message) {
        sessions.entrySet()
                .stream()
                .filter(it -> it.getValue().getGroupId() == groupId)
                .map(Map.Entry::getValue)
                .forEach(session -> sendMessageToSession(session, message));
    }

    public void sendMessageToSession(AwsumSession session, Object message) {
        final String stringifiedId = String.valueOf(session.getAccount().getId());
        template.convertAndSendToUser(stringifiedId, "/queue/feed", message);
    }

    public Optional<AwsumSession> getSession(String sessionId) {
        return Optional.of(sessions.get(sessionId));
    }
}
