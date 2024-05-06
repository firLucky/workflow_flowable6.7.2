

```sql

-- 用户表主键id生成触发器
DELIMITER $$
CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    TRIGGER `fir_flowable`.`id_user` -- 触发器名称
    BEFORE INSERT             -- 触发器被触发的时机
    ON `fir_flowable`.`user`       -- 触发器所作用的表名称
    FOR EACH ROW BEGIN
		SET new.user_id=UPPER(REPLACE(UUID(), '-', '')); -- 触发器执行的逻辑
    END$$

DELIMITER ;SET new.userid=UPPER(REPLACE(UUID(), '-', '')); -- 触发器执行的逻辑
END;
```
用户信息表
```sql
CREATE TABLE `user` (
  `user_id` varchar(255) NOT NULL COMMENT '用户id',
  `username` varchar(255) DEFAULT NULL COMMENT '用户账号',
  `password` varchar(255) DEFAULT NULL COMMENT '用户密码',
  `name` varchar(255) DEFAULT NULL COMMENT '用户名称',
  `org_name` varchar(255) DEFAULT NULL COMMENT '所属公司',
  `dept_name` varchar(255) DEFAULT NULL COMMENT '所属部门',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息';
```