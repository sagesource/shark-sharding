package com.sharksharding.test.service;


/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/1
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface InfoService {

	/**
	 * 场景：单表操作，没有事务注解
	 * 期望：默认走主库, 方法异常事务不回滚
	 */
	void saveInfoNoTransaction();

	/**
	 * 场景：单表操作，事务注解
	 * 期望：事务开启，方法异常回滚
	 * 说明：如果不指定 MasterSlave 注解，默认都会走 Master 库
	 */
	void saveInfoDefault();

	/**
	 * 场景：单表操作，只读事务
	 * 期望：事务开启，无法插入成功
	 */
	void saveInfoByReadOnly();

	/**
	 * 场景：单表查询，无事务注解，不指定主从
	 * 期望：默认走主库查询
	 */
	void findNoTransactionNonMS();

	/**
	 * 场景：单表查询，无事务注解，指定读取 Master
	 * 期望：无事务，从 Master 库查询到数据
	 */
	void findNoTransactionByMaster();

	/**
	 * 场景：单表查询，无事务注解，指定读取 Slave
	 * 期望：无事务，从 Slave 库查询数据
	 */
	void findNoTransactionBySlave();

	/**
	 * 场景：单表查询，开启只读事务注解，不指定主从
	 * 期望：开启事务，默认从 Master 查询到数据
	 */
	void findByTransactionNonMS();

	/**
	 * 场景：单表查询，开启只读事务，指定读取 Master
	 * 期望：开启只读事务，从 Master 库查询到数据
	 */
	void findByTransactionByMaster();

	/**
	 * 场景：单表查询，开启只读事务，指定读取 Slave
	 * 期望：开启只读事务，从 Slave 库查询数据
	 */
	void findByTransactionBySlave();

	/**
	 * 场景：多表保存，不开启事务
	 * 期望：默认走主库，方法异常不回滚
	 */
	void saveMultiNoTransaction();

	/**
	 * 场景：多表保存，开启事务，指定 Master
	 * 期望：开启事务，Master 保存，方法异常回滚
	 * 说明：如果不指定 MasterSlave 注解，默认都会走 Master 库
	 */
	void saveMultiDefault();
}
