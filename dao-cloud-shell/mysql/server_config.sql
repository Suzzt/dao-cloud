# you must first create a dao_cloud
CREATE TABLE `server_config`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',
    `gmt_create`   datetime     NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime     NOT NULL COMMENT '修改时间',
    `proxy`        varchar(255) NOT NULL COMMENT 'server proxy mark',
    `provider`     varchar(255) NOT NULL COMMENT 'service provider',
    `version`      int(11)      NOT NULL COMMENT 'service version',
    `ip`           varchar(20)  NOT NULL COMMENT 'ip',
    `port`         int(10)    DEFAULT NULL COMMENT 'port',
    `status`       tinyint(4) DEFAULT NULL COMMENT 'server status',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='服务配置'