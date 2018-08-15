package com.sharksharding.datasource;

import com.sharksharding.common.enums.MasterSlaveType;
import com.sharksharding.common.holder.MasterSlaveDataSourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.ManagedMap;

import java.util.List;

/**
 * <p>动态数据源 支持一主多从 读写分离</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MasterSlaveDataSource extends AbstarctDataSourceMBean {
	private static Logger LOGGER = LoggerFactory.getLogger(MasterSlaveDataSource.class);

	// 默认数据源名称
	private String defaultMasterDsName;

	@Override
	public void afterPropertiesSet() {
		// 校验主库资源是否存在
		if (super.masterDataSourceMapper == null || super.masterDataSourceMapper.size() == 0 || super.masterDataSourceMapper.size() > 1)
			throw new IllegalArgumentException("the master datasource must onle one.");

		// 设置目标数据源集合，MASTER / SLAVE 都在一起，最后根据 KEY 取出
		ManagedMap<Object, Object> targetMapper = new ManagedMap<>();
		targetMapper.putAll(super.masterDataSourceMapper);
		targetMapper.putAll(super.slaveDataSourceMapper);
		super.setTargetDataSources(targetMapper);

		// 设置默认 Master 数据源名称, 因为该类对应的是单库的读写分离，直接 next 即可
		defaultMasterDsName = super.masterSlaveDataSourceMapper.keySet().iterator().next();

		super.afterPropertiesSet();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		// 获取当前线程上下文的读写分离数据源, 默认为 MASTER 的数据源
		MasterSlaveType masterSlaveType = MasterSlaveDataSourceHolder.getDataSource();
		// 获取当前线程上下文的读写分离数据源, 默认为 MASTER 的数据源
		if (masterSlaveType == null || masterSlaveType == MasterSlaveType.MASTER
				|| super.slaveDataSourceMapper == null || super.slaveDataSourceMapper.size() == 0) {
			// 没有指定数据源类型 强制指定数据源类型为写 从库资源不存在
			String dsKey = defaultMasterDsName;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("choose master datasource key:{}", dsKey);
			}
			return dsKey;
		} else {
			// 根据 master 数据源获取 slave 列表
			List<String> slaveNameList = super.masterSlaveDataSourceMapper.get(defaultMasterDsName);
			String       slaveDsKey    = selectorSlaveDsName(slaveNameList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("choose slave datasource key:{}", slaveDsKey);
			}
			return slaveDsKey;
		}
	}
}
