package com.sharksharding.spring;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.sharksharding.common.Constants;
import com.sharksharding.common.DataSourceUtils;
import com.sharksharding.datasource.MasterSlaveDataSource;
import com.sharksharding.datasource.RepositoryShardingDataSource;
import com.sharksharding.datasource.interceptor.AnnotationMasterSlaveDataSourceInterceptor;
import com.sharksharding.datasource.interceptor.RepositoryShardingDataSourceInterceptor;
import com.sharksharding.model.MatrixAtomModel;
import com.sharksharding.model.MatrixDataSourceGroupModel;
import com.sharksharding.model.MatrixDataSourceMetaModel;
import com.sharksharding.model.MatrixPoolConfigMetaModel;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.MessageFormat;
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

		// 获取连接信息
		MatrixDataSourceMetaModel matrixDataSourceMetaModel = getMatrixDataSourceModel(element);
		// 增加annotation的creator
		AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
		// 注册数据源
		this.registerDsBeanDefinition(matrixDataSourceMetaModel, parserContext);
		// 注册分库拦截器
		if (!StringUtils.isEmpty(matrixDataSourceMetaModel.getRepositoryShardingPointcut()))
			this.registerRepositoryShardingInterceptor(matrixDataSourceMetaModel, parserContext, appDoc);
		// 注册读写分离拦截器
		this.registerMasterSlaveInterceptor(matrixDataSourceMetaModel, parserContext, appDoc);
		// 注册分表拦截器
		// 注册事务管理器
		this.registerTransaction(matrixDataSourceMetaModel, parserContext);

		return null;
	}


	//.............//


	/**
	 * 注册数据源 Bean
	 */
	private void registerDsBeanDefinition(MatrixDataSourceMetaModel matrixDataSourceMetaModel, ParserContext parserContext) {
		// 存放 MASTER / SLAVE 数据源
		ManagedMap<String, RuntimeBeanReference> masterDataSourceMapper = new ManagedMap<>();
		ManagedMap<String, RuntimeBeanReference> slaveDataSourceMapper  = new ManagedMap<>();

		// 创建原子数据源
		String matrixName = matrixDataSourceMetaModel.getMatrixName();
		// 子数据源组列表
		List<MatrixDataSourceGroupModel> matrixDataSourceGroupList = matrixDataSourceMetaModel.getMatrixDataSourceGroupList();
		// Master 数据源对应的 Slave 数据源名称列表
		Map<String, List<String>> masterSlaveDataSourceMapper = new LinkedHashMap<>();
		// atom 数据源对应的数据源配置
		Map<String, MatrixPoolConfigMetaModel> atomDataSourcePoolConfig = matrixDataSourceMetaModel.getAtomDataSourcePoolConfig();
		for (MatrixDataSourceGroupModel dataSourceModel : matrixDataSourceGroupList) {
			// 从库数据源索引位置，为支持一主多从场景
			int          slaveDbIndex    = 0;
			String       msterDsName     = "";
			List<String> slaveDsNameList = new ArrayList<>();
			for (MatrixAtomModel matrixAtomModel : dataSourceModel.getAtoms()) {
				String url = DataSourceUtils.builtUrl(matrixAtomModel);

				// 连接池配置
				RootBeanDefinition  dsBeanDefinition = new RootBeanDefinition(DruidDataSource.class);
				Map<String, Object> dsPoolParams     = DataSourceUtils.buildPoolConfig(matrixAtomModel, atomDataSourcePoolConfig);
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
		if (matrixDataSourceGroupList.size() == 1) {
			// 只有一个 group 的情况为单库读写分离的场景，创建读写分离数据源
			registerReadWriteDs(matrixName, parserContext, masterDataSourceMapper, slaveDataSourceMapper, masterSlaveDataSourceMapper);
		} else {
			// 注册分库读写分离数据源
			registerRepositoryShardingDs(matrixName, parserContext, masterDataSourceMapper, slaveDataSourceMapper, masterSlaveDataSourceMapper);
		}
	}

	/**
	 * 创建分库拦截器
	 *
	 * @param matrixDataSourceMetaModel
	 * @param parserContext
	 * @param appDoc
	 */
	private void registerRepositoryShardingInterceptor(MatrixDataSourceMetaModel matrixDataSourceMetaModel, ParserContext parserContext, Document appDoc) {
		// 数据源名称
		String matrixName = matrixDataSourceMetaModel.getMatrixName();

		RootBeanDefinition repositoryShardingDataSourceInterceptor = new RootBeanDefinition(RepositoryShardingDataSourceInterceptor.class);
		parserContext.getRegistry().registerBeanDefinition(DataSourceUtils.buildBeanName(matrixName, REPOSITORY_SHARDING_DATASOURCE_INTERCEPTOR), repositoryShardingDataSourceInterceptor);

		// 创建 xml 元素
		BeanDefinitionParserDelegate beanDefDelegate = parserContext.getDelegate();
		beanDefDelegate.parseCustomElement(buildRepositoryShardingAopConfigElmt(matrixDataSourceMetaModel, appDoc));
	}

	/**
	 * build RepositorySharding aop config element
	 *
	 * @param matrixDataSourceMetaModel
	 * @param appDoc
	 * @return
	 */
	private Element buildRepositoryShardingAopConfigElmt(MatrixDataSourceMetaModel matrixDataSourceMetaModel, Document appDoc) {
		// 数据源名称
		String matrixName = matrixDataSourceMetaModel.getMatrixName();

		Element repositoryShardingAopConfigElmt = appDoc.createElementNS(AOP_NAMESPACE_URI, CONFIG);
		Element repositoryShardingPointcutChild = appDoc.createElementNS(AOP_NAMESPACE_URI, POINTCUT);
		repositoryShardingPointcutChild.setAttribute(XSD_ID,
				DataSourceUtils.buildBeanName(matrixName, REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT));
		repositoryShardingPointcutChild.setAttribute(EXPRESSION, matrixDataSourceMetaModel.getRepositoryShardingPointcut());
		repositoryShardingAopConfigElmt.appendChild(repositoryShardingPointcutChild);

		Element repositoryShardingAdvisorChild = appDoc.createElementNS(AOP_NAMESPACE_URI, ADVISOR);
		repositoryShardingAdvisorChild.setAttribute(POINTCUT_REF,
				DataSourceUtils.buildBeanName(matrixName, REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT));
		repositoryShardingAdvisorChild.setAttribute(ADVICE_REF,
				DataSourceUtils.buildBeanName(matrixName, REPOSITORY_SHARDING_DATASOURCE_INTERCEPTOR));
		repositoryShardingAdvisorChild.setAttribute(XSD_MATRIX_ORDER, REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT_ORDER);
		repositoryShardingAopConfigElmt.appendChild(repositoryShardingAdvisorChild);
		return repositoryShardingAopConfigElmt;
	}

	/**
	 * 创建主从数据源拦截器
	 *
	 * @param matrixDataSourceMetaModel
	 * @param parserContext
	 * @param appDoc
	 */
	private void registerMasterSlaveInterceptor(MatrixDataSourceMetaModel matrixDataSourceMetaModel, ParserContext parserContext, Document appDoc) {
		if (!parserContext.getRegistry().isBeanNameInUse(ANNOTATION_MASTER_SLAVE_DATASOURCE_INTERCEPTOR)) {

			RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(AnnotationMasterSlaveDataSourceInterceptor.class);
			parserContext.getRegistry().registerBeanDefinition(ANNOTATION_MASTER_SLAVE_DATASOURCE_INTERCEPTOR, rootBeanDefinition);
			// 创建 xml 元素
			BeanDefinitionParserDelegate beanDefDelegate = parserContext.getDelegate();
			beanDefDelegate.parseCustomElement(buildMasterSlaveAopConfigElmt(matrixDataSourceMetaModel, appDoc));
			
		}
	}

	private Element buildMasterSlaveAopConfigElmt(MatrixDataSourceMetaModel matrixDataSourceMetaModel, Document appDoc) {
		// 组装 Spring AOP Config 标签
		Element annotationMasterSlaveAopConfigElmt = appDoc.createElementNS(AOP_NAMESPACE_URI, CONFIG);

		// 切面表达式元素
		Element annotationMasterSlavePointcutChild = appDoc.createElementNS(AOP_NAMESPACE_URI, POINTCUT);
		annotationMasterSlavePointcutChild.setAttribute(XSD_ID, ANNOTATION_MASTER_SLAVE_DATA_SOURCE_POINTCUT);
		annotationMasterSlavePointcutChild.setAttribute(EXPRESSION, MASTERSLAVE_POINTCUT_EXPRESSION);
		annotationMasterSlaveAopConfigElmt.appendChild(annotationMasterSlavePointcutChild);

		// advisor元素
		Element annotationMasterSlaveAdvisorChild = appDoc.createElementNS(AOP_NAMESPACE_URI, ADVISOR);
		annotationMasterSlaveAdvisorChild.setAttribute(POINTCUT_REF, ANNOTATION_MASTER_SLAVE_DATA_SOURCE_POINTCUT);
		annotationMasterSlaveAdvisorChild.setAttribute(ADVICE_REF, ANNOTATION_MASTER_SLAVE_DATASOURCE_INTERCEPTOR);
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
	 * 创建分库读写数据源
	 *
	 * @param matrixName
	 * @param parserContext
	 * @param masterDataSourceMapper
	 * @param slaveDataSourceMapper
	 * @param masterSlaveDataSourceMapper
	 */
	private void registerRepositoryShardingDs(String matrixName, ParserContext parserContext,
	                                          ManagedMap<String, RuntimeBeanReference> masterDataSourceMapper,
	                                          ManagedMap<String, RuntimeBeanReference> slaveDataSourceMapper,
	                                          Map<String, List<String>> masterSlaveDataSourceMapper) {
		if (masterDataSourceMapper == null) {
			throw new IllegalArgumentException("repository sharding datasource masterDataSourceMapper must not null");
		}
		RootBeanDefinition dsBeanDefinition = new RootBeanDefinition(RepositoryShardingDataSource.class);
		dsBeanDefinition.getPropertyValues().add(MASTER_DATASOURCE_MAPPER, masterDataSourceMapper);
		dsBeanDefinition.getPropertyValues().add(SLAVE_DATASOURCE_MAPPER, slaveDataSourceMapper);
		dsBeanDefinition.getPropertyValues().add(MASTER_SLAVE_DATASOURCE_MAPPER, masterSlaveDataSourceMapper);
		dsBeanDefinition.getPropertyValues().add(MARTIX_NAME, matrixName);
		parserContext.getRegistry().registerBeanDefinition(matrixName, dsBeanDefinition);
	}

	/**
	 * 获取连接配置对象
	 *
	 * @param element
	 * @return
	 */
	private MatrixDataSourceMetaModel getMatrixDataSourceModel(Element element) {
		// 获取复合数据源名称
		String matrixName = element.getAttribute(XSD_ID);
		// 获取事务管理器名称
		String transactionManager = element.getAttribute(XSD_TRANSACTION_MANAGER);
		// 数据源 group 列表
		String                           configValue               = PropertyHolder.getProperty(buidMatrixDataKey(matrixName));
		List<MatrixDataSourceGroupModel> matrixDataSourceGroupList = JSONArray.parseArray(configValue, MatrixDataSourceGroupModel.class);

		// 解析数据源配置 atomName - pool config
		Map<String, MatrixPoolConfigMetaModel> atomDataSourcePoolConfig            = new LinkedHashMap<>();
		String                                 repositoryShrdingPointcutExpression = null;
		NodeList                               nodeList                            = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					if (ele.getTagName().endsWith(XSD_MATRIX_POOL_CONFIGS)) {
						// pool-configs 标签解析
						atomDataSourcePoolConfig = parseMatrixPoolConfig(ele);
					} else if (ele.getTagName().endsWith(XSD_MATRIX_REPOSITORY_SHARDING)) {
						if (!StringUtils.isEmpty(repositoryShrdingPointcutExpression)) {
							throw new IllegalArgumentException("repoistory sharding config exist already!");
						}
						repositoryShrdingPointcutExpression = ele.getAttribute(XSD_MATRIX_POINTCUT_EXPRESSION);
					}
				}
			}
		}

		// 数据源配置元素 Model
		MatrixDataSourceMetaModel matrixDataSourceMetaModel = new MatrixDataSourceMetaModel();
		matrixDataSourceMetaModel.setMatrixName(matrixName);
		matrixDataSourceMetaModel.setTransactionManager(transactionManager);
		matrixDataSourceMetaModel.setMatrixDataSourceGroupList(matrixDataSourceGroupList);
		matrixDataSourceMetaModel.setAtomDataSourcePoolConfig(atomDataSourcePoolConfig);
		matrixDataSourceMetaModel.setRepositoryShardingPointcut(repositoryShrdingPointcutExpression);
		return matrixDataSourceMetaModel;
	}

	/**
	 * pool-configs 标签解析
	 *
	 * @param element
	 * @return
	 */
	private Map<String, MatrixPoolConfigMetaModel> parseMatrixPoolConfig(Element element) {
		Map<String, MatrixPoolConfigMetaModel> result = new LinkedHashMap<>();

		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					if (ele.getTagName().endsWith(XSD_MATRIX_POOL_CONFIG)) {
						String                    atomNames                 = ele.getAttribute(XSD_MATRIX_ATOM_NAMES);
						MatrixPoolConfigMetaModel matrixPoolConfigMetaModel = parsePoolConfig(ele);

						String[] atomNameArray = atomNames.split(",");
						for (String atomName : atomNameArray) {
							if (result.get(atomName) != null)
								throw new IllegalArgumentException(MessageFormat.format("atomName:{0} pool config already exist.", atomName));
							result.put(atomName, matrixPoolConfigMetaModel);
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * 解析 pool-config 标签
	 *
	 * @param element
	 * @return
	 */
	private MatrixPoolConfigMetaModel parsePoolConfig(Element element) {
		MatrixPoolConfigMetaModel matrixPoolConfigMetaModel = new MatrixPoolConfigMetaModel();
		NodeList                  nodeList                  = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					matrixPoolConfigMetaModel.addProperty(ele.getAttribute(XSD_NAME), ele.getAttribute(XSD_VALUE));
				}
			}
		}

		return matrixPoolConfigMetaModel;
	}

	private static String buidMatrixDataKey(String matrixName) {
		return RESOURCE_RDBMS_MATRIX_PREFIX + "/" + matrixName;
	}
}
