package com.feng.militia_admin.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 创建请假申请请求
 */
@Data
public class CreateLeaveRequest {

    /**
     * 请假类型：0-事假，1-病假，2-年假，3-其他
     */
    private Integer leaveType;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 请假事由
     */
    private String reason;
}
