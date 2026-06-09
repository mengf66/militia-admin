package com.feng.militia_admin.model.vo;

import com.feng.militia_admin.model.domain.Permission;
import lombok.Data;

import java.util.List;

/**
 * 前端用户信息视图对象（用于登录响应、个人信息页面等）
 */
@Data
public class UserInfoVO {

    /** 用户名 */
    private String name;

    /** 角色标识 */
    private String role;

    /**
     * 权限标识列表（如 militia:info:view）
     * 用于前端按钮级权限控制
     */
    private List<String> permissionList;

    /**
     * 菜单列表（menu_type = 0 目录 或 1 菜单）
     * 用于前端展示页面导航
     */
    private List<Permission> menus;
}
