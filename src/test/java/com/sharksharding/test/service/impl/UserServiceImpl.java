package com.sharksharding.test.service.impl;

import com.alibaba.fastjson.JSON;
import com.sharksharding.common.annotation.MasterSlave;
import com.sharksharding.common.enums.MasterSlaveType;
import com.sharksharding.test.entity.DictEntity;
import com.sharksharding.test.entity.UserEntity;
import com.sharksharding.test.repository.tableshardsdk.DictRepository;
import com.sharksharding.test.repository.tableshardsdk.UserRepository;
import com.sharksharding.test.service.UserService;
import com.sharksharding.test.strategy.UserShardingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

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
	@Autowired
	private DictRepository dictRepository;

	@Override
	public void saveUserNoTransaction() {
		UserEntity userEntity = saveUser();
		LOGGER.warn("====saveUserNoTransaction====" + userEntity);

		// 模拟运行异常
		// int i = 1 / 0;
	}

	@Transactional(value = "TableShardSDKTransactionManager")
	@Override
	public void saveUserDefault() {
		UserEntity userEntity = saveUser();
		LOGGER.warn("====saveUserDefault====" + userEntity);

		// 模拟运行异常
		int i = 1 / 0;
	}

	@Override
	public void findUserNoTransactionNonMS() {
		UserEntity userEntity = queryUser();
		LOGGER.warn("====findUserNoTransactionNonMS====" + userEntity);
	}

	@MasterSlave(type = MasterSlaveType.MASTER)
	@Override
	public void findUserNoTransactionByMaster() {
		UserEntity userEntity = queryUser();
		LOGGER.warn("====findUserNoTransactionByMaster====" + userEntity);
	}

	@MasterSlave(type = MasterSlaveType.SLAVE)
	@Override
	public void findUserNoTransactionBySlave() {
		UserEntity userEntity = queryUser();
		LOGGER.warn("====findUserNoTransactionBySlave====" + userEntity);
	}

	@Transactional(value = "TableShardSDKTransactionManager", readOnly = true)
	@MasterSlave(type = MasterSlaveType.SLAVE)
	@Override
	public void findUserByTransaction() {
		UserEntity userEntity = queryUser();
		LOGGER.warn("====findUserByTransaction====" + userEntity);
	}

	@Override
	public void saveMultiNoTransaction() {
		UserEntity userEntity = saveUser();
		LOGGER.warn("====saveMultiNoTransaction====" + userEntity);

		DictEntity dictEntity = new DictEntity();
		dictEntity.setKey(userEntity.getUserId());
		dictEntity.setValue(System.currentTimeMillis() + "");
		dictRepository.insert(dictEntity);
		LOGGER.warn("====saveMultiNoTransaction====" + dictEntity);

		// 模拟运行异常
		int i = 1 / 0;
	}

	@Transactional(value = "TableShardSDKTransactionManager")
	@MasterSlave(type = MasterSlaveType.MASTER)
	@Override
	public void saveMultiByTransaction() {
		UserEntity userEntity = saveUser();
		LOGGER.warn("====saveMultiByTransaction====" + userEntity);

		DictEntity dictEntity = new DictEntity();
		dictEntity.setKey(userEntity.getUserId());
		dictEntity.setValue(System.currentTimeMillis() + "");
		dictRepository.insert(dictEntity);
		LOGGER.warn("====saveMultiByTransaction====" + dictEntity);

		// 模拟运行异常
		//int i = 1 / 0;
	}

	@Transactional(value = "TableShardSDKTransactionManager")
	@Override
	public void findMultiByJoin() {
		Map map = userRepository.selectByJoin("153433545194908");
		LOGGER.warn("====findMultiByJoin====" + JSON.toJSONString(map));
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

	private UserEntity queryUser() {
		return userRepository.selectByUserId("153433316185008");
	}
}
