package me.rvbiljouw.awsum.controller;

import me.rvbiljouw.awsum.request.SubscribeToGroupRequest;
import me.rvbiljouw.awsum.response.MessageWrapper;
import me.rvbiljouw.awsum.response.SubscribeToGroupResponse;
import me.rvbiljouw.awsum.service.SessionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * @author rvbiljouw
 */
@Controller
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @MessageMapping("/subscribeToGroup")
    public MessageWrapper<SubscribeToGroupResponse> subscribeToGroup(
            SubscribeToGroupRequest request,
            SimpMessageHeaderAccessor header) throws Exception {
        if (!sessionService.setGroupForSession(header.getSessionId(), request.getGroupId())) {
            return new MessageWrapper<>(new SubscribeToGroupResponse(false));
        }
        return new MessageWrapper<>(new SubscribeToGroupResponse(true));
    }

}
