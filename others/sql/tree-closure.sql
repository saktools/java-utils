-- 创建表结构

use saktools_demo;

drop table if exists `tree_closure`;
drop table if exists `tree`;

create table `tree` (
    `id` bigint unsigned primary key not null auto_increment,
    `name` varchar(10) not null comment "项目名",
    `parent_id` bigint unsigned default null comment "父项目 ID",
    `sort_order` int unsigned not null default 1 comment "排序号, 默认为1",
    -- 基础字段
    `is_delete` bit(1) not null default 0 comment "是否已删除, 默认未删除",
    `create_time` timestamp(3) not null default CURRENT_TIMESTAMP(3),
    `create_user_id` bigint unsigned,
    `update_time` timestamp(3) not null default CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `update_user_id` bigint unsigned,
    `version` bigint unsigned not null default 0
) engine=innodb auto_increment=1 default charset=utf8 collate=utf8_general_ci comment="核心 - 项目表 - 无限级联";

create table `tree_closure` (
    `ancestor_id` bigint unsigned not null comment "祖先ID",
    `descendant_id` bigint unsigned not null comment "后代ID",
    `distance` int unsigned not null comment "层级距离, 从0开始, 0 代表该节点与自己本身的层级距离",
    primary key (`ancestor_id`, `descendant_id`)
) engine=innodb auto_increment=1 default charset=utf8 collate=utf8_general_ci comment="tree 节点的级联信息";


