package com.sharksharding.spring;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.sharksharding.common.Constants;
import com.sharksharding.common.DataSourceUtils;
import com.sharksharding.datasource.MasterSlaveDataSource;
import com.sharksharding.datasource.interceptor.AnnotationMasterSlaveDataSourceInterceptor;
import com.sharksharding.model.MatrixAtomModel;
import com.sharksharding.model.MatrixDataSourceMetaModel;
import com.sharksharding.model.MatrixDataSourceModel;
import com.sharksharding.property.PropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.config.TransactionManagementConfigUtils;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
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
		Document appDoc = element.getOwnerDocument();

		// 解析标签元素信息
		MatrixDataSourceMetaModel matrixDataSourceMetaModel = parseMatrixDataSourceMetaModel(element);
		// 获取连接信息
		List<MatrixDataSourceModel> matrixDataSourceModelList = getMatrixDataSourceModel(matrixDataSourceMetaModel);

		// 增加annotation的creator
		AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);

		// 注册数据源
		this.registerDsBeanDefinition(matrixDataSourceMetaModel, matrixDataSourceModelList, parserContext);

		// 注册分库拦截器
		// 注册读写分离拦截器
		this.registerMasterSlaveInterceptor(matrixDataSourceMetaModel, parserContext, appDoc);
		// 注册分表拦截器
		// 注册事务管理器
		this.registerTransaction(matrixDataSourceMetaModel, parserContext);

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

		String matrixName = matrixDataSourceMetaModel.getId();
		// 创建原子数据源
		Map<String, List<String>> masterSlaveDataSourceMapper = new LinkedHashMap<>();
		for (MatrixDataSourceModel dataSourceModel : matrixDataSourceModel) {
			// 从库数据源索引位置，为支持一主多从场景
			int          slaveDbIndex    = 0;
			String       msterDsName     = "";
			List<String> slaveDsNameList = new ArrayList<>();
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
					msterDsName = dsName;
				} else {
					String slaveDsName = dsName + (slaveDbIndex++);
					slaveDataSourceMapper.put(slaveDsName, new RuntimeBeanReference(dsName));
					slaveDsNameList.add(slaveDsName);
				}

				LOGGER.info(">>>>> matrixName:{}, url:{}, dsName:{} <<<<<", matrixName, url, dsName);
			}

			// 设置主库数据源名称与对应从库列表的映射
			masterSlaveDataSourceMapper.put(msterDsName, slaveDsNameList);
		}

		// 创建数据源
		if (matrixDataSourceModel.size() == 1) {
			// 只有一个 group 的情况为单库读写分离的场景，创建读写分离数据源
			registerReadWriteDs(matrixName, parserContext, masterDataSourceMapper, slaveDataSourceMapper, masterSlaveDataSourceMapper);
		} else {
			// TODO
		}
	}

	/**
	 * 创建主从数据源拦截器
	 *
	 * @param matrixDataSourceMetaModel
	 * @param parserContext
	 */
	private void registerMasterSlaveInterceptor(MatrixDataSourceMetaModel matrixDataSourceMetaModel, ParserContext parserContext, Document appDoc) {
		// 数据源名称
		String             matrixName         = matrixDataSourceMetaModel.getId();
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(AnnotationMasterSlaveDataSourceInterceptor.class);
		parserContext.getRegistry().registerBeanDefinition(DataSourceUtils.buildBeanName(matrixName, ANNOTATION_MASTER_SLAVE_DATASOURCE_INTERCEPTOR), rootBeanDefinition);
		// 创建 xml 元素
		BeanDefinitionParserDelegate beanDefDelegate = parserContext.getDelegate();
		beanDefDelegate.parseCustomElement(buildMasterSlaveAopConfigElmt(matrixDataSourceMetaModel, appDoc));
	}

	private Element buildMasterSlaveAopConfigElmt(MatrixDataSourceMetaModel matrixDataSourceMetaModel, Document appDoc) {
		// 数据源名称
		String matrixName = matrixDataSourceMetaModel.getId();

		// 组装 Spring AOP Config 标签
		Element annotationMasterSlaveAopConfigElmt = appDoc.createElementNS(AOP_NAMESPACE_URI, CONFIG);

		// 切面表达式元素
		Element annotationMasterSlavePointcutChild = appDoc.createElementNS(AOP_NAMESPACE_URI, POINTCUT);
		annotationMasterSlavePointcutChild.setAttribute(XSD_ID, DataSourceUtils.buildBeanName(matrixName, ANNOTATION_MASTER_SLAVE_DATA_SOURCE_POINTCUT));
		annotationMasterSlavePointcutChild.setAttribute(EXPRESSION, MASTERSLAVE_POINTCUT_EXPRESSION);
		annotationMasterSlaveAopConfigElmt.appendChild(annotationMasterSlavePointcutChild);

		// advisor元素
		Element annotationMasterSlaveAdvisorChild = appDoc.createElementNS(AOP_NAMESPACE_URI, ADVISOR);
		annotationMasterSlaveAdvisorChild.setAttribute(POINTCUT_REF,
				DataSourceUtils.buildBeanName(matrixName, ANNOTATION_MASTER_SLAVE_DATA_SOURCE_POINTCUT));
		annotationMasterSlaveAdvisorChild.setAttribute(ADVICE_REF,
				DataSourceUtils.buildBeanName(matrixName, ANNOTATION_MASTER_SLAVE_DATASOURCE_INTERCEPTOR));
		annotationMasterSlaveAdvisorChild.setAttribute(XSD_MATRIX_ORDER, ANNOTATION_MASTERSLAVE_POINTCUT_ORDER);
		annotationMasterSlaveAopConfigElmt.appendChild(annotationMasterSlaveAdvisorChild);
		return annotationMasterSlaveAopConfigElmt;
	}

	/**
	 * 注册事务配置
	 *
	 * @param matrixDataSourceMetaModel
	 * @param parserContext
	 */
	private void registerTransaction(MatrixDataSourceMetaModel matrixDataSourceMetaModel, ParserContext parserContext) {
		String transactionManager = matrixDataSourceMetaModel.getTransactionManager();
		if (!StringUtils.isEmpty(transactionManager)) {
			String txAdvisorBeanName = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME;
			if (!parserContext.getRegistry().containsBeanDefinition(txAdvisorBeanName)) {
				// Create the TransactionAttributeSource definition.
				RootBeanDefinition sourceDef = new RootBeanDefinition(AnnotationTransactionAttributeSource.class);
				sourceDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				parserContext.getRegistry().registerBeanDefinition("validAnnotationTransactionAttributeSource", sourceDef);

				// Create the TransactionInterceptor definition.
				RootBeanDefinition interceptorDef = new RootBeanDefinition(TransactionInterceptor.class);
				interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				interceptorDef.getPropertyValues().add("transactionManagerBeanName", transactionManager);
				interceptorDef.getPropertyValues().add("transactionAttributeSource",
						new RuntimeBeanReference("validAnnotationTransactionAttributeSource"));
				String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);

				// Create the TransactionAttributeSourceAdvisor definition.
				RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryTransactionAttributeSourceAdvisor.class);
				advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				advisorDef.getPropertyValues().add("transactionAttributeSource", new RuntimeBeanReference("validAnnotationTransactionAttributeSource"));
				advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
				advisorDef.getPropertyValues().add("order", TRANSACTION_ADVISOR_ORDER);
				parserContext.getRegistry().registerBeanDefinition(txAdvisorBeanName, advisorDef);

			}
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
	                                 ManagedMap<String, RuntimeBeanReference> slaveDataSourceMapper,
	                                 Map<String, List<String>> masterSlaveDataSourceMapper) {
		if (masterDataSourceMapper == null || masterDataSourceMapper.size() != 1) {
			throw new IllegalArgumentException("read/write datasource must only one master data source");
		}

		RootBeanDefinition dsBeanDefinition = new RootBeanDefinition(MasterSlaveDataSource.class);
		dsBeanDefinition.getPropertyValues().add(MASTER_DATASOURCE_MAPPER, masterDataSourceMapper);
		dsBeanDefinition.getPropertyValues().add(SLAVE_DATASOURCE_MAPPER, slaveDataSourceMapper);
		dsBeanDefinition.getPropertyValues().add(MASTER_SLAVE_DATASOURCE_MAPPER, masterSlaveDataSourceMapper);
		parserContext.getRegistry().registerBeanDefinition(matrixName, dsBeanDefinition);
	}

	/**
	 * 获取连接配置对象
	 *
	 * @param matrixDataSourceMetaMode
	 * @return
	 */
	private List<MatrixDataSourceModel> getMatrixDataSourceModel(MatrixDataSourceMetaModel matrixDataSourceMetaMode) {
		String configValue = PropertyHolder.getProperty(buidMatrixDataKey(matrixDataSourceMetaMode.getId()));
		return JSONArray.parseArray(configValue, MatrixDataSourceModel.class);
	}

	private static String buidMatrixDataKey(String matrixName) {
		return RESOURCE_RDBMS_MATRIX_PREFIX + "/" + matrixName;
	}
}
