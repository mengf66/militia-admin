package com.feng.militia_admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.militia_admin.controller.WebSocketController;
import com.feng.militia_admin.mapper.PermissionMapper;
import com.feng.militia_admin.mapper.RoleMapper;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.model.domain.Permission;
import com.feng.militia_admin.model.domain.Role;
import com.feng.militia_admin.model.domain.User;
import com.feng.militia_admin.model.domain.UserApply;
import com.feng.militia_admin.model.request.AddUserApplyRequest;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.model.vo.NotificationMessage;
import com.feng.militia_admin.service.NotificationService;
import com.feng.militia_admin.service.UserApplyService;
import com.feng.militia_admin.mapper.UserApplyMapper;
import com.feng.militia_admin.utils.MD5Util;
import com.feng.militia_admin.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【user_apply(审核工单表)】的数据库操作Service实现
* @createDate 2026-06-05 11:27:37
*/
@Slf4j
@Service
public class UserApplyServiceImpl extends ServiceImpl<UserApplyMapper, UserApply>
    implements UserApplyService{

    @Value("${login.secret-salt}")
    private String salt;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WebSocketController webSocketController;

    @Override
    public boolean addUserApply(AddUserApplyRequest addUserApplyRequest) {
        // 字段非空校验
        if (addUserApplyRequest == null) {
            log.error("添加人员请求为空");
            return false;
        }
        if (addUserApplyRequest.getName() == null || addUserApplyRequest.getName().trim().isEmpty()) {
            log.error("添加人员失败：姓名为空");
            return false;
        }
        if (addUserApplyRequest.getPhone() == null || addUserApplyRequest.getPhone().trim().isEmpty()) {
            log.error("添加人员失败：手机号为空");
            return false;
        }
        if (addUserApplyRequest.getGender() == null) {
            log.error("添加人员失败：性别为空");
            return false;
        }
        if (addUserApplyRequest.getOrgId() == null) {
            log.error("添加人员失败：所属组织为空");
            return false;
        }
        if (addUserApplyRequest.getRoleId() == null) {
            log.error("添加人员失败：角色为空");
            return false;
        }

        UserApply userApply = BeanUtil.copyProperties(addUserApplyRequest, UserApply.class);
        userApply.setStatus(2);
        userApply.setCreateUserId(SecurityUtil.getCurrentUserId());
        userApply.setUpdateUserId(SecurityUtil.getCurrentUserId());
        userApply.setCreateTime(new Date());
        userApply.setUpdateTime(new Date());
        boolean saved = this.save(userApply);

        // 保存成功后，给审批员发送通知
        if (saved) {
            notifyAuditors(userApply);
        }

        return saved;
    }

    /**
     * 给有审批权限的用户发送新工单通知
     */
    private void notifyAuditors(UserApply userApply) {
        try {
            // 1. 查询 audit:person 权限的 ID
            LambdaQueryWrapper<Permission> permWrapper = new LambdaQueryWrapper<>();
            permWrapper.eq(Permission::getPermission, "audit:person")
                    .eq(Permission::getIsDelete, 0);
            Permission auditPermission = permissionMapper.selectOne(permWrapper);
            if (auditPermission == null) {
                log.warn("未找到 audit:person 权限配置");
                return;
            }
            Integer auditPermId = auditPermission.getId().intValue();
            log.info("找到 audit:person 权限ID: {}", auditPermId);

            // 2. 查询所有角色，筛选出包含 audit:person 权限的角色
            List<Role> allRoles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                    .eq(Role::getIsDelete, 0));
            List<Long> auditRoleIds = allRoles.stream()
                    .filter(role -> role.getMenuIds() != null && role.getMenuIds().contains(auditPermId))
                    .map(Role::getId)
                    .collect(Collectors.toList());
            if (auditRoleIds.isEmpty()) {
                log.warn("没有角色包含 audit:person 权限");
                return;
            }
            log.info("包含审批权限的角色ID: {}", auditRoleIds);

            // 3. 查询这些角色下、在当前组织及其上级组织的用户
            List<User> auditors = userMapper.selectAuditUsersByOrgPath(userApply.getOrgId(), auditRoleIds);
            if (auditors.isEmpty()) {
                log.info("当前组织及其上级组织没有找到审批员");
                return;
            }
            log.info("找到 {} 位审批员需要通知", auditors.size());

            // 4. 给每位审批员发送通知
            String applicantName = userApply.getName();
            for (User auditor : auditors) {
                // 跳过申请人自己（如果申请人也有审批权限）
                if (auditor.getId().equals(userApply.getCreateUserId())) {
                    continue;
                }

                // 构建通知请求
                PubNoticeRequest notice = new PubNoticeRequest();
                notice.setUserId(auditor.getId());
                notice.setOrgId(auditor.getOrgId());
                notice.setTitle("新的人员审批工单");
                notice.setContent("【" + applicantName + "】提交了人员审批申请，请及时处理。");
                notice.setType(2); // 提醒通知
                notice.setSourceType(0);
                notice.setSourceId(userApply.getId());

                // 入库并推送
                notificationService.publishAndNotify(notice);
            }
            log.info("已向 {} 位审批员发送新工单通知", auditors.size());
        } catch (Exception e) {
            log.error("发送审批通知失败", e);
        }
    }

    @Override
    public int updateUserApply(Long id, UserApply userApply) {
        userApply.setId(id);
        userApply.setUpdateUserId(SecurityUtil.getCurrentUserId());
        userApply.setUpdateTime(new Date());
        return baseMapper.dynamicUpdate(userApply);
    }

    @Override
    public List<UserApply> getApplyList(Long orgId) {
        // 查询当前组织及其下属组织的审批工单
        return baseMapper.selectApplyListByOrgId(orgId);
    }

    @Override
    public void insertUserFromApply(Long applyId) {
        log.info("[异步任务] 开始执行审批通过后插入用户表, applyId={}", applyId);
        try {
            UserApply apply = this.getById(applyId);
            if (apply == null) {
                log.warn("[异步任务] 审批工单不存在，ID={}", applyId);
                return;
            }
            log.info("[异步任务] 查询到审批工单: name={}, orgId={}, roleId={}", 
                    apply.getName(), apply.getOrgId(), apply.getRoleId());

            // 检查是否已存在同名用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getName, apply.getName());
            Integer count = userMapper.selectCount(wrapper);
            if (count > 0) {
                log.info("[异步任务] 用户已存在，跳过插入: {}", apply.getName());
                return;
            }

            User user = new User();
            user.setName(apply.getName());
            user.setPhone(apply.getPhone());
            user.setGender(apply.getGender());
            user.setOrgId(apply.getOrgId());
            user.setRoleId(apply.getRoleId());
            // 默认密码 123456
            String defaultPassword = MD5Util.md5(salt + "123456");
            user.setPassword(defaultPassword);
            user.setStatus(1);
            user.setIsDelete(0);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            
            int rows = userMapper.insert(user);
            if (rows > 0) {
                log.info("[异步任务] 审批通过，已自动插入用户表: {}, 用户ID={}", apply.getName(), user.getId());
            } else {
                log.error("[异步任务] 插入用户表返回0行, applyId={}, name={}", applyId, apply.getName());
            }
        } catch (Exception e) {
            log.error("[异步任务] 审批通过后插入用户表失败, applyId={}", applyId, e);
        }
    }
}
