package com.sharksharding.spring;

import com.alibaba.druid.pool.DruidDataSource;
import com.sharksharding.common.Constants;
import com.sharksharding.datasource.ReadWriteDataSource;
import com.sharksharding.model.MatrixAtomModel;
import com.sharksharding.model.MatrixDataSourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MatrixDatasourceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser implements Constants {
	private static final Logger LOGGER = LoggerFactory.getLogger(MatrixDatasourceBeanDefinitionParser.class);

	private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

	@Override
	protected Class<?> getBeanClass(Element element) {
		return ReadWriteDataSource.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		// 获取读写分离数据源 ID
		String matrixName = element.getAttribute(XSD_ID);
		LOGGER.info(">>>> init matrix matrixName=[{}] <<<<", matrixName);

		// 获取连接配置对象
		MatrixDataSourceModel matrixDataSourceModel = getMatrixDataSourceModel(matrixName);
		boolean               initMasterDataSource  = false;
		List<Object>          slaveDataSources      = new ArrayList<>();
		for (MatrixAtomModel matrixAtomModel : matrixDataSourceModel.getGroups()) {
			// 初始化连接池
			String driverClassName = "com.mysql.jdbc.Driver";
			String url             = "jdbc:mysql://" + matrixAtomModel.getHost() + ":" + matrixAtomModel.getPort() + "/" + matrixAtomModel.getDbName() + "?" + matrixAtomModel.getParam();
			String initMethod      = "init";
			String destoryMethod   = "close";
			String username        = matrixAtomModel.getUsername();
			String password        = matrixAtomModel.getPassword();

			Map<String, String> params = new HashMap<>();
			params.put("driverClassName", driverClassName);
			params.put("url", url);
			params.put("username", username);
			params.put("password", password);
			RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(DruidDataSource.class);
			rootBeanDefinition.getPropertyValues().addPropertyValues(params);
			rootBeanDefinition.setInitMethodName(initMethod);
			rootBeanDefinition.setDestroyMethodName(destoryMethod);

			// 判断 Master 数据源是否已经初始化
			if (matrixAtomModel.getIsMaster()) {
				if (initMasterDataSource)
					throw new IllegalArgumentException("must only one master datasource!");

				String masterDataSourceName = matrixName + "-masterDataSource";
				parserContext.getRegistry().registerBeanDefinition(masterDataSourceName, rootBeanDefinition);

				// 将 Master 数据源注册
				builder.addPropertyReference("masterDataSource", masterDataSourceName);
				initMasterDataSource = true;
			} else {
				// 创建 Slave 数据源
				String slaveDataSourceName = matrixName + "-slaveDataSource" + ATOMIC_INTEGER.incrementAndGet();
				parserContext.getRegistry().registerBeanDefinition(slaveDataSourceName, rootBeanDefinition);

				slaveDataSources.add(new RuntimeBeanReference(slaveDataSourceName));
			}
		}

		// 将从库设置到数据源中
		if (!initMasterDataSource) {
			throw new IllegalArgumentException("must init master datasource");
		}
		builder.addPropertyValue("slaveDataSources", slaveDataSources);
	}


	//.............//

	/**
	 * 获取连接配置对象
	 *
	 * @param matrixName
	 * @return
	 */
	private MatrixDataSourceModel getMatrixDataSourceModel(String matrixName) {
		MatrixAtomModel matrixAtomModel = new MatrixAtomModel();
		matrixAtomModel.setAtomName("test_master_01");
		matrixAtomModel.setDbName("test_master_01");
		matrixAtomModel.setHost("127.0.0.1");
		matrixAtomModel.setIsMaster(true);
		matrixAtomModel.setPort(3306);
		matrixAtomModel.setUsername("root");
		matrixAtomModel.setPassword("root");
		matrixAtomModel.setParam("zeroDateTimeBehavior=convertToNull");

		MatrixAtomModel matrixAtomModel1 = new MatrixAtomModel();
		matrixAtomModel1.setAtomName("test_slave_01");
		matrixAtomModel1.setDbName("test_slave_01");
		matrixAtomModel1.setHost("127.0.0.1");
		matrixAtomModel1.setIsMaster(false);
		matrixAtomModel1.setPort(3306);
		matrixAtomModel1.setUsername("root");
		matrixAtomModel1.setPassword("root");
		matrixAtomModel1.setParam("zeroDateTimeBehavior=convertToNull");

		MatrixDataSourceModel model = new MatrixDataSourceModel();
		model.setGroups(Arrays.asList(matrixAtomModel, matrixAtomModel1));
		model.setMatrixName("TestSDK");

		return model;
	}
}
