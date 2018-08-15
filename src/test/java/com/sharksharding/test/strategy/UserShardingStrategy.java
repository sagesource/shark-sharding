package com.sharksharding.test.strategy;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>用户分表策略</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/15
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class UserShardingStrategy {

	/**
	 * 按照创建日期分表策略
	 *
	 * @param createTime
	 * @return
	 */
	public static String strategy(Date createTime) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
		return simpleDateFormat.format(createTime);
	}


	/**
	 * 根据 userId 分表策略，截取最后两位为分表标志位
	 *
	 * @return
	 */
	public static String strategyByUserId(String userId) {
		return userId.substring(userId.length() - 2);
	}

}
