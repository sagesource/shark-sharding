package com.sharksharding.test.service.impl;

import com.sharksharding.test.entity.UserEntity;
import com.sharksharding.test.repository.tableshardsdk.UserRepository;
import com.sharksharding.test.service.UserService;
import com.sharksharding.test.strategy.UserShardingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/15
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Service
public class UserServiceImpl implements UserService {

	private static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public void saveInfoNoTransaction() {
		UserEntity userEntity = saveUser();
		LOGGER.warn("====saveInfoNoTransaction====" + userEntity);
	}

	//.............//
	private UserEntity saveUser() {
		Date   date   = new Date();
		String index  = UserShardingStrategy.strategy(date);
		String userId = System.currentTimeMillis() + index;

		UserEntity userEntity = new UserEntity();
		userEntity.setUserId(userId);
		userEntity.setCreateTime(date);
		userEntity.setUpdateTime(date);
		userRepository.insert(userEntity);
		return userEntity;
	}
}
