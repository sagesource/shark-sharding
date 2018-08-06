package com.sharksharding.model;

import java.util.List;

/**
 * <p>数据源配置模型</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MatrixDataSourceModel {
	// 数据源组名称，分库时为分库shardkey
	private String                groupName;
	// 读写分离 LB 策略
	private String                loadBalance;
	// 原子数据源信息
	private List<MatrixAtomModel> atoms;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getLoadBalance() {
		return loadBalance;
	}

	public void setLoadBalance(String loadBalance) {
		this.loadBalance = loadBalance;
	}

	public List<MatrixAtomModel> getAtoms() {
		return atoms;
	}

	public void setAtoms(List<MatrixAtomModel> atoms) {
		this.atoms = atoms;
	}
}
