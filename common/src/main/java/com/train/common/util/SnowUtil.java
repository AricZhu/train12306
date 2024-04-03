package com.train.common.util;

import cn.hutool.core.util.IdUtil;

public class SnowUtil {
    private static final long datacenterId = 1L;
    private static final long workId = 1L;

    public static long getSnowflakeNextId() {
        return IdUtil.getSnowflake(workId, datacenterId).nextId();
    }

    public static String getSnowflakeNextIdStr() {
        return IdUtil.getSnowflake(workId, datacenterId).nextIdStr();
    }
}
