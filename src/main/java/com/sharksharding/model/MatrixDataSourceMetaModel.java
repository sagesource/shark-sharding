package com.sharksharding.model;

import com.sharksharding.common.Constants;

/**
 * <p>解析数据源标签数据</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MatrixDataSourceMetaModel {
	// id 属性值
	private String id;
	// 数据源事务管理器
	private String transactionManager = Constants.DEFAULT_TRANSACTION_MANAGER_NAME;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(String transactionManager) {
		this.transactionManager = transactionManager;
	}
}
