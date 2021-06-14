-- 新增一些初始数据

insert into `tree` (`name`, `parent_id`, `sort_order`, `is_delete`) values ('权限管理', null, 1, false);
insert into `tree_closure` (`ancestor_id`, `descendant_id`, `distance`) values (1, 1, 0);

insert into `tree` (`name`, `parent_id`, `sort_order`, `is_delete`) values
    ('用户管理', 1, 1, false),
    ('角色管理', 1, 2, false),
    ('资源管理', 1, 3, false);
insert into `tree_closure` (`ancestor_id`, `descendant_id`, `distance`) values
    (2, 2, 0), (3, 3, 0), (4, 4, 0),
    (1, 2, 1), (1, 3, 1), (1, 4, 1);






