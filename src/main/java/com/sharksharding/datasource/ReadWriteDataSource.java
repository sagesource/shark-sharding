package com.sharksharding.datasource;

import com.sharksharding.enums.ReadWriteType;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * <p>动态数据源 支持一主多从 读写分离</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ReadWriteDataSource extends AbstractRoutingDataSource {
	//private static final Logger LOGGER = LoggerFactory.getLogger(ReadWriteDataSource.class);

	/**
	 * 主数据源
	 */
	private RuntimeBeanReference       masterDataSource;
	/**
	 * 从数据源列表
	 */
	private List<RuntimeBeanReference> slaveDataSources;

	@Override
	public void afterPropertiesSet() {
		if (masterDataSource == null)
			throw new IllegalArgumentException("master datasource is required!");

		// 设置默认数据源为 master
		super.setDefaultTargetDataSource(this.masterDataSource);

		// 设置目标数据源信息
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put(ReadWriteType.MASTER.name(), this.masterDataSource);
		if (this.slaveDataSources != null) {
			if (this.slaveDataSources != null && this.slaveDataSources.size() > 0)
				for (int index = 0; index < slaveDataSources.size(); index++) {
					// 使用 SLAVE_INDEX 作为从数据源的 KEY
					targetDataSources.put(ReadWriteType.SLAVE.name() + "_" + index, this.slaveDataSources);
				}
		}
		super.setTargetDataSources(targetDataSources);
		super.afterPropertiesSet();

	}

	@Override
	protected Object determineCurrentLookupKey() {
		// 获取当前线程上下文的读写分离数据源, 默认为 MASTER 的数据源
		ReadWriteType readWriteType = ReadWriteDataSourceHolder.getDataSource();
		if (readWriteType == null || readWriteType == ReadWriteType.MASTER) {
			return ReadWriteType.MASTER.name();
		}

		// 如果没有配置从数据源,返回 MASTER 数据源
		if (this.slaveDataSources == null || this.slaveDataSources.size() == 0)
			return ReadWriteType.MASTER.name();

		// 使用随机算法获取多从的数据源
		Random random = new Random();
		int    index  = random.nextInt(this.slaveDataSources.size());
		return ReadWriteType.SLAVE.name() + "_" + index;
	}

	public void setMasterDataSource(RuntimeBeanReference masterDataSource) {
		this.masterDataSource = masterDataSource;
	}

	public void setSlaveDataSources(List<RuntimeBeanReference> slaveDataSources) {
		this.slaveDataSources = slaveDataSources;
	}
}
