package com.sharksharding.common.holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/14
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class TableShardingDataSourceHolder {
	private static Logger LOGGER = LoggerFactory.getLogger(TableShardingDataSourceHolder.class);

	// 存放分表 sharding key 的 threadlocal
	private static final ThreadLocal<Map<String, String>> tableShardingKeyHolder = new ThreadLocal<>();

	private TableShardingDataSourceHolder() {
	}

	/**
	 * 设置分表 sharding key 信息
	 *
	 * @param key
	 * @param value
	 */
	public static void putTableShardingKey(String key, String value) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("set table sharding key holder: {}-{}", key, value);

		Map<String, String> map = tableShardingKeyHolder.get();
		if (map == null) {
			map = new HashMap<>();
		}
		map.put(key, value);
		tableShardingKeyHolder.set(map);
	}

	public static Map<String, String> getTableShardingKey() {
		return tableShardingKeyHolder.get();
	}

	public static void removeTableShardingKey() {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("remove table sharding key holder");
		tableShardingKeyHolder.remove();
	}
}
