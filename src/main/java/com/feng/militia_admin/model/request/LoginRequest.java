package com.feng.militia_admin.model.request;

import lombok.Data;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/4 18:05
 * @description 类功能描述
 */
@Data
public class LoginRequest {
    public String account;
    public String password;
}
