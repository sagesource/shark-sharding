package com.sharksharding.test;

import com.sharksharding.test.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/15
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class UserServiceTest extends BaseTest {

	@Autowired
	private UserService userService;

	@Test
	public void saveUserNoTransaction_test() {
		userService.saveUserNoTransaction();
	}

	@Test
	public void saveUserDefault_test() {
		userService.saveUserDefault();
	}

	@Test
	public void findUserNoTransactionNonMS_test() {
		userService.findUserNoTransactionNonMS();
	}

	@Test
	public void findUserNoTransactionByMaster_test() {
		userService.findUserNoTransactionByMaster();
	}

	@Test
	public void findUserNoTransactionBySlave_test() {
		userService.findUserNoTransactionBySlave();
	}

	@Test
	public void findUserByTransaction_test() {
		userService.findUserByTransaction();
	}

	@Test
	public void saveMultiNoTransaction_test() {
		userService.saveMultiNoTransaction();
	}

	@Test
	public void saveMultiByTransaction_test() {
		userService.saveMultiByTransaction();
	}

	@Test
	public void findMultiByJoin_test() {
		userService.findMultiByJoin();
	}
}
