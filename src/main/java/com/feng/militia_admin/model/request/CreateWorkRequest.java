package com.feng.militia_admin.model.request;

import lombok.Data;

/**
 * 创建工作请求
 */
@Data
public class CreateWorkRequest {

    private String title;

    private String content;

    /**
     * 工作类型：0-月工作计划，1-工作总结，2-专项活动报告
     */
    private Integer type;

    /**
     * 目标组织ID，逗号分隔
     */
    private String targetOrgIds;

    /**
     * 状态：0-草稿，1-直接发布
     */
    private Integer status;
}
