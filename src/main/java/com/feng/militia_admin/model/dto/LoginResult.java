package com.feng.militia_admin.model.dto;

import com.feng.militia_admin.model.vo.UserInfoVO;
import lombok.Data;

/**
 * 登录结果（内部使用，不返回给前端）
 */
@Data
public class LoginResult {

    /** 用户ID（用于生成Token） */
    private Long userId;

    /** 前端用户信息（用于前端展示） */
    private UserInfoVO userInfo;
}
