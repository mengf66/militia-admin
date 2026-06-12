create table if not exists militia—admin.announcement
(
    id             bigint auto_increment comment '主键'
    primary key,
    title          varchar(255)                       not null comment '标题',
    content        text                               not null comment '内容',
    type           tinyint  default 0                 not null comment '类型：0=通知公告，1=教育学习',
    publish_org_id bigint                             not null comment '发布组织ID',
    target_org_ids text                               not null comment '目标组织ID，逗号分隔',
    status         tinyint  default 0                 not null comment '状态：0=草稿，1=已发布，2=已撤回',
    create_user_id bigint                             not null comment '创建人',
    update_user_id bigint                             not null comment '更新人',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete      tinyint  default 0                 not null comment '是否删除'
    )
    comment '公告表';

create index idx_publish_org
    on militia—admin.announcement (publish_org_id);

create index idx_status
    on militia—admin.announcement (status);

create table if not exists militia—admin.`leave`
(
    id              bigint auto_increment comment '主键，请假编号'
    primary key,
    user_id         bigint                             not null comment '请假人ID',
    user_name       varchar(64)                        null comment '请假人姓名（冗余快照）',
    org_id          bigint                             not null comment '所属组织ID',
    leave_type      int      default 0                 not null comment '请假类型：0-事假，1-病假，2-年假，3-其他',
    start_time      datetime                           not null comment '开始时间',
    end_time        datetime                           not null comment '结束时间',
    reason          varchar(500)                       null comment '请假事由',
    status          int      default 2                 not null comment '状态：0-已通过，1-已驳回，2-待审批',
    audit_remark    varchar(500)                       null comment '审批意见',
    audit_user_id   bigint                             null comment '审批人ID',
    audit_user_name varchar(64)                        null comment '审批人姓名',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       int      default 0                 not null comment '是否删除：0-正常，1-删除'
    )
    comment '请假表';

create index idx_org_id
    on militia—admin.`leave` (org_id);

create index idx_status
    on militia—admin.`leave` (status);

create index idx_user_id
    on militia—admin.`leave` (user_id);

create table if not exists militia—admin.notification
(
    id               bigint auto_increment comment '主键'
    primary key,
    user_id          bigint                             not null comment '接收用户ID',
    org_id           bigint                             not null comment '接收时所属组织ID（冗余快照）',
    publisher_org_id bigint                             null comment '发布组织ID',
    publisher_id     bigint                             null comment '发布人ID',
    title            varchar(255)                       not null comment '通知标题',
    content          text                               null comment '通知内容',
    type             tinyint  default 0                 not null comment '通知类型：0=公告通知，1=学习通知，2=提醒通知',
    source_type      tinyint  default 0                 not null comment '来源类型：0=公告，1=学习',
    source_id        bigint                             not null comment '关联的公告ID或学习ID',
    is_read          tinyint  default 0                 not null comment '是否已读：0=未读，1=已读',
    read_time        datetime                           null comment '阅读时间',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间'
    )
    comment '通知消息表';

create index idx_create_time
    on militia—admin.notification (create_time desc);

create index idx_publisher_org_id
    on militia—admin.notification (publisher_org_id);

create index idx_user_read
    on militia—admin.notification (user_id, is_read);

create table if not exists militia—admin.organization
(
    id          bigint auto_increment comment '主键，组织编号'
    primary key,
    name        varchar(20)                          not null comment '组织名字',
    org_level   tinyint(1) default 0                 null comment '组织层级（0-民兵，1-营部、连、分队，2-团机关，3-师机关，4-军机关）',
    org_type    tinyint(1) default 0                 null comment '组织类型（0-民兵，1-营部，2-连，3-分队，4-团机关，5-师机关，6-军机关）',
    parent_id   bigint                               null comment '所属父节点编号',
    leader_id   bigint                               null comment '负责人编号',
    is_delete   tinyint(1) default 0                 null comment '是否删除（0-正常，1-删除）',
    create_time datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    path        varchar(500)                         null comment '层级路径，如 1.2.5'
    )
    comment '组织表';

create index idx_leader_id
    on militia—admin.organization (leader_id);

create index idx_org_level
    on militia—admin.organization (org_level);

create index idx_org_type
    on militia—admin.organization (org_type);

create index idx_parent_id
    on militia—admin.organization (parent_id);

create table if not exists militia—admin.permission
(
    id          bigint auto_increment comment '主键，权限编号'
    primary key,
    name        varchar(50)                          not null comment '权限名',
    permission  varchar(100)                         not null comment '权限标识（如 militia:info:view）',
    menu_type   tinyint(1) default 0                 null comment '权限类型（0-目录，1-菜单，2-按钮/接口）',
    is_delete   tinyint(1) default 0                 null comment '是否删除（0-正常，1-删除）',
    create_time datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_permission
    unique (permission)
    )
    comment '权限表';

