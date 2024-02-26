# you must first create a dao_cloud
CREATE DATABASE IF NOT EXISTS dao_cloud;
# 网关配置
CREATE TABLE IF NOT EXISTS `gateway_config`
(
    `id`              bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',
    `gmt_create`      datetime     NOT NULL COMMENT '创建时间',
    `gmt_modified`    datetime     NOT NULL COMMENT '修改时间',
    `proxy`           varchar(255) NOT NULL COMMENT 'server proxy mark',
    `provider`        varchar(255) NOT NULL COMMENT 'service provider',
    `version`         int(11) NOT NULL COMMENT 'service version',
    `timeout`         bigint(10)   NOT NULL COMMENT '请求超时时间',
    `limit_algorithm` int(1)      NOT NULL COMMENT '限流算法: 1=计数, 2=令牌, 3=漏桶',
    `limit_number`    int(11)      NOT NULL COMMENT '限流数量',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='网关配置';