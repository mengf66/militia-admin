package com.feng.militia_admin.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/9 16:23
 * @description 类功能描述
 */
@Data
public class PubNoticeRequest {
    private Long userId;

    private Long orgId;

    private String title;

    private String content;

    /**
     * 通知类型：0=公告通知，1=学习通知，2=提醒通知
     */
    private Integer type = 0;

    /**
     * 来源类型：0=公告，1=学习
     */
    private Integer sourceType = 0;

    /**
     * 关联的公告ID或学习ID
     */
    private Long sourceId = 0L;
}
