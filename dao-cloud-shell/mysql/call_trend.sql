# you must first create a dao_cloud
CREATE DATABASE IF NOT EXISTS dao_cloud;
# config
CREATE TABLE IF NOT EXISTS dao_cloud.call_trend
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   datetime     NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime     NOT NULL COMMENT '修改时间',
    `proxy`        varchar(255) NOT NULL COMMENT 'server proxy mark',
    `provider`     varchar(255) NOT NULL COMMENT 'provider',
    `version`      int(11)      NOT NULL COMMENT '版本',
    `method_name`  varchar(512) NOT NULL COMMENT '函数方法名',
    `count`        bigint(20)   NOT NULL COMMENT '计数值',
    PRIMARY KEY (`id`),
    UNIQUE KEY `config_uk_p_p_v_m` (`proxy`, `provider`, `version`, `method_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='接口调用趋势表';