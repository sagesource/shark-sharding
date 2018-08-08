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
	public static final String XSD_ID                        = "id";
	public static final String XSD_TRANSACTION_MANAGER       = "transactionManager";
	public static final String XSD_MATRIX_ORDER              = "order";
	public static final String XSD_IGNORE_RESOURCE_NOT_FOUND = "ignore-resource-not-found";

	// default value
	public static final String DEFAULT_TRANSACTION_MANAGER_NAME = "transactionManager";
	public static final String DEFAULT_DB_DRIVER                = "com.mysql.jdbc.Driver";
	public static final String DEFAULT_INIT_METHOD              = "init";
	public static final String DEFAULT_DESTORY_METHOD           = "close";

	// field name
	public static final String MASTER_DATASOURCE_MAPPER = "masterDataSourceMapper";
	public static final String SLAVE_DATASOURCE_MAPPER  = "slaveDataSourceMapper";

	// spring bean name
	public static final String PROPERTY_PLACE_HOLDER                          = "propertyConfigurer";
	public static final String ANNOTATION_MASTER_SLAVE_DATASOURCE_INTERCEPTOR = "annotationReadWriteDataSourceInterceptor";

	// spring aop config
	public static final String AOP_NAMESPACE_URI                            = "http://www.springframework.org/schema/aop";
	public static final String ANNOTATION_MASTER_SLAVE_DATA_SOURCE_POINTCUT = "annotationMasterSlaveDataSourcePointcut";
	public static final String REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT     = "repositoryShardingDataSourcePointcut";
	public static final String TABLE_SHARDING_DATA_SOURCE_POINTCUT          = "tableShardingDataSourcePointcut";
	public static final String MASTERSLAVE_POINTCUT_EXPRESSION              = "@annotation(com.sharksharding.common.annotation.MasterSlave)";
	public static final String EXPRESSION                                   = "expression";
	public static final String ADVICE_REF                                   = "advice-ref";
	public static final String POINTCUT_REF                                 = "pointcut-ref";
	public static final String ADVISOR                                      = "advisor";
	public static final String CONFIG                                       = "config";
	public static final String POINTCUT                                     = "pointcut";

	// inteceptor order
	public static final String TABLE_SHARDING_DATA_SOURCE_POINTCUT_ORDER      = "100";
	public static final String REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT_ORDER = "100";
	public static final String ANNOTATION_MASTERSLAVE_POINTCUT_ORDER          = "200";
	public static final String TRANSACTION_ADVISOR_ORDER                      = "300";

	// matrix config zk path
	public static final String RESOURCE_RDBMS_MATRIX_PREFIX = "/resource/RDBMS/matrix";

	// spring.profiles.active
	public static final String PROFILES_ACTIVE_TEST       = "test";
	public static final String PROFILES_ACTIVE_PRODUCTION = "production";
}
