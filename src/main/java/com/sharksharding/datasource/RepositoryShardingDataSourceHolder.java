package com.sharksharding.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/8
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class RepositoryShardingDataSourceHolder {
	private static Logger LOGGER = LoggerFactory.getLogger(RepositoryShardingDataSourceHolder.class);

	// 存放分库 sharding key 的 threadlocal
	private static final ThreadLocal<String> repoShardingKeyHolder = new ThreadLocal<>();

	private RepositoryShardingDataSourceHolder() {
	}

	/**
	 * 设置分库 ShardingKey
	 *
	 * @param repoShardingKey
	 */
	public static void putRepoShardingKey(String repoShardingKey) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("set repository sharding key holder: {}", repoShardingKey);
		}
		repoShardingKeyHolder.set(repoShardingKey);
	}

	public static String getRepoShardingKey() {
		return repoShardingKeyHolder.get();
	}

	public static void removeRepoShardingKey() {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("remove repository sharding key holder");
		repoShardingKeyHolder.remove();
	}
}
