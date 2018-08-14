package com.sharksharding.test.service;

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
	 * 场景：保存订单，不使用事务注解
	 * 期望：默认走主库，使用 dbIndex 指定的分库保存数据，异常不回滚
	 *
	 * @param dbIndex
	 * @return
	 */
	OrderEntity saveNoTransaction(int dbIndex);

	/**
	 * 场景：保存订单，使用事务注解
	 * 期望：默认走主库，使用 dbIndex 指定的分库保存数据
	 *
	 * @param dbIndex
	 */
	OrderEntity saveByTransaction(int dbIndex);

	/**
	 * 场景：保存订单，事务事务注解，并开启 readonly
	 * 期望：默认走主库，无法插入成功
	 *
	 * @param dbIndex
	 * @return
	 */
	OrderEntity saveByReadOnly(int dbIndex);

	/**
	 * 场景：查询订单，无事务注解
	 * 期望：默认查询主库，使用 dbIndex 指定的分库查询数据
	 *
	 * @param id
	 * @param dbIndex
	 * @return
	 */
	OrderEntity findNoTransactionNonMS(long id, int dbIndex);

	/**
	 * 场景：查询订单，无事务注解，指定 Master 数据源
	 * 期望：走主库查询，使用 dbIndex 指定的分库查询数据
	 *
	 * @param id
	 * @param dbIndex
	 * @return
	 */
	OrderEntity findNoTransactionByMaster(long id, int dbIndex);

	/**
	 * 场景：查询订单，无事务注解，指定 Slave 数据源
	 * 期望：走从库查询，使用 dbIndex 指定的分库查询数据
	 *
	 * @param id
	 * @param dbIndex
	 * @return
	 */
	OrderEntity findNoTransactionBySlave(long id, int dbIndex);

	/**
	 * 场景：查询订单，开启只读事务注解，不指定数据源类型
	 * 期望：默认走主库查询，使用 dbIndex 指定的分库查询数据
	 *
	 * @param id
	 * @param dbIndex
	 * @return
	 */
	OrderEntity findByTransactionNonMS(long id, int dbIndex);

	/**
	 * 场景：查询订单，开启只读事务注解，指定走主库
	 * 期望：走主库查询，使用 dbIndex 指定的分库查询数据
	 *
	 * @param id
	 * @param dbIndex
	 * @return
	 */
	OrderEntity findByTransactionByMaster(long id, int dbIndex);

	/**
	 * 场景：查询订单，开启只读事务注解，指定走从库
	 * 期望：走从库查询，使用 dbIndex 指定的分库查询数据
	 *
	 * @param id
	 * @param dbIndex
	 * @return
	 */
	OrderEntity findByTransactionBySlave(long id, int dbIndex);
}
