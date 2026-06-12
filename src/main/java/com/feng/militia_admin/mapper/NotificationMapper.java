package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Lenovo
* @description 针对表【notification(通知消息表)】的数据库操作Mapper
* @createDate 2026-06-09 17:25:19
* @Entity com.feng.militia_admin.model.domain.Notification
*/
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

}




