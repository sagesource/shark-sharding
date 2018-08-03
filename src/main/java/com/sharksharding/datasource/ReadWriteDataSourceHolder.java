package com.sharksharding.datasource;

import com.sharksharding.enums.ReadWriteType;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ReadWriteDataSourceHolder {
	private static final ThreadLocal<ReadWriteType> holder = new ThreadLocal<>();

	private ReadWriteDataSourceHolder() {
	}

	// 设置数据源
	public static void putDataSource(ReadWriteType dataSource) {
		holder.set(dataSource);
	}

	//获取数据源
	public static ReadWriteType getDataSource() {
		return holder.get();
	}

	// 清除数据源
	public static void clearDataSource() {
		holder.remove();
	}
}
