package com.feng.militia_admin.model.request;

import lombok.Data;

/**
 * 团部审批工作请求
 */
@Data
public class AuditWorkRequest {

    private Long id;

    /**
     * 审批结果：3-通过，4-驳回
     */
    private Integer status;

    /**
     * 审批意见
     */
    private String remark;
}
