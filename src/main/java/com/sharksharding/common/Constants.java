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
	String ARTIFACT_ALL_PREFIX = "${";
	String ARTIFACT_ALL_SUFFIX = "}";
	String DEFAULT_CHARSET     = "utf-8";
	String NUM_ZERO            = "0";

	// xsd element attribute
	String XSD_ID                         = "id";
	String XSD_TRANSACTION_MANAGER        = "transactionManager";
	String XSD_MYBATIS_SQLSESSION_FACTORY = "myBatisSqlSessionFactory";
	String XSD_MATRIX_ORDER               = "order";
	String XSD_IGNORE_RESOURCE_NOT_FOUND  = "ignore-resource-not-found";
	String XSD_MATRIX_POOL_CONFIGS        = "pool-configs";
	String XSD_MATRIX_POOL_CONFIG         = "pool-config";
	String XSD_MATRIX_ATOM_NAMES          = "atom-names";
	String XSD_NAME                       = "name";
	String XSD_VALUE                      = "value";
	String XSD_MATRIX_REPOSITORY_SHARDING = "repository-sharding";
	String XSD_MATRIX_TABLE_SHARDING      = "table-sharding";
	String XSD_MATRIX_POINTCUT_EXPRESSION = "pointcut-expression";

	// default value
	String DEFAULT_TRANSACTION_MANAGER_NAME        = "transactionManager";
	String DEFAULT_MYBATIS_SQLSESSION_FACTORY_NAME = "myBatisSqlSessionFactory";
	String DEFAULT_DB_DRIVER                       = "com.mysql.jdbc.Driver";
	String DEFAULT_INIT_METHOD                     = "init";
	String DEFAULT_DESTORY_METHOD                  = "close";

	// field name
	String MASTER_DATASOURCE_MAPPER       = "masterDataSourceMapper";
	String SLAVE_DATASOURCE_MAPPER        = "slaveDataSourceMapper";
	String MASTER_SLAVE_DATASOURCE_MAPPER = "masterSlaveDataSourceMapper";
	String MARTIX_NAME                    = "matrixName";
	String PLUGINS                        = "plugins";

	// spring bean name
	String ANNOTATION_MASTER_SLAVE_DATASOURCE_INTERCEPTOR = "annotationReadWriteDataSourceInterceptor";
	String REPOSITORY_SHARDING_DATASOURCE_INTERCEPTOR     = "repositoryShardingDataSourceInterceptor";
	String TABLE_SHARDING_DATASOURCE_INTERCEPTOR          = "tableShardingDataSourceInterceptor";
	String SHARDING_PLUGIN                                = "shardingPlugin";

	// spring aop config
	String AOP_NAMESPACE_URI                            = "http://www.springframework.org/schema/aop";
	String ANNOTATION_MASTER_SLAVE_DATA_SOURCE_POINTCUT = "annotationMasterSlaveDataSourcePointcut";
	String REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT     = "repositoryShardingDataSourcePointcut";
	String TABLE_SHARDING_DATA_SOURCE_POINTCUT          = "tableShardingDataSourcePointcut";
	String MASTERSLAVE_POINTCUT_EXPRESSION              = "@annotation(com.sharksharding.common.annotation.MasterSlave)";
	String EXPRESSION                                   = "expression";
	String ADVICE_REF                                   = "advice-ref";
	String POINTCUT_REF                                 = "pointcut-ref";
	String ADVISOR                                      = "advisor";
	String CONFIG                                       = "config";
	String POINTCUT                                     = "pointcut";

	// inteceptor order
	String TABLE_SHARDING_DATA_SOURCE_POINTCUT_ORDER      = "200";
	String REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT_ORDER = "200";
	String ANNOTATION_MASTERSLAVE_POINTCUT_ORDER          = "100";
	String TRANSACTION_ADVISOR_ORDER                      = "300";

	// matrix config zk path
	String RESOURCE_RDBMS_MATRIX_PREFIX = "/resource/RDBMS/matrix";

	// spring.profiles.active
	String PROFILES_ACTIVE_TEST       = "test";
	String PROFILES_ACTIVE_PRODUCTION = "production";
}
