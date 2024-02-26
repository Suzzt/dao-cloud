# you must first create a dao_cloud
CREATE DATABASE IF NOT EXISTS dao_cloud;
# config
CREATE TABLE IF NOT EXISTS dao_cloud.config
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   datetime     NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime     NOT NULL COMMENT '修改时间',
    `proxy`        varchar(255) NOT NULL COMMENT 'server proxy mark',
    `key`          varchar(255) NOT NULL COMMENT 'key',
    `version`      int(11)      NOT NULL COMMENT 'config版本',
    `value`        longtext     NOT NULL COMMENT '配置值',
    PRIMARY KEY (`id`),
    UNIQUE KEY `config_uk_p_k_v` (`proxy`, `key`, `version`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='配置中心存储内容表';
# example init data
INSERT INTO dao_cloud.config (gmt_create, gmt_modified, proxy, `key`, version, value)
VALUES (now(), now(), 'dao-cloud', 'dao-cloud', 0, 'Welcome to dao-cloud!');
