package com.feng.militia_admin.model.request;

import lombok.Data;

/**
 * 更新工作状态请求
 */
@Data
public class UpdateWorkStatusRequest {

    private Long id;

    /**
     * 状态：0-草稿，1-进行中，2-已完成，3-已取消
     */
    private Integer status;
}
