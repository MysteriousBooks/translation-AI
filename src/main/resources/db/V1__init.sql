-- AI翻译服务数据库初始化脚本

CREATE DATABASE IF NOT EXISTS `translation-ai` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `translation-ai`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user`
(
    `id`
                     bigint
                              NOT
                                  NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `email`
                     varchar(128)      DEFAULT NULL COMMENT '邮箱',
    `password`       varchar(128)      DEFAULT NULL COMMENT '密码(BCrypt)',
    `nickname`       varchar(64)       DEFAULT NULL COMMENT '昵称',
    `avatar`         varchar(512)      DEFAULT NULL COMMENT '头像URL',
    `phone`          varchar(20)       DEFAULT NULL COMMENT '手机号',
    `wechat_openid`  varchar(128)      DEFAULT NULL COMMENT '微信OpenID',
    `alipay_user_id` varchar(128)      DEFAULT NULL COMMENT '支付宝用户ID',
    `login_type`     tinyint  NOT NULL DEFAULT 1 COMMENT '登录类型:1邮箱 2微信 3支付宝',
    `status`         tinyint  NOT NULL DEFAULT 1 COMMENT '状态:0停用 1正常',
    `balance`        decimal(12,
                         2)   NOT NULL DEFAULT 0.00 COMMENT '账户余额(元)',
    `total_consume`  decimal(12,
                         2)   NOT NULL DEFAULT 0.00 COMMENT '累计消耗(元)',
    `deleted`        tinyint  NOT NULL DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除',
    `create_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
        (
         `id`
            ),
    UNIQUE KEY `uk_email`
        (
         `email`
            ),
    UNIQUE KEY `uk_wechat_openid`
        (
         `wechat_openid`
            ),
    UNIQUE KEY `uk_alipay_user_id`
        (
         `alipay_user_id`
            ),
    KEY `idx_status`
        (
         `status`
            ),
    KEY `idx_create_time`
        (
         `create_time`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin`
(
    `id`
                  bigint
                               NOT
                                   NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `username`
                  varchar(64)  NOT NULL COMMENT '用户名',
    `password`    varchar(128) NOT NULL COMMENT '密码(BCrypt)',
    `nickname`    varchar(64)           DEFAULT NULL COMMENT '昵称',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态:0停用 1正常',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
        (
         `id`
            ),
    UNIQUE KEY `uk_username`
        (
         `username`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='管理员表';

-- 钱包流水表
CREATE TABLE IF NOT EXISTS `wallet_record`
(
    `id`
                       bigint
                                NOT
                                    NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `user_id`
                       bigint
                                NOT
                                    NULL
        COMMENT
            '用户ID',
    `type`
                       tinyint
                                NOT
                                    NULL
        COMMENT
            '类型:1充值 2消耗 3退款',
    `amount`
                       decimal(12,
                           2)   NOT NULL COMMENT '金额(元)',
    `balance_before`   decimal(12,
                           2)   NOT NULL COMMENT '变动前余额',
    `balance_after`    decimal(12,
                           2)   NOT NULL COMMENT '变动后余额',
    `related_order_no` varchar(64)       DEFAULT NULL COMMENT '关联订单号',
    `description`      varchar(256)      DEFAULT NULL COMMENT '描述',
    `create_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY
        (
         `id`
            ),
    KEY `idx_user_id`
        (
         `user_id`
            ),
    KEY `idx_create_time`
        (
         `create_time`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='钱包流水表';

-- 翻译记录表
CREATE TABLE IF NOT EXISTS `translate_record`
(
    `id`
                      bigint
                                  NOT
                                      NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `user_id`
                      bigint
                                  NOT
                                      NULL
        COMMENT
            '用户ID',
    `source_lang`
                      varchar(16) NOT NULL COMMENT '源语言',
    `target_lang`     varchar(16) NOT NULL COMMENT '目标语言',
    `source_text`     text        NOT NULL COMMENT '原文',
    `translated_text` text COMMENT '译文',
    `char_count`      int         NOT NULL DEFAULT 0 COMMENT '字符数',
    `cost_amount`     decimal(12,
                          6)      NOT NULL DEFAULT 0.000000 COMMENT '消耗金额(元)',
    `price_per_kchar` decimal(12,
                          6)      NOT NULL DEFAULT 0.000000 COMMENT '每千字符单价(元)',
    `token_count`     int         NOT NULL DEFAULT 0 COMMENT 'LLM消耗Token数',
    `status`          tinyint     NOT NULL DEFAULT 0 COMMENT '状态:0翻译中 1成功 2失败',
    `error_msg`       varchar(512)         DEFAULT NULL COMMENT '错误信息',
    `deleted`         tinyint     NOT NULL DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
        (
         `id`
            ),
    KEY `idx_user_id`
        (
         `user_id`
            ),
    KEY `idx_status`
        (
         `status`
            ),
    KEY `idx_create_time`
        (
         `create_time`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='翻译记录表';

-- 充值订单表
CREATE TABLE IF NOT EXISTS `order`
(
    `id`
                     bigint
                                 NOT
                                     NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `order_no`
                     varchar(64) NOT NULL COMMENT '订单号',
    `user_id`        bigint      NOT NULL COMMENT '用户ID',
    `pay_type`       tinyint     NOT NULL COMMENT '支付类型:1支付宝 2微信',
    `amount`         decimal(12,
                         2)      NOT NULL COMMENT '充值金额(元)',
    `status`         tinyint     NOT NULL DEFAULT 0 COMMENT '状态:0待支付 1已支付 2已取消 3已退款',
    `pay_time`       datetime             DEFAULT NULL COMMENT '支付时间',
    `transaction_id` varchar(128)         DEFAULT NULL COMMENT '第三方交易号',
    `deleted`        tinyint     NOT NULL DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
        (
         `id`
            ),
    UNIQUE KEY `uk_order_no`
        (
         `order_no`
            ),
    KEY `idx_user_id`
        (
         `user_id`
            ),
    KEY `idx_status`
        (
         `status`
            ),
    KEY `idx_create_time`
        (
         `create_time`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='充值订单表';

-- 退款记录表
CREATE TABLE IF NOT EXISTS `refund_record`
(
    `id`
                   bigint
                               NOT
                                   NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `order_id`
                   bigint
                               NOT
                                   NULL
        COMMENT
            '关联订单ID',
    `user_id`
                   bigint
                               NOT
                                   NULL
        COMMENT
            '用户ID',
    `refund_no`
                   varchar(64) NOT NULL COMMENT '退款单号',
    `amount`       decimal(12,
                       2)      NOT NULL COMMENT '退款金额',
    `reason`       varchar(512)         DEFAULT NULL COMMENT '退款原因',
    `status`       tinyint     NOT NULL DEFAULT 0 COMMENT '状态:0待审核 1同意 2拒绝 3退款中 4已完成',
    `admin_id`     bigint               DEFAULT NULL COMMENT '审核管理员ID',
    `audit_remark` varchar(256)         DEFAULT NULL COMMENT '审核备注',
    `deleted`      tinyint     NOT NULL DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除',
    `create_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
        (
         `id`
            ),
    UNIQUE KEY `uk_refund_no`
        (
         `refund_no`
            ),
    KEY `idx_user_id`
        (
         `user_id`
            ),
    KEY `idx_order_id`
        (
         `order_id`
            ),
    KEY `idx_status`
        (
         `status`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='退款记录表';

-- 意见反馈表
CREATE TABLE IF NOT EXISTS `feedback`
(
    `id`
        bigint
        NOT
            NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `user_id`
        bigint
        NOT
            NULL
        COMMENT
            '用户ID',
    `content`
        text
        NOT
            NULL
        COMMENT
            '反馈内容',
    `reply`
        text
        DEFAULT
            NULL
        COMMENT
            '回复内容',
    `status`
        tinyint
        NOT
            NULL
        DEFAULT
            0
        COMMENT
            '状态:0未处理 1已回复',
    `deleted`
        tinyint
        NOT
            NULL
        DEFAULT
            0
        COMMENT
            '逻辑删除:0未删除 1已删除',
    `create_time`
        datetime
        NOT
            NULL
        DEFAULT
            CURRENT_TIMESTAMP
        COMMENT
            '创建时间',
    `update_time`
        datetime
        NOT
            NULL
        DEFAULT
            CURRENT_TIMESTAMP
        ON
            UPDATE
            CURRENT_TIMESTAMP
        COMMENT
            '更新时间',
    PRIMARY
        KEY
        (
         `id`
            ),
    KEY `idx_user_id`
        (
         `user_id`
            ),
    KEY `idx_status`
        (
         `status`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='意见反馈表';

-- 公告表
CREATE TABLE IF NOT EXISTS `announcement`
(
    `id`
                   bigint
                                NOT
                                    NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `title`
                   varchar(128) NOT NULL COMMENT '标题',
    `content`      text         NOT NULL COMMENT '内容',
    `type`         tinyint      NOT NULL DEFAULT 1 COMMENT '类型:1通知 2公告',
    `status`       tinyint      NOT NULL DEFAULT 0 COMMENT '状态:0草稿 1已发布',
    `publish_time` datetime              DEFAULT NULL COMMENT '发布时间',
    `deleted`      tinyint      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
        (
         `id`
            ),
    KEY `idx_status`
        (
         `status`
            ),
    KEY `idx_publish_time`
        (
         `publish_time`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='公告表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config`
(
    `id`
                   bigint
                                NOT
                                    NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `config_key`
                   varchar(128) NOT NULL COMMENT '配置键',
    `config_value` text         NOT NULL COMMENT '配置值',
    `description`  varchar(256)          DEFAULT NULL COMMENT '描述',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
        (
         `id`
            ),
    UNIQUE KEY `uk_config_key`
        (
         `config_key`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统配置表';

-- Token统计表(每日汇总)
CREATE TABLE IF NOT EXISTS `token_statistics`
(
    `id`
                    bigint
                             NOT
                                 NULL
        AUTO_INCREMENT
        COMMENT
            '主键',
    `stat_date`
                    date
                             NOT
                                 NULL
        COMMENT
            '统计日期',
    `total_calls`
                    int
                             NOT
                                 NULL
                                      DEFAULT
                                          0
        COMMENT
            '调用次数',
    `total_tokens`
                    int
                             NOT
                                 NULL
                                      DEFAULT
                                          0
        COMMENT
            '消耗Token数',
    `total_chars`
                    int
                             NOT
                                 NULL
                                      DEFAULT
                                          0
        COMMENT
            '翻译字符数',
    `total_cost`
                    decimal(12,
                        6)   NOT NULL DEFAULT 0.000000 COMMENT '消耗金额(元)',
    `success_count` int      NOT NULL DEFAULT 0 COMMENT '成功次数',
    `fail_count`    int      NOT NULL DEFAULT 0 COMMENT '失败次数',
    `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
        (
         `id`
            ),
    UNIQUE KEY `uk_stat_date`
        (
         `stat_date`
            )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='Token统计表';

-- 初始化管理员账号(密码: admin123)
INSERT INTO `admin` (`username`, `password`, `nickname`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 1);

-- 初始化系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`)
VALUES ('price_per_kchar', '1.0', '每千字符翻译价格(元)'),
       ('min_consume', '0.01', '最低消费金额(元)'),
       ('supported_languages', 'zh-CN,en,ja,ko,fr,de,es,ru', '支持的语言列表'),
       ('translate_timeout', '60', '翻译超时时间(秒)'),
       ('max_translate_length', '5000', '单次翻译最大字符数');