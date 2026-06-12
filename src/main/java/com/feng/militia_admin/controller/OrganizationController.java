package com.feng.militia_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.model.domain.Organization;
import com.feng.militia_admin.service.OrganizationService;
import com.feng.militia_admin.mapper.OrganizationMapper;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织管理控制器
 * 提供三个独立的组织查询接口，分别用于不同业务场景
 */
@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 【添加人员】获取当前用户所在组织及其所有下属组织（包含自己）
     * 用途：添加人员时选择"所属组织"
     * 范围：营级干部 → 营+连+分队；连级干部 → 连+分队
     */
    @GetMapping("/for-add-person")
    public R<List<Organization>> forAddPerson() {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        if (orgId == null) {
            return R.ok(new ArrayList<>());
        }
        return R.ok(organizationMapper.selectSelfAndSubordinates(orgId));
    }

    /**
     * 【工作管理】获取当前组织的所有下属组织（排除自己），只返回营级
     * 用途：军长/师级发布工作时选择"目标组织"
     * 范围：排除自己，只保留营级组织（org_type=4）
     */
    @GetMapping("/for-work-target")
    public R<List<Organization>> forWorkTarget() {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        if (orgId == null) {
            return R.ok(new ArrayList<>());
        }
        List<Organization> list = organizationMapper.selectSubordinates(orgId);
        // 只保留营级组织（org_type=4）
        list.removeIf(org -> org.getOrgType() == null || org.getOrgType() != 4);
        return R.ok(list);
    }

    /**
     * 【通知公告】获取当前用户所在组织及其所有下属组织（包含自己）
     * 用途：发布公告时选择"目标组织"
     * 范围：自己及所有下属组织（营→连→分队；连→分队）
     */
    @GetMapping("/for-announcement")
    public R<List<Organization>> forAnnouncement() {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        if (orgId == null) {
            return R.ok(new ArrayList<>());
        }
        return R.ok(organizationMapper.selectSelfAndSubordinates(orgId));
    }

    /**
     * 【通用】获取所有有效组织列表
     * 保留原有接口供其他场景使用
     */
    @GetMapping("/list")
    public R<List<Organization>> list() {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getIsDelete, 0)
               .orderByAsc(Organization::getPath);
        return R.ok(organizationService.list(wrapper));
    }

    /**
     * 【通用】获取当前组织及其所有下属组织（包含自己）
     * 保留原有接口供其他场景使用
     */
    @GetMapping("/self-and-subordinates")
    public R<List<Organization>> selfAndSubordinates() {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        if (orgId == null) {
            return R.ok(new ArrayList<>());
        }
        return R.ok(organizationMapper.selectSelfAndSubordinates(orgId));
    }

    /**
     * 【通用】获取当前组织的所有下属组织（排除自己）
     * 保留原有接口供其他场景使用
     */
    @GetMapping("/subordinates")
    public R<List<Organization>> subordinates() {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        if (orgId == null) {
            return R.ok(new ArrayList<>());
        }
        return R.ok(organizationMapper.selectSubordinates(orgId));
    }
}
