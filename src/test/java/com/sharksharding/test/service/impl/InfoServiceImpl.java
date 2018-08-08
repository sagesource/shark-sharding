package com.sharksharding.test.service.impl;

import com.sharksharding.datasource.annotation.MasterSlave;
import com.sharksharding.enums.MasterSlaveType;
import com.sharksharding.test.entity.InfoEntity;
import com.sharksharding.test.entity.LogEntity;
import com.sharksharding.test.repository.testsdk.InfoRepository;
import com.sharksharding.test.repository.testsdk.LogRepository;
import com.sharksharding.test.service.InfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/1
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Service
public class InfoServiceImpl implements InfoService {
	private static Logger LOGGER = LoggerFactory.getLogger(InfoServiceImpl.class);

	@Autowired
	private InfoRepository infoRepository;
	@Autowired
	private LogRepository  logRepository;


	@Override
	public void saveInfoNoTransaction() {
		InfoEntity infoEntity = saveInfoEntity();
		LOGGER.warn("====saveInfoNoTransaction====id:" + infoEntity.getId());

		// 模拟运行异常
		int i = 1 / 0;
	}

	@Override
	@Transactional(value = "TestSDKTransactionManager")
	@MasterSlave
	public void saveInfoDefault() {
		InfoEntity infoEntity = saveInfoEntity();
		LOGGER.warn("====saveInfoDefault====id:" + infoEntity.getId());

		// 模拟运行异常
		// int i = 1 / 0;
	}

	@Override
	@Transactional(value = "TestSDKTransactionManager", readOnly = true)
	public void saveInfoByReadOnly() {
		InfoEntity infoEntity = saveInfoEntity();
		LOGGER.warn("====saveInfoByReadOnly====id:" + infoEntity.getId());
	}

	@Override
	public void findNoTransactionNonMS() {
		long       id     = 29L;
		InfoEntity result = infoRepository.selectById(id);
		LOGGER.warn("====" + result + "====");
	}

	@Override
	@MasterSlave(type = MasterSlaveType.MASTER)
	public void findNoTransactionByMaster() {
		long       id     = 29L;
		InfoEntity result = infoRepository.selectById(id);
		LOGGER.warn("====findNoTransactionByMaster:" + result + "====");
	}

	@Override
	@MasterSlave(type = MasterSlaveType.SLAVE)
	public void findNoTransactionBySlave() {
		long       id     = 29L;
		InfoEntity result = infoRepository.selectById(id);
		LOGGER.warn("====findNoTransactionBySlave:" + result + "====");
	}

	@Override
	@Transactional(value = "TestSDKTransactionManager", readOnly = true)
	public void findByTransactionNonMS() {
		long       id     = 29L;
		InfoEntity result = infoRepository.selectById(id);
		LOGGER.warn("====findByTransactionNonMS:" + result + "====");
	}

	@Override
	@Transactional(value = "TestSDKTransactionManager", readOnly = true)
	@MasterSlave(type = MasterSlaveType.MASTER)
	public void findByTransactionByMaster() {
		long       id     = 29L;
		InfoEntity result = infoRepository.selectById(id);
		LOGGER.warn("====findByTransactionByMaster:" + result + "====");
	}

	@Override
	@Transactional(value = "TestSDKTransactionManager", readOnly = true)
	@MasterSlave(type = MasterSlaveType.SLAVE)
	public void findByTransactionBySlave() {
		long       id     = 29L;
		InfoEntity result = infoRepository.selectById(id);
		LOGGER.warn("====findByTransactionBySlave:" + result + "====");
	}

	@Override
	public void saveMultiNoTransaction() {
		InfoEntity infoEntity = saveInfoEntity();
		LOGGER.warn(">>>>saveMultiNoTransaction<<<< info:" + infoEntity);

		// 模拟运行异常
		//int i = 1 / 0;

		LogEntity logEntity = saveLogEntity();
		LOGGER.warn(">>>>saveMultiNoTransaction<<<< log:" + logEntity);

		// 模拟运行异常
		int i = 1 / 0;
	}

	@Override
	@Transactional(value = "TestSDKTransactionManager")
	@MasterSlave(type = MasterSlaveType.MASTER)
	public void saveMultiDefault() {
		InfoEntity infoEntity = saveInfoEntity();
		LOGGER.warn(">>>>saveMultiDefault<<<< info:" + infoEntity);

		// 模拟运行异常
		//int i = 1 / 0;

		LogEntity logEntity = saveLogEntity();
		LOGGER.warn(">>>>saveMultiDefault<<<< log:" + logEntity);

		// 模拟运行异常
		int i = 1 / 0;
	}

	//.....//
	private InfoEntity saveInfoEntity() {
		Date       date       = new Date();
		InfoEntity infoEntity = new InfoEntity();
		infoEntity.setName("薛琪");
		infoEntity.setAddress("Address:" + System.currentTimeMillis());
		infoEntity.setCreateTime(date);
		infoEntity.setUpdateTime(date);
		infoRepository.insert(infoEntity);
		return infoEntity;
	}

	private LogEntity saveLogEntity() {
		LogEntity logEntity = new LogEntity();
		logEntity.setAction("insert" + System.currentTimeMillis());
		logRepository.insert(logEntity);
		return logEntity;
	}
}
