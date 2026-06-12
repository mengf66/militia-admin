package com.feng.militia_admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.militia_admin.mapper.OrganizationMapper;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.mapper.WorkMapper;
import com.feng.militia_admin.mapper.WorkSubmissionMapper;
import com.feng.militia_admin.model.domain.Work;
import com.feng.militia_admin.model.domain.WorkSubmission;
import com.feng.militia_admin.model.request.AuditWorkRequest;
import com.feng.militia_admin.model.request.CreateWorkRequest;
import com.feng.militia_admin.model.request.FillWorkRequest;
import com.feng.militia_admin.model.vo.ShowWorkInfoVo;
import com.feng.militia_admin.model.vo.ShowWorkSubmissionVo;
import com.feng.militia_admin.service.WorkService;
import com.feng.militia_admin.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工作管理 Service 实现（双表结构）
 * work：工作内容表（军长发布的工作任务）
 * work_submission：工作填写表（每个目标组织一条记录）
 */
@Slf4j
@Service
public class WorkServiceImpl extends ServiceImpl<WorkMapper, Work>
    implements WorkService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkSubmissionMapper submissionMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    public Long createWork(CreateWorkRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long orgId = userMapper.getUserOrgId(currentUserId);

        Work work = new Work();
        work.setTitle(request.getTitle());
        work.setType(request.getType());
        work.setOrgId(orgId);
        work.setTargetOrgIds(request.getTargetOrgIds());
        work.setStatus(0); // 草稿
        work.setCreateUserId(currentUserId);
        work.setIsDelete(0);
        work.setCreateTime(new Date());
        work.setUpdateTime(new Date());

        baseMapper.insert(work);
        return work.getId();
    }

    @Override
    @Transactional
    public boolean publishWork(Long id) {
        Work work = baseMapper.selectById(id);
        if (work == null || Integer.valueOf(1).equals(work.getIsDelete())) {
            return false;
        }
        if (!Integer.valueOf(0).equals(work.getStatus())) {
            log.warn("工作状态不是草稿，无法发布: id={}, status={}", id, work.getStatus());
            return false;
        }

        // 更新 work 状态为已发布
        work.setStatus(1);
        work.setUpdateTime(new Date());
        baseMapper.updateById(work);

        // 根据 target_org_ids 自动创建 submission 记录
        String targetOrgIds = work.getTargetOrgIds();
        if (StringUtils.hasText(targetOrgIds)) {
            String[] orgIds = targetOrgIds.split(",");
            for (String orgIdStr : orgIds) {
                try {
                    Long targetOrgId = Long.valueOf(orgIdStr.trim());
                    // 检查是否已存在
                    WorkSubmission exist = submissionMapper.selectByWorkIdAndOrgId(id, targetOrgId);
                    if (exist == null) {
                        WorkSubmission submission = new WorkSubmission();
                        submission.setWorkId(id);
                        submission.setOrgId(targetOrgId);
                        submission.setFillStatus(0); // 未填写
                        submission.setAuditStatus(0); // 待审批
                        submission.setIsDelete(0);
                        submission.setCreateTime(new Date());
                        submission.setUpdateTime(new Date());
                        submissionMapper.insert(submission);
                    }
                } catch (NumberFormatException e) {
                    log.warn("目标组织ID格式错误: {}", orgIdStr);
                }
            }
        }

        return true;
    }

    @Override
    public boolean fillWork(FillWorkRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long orgId = userMapper.getUserOrgId(currentUserId);

        WorkSubmission submission = submissionMapper.selectByWorkIdAndOrgId(request.getId(), orgId);
        if (submission == null || Integer.valueOf(1).equals(submission.getIsDelete())) {
            log.warn("未找到该组织的填写记录: workId={}, orgId={}", request.getId(), orgId);
            return false;
        }
        if (!Integer.valueOf(0).equals(submission.getFillStatus())) {
            log.warn("该工作已填写，不能重复填写: submissionId={}", submission.getId());
            return false;
        }

        submission.setContent(request.getContent());
        submission.setFillStatus(1); // 已填写
        submission.setUpdateTime(new Date());
        return submissionMapper.updateById(submission) > 0;
    }

    @Override
    public boolean auditWork(AuditWorkRequest request) {
        WorkSubmission submission = submissionMapper.selectById(request.getId());
        if (submission == null || Integer.valueOf(1).equals(submission.getIsDelete())) {
            return false;
        }
        // 只有已填写的才能审批
        if (!Integer.valueOf(1).equals(submission.getFillStatus())) {
            log.warn("工作未填写，无法审批: submissionId={}", request.getId());
            return false;
        }
        if (!Integer.valueOf(1).equals(request.getStatus()) && !Integer.valueOf(2).equals(request.getStatus())) {
            return false;
        }

        submission.setAuditStatus(request.getStatus());
        submission.setAuditRemark(request.getRemark());
        submission.setAuditUserId(SecurityUtil.getCurrentUserId());
        submission.setUpdateTime(new Date());
        return submissionMapper.updateById(submission) > 0;
    }

    @Override
    public List<ShowWorkInfoVo> getWorkList(Long orgId) {
        if (orgId == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectWorkListByOrgId(orgId);
    }

    @Override
    public List<ShowWorkSubmissionVo> getSubmissionList(Long userId, Long orgId, String role) {
        if (orgId == null) {
            log.warn("getSubmissionList orgId is null, userId={}", userId);
            return new ArrayList<>();
        }
        // 军长和师机关：查看所有下属组织的填写记录
        if ("army".equals(role) || "division".equals(role)) {
            return submissionMapper.selectSubmissionListByOrgId(orgId);
        }
        // 团机关：查看下属组织已提交的填写记录
        if ("regiment".equals(role)) {
            return submissionMapper.selectSubmissionListForAudit(orgId);
        }
        // 营连分队干部：查看分配给自己的填写记录
        if ("battalion".equals(role)) {
            return submissionMapper.selectSubmissionListForFill(orgId);
        }
        log.warn("未知角色无法查询填写记录: role={}", role);
        return new ArrayList<>();
    }
}
