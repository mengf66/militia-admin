package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.UserApply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【user_apply(审核工单表)】的数据库操作Mapper
* @createDate 2026-06-05 11:27:37
* @Entity com.feng.militia_admin.model.domain.UserApply
*/
@Mapper
public interface UserApplyMapper extends BaseMapper<UserApply> {

    int dynamicUpdate(UserApply userApply);

    /**
     * 查询指定组织及其下属组织的审批工单列表
     */
    List<UserApply> selectApplyListByOrgId(@Param("orgId") Long orgId);
}




