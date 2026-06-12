package com.feng.militia_admin.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 请假记录展示VO
 */
@Data
public class ShowLeaveInfoVo {

    private Long id;

    /**
     * 请假人ID
     */
    private Long userId;

    /**
     * 请假人姓名
     */
    private String userName;

    /**
     * 所属组织ID
     */
    private Long orgId;

    /**
     * 所属组织名称
     */
    private String orgName;

    /**
     * 请假类型：0-事假，1-病假，2-年假，3-其他
     */
    private Integer leaveType;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 请假事由
     */
    private String reason;

    /**
     * 状态：0-已通过，1-已驳回，2-待审批
     */
    private Integer status;

    /**
     * 审批意见
     */
    private String auditRemark;

    /**
     * 审批人姓名
     */
    private String auditUserName;

    /**
     * 销假状态：0-未销假，1-已销假
     */
    private Integer cancelStatus;

    /**
     * 销假时间
     */
    private Date cancelTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
