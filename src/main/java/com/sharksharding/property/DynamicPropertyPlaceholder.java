package com.sharksharding.property;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * <p>当Spring的配置文件中存在${}引入配置文件值的时候，才会起效</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/8
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class DynamicPropertyPlaceholder extends PropertyPlaceholderConfigurer {

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		if (PropertyHolder.getProperties() != null)
			props.putAll(PropertyHolder.getProperties());

		super.processProperties(beanFactoryToProcess, props);
	}

	@Override
	public void setLocations(Resource... locations) {
		super.setLocations(locations);
	}

	@Override
	public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
		super.setIgnoreResourceNotFound(ignoreResourceNotFound);
	}
}
