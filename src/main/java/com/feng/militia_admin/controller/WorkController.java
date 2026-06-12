package com.feng.militia_admin.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.model.request.AuditWorkRequest;
import com.feng.militia_admin.model.request.CreateWorkRequest;
import com.feng.militia_admin.model.request.FillWorkRequest;
import com.feng.militia_admin.model.request.UpdateWorkStatusRequest;
import com.feng.militia_admin.model.vo.ShowWorkInfoVo;
import com.feng.militia_admin.model.vo.ShowWorkSubmissionVo;
import com.feng.militia_admin.service.WorkService;
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
 * 工作管理控制器（双表结构）
 * work：工作内容表（军长发布的工作任务）
 * work_submission：工作填写表（每个目标组织一条记录）
 * 流程：军长新建(草稿) → 军长发布 → 自动创建submission → 干部填写 → 团部审批
 */
@RestController
@RequestMapping("/work")
@RequiredArgsConstructor
@Slf4j
public class WorkController {

    @Autowired
    private WorkService workService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 军长新建工作（草稿状态）
     */
    @PreAuthorize("hasAuthority('work:create')")
    @PostMapping("/create")
    public R<Long> create(@RequestBody CreateWorkRequest request) {
        if (request == null) {
            return R.failed("参数错误");
        }
        Long id = workService.createWork(request);
        return R.ok(id);
    }

    /**
     * 军长发布工作（草稿→已发布，同时自动创建 submission 记录）
     */
    @PreAuthorize("hasAuthority('work:create')")
    @PostMapping("/publish")
    public R<String> publish(@RequestBody UpdateWorkStatusRequest request) {
        if (request == null || request.getId() == null) {
            return R.failed("参数错误");
        }
        boolean success = workService.publishWork(request.getId());
        if (!success) {
            return R.failed("发布失败");
        }
        return R.ok("发布成功，已分配给各组织");
    }

    /**
     * 营/连/分队干部填写工作
     */
    @PreAuthorize("hasAuthority('work:fill')")
    @PostMapping("/fill")
    public R<String> fill(@RequestBody FillWorkRequest request) {
        if (request == null || request.getId() == null) {
            return R.failed("参数错误");
        }
        boolean success = workService.fillWork(request);
        if (!success) {
            return R.failed("填写失败");
        }
        return R.ok("填写成功，已送团部审批");
    }

    /**
     * 团部审批工作
     */
    @PreAuthorize("hasAuthority('work:audit')")
    @PostMapping("/audit")
    public R<String> audit(@RequestBody AuditWorkRequest request) {
        if (request == null || request.getId() == null) {
            return R.failed("参数错误");
        }
        boolean success = workService.auditWork(request);
        if (!success) {
            return R.failed("审批失败");
        }
        String msg = request.getStatus() == 1 ? "审批通过" : "已驳回";
        return R.ok(msg);
    }

    /**
     * 军长查看自己发布的工作列表
     */
    @PreAuthorize("hasAuthority('work:create')")
    @GetMapping("/list")
    public R<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        List<ShowWorkInfoVo> allList = workService.getWorkList(orgId);
        int total = allList.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<ShowWorkInfoVo> subList = from < total ? allList.subList(from, to) : new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("list", subList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    /**
     * 查询工作填写记录（根据角色返回不同数据）
     * 营连分队/团部/师机关/军长都调用此接口查看填写情况
     */
    @PreAuthorize("hasAuthority('work:view')")
    @GetMapping("/submission/list")
    public R<Map<String, Object>> submissionList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long orgId = userMapper.getUserOrgId(userId);
        String role = SecurityUtil.getCurrentRole();
        log.info("查询工作填写记录: userId={}, orgId={}, role={}", userId, orgId, role);
        if (orgId == null) {
            log.warn("无法获取用户组织ID: userId={}", userId);
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("list", new ArrayList<>());
            emptyResult.put("total", 0);
            emptyResult.put("page", page);
            emptyResult.put("size", size);
            return R.ok(emptyResult);
        }
        List<ShowWorkSubmissionVo> allList = workService.getSubmissionList(userId, orgId, role);
        int total = allList.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<ShowWorkSubmissionVo> subList = from < total ? allList.subList(from, to) : new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("list", subList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }
}
