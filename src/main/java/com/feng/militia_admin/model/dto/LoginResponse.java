package com.feng.militia_admin.model.dto;

import com.feng.militia_admin.model.vo.UserInfoVO;
import lombok.Data;

/**
 * 登录响应数据
 */
@Data
public class LoginResponse {

    /** JWT Token */
    private String token;

    /** 当前登录用户信息（包含角色、权限、菜单等，用于前端展示） */
    private UserInfoVO user;
}
