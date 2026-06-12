package com.feng.militia_admin.service;

import com.feng.militia_admin.model.domain.Work;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.militia_admin.model.request.AuditWorkRequest;
import com.feng.militia_admin.model.request.CreateWorkRequest;
import com.feng.militia_admin.model.request.FillWorkRequest;
import com.feng.militia_admin.model.vo.ShowWorkInfoVo;
import com.feng.militia_admin.model.vo.ShowWorkSubmissionVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作内容表 Service
 * 双表结构：work（工作内容）+ work_submission（填写记录）
 */
@Service
public interface WorkService extends IService<Work> {

    /**
     * 军长新建工作（草稿状态）
     */
    Long createWork(CreateWorkRequest request);

    /**
     * 军长发布工作（草稿→已发布，同时创建 submission 记录）
     */
    boolean publishWork(Long id);

    /**
     * 营/连/分队干部填写工作
     */
    boolean fillWork(FillWorkRequest request);

    /**
     * 团部审批工作
     */
    boolean auditWork(AuditWorkRequest request);

    /**
     * 军长查看自己发布的工作列表
     */
    List<ShowWorkInfoVo> getWorkList(Long orgId);

    /**
     * 查询工作填写记录（根据角色返回不同数据）
     * @param userId 当前用户ID
     * @param orgId 当前用户组织ID
     * @param role 当前用户角色
     */
    List<ShowWorkSubmissionVo> getSubmissionList(Long userId, Long orgId, String role);
}
