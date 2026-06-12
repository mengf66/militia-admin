package com.feng.militia_admin.model.request;

import lombok.Data;

/**
 * 审批请假请求
 */
@Data
public class ApproveLeaveRequest {

    /**
     * 请假记录ID
     */
    private Long id;

    /**
     * 审批状态：0-通过，1-驳回
     */
    private Integer status;

    /**
     * 审批意见
     */
    private String auditRemark;
}
