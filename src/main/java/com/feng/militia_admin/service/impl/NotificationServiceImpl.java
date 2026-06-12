package com.feng.militia_admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.militia_admin.controller.WebSocketController;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.model.domain.Notification;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.model.vo.NotificationMessage;
import com.feng.militia_admin.service.NotificationService;
import com.feng.militia_admin.mapper.NotificationMapper;
import com.feng.militia_admin.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @description 针对表【notification(通知消息表)】的数据库操作Service实现
 * @createDate 2026-06-09 17:25:19
 */
@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private WebSocketController webSocketController;

    @Override
    public int publish(PubNoticeRequest pubNoticeRequest) {
        Notification notification = BeanUtil.copyProperties(pubNoticeRequest, Notification.class);
        notification.setPublisherId(SecurityUtil.getCurrentUserId());
        notification.setCreateTime(new Date());
        notification.setIsRead(0);
        notification.setPublisherOrgId(userMapper.getUserOrgId(SecurityUtil.getCurrentUserId()));
        int insert = notificationMapper.insert(notification);
        return insert;
    }

    @Override
    public boolean publishBatch(List<PubNoticeRequest> list) {
        List<Notification> notifications = list.stream()
                .map(pub -> {
                    Notification n = BeanUtil.copyProperties(pub, Notification.class);
                    return n;
                })
                .collect(Collectors.toList());
        // 用 Stream 统一赋值
        notifications.forEach(n -> {
            n.setPublisherId(SecurityUtil.getCurrentUserId());
            n.setCreateTime(new Date());
            n.setIsRead(0);
        });
        boolean saved = saveBatch(notifications);

        // WebSocket 即时推送给每个接收用户
        if (saved) {
            notifications.forEach(n -> {
                Long targetUserId = n.getUserId();
                if (targetUserId != null && targetUserId > 0) {
                    Long unreadCount = getUnreadCount(targetUserId);
                    NotificationMessage message = buildMessage(n, unreadCount);
                    webSocketController.sendNotificationToUser(targetUserId, message);
                }
            });
        }

        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishAndNotify(PubNoticeRequest pubNoticeRequest) {
        // 1. 入库
        Notification notification = BeanUtil.copyProperties(pubNoticeRequest, Notification.class);
        Long currentUserId = SecurityUtil.getCurrentUserId();
        notification.setPublisherId(currentUserId);
        notification.setCreateTime(new Date());
        notification.setIsRead(0);
        notification.setPublisherOrgId(userMapper.getUserOrgId(currentUserId));
        notificationMapper.insert(notification);

        // 2. 即时推送
        Long targetUserId = notification.getUserId();
        if (targetUserId != null && targetUserId > 0) {
            Long unreadCount = getUnreadCount(targetUserId);
            NotificationMessage message = buildMessage(notification, unreadCount);
            webSocketController.sendNotificationToUser(targetUserId, message);
            log.info("即时通知已推送给用户 {}，通知ID={}", targetUserId, notification.getId());
        } else {
            // 如果没有指定用户，则广播给组织内所有用户
            Long orgId = notification.getOrgId();
            if (orgId != null && orgId > 0) {
                List<Long> userIds = userMapper.getUserIds(orgId);
                for (Long uid : userIds) {
                    Long unreadCount = getUnreadCount(uid);
                    NotificationMessage message = buildMessage(notification, unreadCount);
                    webSocketController.sendNotificationToUser(uid, message);
                }
                log.info("即时通知已广播给组织 {} 的 {} 位用户，通知ID={}", orgId, userIds.size(), notification.getId());
            }
        }

        return notification.getId();
    }

    @Override
    public List<Notification> getUnreadList(Long userId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_read", 0)
                .orderByDesc("create_time");
        return notificationMapper.selectList(wrapper);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_read", 0);
        return notificationMapper.selectCount(wrapper).longValue();
    }

    @Override
    public boolean markAsRead(Long notificationId, Long userId) {
        UpdateWrapper<Notification> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", notificationId)
                .eq("user_id", userId)
                .set("is_read", 1)
                .set("read_time", new Date());
        boolean updated = notificationMapper.update(null, wrapper) > 0;
        if (updated) {
            // 推送已读确认，更新前端角标
            Long unreadCount = getUnreadCount(userId);
            webSocketController.sendReadConfirmToUser(userId, unreadCount);
        }
        return updated;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        UpdateWrapper<Notification> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_read", 0)
                .set("is_read", 1)
                .set("read_time", new Date());
        boolean updated = notificationMapper.update(null, wrapper) > 0;
        if (updated) {
            webSocketController.sendReadConfirmToUser(userId, 0L);
        }
        return updated;
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("create_time");
        return notificationMapper.selectList(wrapper);
    }

    @Override
    public NotificationMessage buildMessage(Notification notification, Long unreadCount) {
        return NotificationMessage.builder()
                .msgType("NEW_NOTIFICATION")
                .notificationId(notification.getId())
                .sourceId(notification.getSourceId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .sourceType(notification.getSourceType())
                .publishTime(notification.getCreateTime())
                .unreadCount(unreadCount)
                .build();
    }
}
