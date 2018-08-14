package com.sharksharding.test;

import com.sharksharding.test.entity.OrderEntity;
import com.sharksharding.test.service.InfoService;
import com.sharksharding.test.service.OrderService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/14
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MultiServiceTest extends BaseTest {
	private static Logger LOGGER = LoggerFactory.getLogger(MultiServiceTest.class);

	@Autowired
	private InfoService  infoService;
	@Autowired
	private OrderService orderService;

	/**
	 * 场景：Info 执行保存，走主库；Order 执行查询，走从库
	 * 期望：同一线程中，彼此操作互不影响主从选择和事务
	 */
	@Test
	public void multiTest_1() {
		infoService.saveInfoDefault();
		OrderEntity order = orderService.findByTransactionBySlave(1L, 2);
		LOGGER.warn("=====" + order + "=====");
	}
}
