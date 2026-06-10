package com.feng.militia_admin.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.model.domain.Notification;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.service.NotificationService;
import com.feng.militia_admin.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知消息控制器
 * 提供通知发布、查询、已读标记等 REST API
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 发布通知（仅入库，不推送）
     */
    @PostMapping("/publish")
    public R<String> publishNotification(@RequestBody PubNoticeRequest pubNoticeRequest) {
        if (pubNoticeRequest == null) {
            return R.failed("发布失败，请求体为空");
        }
        int i = notificationService.publish(pubNoticeRequest);
        if (i != 1) {
            return R.failed("发布失败");
        }
        return R.ok("发布成功");
    }

    /**
     * 发布通知并即时推送给目标用户
     */
    @PostMapping("/publish-and-notify")
    public R<Long> publishAndNotify(@RequestBody PubNoticeRequest pubNoticeRequest) {
        if (pubNoticeRequest == null) {
            return R.failed("发布失败，请求体为空");
        }
        Long notificationId = notificationService.publishAndNotify(pubNoticeRequest);
        if (notificationId == null) {
            return R.failed("发布失败");
        }
        return R.ok(notificationId);
    }

    /**
     * 获取当前登录用户的未读通知列表
     */
    @GetMapping("/unread-list")
    public R<List<Notification>> getUnreadList() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return R.failed("用户未登录");
        }
        List<Notification> list = notificationService.getUnreadList(userId);
        return R.ok(list);
    }

    /**
     * 获取当前登录用户的未读通知数量
     */
    @GetMapping("/unread-count")
    public R<Long> getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return R.failed("用户未登录");
        }
        Long count = notificationService.getUnreadCount(userId);
        return R.ok(count);
    }

    /**
     * 获取当前登录用户的全部通知列表
     */
    @GetMapping("/list")
    public R<List<Notification>> getUserNotifications() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return R.failed("用户未登录");
        }
        List<Notification> list = notificationService.getUserNotifications(userId);
        return R.ok(list);
    }

    /**
     * 标记单条通知为已读
     */
    @PostMapping("/mark-read/{notificationId}")
    public R<String> markAsRead(@PathVariable Long notificationId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return R.failed("用户未登录");
        }
        boolean success = notificationService.markAsRead(notificationId, userId);
        if (!success) {
            return R.failed("标记已读失败，通知不存在或无权操作");
        }
        return R.ok("标记已读成功");
    }

    /**
     * 标记当前用户所有通知为已读
     */
    @PostMapping("/mark-all-read")
    public R<String> markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return R.failed("用户未登录");
        }
        notificationService.markAllAsRead(userId);
        return R.ok("全部标记已读成功");
    }
}
