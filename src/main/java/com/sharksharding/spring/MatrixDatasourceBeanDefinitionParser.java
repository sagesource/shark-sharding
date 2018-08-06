package com.sharksharding.spring;

import com.alibaba.druid.pool.DruidDataSource;
import com.sharksharding.common.Constants;
import com.sharksharding.common.DataSourceUtils;
import com.sharksharding.datasource.MasterSlaveDataSource;
import com.sharksharding.model.MatrixAtomModel;
import com.sharksharding.model.MatrixDataSourceMetaModel;
import com.sharksharding.model.MatrixDataSourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MatrixDatasourceBeanDefinitionParser implements BeanDefinitionParser, Constants {
	private static final Logger LOGGER = LoggerFactory.getLogger(MatrixDatasourceBeanDefinitionParser.class);

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// 解析标签元素信息
		MatrixDataSourceMetaModel matrixDataSourceMetaModel = parseMatrixDataSourceMetaModel(element);
		// 获取连接信息
		List<MatrixDataSourceModel> matrixDataSourceModelList = getMatrixDataSourceModel(matrixDataSourceMetaModel);

		// 增加annotation的creator
		AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);

		// 注册数据源
		registerDsBeanDefinition(matrixDataSourceMetaModel, matrixDataSourceModelList, parserContext);

		// 注册拦截器
		// 注册事务管理器

		return null;
	}


	//.............//

	/**
	 * 转换解析数据源标签数据
	 *
	 * @param element
	 * @return
	 */
	private MatrixDataSourceMetaModel parseMatrixDataSourceMetaModel(Element element) {
		MatrixDataSourceMetaModel matrixDataSourceMetaModel = new MatrixDataSourceMetaModel();
		matrixDataSourceMetaModel.setId(element.getAttribute(XSD_ID));
		matrixDataSourceMetaModel.setTransactionManager(element.getAttribute(XSD_TRANSACTION_MANAGER));
		return matrixDataSourceMetaModel;
	}

	/**
	 * 注册数据源 Bean
	 */
	private void registerDsBeanDefinition(MatrixDataSourceMetaModel matrixDataSourceMetaModel, List<MatrixDataSourceModel> matrixDataSourceModel, ParserContext parserContext) {
		// 存放 MASTER / SLAVE 数据源
		ManagedMap<String, RuntimeBeanReference> masterDataSourceMapper = new ManagedMap<>();
		ManagedMap<String, RuntimeBeanReference> slaveDataSourceMapper  = new ManagedMap<>();

		// 创建原子数据源
		String matrixName = matrixDataSourceMetaModel.getId();
		for (MatrixDataSourceModel dataSourceModel : matrixDataSourceModel) {
			for (MatrixAtomModel matrixAtomModel : dataSourceModel.getAtoms()) {
				String url = DataSourceUtils.builtUrl(matrixAtomModel);

				// 连接池配置
				RootBeanDefinition  dsBeanDefinition = new RootBeanDefinition(DruidDataSource.class);
				Map<String, String> dsPoolParams     = new LinkedHashMap<>();
				dsPoolParams.put("driverClassName", DEFAULT_DB_DRIVER);
				dsPoolParams.put("url", url);
				dsPoolParams.put("username", matrixAtomModel.getUsername());
				dsPoolParams.put("password", matrixAtomModel.getPassword());
				dsBeanDefinition.getPropertyValues().addPropertyValues(dsPoolParams);
				dsBeanDefinition.setInitMethodName(DEFAULT_INIT_METHOD);
				dsBeanDefinition.setDestroyMethodName(DEFAULT_DESTORY_METHOD);

				// 数据源名称
				String dsName = DataSourceUtils.buildDsName(matrixName, dataSourceModel, matrixAtomModel);
				parserContext.getRegistry().registerBeanDefinition(dsName, dsBeanDefinition);

				if (matrixAtomModel.getIsMaster()) {
					masterDataSourceMapper.put(dsName, new RuntimeBeanReference(dsName));
				} else {
					slaveDataSourceMapper.put(dsName, new RuntimeBeanReference(dsName));
				}

				LOGGER.info(">>>>> matrixName:{}, url:{}, dsName:{} <<<<<", matrixName, url, dsName);
			}
		}

		// 创建数据源
		if (matrixDataSourceModel.size() == 1) {
			// 只有一个 group 的情况为单库读写分离的场景，创建读写分离数据源
			registerReadWriteDs(matrixName, parserContext, masterDataSourceMapper, slaveDataSourceMapper);
		} else {
			// TODO
		}
	}

	/**
	 * 创建读写分离数据源
	 *
	 * @param masterDataSourceMapper
	 * @param slaveDataSourceMapper
	 */
	private void registerReadWriteDs(String matrixName, ParserContext parserContext,
	                                 ManagedMap<String, RuntimeBeanReference> masterDataSourceMapper,
	                                 ManagedMap<String, RuntimeBeanReference> slaveDataSourceMapper) {
		if (masterDataSourceMapper == null || masterDataSourceMapper.size() != 1) {
			throw new IllegalArgumentException("read/write datasource must only one master data source");
		}

		RootBeanDefinition dsBeanDefinition = new RootBeanDefinition(MasterSlaveDataSource.class);
		dsBeanDefinition.getPropertyValues().add(MASTER_DATASOURCE_MAPPER, masterDataSourceMapper);
		dsBeanDefinition.getPropertyValues().add(SLAVE_DATASOURCE_MAPPER, slaveDataSourceMapper);
		parserContext.getRegistry().registerBeanDefinition(matrixName, dsBeanDefinition);
	}

	/**
	 * 获取连接配置对象
	 *
	 * @param matrixDataSourceMetaMode
	 * @return
	 */
	private List<MatrixDataSourceModel> getMatrixDataSourceModel(MatrixDataSourceMetaModel matrixDataSourceMetaMode) {
		MatrixDataSourceModel model = new MatrixDataSourceModel();

		MatrixAtomModel model_1 = new MatrixAtomModel();
		model_1.setHost("127.0.0.1");
		model_1.setPort("3306");
		model_1.setDbName("test_master_01");
		model_1.setUsername("root");
		model_1.setPassword("root");
		model_1.setParams("zeroDateTimeBehavior=convertToNull");
		model_1.setIsMaster(true);

		MatrixAtomModel model_2 = new MatrixAtomModel();
		model_2.setHost("127.0.0.1");
		model_2.setPort("3306");
		model_2.setDbName("test_slave_01");
		model_2.setUsername("root");
		model_2.setPassword("root");
		model_2.setParams("zeroDateTimeBehavior=convertToNull");
		model_2.setIsMaster(false);

		model.setGroupName("rwds");
		model.setLoadBalance("random");
		model.setAtoms(Arrays.asList(model_1, model_2));
		return Arrays.asList(model);
	}
}
