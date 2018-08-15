package com.sharksharding.common;

import com.sharksharding.common.enums.MasterSlaveType;
import com.sharksharding.model.MatrixAtomModel;
import com.sharksharding.model.MatrixDataSourceGroupModel;
import com.sharksharding.model.MatrixPoolConfigMetaModel;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class DataSourceUtils {

	private static final String NAME_APPEND = "-";

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
	 * @param matrixDataSourceGroupModel
	 * @param atomModel
	 * @return
	 */
	public static String buildDsName(String matrixName, MatrixDataSourceGroupModel matrixDataSourceGroupModel, MatrixAtomModel atomModel) {
		StringBuilder result = new StringBuilder(matrixName).append(NAME_APPEND).append(matrixDataSourceGroupModel.getGroupName());
		if (atomModel.getIsMaster()) {
			result.append(NAME_APPEND).append(MasterSlaveType.MASTER.name());
		} else {
			result.append(NAME_APPEND).append(MasterSlaveType.SLAVE.name());
		}
		return result.toString();
	}

	/**
	 * 构建数据源名称
	 *
	 * @param matrixName
	 * @param shardingKey
	 * @param masterSlaveType
	 * @return
	 */
	public static String buildDsName(String matrixName, String shardingKey, MasterSlaveType masterSlaveType) {
		StringBuilder result = new StringBuilder(matrixName).append(NAME_APPEND).append(shardingKey);
		if (masterSlaveType != null)
			result.append(NAME_APPEND).append(masterSlaveType.name());
		return result.toString();
	}

	/**
	 * 构建 bean name
	 *
	 * @param dataSourceId
	 * @param beanName
	 * @return
	 */
	public static String buildBeanName(String dataSourceId, String beanName) {
		return dataSourceId + NAME_APPEND + beanName;
	}

	/**
	 * 构建数据源配置参数
	 *
	 * @param matrixAtomModel
	 * @param atomDataSourcePoolConfig
	 * @return
	 */
	public static Map<String, Object> buildPoolConfig(MatrixAtomModel matrixAtomModel, Map<String, MatrixPoolConfigMetaModel> atomDataSourcePoolConfig) {
		String url = DataSourceUtils.builtUrl(matrixAtomModel);

		// 数据源连接参数 Map
		Map<String, Object> dsPoolParams = new LinkedHashMap<>();
		dsPoolParams.put("driverClassName", Constants.DEFAULT_DB_DRIVER);
		dsPoolParams.put("url", url);
		dsPoolParams.put("username", matrixAtomModel.getUsername());
		dsPoolParams.put("password", matrixAtomModel.getPassword());

		String                    atomName                  = matrixAtomModel.getAtomName();
		MatrixPoolConfigMetaModel matrixPoolConfigMetaModel = atomDataSourcePoolConfig.get(atomName);
		if (matrixPoolConfigMetaModel == null) {
			matrixPoolConfigMetaModel = atomDataSourcePoolConfig.get("*");
			dsPoolParams.putAll(matrixPoolConfigMetaModel.getProperties());
		} else {
			dsPoolParams.putAll(matrixPoolConfigMetaModel.getProperties());
		}

		return dsPoolParams;
	}
}
