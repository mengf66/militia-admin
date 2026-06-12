package com.feng.militia_admin.config;

import com.feng.militia_admin.interceptor.WebSocketAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置类
 * 启用 STOMP 消息代理，支持广播和点对点推送
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单内存消息代理，/topic 用于广播，/queue 用于点对点
        config.enableSimpleBroker("/topic", "/queue");
        // 设置用户目的地前缀，用于点对点消息（如 /user/topic/notifications）
        config.setUserDestinationPrefix("/user");
        // 应用目的地前缀，客户端发送消息时使用 /app/xxx
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 STOMP 端点，/ws 用于 WebSocket 连接
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(webSocketAuthInterceptor)
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();
    }
}
