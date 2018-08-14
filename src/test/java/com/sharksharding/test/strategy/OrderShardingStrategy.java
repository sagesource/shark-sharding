package com.sharksharding.test.strategy;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/13
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class OrderShardingStrategy {

	private static final String SHARDING_KEY_PREFIX = "sharding_db_";

	/**
	 * 指定分库编号策略
	 *
	 * @param dbIndex
	 * @return
	 */
	public static String strategy(int dbIndex) {
		String shardingkey = String.format("%s%02d", SHARDING_KEY_PREFIX, dbIndex);
		return shardingkey;
	}

}
