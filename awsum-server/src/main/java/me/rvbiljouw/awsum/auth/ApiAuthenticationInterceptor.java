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
package me.rvbiljouw.awsum.auth;

import me.rvbiljouw.awsum.model.AuthToken;
import me.rvbiljouw.awsum.repository.AuthTokenRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * A filter that looks at the Authentication header to extract and validate a token
 *
 * @author rvbiljouw
 */
@Component
public class ApiAuthenticationInterceptor implements ChannelInterceptor {
    private final AuthTokenRepository authTokenRepository;

    public ApiAuthenticationInterceptor(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = accessor != null ? accessor.getCommand() : null;
        if (command == StompCommand.CONNECT) {
            handleAuthentication(accessor);
        }
        return message;
    }

    private void handleAuthentication(StompHeaderAccessor accessor) {
        final String authHeader = accessor.getFirstNativeHeader("Authorization");
        final Optional<AuthToken> token = retrieveToken(authHeader);
        if (!token.isPresent()) {
            SecurityContextHolder.getContext().setAuthentication(null);
            accessor.setUser(null);
        } else {
            final AuthenticatedUser wrapper = new AuthenticatedUser(token.get());
            SecurityContextHolder.getContext().setAuthentication(wrapper);
            accessor.setUser(wrapper);
        }
    }

    /**
     * Check if the supplied token string is valid
     *
     * @param token token string
     * @return true if valid
     */
    private boolean isTokenValid(String token) {
        return token != null && token.startsWith("Bearer");
    }

    /**
     * Checks the validity of the token and fetch it from the database
     *
     * @param token token string
     * @return an auth token if it was found
     */
    private Optional<AuthToken> retrieveToken(String token) {
        if (!isTokenValid(token)) {
            return Optional.empty();
        }
        return authTokenRepository.findByToken(token.replace("Bearer ", ""));
    }

}
