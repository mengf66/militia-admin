package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.Announcement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.militia_admin.model.vo.ShowAnnounceInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【announcement(公告表)】的数据库操作Mapper
* @createDate 2026-06-08 10:01:58
* @Entity com.feng.militia_admin.model.domain.Announcement
*/
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    List<ShowAnnounceInfoVo> selectSuperiorAnnouncements(@Param("orgId") Long orgId);
}




