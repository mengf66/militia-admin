package com.feng.militia_admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.model.domain.Announcement;
import com.feng.militia_admin.model.request.CreateAnnouncementRequest;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.model.vo.ShowAnnounceInfoVo;
import com.feng.militia_admin.model.vo.ShowUserInfoVo;
import com.feng.militia_admin.service.AnnouncementService;
import com.feng.militia_admin.mapper.AnnouncementMapper;
import com.feng.militia_admin.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @author Lenovo
* @description 针对表【announcement(公告表)】的数据库操作Service实现
* @createDate 2026-06-08 10:01:58
*/
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
    implements AnnouncementService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public Long addAnnouncement(CreateAnnouncementRequest announcementRequest) {
        Announcement announcement = BeanUtil.copyProperties(announcementRequest, Announcement.class);
        Long currentUserId = SecurityUtil.getCurrentUserId();
        announcement.setCreateUserId(currentUserId);
        announcement.setUpdateUserId(currentUserId);
        announcement.setPublishOrgId(userMapper.getUserOrgId(currentUserId));
        announcement.setCreateTime(new Date());
        announcement.setUpdateTime(new Date());
        announcement.setIsDelete(0);
        announcementMapper.insert(announcement);
        return announcement.getId();
    }

    @Override
    public List<ShowAnnounceInfoVo> showList() {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        return announcementMapper.selectSuperiorAnnouncements(orgId);
    }

    @Override
    public List<PubNoticeRequest> getPubNotice(CreateAnnouncementRequest announcement, Long id) {
        List<PubNoticeRequest> list = new ArrayList<>();
        Long publishOrgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());

        // 解析目标组织ID列表
        List<Long> targetOrgIds = new ArrayList<>();
        if (announcement.getTargetOrgIds() != null && !announcement.getTargetOrgIds().trim().isEmpty()) {
            for (String s : announcement.getTargetOrgIds().split(",")) {
                try {
                    targetOrgIds.add(Long.valueOf(s.trim()));
                } catch (NumberFormatException e) {
                    // 忽略无效ID
                }
            }
        }

        // 如果没有指定目标组织，默认发给发布组织及其所有下属组织的用户
        if (targetOrgIds.isEmpty()) {
            targetOrgIds.add(publishOrgId);
        }

        // 收集所有目标用户（去重）
        Set<Long> userIdSet = new HashSet<>();
        for (Long orgId : targetOrgIds) {
            List<ShowUserInfoVo> users = userMapper.selectUsersByOrgId(orgId, "");
            for (ShowUserInfoVo user : users) {
                if (user.getUserId() != null) {
                    userIdSet.add(user.getUserId());
                }
            }
        }

        for (Long userId : userIdSet) {
            PubNoticeRequest pubNoticeRequest = new PubNoticeRequest();
            pubNoticeRequest.setContent(announcement.getContent());
            pubNoticeRequest.setTitle(announcement.getTitle());
            pubNoticeRequest.setType(announcement.getType());
            pubNoticeRequest.setSourceType(announcement.getType());
            pubNoticeRequest.setSourceId(id);
            pubNoticeRequest.setUserId(userId);
            pubNoticeRequest.setOrgId(publishOrgId);
            list.add(pubNoticeRequest);
        }

        return list;
    }
}




