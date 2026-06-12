package com.feng.militia_admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * WebSocket 即时通知消息 VO
 * 用于推送给前端的消息格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    /**
     * 消息类型：NEW_NOTIFICATION=新通知，BROADCAST=广播，READ_CONFIRM=已读确认
     */
    private String msgType;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 来源ID（公告ID或学习ID）
     */
    private Long sourceId;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容摘要
     */
    private String content;

    /**
     * 通知类型：0=公告通知，1=学习通知，2=提醒通知
     */
    private Integer type;

    /**
     * 来源类型：0=公告，1=学习
     */
    private Integer sourceType;

    /**
     * 发布人名称
     */
    private String publisherName;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 未读通知总数（推送时附带，方便前端更新角标）
     */
    private Long unreadCount;
}
