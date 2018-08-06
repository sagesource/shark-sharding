package com.sharksharding.datasource;

import com.sharksharding.enums.MasterSlaveType;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MasterSlaveDataSourceHolder {
	private static final ThreadLocal<MasterSlaveType> holder = new ThreadLocal<>();

	private MasterSlaveDataSourceHolder() {
	}

	// 设置数据源
	public static void putDataSource(MasterSlaveType dataSource) {
		holder.set(dataSource);
	}

	//获取数据源
	public static MasterSlaveType getDataSource() {
		return holder.get();
	}

	// 清除数据源
	public static void clearDataSource() {
		holder.remove();
	}
}
