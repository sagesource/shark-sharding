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
	void saveUserNoTransaction();

	/**
	 * 场景：单表操作，事务注解
	 * 期望：事务开启，方法异常回滚
	 */
	void saveUserDefault();

	/**
	 * 场景：单表查询，无事务注解，不指定主从
	 * 期望：默认走主库查询，根据分表规则查询到对应表
	 */
	void findUserNoTransactionNonMS();

	/**
	 * 场景：单表查询，无注解事务，指定读取 Master
	 * 期望：无事务，从 Master 库根据分表规则查询数据
	 */
	void findUserNoTransactionByMaster();

	/**
	 * 场景：单表查询，无事务注解，指定读取 Slave
	 * 期望：无事务，从 Slave 库根据分表规则查询数据
	 */
	void findUserNoTransactionBySlave();

	/**
	 * 场景：单表查询，只读事务注解
	 * 期望：根据指定的 MS 查询主从库
	 */
	void findUserByTransaction();

	/**
	 * 场景：多表保存，不开启事务
	 * 期望：默认走主库，方法异常不回滚
	 */
	void saveMultiNoTransaction();

	/**
	 * 场景：多表保存，开启事务
	 * 期望：默认走主库，方法异常回滚
	 */
	void saveMultiByTransaction();

	/**
	 * 场景：连表查询，开启事务，走主库
	 * 期望：走主库，SQL 中分表 shardingKey 被替换，单表不受影响
	 */
	void findMultiByJoin();
}
