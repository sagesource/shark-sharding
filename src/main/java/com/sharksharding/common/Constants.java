package com.sharksharding.common;

/**
 * <p>常量值</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface Constants {
	public static final String ARTIFACT_ALL_PREFIX = "${";
	public static final String ARTIFACT_ALL_SUFFIX = "}";
	public static final String DEFAULT_CHARSET     = "utf-8";

	// xsd element attribute
	public static final String XSD_ID                  = "id";
	public static final String XSD_TRANSACTION_MANAGER = "transactionManager";

	// default value
	public static final String DEFAULT_TRANSACTION_MANAGER_NAME = "transactionManager";
	public static final String DEFAULT_DB_DRIVER                = "com.mysql.jdbc.Driver";
	public static final String DEFAULT_INIT_METHOD              = "init";
	public static final String DEFAULT_DESTORY_METHOD           = "close";

	// field name
	public static final String MASTER_DATASOURCE_MAPPER = "masterDataSourceMapper";
	public static final String SLAVE_DATASOURCE_MAPPER  = "slaveDataSourceMapper";
}
