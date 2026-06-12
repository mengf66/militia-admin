package com.feng.militia_admin.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 工作记录展示VO
 */
@Data
public class ShowWorkInfoVo {

    private Long id;

    private String title;

    private String content;

    /**
     * 工作类型：0-月工作计划，1-工作总结，2-专项活动报告
     */
    private Integer type;

    /**
     * 发布组织ID
     */
    private Long orgId;

    /**
     * 发布组织名称
     */
    private String orgName;

    /**
     * 目标组织ID，逗号分隔
     */
    private String targetOrgIds;

    /**
     * 状态：0-草稿，1-进行中，2-已完成，3-已取消
     */
    private Integer status;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;
}
