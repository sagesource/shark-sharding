package com.sharksharding.test.service.impl.sharding;

import com.sharksharding.datasource.annotation.MasterSlave;
import com.sharksharding.datasource.annotation.RepositorySharding;
import com.sharksharding.enums.MasterSlaveType;
import com.sharksharding.test.entity.OrderEntity;
import com.sharksharding.test.repository.shardingdbsdk.OrderRepository;
import com.sharksharding.test.service.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/13
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Transactional(value = "ShardingDbSDKTransactionManager")
	@RepositorySharding(strategy = "T(com.sharksharding.test.strategy.OrderShardingStrategy).strategy(#dbIndex)")
	@MasterSlave(type = MasterSlaveType.MASTER)
	@Override
	public OrderEntity save(int dbIndex) {
		Date        date        = new Date();
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setOrderId(System.currentTimeMillis() + String.format("%02d", dbIndex));
		orderEntity.setOrderAmount(new BigDecimal("1000"));
		orderEntity.setCreateTime(date);
		orderEntity.setUpdateTime(date);

		orderRepository.insert(orderEntity);

		// 模拟事务回滚
		int i = 1 / 0;

		return orderEntity;
	}
}
