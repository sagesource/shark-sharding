package com.sharksharding.test.repository.shardingdbsdk;

import com.sharksharding.test.entity.OrderEntity;
import org.springframework.stereotype.Repository;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Repository
public interface OrderRepository {

	void insert(OrderEntity params);

	OrderEntity selectById(Long id);
}
