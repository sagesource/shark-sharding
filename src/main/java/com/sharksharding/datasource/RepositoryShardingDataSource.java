package com.sharksharding.datasource;

import com.sharksharding.common.DataSourceUtils;
import com.sharksharding.enums.MasterSlaveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>分库数据源</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/7
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class RepositoryShardingDataSource extends AbstarctDataSourceMBean {
	private static Logger LOGGER = LoggerFactory.getLogger(RepositoryShardingDataSource.class);

	// matrix数据源名称
	private String matrixName;

	@Override
	public void afterPropertiesSet() {
		// 校验主库资源是否存在
		if (super.masterDataSourceMapper == null || super.masterDataSourceMapper.size() == 0)
			throw new IllegalArgumentException("repository sharding master datasource must not null");

		// 设置目标数据源集合
		ManagedMap<Object, Object> targetMapper = new ManagedMap<>();
		targetMapper.putAll(super.masterDataSourceMapper);
		targetMapper.putAll(super.slaveDataSourceMapper);
		super.setTargetDataSources(targetMapper);

		super.afterPropertiesSet();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		// 获取当前线程上下文的读写分离数据源, 默认为 MASTER 的数据源
		MasterSlaveType masterSlaveType = MasterSlaveDataSourceHolder.getDataSource();
		String          shardingKey     = RepositoryShardingDataSourceHolder.getRepoShardingKey();

		// 判断 shardingKey 是否存在
		if (StringUtils.isEmpty(shardingKey)) {
			throw new IllegalArgumentException("repository sharding key is null");
		}

		// 配置 Master 数据源名称
		String masterDsKey = DataSourceUtils.buildDsName(this.matrixName, shardingKey, MasterSlaveType.MASTER);

		// 获取当前线程上下文的读写分离数据源, 默认为 MASTER 的数据源
		if (masterSlaveType == null || masterSlaveType == MasterSlaveType.MASTER
				|| super.slaveDataSourceMapper == null || super.slaveDataSourceMapper.size() == 0) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("choose sharding {} master datasource {}", shardingKey, masterDsKey);
			}
			return masterDsKey;
		} else {
			// 根据 master 数据源获取 slave 列表
			List<String> slaveNameList = super.masterSlaveDataSourceMapper.get(masterDsKey);
			String       slaveDsKey    = selectorSlaveDsName(slaveNameList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("choose sharding {} slave datasource {}", shardingKey, slaveDsKey);
			}

			return slaveDsKey;
		}
	}

	public void setMatrixName(String matrixName) {
		this.matrixName = matrixName;
	}
}
