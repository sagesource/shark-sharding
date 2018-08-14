package com.sharksharding.test;

import com.sharksharding.test.entity.OrderEntity;
import com.sharksharding.test.service.impl.OrderService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/13
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class OrderServiceTest extends BaseTest {
	private static Logger LOGGER = LoggerFactory.getLogger(OrderServiceTest.class);

	@Autowired
	private OrderService orderService;

	@Test
	public void save_test() {
		OrderEntity order = orderService.save(1);
		LOGGER.warn("=====" + order + "=====");
	}

}
