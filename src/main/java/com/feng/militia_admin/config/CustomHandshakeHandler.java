package com.feng.militia_admin.config;

import com.feng.militia_admin.model.dto.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 自定义 WebSocket 握手处理器
 * 将 WebSocket Session 中的用户信息转换为 Principal，
 * 使 Spring 的 convertAndSendToUser 能够根据用户ID点对点推送
 */
@Slf4j
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Object userId = attributes.get("userId");
        Object username = attributes.get("username");
        Object securityUser = attributes.get("securityUser");

        if (userId != null) {
            return new StompPrincipal(String.valueOf(userId), username != null ? String.valueOf(username) : null,
                    (SecurityUser) securityUser);
        }
        return null;
    }

    /**
     * 自定义 Principal 实现，使用用户ID作为唯一标识
     */
    public static class StompPrincipal implements Principal {
        private final String userId;
        private final String username;
        private final SecurityUser securityUser;

        public StompPrincipal(String userId, String username, SecurityUser securityUser) {
            this.userId = userId;
            this.username = username;
            this.securityUser = securityUser;
        }

        @Override
        public String getName() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public SecurityUser getSecurityUser() {
            return securityUser;
        }
    }
}
