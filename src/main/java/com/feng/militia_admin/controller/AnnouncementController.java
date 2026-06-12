package com.feng.militia_admin.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.model.request.CreateAnnouncementRequest;
import com.feng.militia_admin.model.request.PubNoticeRequest;
import com.feng.militia_admin.model.vo.ShowAnnounceInfoVo;
import com.feng.militia_admin.service.AnnouncementService;
import com.feng.militia_admin.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告管理控制器
 */
@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
@Slf4j
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserMapper userMapper;


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('announcement:create')")
    public R<String> create(@RequestBody CreateAnnouncementRequest announcement) {
        if(announcement == null) {
            return R.failed("参数错误");
        }

        Long id = announcementService.addAnnouncement(announcement);

        if(id == 0) {
            return R.failed("发布失败");
        }

        List<PubNoticeRequest> list = announcementService.getPubNotice(announcement, id);
        boolean b = notificationService.publishBatch(list);
        return R.ok("发布成功");
    }

    @GetMapping("show")
    @PreAuthorize("hasAuthority('announcement:list')")
    public R<Map<String, Object>> showList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ShowAnnounceInfoVo> allList = announcementService.showList();
        int total = allList.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<ShowAnnounceInfoVo> subList = from < total ? allList.subList(from, to) : new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("list", subList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

}