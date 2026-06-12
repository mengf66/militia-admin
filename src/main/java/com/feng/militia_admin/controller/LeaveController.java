package com.feng.militia_admin.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.model.request.ApproveLeaveRequest;
import com.feng.militia_admin.model.request.CancelLeaveRequest;
import com.feng.militia_admin.model.request.CreateLeaveRequest;
import com.feng.militia_admin.model.vo.ShowLeaveInfoVo;
import com.feng.militia_admin.service.LeaveService;
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
 * 请假管理控制器
 */
@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
@Slf4j
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 提交请假申请
     */
    @PreAuthorize("hasAuthority('leave:apply')")
    @PostMapping("/apply")
    public R<Long> apply(@RequestBody CreateLeaveRequest request) {
        if (request == null) {
            return R.failed("参数错误");
        }
        Long id = leaveService.createLeave(request);
        return R.ok(id);
    }

    /**
     * 干部确认销假
     */
    @PreAuthorize("hasAuthority('leave:audit')")
    @PostMapping("/confirm-cancel")
    public R<String> confirmCancel(@RequestBody CancelLeaveRequest request) {
        if (request == null || request.getId() == null) {
            return R.failed("参数错误");
        }
        Long auditorUserId = SecurityUtil.getCurrentUserId();
        boolean success = leaveService.confirmCancelLeave(request.getId(), auditorUserId);
        if (!success) {
            return R.failed("确认销假失败");
        }
        return R.ok("已确认销假");
    }

    /**
     * 销假（请假人回来后提交销假申请）
     */
    @PreAuthorize("hasAuthority('leave:apply')")
    @PostMapping("/cancel")
    public R<String> cancel(@RequestBody CancelLeaveRequest request) {
        if (request == null || request.getId() == null) {
            return R.failed("参数错误");
        }
        Long userId = SecurityUtil.getCurrentUserId();
        boolean success = leaveService.cancelLeave(request.getId(), userId);
        if (!success) {
            return R.failed("销假失败，请检查请假状态");
        }
        return R.ok("销假申请已提交，等待干部确认");
    }

    /**
     * 审批请假
     */
    @PreAuthorize("hasAuthority('leave:audit')")
    @PostMapping("/approve")
    public R<String> approve(@RequestBody ApproveLeaveRequest request) {
        if (request == null || request.getId() == null) {
            return R.failed("参数错误");
        }
        boolean success = leaveService.approveLeave(request);
        if (!success) {
            return R.failed("审批失败");
        }
        return R.ok("审批完成");
    }

    /**
     * 查询当前组织及其下属组织的请假记录（审批人/查看视角）
     */
    @PreAuthorize("hasAnyAuthority('leave:audit', 'leave:view')")
    @GetMapping("/list")
    public R<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        List<ShowLeaveInfoVo> allList = leaveService.getLeaveList(orgId);
        int total = allList.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<ShowLeaveInfoVo> subList = from < total ? allList.subList(from, to) : new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("list", subList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    /**
     * 查询当前用户的请假记录（个人视角）
     */
    @PreAuthorize("hasAuthority('leave:apply')")
    @GetMapping("/my-list")
    public R<Map<String, Object>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ShowLeaveInfoVo> allList = leaveService.getMyLeaveList(userId);
        int total = allList.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<ShowLeaveInfoVo> subList = from < total ? allList.subList(from, to) : new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("list", subList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }
}
