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
	// 数据源名称
	private String                matrixName;
	// 原子数据源配置
	private List<MatrixAtomModel> groups;

	public String getMatrixName() {
		return matrixName;
	}

	public void setMatrixName(String matrixName) {
		this.matrixName = matrixName;
	}

	public List<MatrixAtomModel> getGroups() {
		return groups;
	}

	public void setGroups(List<MatrixAtomModel> groups) {
		this.groups = groups;
	}
}
