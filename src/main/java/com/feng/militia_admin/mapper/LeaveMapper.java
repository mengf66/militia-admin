package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.Leave;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.militia_admin.model.vo.ShowLeaveInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Lenovo
 * @description 针对表【leave(请假表)】的数据库操作Mapper
 * @createDate 2026-06-10
 * @Entity com.feng.militia_admin.model.domain.Leave
 */
@Mapper
public interface LeaveMapper extends BaseMapper<Leave> {

    /**
     * 查询指定组织及其下属组织的请假记录列表
     */
    List<ShowLeaveInfoVo> selectLeaveListByOrgId(@Param("orgId") Long orgId);

    /**
     * 查询当前用户的请假记录
     */
    List<ShowLeaveInfoVo> selectMyLeaveList(@Param("userId") Long userId);
}
