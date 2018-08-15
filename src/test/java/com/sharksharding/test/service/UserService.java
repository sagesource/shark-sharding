package com.sharksharding.test.service;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/15
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface UserService {
	/**
	 * 场景：单表操作，没有事务注解
	 * 期望：默认走主库, 方法异常事务不回滚
	 */
	void saveInfoNoTransaction();
}
