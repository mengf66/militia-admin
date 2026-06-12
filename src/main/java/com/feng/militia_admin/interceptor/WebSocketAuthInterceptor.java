package com.feng.militia_admin.interceptor;

import com.feng.militia_admin.model.dto.SecurityUser;
import com.feng.militia_admin.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 * 从 URL 查询参数中解析 JWT Token，建立用户认证上下文
 */
@Slf4j
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");

            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                SecurityUser securityUser = new SecurityUser();
                securityUser.setId(userId);
                securityUser.setName(username);
                securityUser.setRole(role);

                // 将用户信息存入 WebSocket Session 属性，供后续使用
                attributes.put("userId", userId);
                attributes.put("username", username);
                attributes.put("securityUser", securityUser);
                log.debug("WebSocket 握手成功，用户ID: {}", userId);
                return true;
            }
        }
        // 没有 token 也允许握手，但用户无法接收点对点消息
        log.warn("WebSocket 握手未携带有效 token");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 无需处理
    }
}
