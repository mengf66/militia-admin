package com.feng.militia_admin.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 工作填写记录展示VO
 */
@Data
public class ShowWorkSubmissionVo {

    private Long id;

    /**
     * 关联工作内容ID
     */
    private Long workId;

    /**
     * 工作标题
     */
    private String workTitle;

    /**
     * 工作类型：0-月工作计划，1-工作总结，2-专项活动报告
     */
    private Integer workType;

    /**
     * 填写组织ID
     */
    private Long orgId;

    /**
     * 填写组织名称
     */
    private String orgName;

    /**
     * 填写内容
     */
    private String content;

    /**
     * 填写状态：0-未填写，1-已填写
     */
    private Integer fillStatus;

    /**
     * 审批状态：0-待审批，1-已通过，2-已驳回
     */
    private Integer auditStatus;

    /**
     * 审批意见
     */
    private String auditRemark;

    /**
     * 审批人姓名
     */
    private String auditUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
