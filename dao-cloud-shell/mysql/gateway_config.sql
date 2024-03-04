# you must first create a dao_cloud
CREATE DATABASE IF NOT EXISTS dao_cloud;
# 网关配置
CREATE TABLE IF NOT EXISTS `gateway_config`
(
    `id`                             bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',
    `gmt_create`                     datetime     NOT NULL COMMENT '创建时间',
    `gmt_modified`                   datetime     NOT NULL COMMENT '修改时间',
    `proxy`                          varchar(255) NOT NULL COMMENT 'server proxy mark',
    `provider`                       varchar(255) NOT NULL COMMENT 'service provider',
    `version`                        int(11)      NOT NULL COMMENT 'service version',
    `timeout`                        bigint(10) COMMENT '请求超时时间',
    `limit_algorithm`                int(1) COMMENT '限流算法: 1=计数, 2=令牌, 3=漏桶',
    `slide_date_window_size`         bigint(20) COMMENT '滑动时间窗口大小(ms)',
    `slide_window_max_request_count` int(11) COMMENT '时间窗口内允许的最大请求数',
    `token_bucket_max_size`          int(11) COMMENT '令牌桶的最大令牌数',
    `token_bucket_refill_rate`       int(11) COMMENT '每秒新增的令牌数',
    `leaky_bucket_capacity`          int(11) COMMENT '漏桶的容量',
    `leaky_bucket_refill_rate`       int(11) COMMENT '漏桶令牌填充的速度(每秒)',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='网关配置';