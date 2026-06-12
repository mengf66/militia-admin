package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 通知消息表
 * @TableName notification
 */
@TableName(value ="notification")
@Data
public class Notification implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接收用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 接收时所属组织ID（冗余快照）
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * 发布组织ID
     */
    @TableField(value = "publisher_org_id")
    private Long publisherOrgId;

    /**
     * 发布人ID
     */
    @TableField(value = "publisher_id")
    private Long publisherId;

    /**
     * 通知标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 通知内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 通知类型：0=公告通知，1=学习通知，2=提醒通知
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 来源类型：0=公告，1=学习
     */
    @TableField(value = "source_type")
    private Integer sourceType;

    /**
     * 关联的公告ID或学习ID
     */
    @TableField(value = "source_id")
    private Long sourceId;

    /**
     * 是否已读：0=未读，1=已读
     */
    @TableField(value = "is_read")
    private Integer isRead;

    /**
     * 阅读时间
     */
    @TableField(value = "read_time")
    private Date readTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}