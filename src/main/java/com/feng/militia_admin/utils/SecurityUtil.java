package com.feng.militia_admin.utils;

import com.feng.militia_admin.model.dto.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类
 * 用于获取当前登录用户信息
 */
public class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        SecurityUser user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        SecurityUser user = getCurrentUser();
        return user != null ? user.getName() : null;
    }

    /**
     * 获取当前登录用户角色
     */
    public static String getCurrentRole() {
        SecurityUser user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    /**
     * 获取当前登录用户对象
     */
    public static SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        }
        return null;
    }
}
