package com.sharksharding.common;

import com.sharksharding.enums.MasterSlaveType;
import com.sharksharding.model.MatrixAtomModel;
import com.sharksharding.model.MatrixDataSourceModel;
import org.springframework.util.StringUtils;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class DataSourceUtils {

	/**
	 * 构建数据库连接字符串
	 *
	 * @param atomModel
	 * @return
	 */
	public static String builtUrl(MatrixAtomModel atomModel) {
		StringBuilder result = new StringBuilder("jdbc:mysql://");
		result.append(atomModel.getHost()).append(":").append(atomModel.getPort()).append("/").append(atomModel.getDbName());
		if (StringUtils.isEmpty(atomModel.getParams())) {
			result.append("?").append(atomModel.getParams());
		}
		return result.toString();
	}

	/**
	 * 构建数据源名称
	 *
	 * @param matrixName
	 * @param matrixDataSourceModel
	 * @param atomModel
	 * @return
	 */
	public static String buildDsName(String matrixName, MatrixDataSourceModel matrixDataSourceModel, MatrixAtomModel atomModel) {
		StringBuilder result = new StringBuilder(matrixName).append("-").append(matrixDataSourceModel.getGroupName());
		if (atomModel.getIsMaster()) {
			result.append("-").append(MasterSlaveType.MASTER.name());
		} else {
			result.append("-").append(MasterSlaveType.SLAVE.name());
		}
		return result.toString();
	}
}
