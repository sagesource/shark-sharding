package com.sharksharding.datasource;

import com.sharksharding.enums.MasterSlaveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MasterSlaveDataSourceHolder {
	private static Logger LOGGER = LoggerFactory.getLogger(MasterSlaveDataSourceHolder.class);

	private static final ThreadLocal<MasterSlaveType> holder = new ThreadLocal<>();

	private MasterSlaveDataSourceHolder() {
	}

	// 设置数据源
	public static void putDataSource(MasterSlaveType dataSource) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("set master/slave thread holder:{}", dataSource);
		holder.set(dataSource);
	}

	//获取数据源
	public static MasterSlaveType getDataSource() {
		return holder.get();
	}

	// 清除数据源
	public static void clearDataSource() {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("remove master/slave thread holder");
		holder.remove();
	}
}
