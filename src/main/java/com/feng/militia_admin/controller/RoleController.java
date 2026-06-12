package com.feng.militia_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.model.domain.Role;
import com.feng.militia_admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取所有有效角色列表
     */
    @GetMapping("/list")
    public R<List<Role>> list() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getIsDelete, 0)
               .orderByAsc(Role::getId);
        return R.ok(roleService.list(wrapper));
    }
}
