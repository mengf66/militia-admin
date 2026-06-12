package com.feng.militia_admin.model.request;

import lombok.Data;

/**
 * 干部填写工作请求
 */
@Data
public class FillWorkRequest {

    private Long id;

    /**
     * 填写的工作内容
     */
    private String content;
}
