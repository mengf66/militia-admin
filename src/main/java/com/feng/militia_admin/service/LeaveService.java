package com.feng.militia_admin.service;

import com.feng.militia_admin.model.domain.Leave;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.militia_admin.model.request.ApproveLeaveRequest;
import com.feng.militia_admin.model.request.CreateLeaveRequest;
import com.feng.militia_admin.model.vo.ShowLeaveInfoVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lenovo
 * @description 针对表【leave(请假表)】的数据库操作Service
 * @createDate 2026-06-10
 */
@Service
public interface LeaveService extends IService<Leave> {

    /**
     * 提交请假申请
     */
    Long createLeave(CreateLeaveRequest request);

    /**
     * 审批请假
     */
    boolean approveLeave(ApproveLeaveRequest request);

    /**
     * 销假（请假人回来后提交销假申请，状态变为待确认）
     */
    boolean cancelLeave(Long leaveId, Long userId);

    /**
     * 干部确认销假（状态变为已销假）
     */
    boolean confirmCancelLeave(Long leaveId, Long auditorUserId);

    /**
     * 查询当前组织及其下属组织的请假记录（用于审批人查看）
     */
    List<ShowLeaveInfoVo> getLeaveList(Long orgId);

    /**
     * 查询当前用户的请假记录
     */
    List<ShowLeaveInfoVo> getMyLeaveList(Long userId);
}
