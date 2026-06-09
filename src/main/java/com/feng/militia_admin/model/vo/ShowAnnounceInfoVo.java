package com.feng.militia_admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/8 11:47
 * @description 类功能描述
 */
@Data
public class ShowAnnounceInfoVo {

    private Long id;

    private String title;

    private String content;

    private Integer type;

    /**
     * 发布组织ID
     */
    private Long publishOrgId;

    /**
     * 目标组织ID，逗号分隔
     */
    private String targetOrgIds;

    /**
     * 状态：0=草稿，1=已发布，2=已撤回
     */
    private Integer status;


    /**
     * 创建时间
     */
    private Date createTime;
}
