create database if not exists PackageSystem;
use PackageSystem;
drop table if exists employee;
create table if not exists employee(
        id        int primary key auto_increment,
        pack      VARCHAR(20),
        quantity  int,
        name      varchar(8),
        userName  varchar(8),
        task      varchar(20)
)auto_increment=20260001;
insert into employee(
pack     ,
quantity ,
name     ,
userName ,
task     )
values ('快递',20,'记者','裘克','抵挡奥菲'),
       ('快递','12','超模幸运儿','厂长','放木炭');

delete from employee where name='幸运儿';
delete from employee where name='裘克';
select*from employee;