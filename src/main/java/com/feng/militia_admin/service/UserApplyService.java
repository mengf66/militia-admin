package com.feng.militia_admin.service;

import com.feng.militia_admin.model.domain.UserApply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.militia_admin.model.request.AddUserApplyRequest;
import com.feng.militia_admin.model.vo.ShowUserInfoVo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【user_apply(审核工单表)】的数据库操作Service
* @createDate 2026-06-05 11:27:37
*/
@Service
public interface UserApplyService extends IService<UserApply> {

    boolean addUserApply(AddUserApplyRequest addUserApplyRequest);

    int updateUserApply(Long id, UserApply userApply);

    /**
     * 查询指定组织及其下属组织的审批工单列表
     */
    List<UserApply> getApplyList(Long orgId);

    /**
     * 审批通过后将工单信息插入用户表（异步）
     */
    @Async("taskExecutor")
    void insertUserFromApply(Long applyId);
}
