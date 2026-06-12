package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.Work;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.militia_admin.model.vo.ShowWorkInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作内容表 Mapper
 */
@Mapper
public interface WorkMapper extends BaseMapper<Work> {

    /**
     * 军长查看自己发布的工作列表
     */
    List<ShowWorkInfoVo> selectWorkListByOrgId(@Param("orgId") Long orgId);
}
