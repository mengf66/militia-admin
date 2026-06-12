package com.feng.militia_admin.model.request;

import lombok.Data;

import java.util.Date;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/8 9:52
 * @description 类功能描述
 */
@Data
public class CreateAnnouncementRequest {

    private String title;

    private String content;

    private Integer type;

    private Long publishOrgId;

    private String targetOrgIds;

    private Integer status;
}
