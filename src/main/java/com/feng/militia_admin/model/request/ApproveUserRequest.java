package com.feng.militia_admin.model.request;

import lombok.Data;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/7 20:15
 * @description 类功能描述
 */
@Data
public class ApproveUserRequest {
    private Long id;
    private Integer status;
    private String auditRemark;
}
