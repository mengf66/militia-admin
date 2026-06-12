package com.feng.militia_admin.service;

import com.feng.militia_admin.model.domain.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.militia_admin.model.request.CreateAnnouncementRequest;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.model.vo.ShowAnnounceInfoVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【announcement(公告表)】的数据库操作Service
* @createDate 2026-06-08 10:01:58
*/
@Service
public interface AnnouncementService extends IService<Announcement> {

    Long addAnnouncement(CreateAnnouncementRequest announce);

    List<ShowAnnounceInfoVo> showList();

    List<PubNoticeRequest> getPubNotice(CreateAnnouncementRequest announcement, Long id);
}
