package com.sharksharding.common.annotation;

import java.lang.annotation.*;

/**
 * <p>分库注解</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RepositorySharding {

	/**
	 * 分库策略表达式
	 */
	String strategy();
}
