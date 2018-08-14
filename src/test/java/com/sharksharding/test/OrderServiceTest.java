package com.sharksharding.test;

import com.sharksharding.test.entity.OrderEntity;
import com.sharksharding.test.service.OrderService;
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
	public void saveNoTransaction_test() {
		OrderEntity order = orderService.saveNoTransaction(1);
		LOGGER.warn("=====" + order + "=====");
	}

	@Test
	public void saveByTransaction_test() {
		OrderEntity order = orderService.saveByTransaction(2);
		LOGGER.warn("=====" + order + "=====");
	}

	@Test
	public void saveByReadOnly_test() {
		OrderEntity order = orderService.saveByReadOnly(1);
		LOGGER.warn("=====" + order + "=====");
	}

	@Test
	public void findNoTransactionNonMS_test() {
		OrderEntity order = orderService.findNoTransactionNonMS(1L, 2);
		LOGGER.warn("=====" + order + "=====");
	}

	@Test
	public void findNoTransactionByMaster_test() {
		OrderEntity order = orderService.findNoTransactionByMaster(1L, 1);
		LOGGER.warn("=====" + order + "=====");
	}

	@Test
	public void findNoTransactionBySlave_test() {
		OrderEntity order = orderService.findNoTransactionBySlave(1L, 2);
		LOGGER.warn("=====" + order + "=====");
	}

	@Test
	public void findByTransactionNonMS_test() {
		OrderEntity order = orderService.findByTransactionNonMS(1L, 1);
		LOGGER.warn("=====" + order + "=====");
	}

	@Test
	public void findByTransactionByMaster_test() {
		OrderEntity order = orderService.findByTransactionByMaster(1L, 2);
		LOGGER.warn("=====" + order + "=====");
	}

	@Test
	public void findByTransactionBySlave_test() {
		OrderEntity order = orderService.findByTransactionBySlave(1L, 2);
		LOGGER.warn("=====" + order + "=====");
	}
}
