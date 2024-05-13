CREATE TABLE `user_backpack`  (
                                  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
                                  `uid` bigint(20) NOT NULL COMMENT 'uid',
                                  `item_id` bigint(20) NOT NULL COMMENT '物品id',
                                  `status` int(11) NOT NULL COMMENT '使用状态 0.待使用 1已使用',
                                  `idempotent` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '幂等号',
                                  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uniq_idempotent`(`idempotent`) USING BTREE,
                                  INDEX `idx_uid`(`uid`) USING BTREE,
                                  INDEX `idx_create_time`(`create_time`) USING BTREE,
                                  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户背包表' ROW_FORMAT = Dynamic;
