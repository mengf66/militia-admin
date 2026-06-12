package com.feng.militia_admin.service;

import com.feng.militia_admin.model.domain.Notification;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.model.vo.NotificationMessage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lenovo
 * @description 针对表【notification(通知消息表)】的数据库操作Service
 * @createDate 2026-06-09 17:25:19
*/
@Service
public interface NotificationService extends IService<Notification> {

    /**
     * 发布单条通知（仅入库）
     */
    int publish(PubNoticeRequest pubNoticeRequest);

    /**
     * 批量发布通知（仅入库）
     */
    boolean publishBatch(List<PubNoticeRequest> list);

    /**
     * 发布通知并即时推送给目标用户
     * @return 发布的通知ID
     */
    Long publishAndNotify(PubNoticeRequest pubNoticeRequest);

    /**
     * 获取用户的未读通知列表
     */
    List<Notification> getUnreadList(Long userId);

    /**
     * 获取用户的未读通知数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记单条通知为已读
     */
    boolean markAsRead(Long notificationId, Long userId);

    /**
     * 标记用户所有通知为已读
     */
    boolean markAllAsRead(Long userId);

    /**
     * 获取用户的通知列表（包含已读和未读）
     */
    List<Notification> getUserNotifications(Long userId);

    /**
     * 构建 WebSocket 推送消息体
     */
    NotificationMessage buildMessage(Notification notification, Long unreadCount);
}