create index idx_menu_type
    on militia—admin.permission (menu_type);

create table if not exists militia—admin.role
(
    id          bigint auto_increment comment '主键，角色编号'
    primary key,
    name        varchar(10)                          not null comment '角色名',
    menu_ids    json                                 null comment '权限列表（关联权限表ID数组）',
    is_delete   tinyint(1) default 0                 null comment '是否删除（0-正常，1-删除）',
    create_time datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    role        varchar(100)                         null comment '角色标识',
    constraint uk_name
    unique (name)
    )
    comment '角色表';

create table if not exists militia—admin.user
(
    id          bigint auto_increment comment '主键，人员编号'
    primary key,
    name        varchar(15)                          not null comment '人员姓名',
    phone       varchar(12)                          null comment '人员手机号',
    gender      tinyint(1) default 0                 null comment '人员性别（0-女生，1-男生）',
    password    varchar(64)                          not null comment '账号密码，MD5加密存储',
    org_id      bigint                               null comment '所属组织id',
    role_id     bigint                               null comment '角色id',
    status      tinyint(1) default 1                 null comment '账号状态（0-冻结，1-正常）',
    is_delete   tinyint(1) default 0                 null comment '是否删除（0-正常，1-删除）',
    create_time datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '人员表';

create index idx_org_id
    on militia—admin.user (org_id);

create index idx_phone
    on militia—admin.user (phone);

create index idx_role_id
    on militia—admin.user (role_id);

create table if not exists militia—admin.user_apply
(
    id             bigint auto_increment comment '主键，审核工单编号'
    primary key,
    name           varchar(15)                          not null comment '人员姓名',
    phone          varchar(12)                          null comment '人员手机号',
    gender         tinyint(1) default 0                 null comment '人员性别（0-女生，1-男生）',
    org_id         bigint                               null comment '所属组织id',
    role_id        bigint                               null comment '角色id',
    status         tinyint(1) default 2                 null comment '审核工单状态（0-通过，1-驳回，2-未审批）',
    audit_remark   varchar(255)                         null comment '审批意见/备注',
    create_user_id bigint                               null comment '创建人id',
    update_user_id bigint                               null comment '更新人id',
    is_delete      tinyint(1) default 0                 null comment '是否删除（0-正常，1-删除）',
    create_time    datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '审核工单表';

create index idx_create_user_id
    on militia—admin.user_apply (create_user_id);

create index idx_org_id
    on militia—admin.user_apply (org_id);

create index idx_phone
    on militia—admin.user_apply (phone);

create index idx_role_id
    on militia—admin.user_apply (role_id);

create index idx_status
    on militia—admin.user_apply (status);

create table if not exists militia—admin.work
(
    id             bigint auto_increment comment '主键，工作编号'
    primary key,
    title          varchar(200)                       not null comment '工作标题',
    type           int      default 0                 not null comment '工作类型：0-月工作计划，1-工作总结，2-专项活动报告',
    org_id         bigint                             not null comment '发布组织ID',
    target_org_ids varchar(500)                       null comment '目标组织ID，逗号分隔',
    status         int      default 0                 not null comment '状态：0-草稿，1-已发布',
    create_user_id bigint                             not null comment '创建人ID',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete      int      default 0                 not null comment '是否删除：0-正常，1-删除'
    )
    comment '工作内容表（军长发布的工作任务）';

create index idx_org_id
    on militia—admin.work (org_id);

create index idx_status
    on militia—admin.work (status);

create index idx_type
    on militia—admin.work (type);

create table if not exists militia—admin.work_submission
(
    id            bigint auto_increment comment '主键'
    primary key,
    work_id       bigint                             not null comment '关联工作内容ID',
    org_id        bigint                             not null comment '填写组织ID',
    content       text                               null comment '填写的工作内容',
    fill_status   int      default 0                 not null comment '填写状态：0-未填写，1-已填写',
    audit_status  int      default 0                 not null comment '审批状态：0-待审批，1-已通过，2-已驳回',
    audit_remark  varchar(500)                       null comment '审批意见',
    audit_user_id bigint                             null comment '审批人ID',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     int      default 0                 not null comment '是否删除：0-正常，1-删除',
    constraint uk_work_org
    unique (work_id, org_id)
    )
    comment '工作填写表（每个目标组织一条记录）';

create index idx_audit_status
    on militia—admin.work_submission (audit_status);

create index idx_fill_status
    on militia—admin.work_submission (fill_status);

create index idx_org_id
    on militia—admin.work_submission (org_id);

create index idx_work_id
    on militia—admin.work_submission (work_id);
