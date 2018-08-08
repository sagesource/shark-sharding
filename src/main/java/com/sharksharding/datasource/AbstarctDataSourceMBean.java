package com.sharksharding.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/8
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public abstract class AbstarctDataSourceMBean extends AbstractRoutingDataSource {
	/**
	 * 分库 Master 数据源
	 */
	protected Map<String, DataSource>   masterDataSourceMapper;
	/**
	 * 分库 Slave 数据源
	 */
	protected Map<String, DataSource>   slaveDataSourceMapper;
	/**
	 * Master - Slave DS 名称映射
	 */
	protected Map<String, List<String>> masterSlaveDataSourceMapper;
	/**
	 * 负载均衡策略
	 */
	protected String                    lbStrategy;

	protected String selectorSlaveDsName(List<String> slaveNameList) {
		String dsKey = null;
		// 根据 master 数据源获取 slave 列表
		if (slaveNameList.size() == 1) {
			// 只有一个从库的场景，直接返回第一个从库
			dsKey = slaveNameList.get(0);
		} else {
			// todo:lb 策略不存在，使用随机
			Random random = new Random();
			int    index  = random.nextInt(slaveNameList.size());
			dsKey = slaveNameList.get(index);
		}
		return dsKey;
	}

	public void setMasterDataSourceMapper(Map<String, DataSource> masterDataSourceMapper) {
		this.masterDataSourceMapper = masterDataSourceMapper;
	}

	public void setSlaveDataSourceMapper(Map<String, DataSource> slaveDataSourceMapper) {
		this.slaveDataSourceMapper = slaveDataSourceMapper;
	}

	public void setLbStrategy(String lbStrategy) {
		this.lbStrategy = lbStrategy;
	}

	public void setMasterSlaveDataSourceMapper(Map<String, List<String>> masterSlaveDataSourceMapper) {
		this.masterSlaveDataSourceMapper = masterSlaveDataSourceMapper;
	}
}
