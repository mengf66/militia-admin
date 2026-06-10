package com.feng.militia_admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.model.domain.UserApply;
import com.feng.militia_admin.model.request.AddUserApplyRequest;
import com.feng.militia_admin.model.request.ApproveUserRequest;
import com.feng.militia_admin.model.vo.ShowUserInfoVo;
import com.feng.militia_admin.service.UserApplyService;
import com.feng.militia_admin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public R<List<ShowUserInfoVo>> showUser() {
        return R.ok(userService.showOrgUser());
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
        return R.ok("审批完成");
    }

}
