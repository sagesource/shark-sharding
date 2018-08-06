package com.sharksharding.datasource;

import com.sharksharding.enums.MasterSlaveType;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
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
public class MasterSlaveDataSource extends AbstractRoutingDataSource {
	/**
	 * 主数据源
	 */
	private Map<String, DataSource> masterDataSourceMapper;
	/**
	 * 从数据源列表
	 */
	private Map<String, DataSource> slaveDataSourceMapper;
	/**
	 * 负载均衡策略
	 */
	private String                  lbStrategy;
	/**
	 * 主库资源名称列表
	 */
	private List<String>            masterDataSourceKeyList;
	/**
	 * 从库资源名称列表
	 */
	private List<String>            slaveDataSourceList;

	@Override
	public void afterPropertiesSet() {
		// 校验主库资源是否存在
		if (this.masterDataSourceMapper == null || this.masterDataSourceMapper.size() == 0 || this.masterDataSourceMapper.size() > 1)
			throw new IllegalArgumentException("the master datasource must onle one.");

		// 设置目标数据源集合，MASTER / SLAVE 都在一起，最后根据 KEY 取出
		ManagedMap<Object, Object> targetMapper = new ManagedMap<>();
		targetMapper.putAll(this.masterDataSourceMapper);
		targetMapper.putAll(this.slaveDataSourceMapper);
		super.setTargetDataSources(targetMapper);

		// 转换资源名称 List
		convertDataSourceList();

		super.afterPropertiesSet();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		// 获取当前线程上下文的读写分离数据源, 默认为 MASTER 的数据源
		MasterSlaveType masterSlaveType = MasterSlaveDataSourceHolder.getDataSource();
		// 获取当前线程上下文的读写分离数据源, 默认为 MASTER 的数据源
		if (masterSlaveType == null || masterSlaveType == MasterSlaveType.MASTER
				|| this.slaveDataSourceMapper == null || this.slaveDataSourceMapper.size() == 0) {
			// 没有指定数据源类型 强制指定数据源类型为写 从库资源不存在
			return this.masterDataSourceKeyList.get(0);
		} else {
			if (this.slaveDataSourceList.size() == 1)
				return this.slaveDataSourceList.get(0);

			// todo：从库筛选策略暂时为随机
			Random random = new Random();
			int    index  = random.nextInt(this.slaveDataSourceList.size());
			return this.slaveDataSourceList.get(index);
		}
	}

	//...............................//

	/**
	 * 转换资源名称 List
	 */
	private void convertDataSourceList() {
		// 将主从库数据源转换为 list
		this.masterDataSourceKeyList = new ArrayList<>();
		for (String masterKey : this.masterDataSourceMapper.keySet()) {
			this.masterDataSourceKeyList.add(masterKey);
		}
		this.slaveDataSourceList = new ArrayList<>();
		for (String slaveKey : this.slaveDataSourceMapper.keySet()) {
			this.slaveDataSourceList.add(slaveKey);
		}
	}
	//...............................//

	public void setMasterDataSourceMapper(Map<String, DataSource> masterDataSourceMapper) {
		this.masterDataSourceMapper = masterDataSourceMapper;
	}

	public void setSlaveDataSourceMapper(Map<String, DataSource> slaveDataSourceMapper) {
		this.slaveDataSourceMapper = slaveDataSourceMapper;
	}

	public void setLbStrategy(String lbStrategy) {
		this.lbStrategy = lbStrategy;
	}
}
