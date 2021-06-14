-- MySQL 8

-- 创建数据库
drop database if exists `saktools_demo`;
drop user IF EXISTS xpu_sakdemo@'localhost';
drop user IF EXISTS xpu_sakdemo@'%';

create user 'xpu_sakdemo'@'localhost' identified by 'xpuSakdemo';
create user 'xpu_sakdemo'@'%' identified by 'xpuSakdemo';
create database `saktools_demo` default charset utf8 collate utf8_general_ci;
grant all on `saktools_demo`.* TO 'xpu_sakdemo'@'localhost' with grant option;
grant all on `saktools_demo`.* TO 'xpu_sakdemo'@'%' with grant option;
flush privileges;
