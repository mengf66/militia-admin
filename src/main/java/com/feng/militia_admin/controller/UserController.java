package com.feng.militia_admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.model.domain.UserApply;
import com.feng.militia_admin.model.request.AddUserApplyRequest;
import com.feng.militia_admin.model.request.ApproveUserRequest;
import com.feng.militia_admin.model.vo.ShowUserInfoVo;
import com.feng.militia_admin.service.UserApplyService;
import com.feng.militia_admin.service.UserService;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/5 8:14
 * @description 类功能描述
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final UserApplyService userApplyService;

    private final UserMapper userMapper;

    @PreAuthorize("hasAuthority('militia:info:add')")
    @PostMapping("/add")
    public R<String> addUser(@RequestBody AddUserApplyRequest addUserApplyRequest) {
        if(addUserApplyRequest == null) {
            return R.failed("参数错误");
        }
        boolean b = userApplyService.addUserApply(addUserApplyRequest);
        if(!b) {
            return R.failed("添加人员失败");
        }
        return R.ok("更新成功");
    }

    @PreAuthorize("hasAuthority('militia:info:view')")
    @GetMapping("/show")
    public R<Map<String, Object>> showUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ShowUserInfoVo> allList = userService.showOrgUser();
        int total = allList.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<ShowUserInfoVo> subList = from < total ? allList.subList(from, to) : new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("list", subList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    @PreAuthorize("hasAuthority('audit:person')")
    @GetMapping("/apply-list")
    public R<Map<String, Object>> showApplyList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        List<UserApply> allList = userApplyService.getApplyList(orgId);
        int total = allList.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<UserApply> subList = from < total ? allList.subList(from, to) : new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("list", subList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    @PreAuthorize("hasAuthority('audit:person')")
    @PostMapping("/approve")
    public R<String> approveUserApply(@RequestBody ApproveUserRequest approveUserRequest) {
        if(approveUserRequest == null) {
            return R.failed("参数错误");
        }

        UserApply userApply = BeanUtil.copyProperties(approveUserRequest, UserApply.class);
        int i = userApplyService.updateUserApply(approveUserRequest.getId(), userApply);
        if(i != 1) {
            return R.failed("审批出错");
        }

        // 审批通过后，异步插入用户表
        if (approveUserRequest.getStatus() != null && approveUserRequest.getStatus() == 0) {
            userApplyService.insertUserFromApply(approveUserRequest.getId());
        }

        return R.ok("审批完成");
    }

}
