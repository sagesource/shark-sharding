package com.sharksharding.common.annotation;

import java.lang.annotation.*;

/**
 * <p>分表注解</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/14
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TableSharding {

	/**
	 * 分表 shardingKey
	 *
	 * @return
	 */
	String shardingKey();

	/**
	 * 分表策略表达式
	 */
	String strategy();
}
