package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.WorkSubmission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.militia_admin.model.vo.ShowWorkSubmissionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作填写表 Mapper
 */
@Mapper
public interface WorkSubmissionMapper extends BaseMapper<WorkSubmission> {

    /**
     * 军长/师机关：查看所有下属组织的填写记录
     */
    List<ShowWorkSubmissionVo> selectSubmissionListByOrgId(@Param("orgId") Long orgId);

    /**
     * 团机关：查看下属组织已提交（fill_status=1）的填写记录
     */
    List<ShowWorkSubmissionVo> selectSubmissionListForAudit(@Param("orgId") Long orgId);

    /**
     * 营/连/分队干部：查看分配给自己的填写记录
     */
    List<ShowWorkSubmissionVo> selectSubmissionListForFill(@Param("orgId") Long orgId);

    /**
     * 根据 work_id 和 org_id 查询单条记录
     */
    WorkSubmission selectByWorkIdAndOrgId(@Param("workId") Long workId, @Param("orgId") Long orgId);
}
