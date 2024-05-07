CREATE TABLE `item_config`  (
                                `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
                                `type` int(11) NOT NULL COMMENT '物品类型 1改名卡 2徽章',
                                `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品图片',
                                `describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品功能描述',
                                `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `idx_create_time`(`create_time`) USING BTREE,
                                INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '功能物品配置表' ROW_FORMAT = Dynamic;
