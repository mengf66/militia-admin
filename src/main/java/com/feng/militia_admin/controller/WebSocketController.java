package com.feng.militia_admin.controller;

import com.feng.militia_admin.model.vo.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 消息控制器
 * 负责即时推送通知给在线用户
 */
@Slf4j
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 客户端订阅确认（测试用）
     * 订阅 /app/subscribe 时返回连接成功消息
     */
    @SubscribeMapping("/subscribe")
    public Map<String, Object> handleSubscribe() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "connected");
        response.put("message", "WebSocket 连接成功");
        return response;
    }

    /**
     * 向指定用户推送通知消息
     * 前端需订阅 /user/topic/notifications
     */
    public void sendNotificationToUser(Long userId, NotificationMessage message) {
        String destination = "/user/topic/notifications";
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/topic/notifications",
                message
        );
        log.debug("已向用户 {} 推送通知: {}", userId, message.getTitle());
    }

    /**
     * 向指定用户推送已读确认（更新未读角标）
     * 前端需订阅 /user/topic/notifications
     */
    public void sendReadConfirmToUser(Long userId, Long unreadCount) {
        NotificationMessage confirm = NotificationMessage.builder()
                .msgType("READ_CONFIRM")
                .unreadCount(unreadCount)
                .build();
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/topic/notifications",
                confirm
        );
        log.debug("已向用户 {} 推送已读确认，剩余未读: {}", userId, unreadCount);
    }

    /**
     * 广播通知给所有在线用户
     * 前端需订阅 /topic/broadcast
     */
    public void broadcastNotification(NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/broadcast", message);
        log.debug("已广播通知: {}", message.getTitle());
    }
}
