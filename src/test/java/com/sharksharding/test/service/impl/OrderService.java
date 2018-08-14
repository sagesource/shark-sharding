package com.sharksharding.test.service.impl;

import com.sharksharding.test.entity.OrderEntity;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/13
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface OrderService {

	/**
	 * 场景：保存订单，使用事务注解
	 * 期望：默认走主库，使用 dbIndex 指定的分库保存数据
	 *
	 * @param dbIndex
	 */
	OrderEntity save(int dbIndex);

}
