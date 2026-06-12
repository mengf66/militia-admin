package com.feng.militia_admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.militia_admin.mapper.LeaveMapper;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.model.domain.Leave;
import com.feng.militia_admin.model.domain.User;
import com.feng.militia_admin.model.request.ApproveLeaveRequest;
import com.feng.militia_admin.model.request.CreateLeaveRequest;
import com.feng.militia_admin.model.vo.ShowLeaveInfoVo;
import com.feng.militia_admin.service.LeaveService;
import com.feng.militia_admin.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import com.feng.militia_admin.model.domain.Permission;
import com.feng.militia_admin.model.domain.Role;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.service.NotificationService;
import com.feng.militia_admin.mapper.PermissionMapper;
import com.feng.militia_admin.mapper.RoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @description 针对表【leave(请假表)】的数据库操作Service实现
 * @createDate 2026-06-10
 */
@Slf4j
@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, Leave>
    implements LeaveService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Long createLeave(CreateLeaveRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        User user = userMapper.selectById(currentUserId);

        Leave leave = new Leave();
        leave.setUserId(currentUserId);
        leave.setUserName(user != null ? user.getName() : "");
        leave.setOrgId(user != null ? user.getOrgId() : null);
        leave.setLeaveType(request.getLeaveType());
        leave.setStartTime(request.getStartTime());
        leave.setEndTime(request.getEndTime());
        leave.setReason(request.getReason());
        leave.setStatus(2); // 待审批
        leave.setIsDelete(0);
        leave.setCreateTime(new Date());
        leave.setUpdateTime(new Date());

        baseMapper.insert(leave);
        return leave.getId();
    }

    @Override
    public boolean approveLeave(ApproveLeaveRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        User auditUser = userMapper.selectById(currentUserId);

        Leave leave = new Leave();
        leave.setId(request.getId());
        leave.setStatus(request.getStatus());
        leave.setAuditRemark(request.getAuditRemark());
        leave.setAuditUserId(currentUserId);
        leave.setAuditUserName(auditUser != null ? auditUser.getName() : "");
        leave.setUpdateTime(new Date());

        return baseMapper.updateById(leave) > 0;
    }

    @Override
    public boolean cancelLeave(Long leaveId, Long userId) {
        Leave leave = baseMapper.selectById(leaveId);
        if (leave == null || Integer.valueOf(1).equals(leave.getIsDelete())) {
            log.warn("销假失败：请假记录不存在, leaveId={}", leaveId);
            return false;
        }
        // 只能销假自己的请假记录
        if (!leave.getUserId().equals(userId)) {
            log.warn("销假失败：不能销假他人的请假记录, leaveId={}, userId={}", leaveId, userId);
            return false;
        }
        // 只有已通过的请假才能销假
        if (!Integer.valueOf(0).equals(leave.getStatus())) {
            log.warn("销假失败：只有已通过的请假才能销假, leaveId={}, status={}", leaveId, leave.getStatus());
            return false;
        }
        // 已提交销假或已销假的不能重复销假
        if (leave.getCancelStatus() != null && leave.getCancelStatus() >= 1) {
            log.warn("销假失败：已提交销假或已销假, leaveId={}, cancelStatus={}", leaveId, leave.getCancelStatus());
            return false;
        }

        // 设置销假状态为"待确认"
        leave.setCancelStatus(1);
        leave.setCancelTime(new Date());
        leave.setUpdateTime(new Date());
        boolean updated = baseMapper.updateById(leave) > 0;

        // 发送通知给干部确认销假
        if (updated) {
            notifyCancelAuditors(leave);
        }
        return updated;
    }

    /**
     * 干部确认销假
     */
    @Override
    public boolean confirmCancelLeave(Long leaveId, Long auditorUserId) {
        Leave leave = baseMapper.selectById(leaveId);
        if (leave == null || Integer.valueOf(1).equals(leave.getIsDelete())) {
            log.warn("确认销假失败：请假记录不存在, leaveId={}", leaveId);
            return false;
        }
        // 只有待确认的销假才能确认
        if (!Integer.valueOf(1).equals(leave.getCancelStatus())) {
            log.warn("确认销假失败：不是待确认状态, leaveId={}, cancelStatus={}", leaveId, leave.getCancelStatus());
            return false;
        }

        User auditor = userMapper.selectById(auditorUserId);
        leave.setCancelStatus(2); // 已销假
        leave.setUpdateTime(new Date());
        return baseMapper.updateById(leave) > 0;
    }

    /**
     * 发送销假通知给有审批权限的干部
     */
    private void notifyCancelAuditors(Leave leave) {
        try {
            // 1. 查询 leave:audit 权限的 ID
            LambdaQueryWrapper<Permission> permWrapper = new LambdaQueryWrapper<>();
            permWrapper.eq(Permission::getPermission, "leave:audit")
                    .eq(Permission::getIsDelete, 0);
            Permission auditPermission = permissionMapper.selectOne(permWrapper);
            if (auditPermission == null) {
                log.warn("未找到 leave:audit 权限配置");
                return;
            }
            Integer auditPermId = auditPermission.getId().intValue();

            // 2. 查询所有角色，筛选出包含 leave:audit 权限的角色
            List<Role> allRoles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                    .eq(Role::getIsDelete, 0));
            List<Long> auditRoleIds = allRoles.stream()
                    .filter(role -> role.getMenuIds() != null && role.getMenuIds().contains(auditPermId))
                    .map(Role::getId)
                    .collect(Collectors.toList());
            if (auditRoleIds.isEmpty()) {
                log.warn("没有角色包含 leave:audit 权限");
                return;
            }

            // 3. 查询这些角色下、在当前组织及其上级组织的用户
            List<User> auditors = userMapper.selectAuditUsersByOrgPath(leave.getOrgId(), auditRoleIds);
            if (auditors.isEmpty()) {
                log.info("当前组织及其上级组织没有找到审批员");
                return;
            }

            // 4. 给每位审批员发送销假通知
            String applicantName = leave.getUserName();
            for (User auditor : auditors) {
                // 跳过申请人自己
                if (auditor.getId().equals(leave.getUserId())) {
                    continue;
                }

                PubNoticeRequest notice = new PubNoticeRequest();
                notice.setUserId(auditor.getId());
                notice.setOrgId(auditor.getOrgId());
                notice.setTitle("销假待确认");
                notice.setContent("【" + applicantName + "】已提交销假申请，销假时间：" + leave.getCancelTime() + "，请及时确认。");
                notice.setType(2); // 提醒通知
                notice.setSourceType(1); // 请假类型
                notice.setSourceId(leave.getId());

                notificationService.publishAndNotify(notice);
            }
            log.info("已向 {} 位审批员发送销假通知", auditors.size());
        } catch (Exception e) {
            log.error("发送销假通知失败", e);
        }
    }

    @Override
    public List<ShowLeaveInfoVo> getLeaveList(Long orgId) {
        return baseMapper.selectLeaveListByOrgId(orgId);
    }

    @Override
    public List<ShowLeaveInfoVo> getMyLeaveList(Long userId) {
        return baseMapper.selectMyLeaveList(userId);
    }
}
