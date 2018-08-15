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
	public void saveInfoNoTransaction_test() {
		userService.saveInfoNoTransaction();
	}

}
