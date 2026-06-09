package com.feng.militia_admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/8 10:22
 * @description 类功能描述
 */
@Data
public class ShowUserInfoVo {

    /** 用户ID */
    private Long userId;

    /** 姓名（用户名） */
    private String name;

    /** 账号（手机号） */
    private String phone;

    /** 性别：0-女 1-男 2-保密 */
    private Integer gender;

    /** 组织ID */
    private Long orgId;

    /** 组织名称 */
    private String orgName;

    /** 组织层级：1-军事部 2-师 3-团 4-营 5-连 6-分队 */
    private Integer orgLevel;

    /** 角色 */
    private String role;

    /** 账号状态：0-冻结 1-正常 */
    private Integer status;
}
