package com.feng.militia_admin.service.impl;

import com.feng.militia_admin.controller.WebSocketController;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.model.domain.Notification;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.model.vo.NotificationMessage;
import com.feng.militia_admin.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * WebSocket 推送服务
 * 封装公告/通知的推送逻辑，供 AnnouncementService 等调用
 */
@Service
public class WebSocketPushService {

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发布公告并推送给组织内所有用户
     */
    public void pushAnnouncement(PubNoticeRequest request) {
        Long notificationId = notificationService.publishAndNotify(request);
    }

    /**
     * 向指定用户列表推送通知
     */
    public void pushToSpecificUsers(PubNoticeRequest request, List<Long> userIds) {
        for (Long userId : userIds) {
            request.setUserId(userId);
            notificationService.publishAndNotify(request);
        }
    }
}
