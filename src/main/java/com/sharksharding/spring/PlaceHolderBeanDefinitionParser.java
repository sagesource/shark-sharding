package com.sharksharding.spring;

import com.sharksharding.common.Constants;
import com.sharksharding.property.DynamicPropertyPlaceholder;
import com.sharksharding.property.PropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * <p>place-holder 标签解析</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/8
 *     email       job.xueqi@outllok.com
 * </pre>
 */
public class PlaceHolderBeanDefinitionParser implements BeanDefinitionParser, Constants {
	private static Logger LOGGER = LoggerFactory.getLogger(PlaceHolderBeanDefinitionParser.class);

	// 全局配置文件路径，与环境无关
	private static final String GLOBAL_PROPERTIES   = "classpath*:properties/application.properties";
	private static final String UNITTEST_PROPERTIES = "classpath*:properties/application-test.properties";
	private static final String PROFILE_PROPERTIES  = "classpath*:properties-{0}/application-{1}.properties";

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		try {
			// 根据 spring.profiles.active 获取配置文件
			String activeProfile = parserContext.getReaderContext().getEnvironment().getActiveProfiles()[0];
			if (activeProfile == null) {
				activeProfile = PROFILES_ACTIVE_PRODUCTION;
			} else {
				activeProfile = activeProfile.toLowerCase();
			}

			// 获取 properties 的 Resources
			PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
			Resource[]                          propertiesFileArray;
			if (PROFILES_ACTIVE_TEST.equals(activeProfile)) {
				// 单元测试特殊处理
				propertiesFileArray = new Resource[]{pathMatchingResourcePatternResolver.getResources(UNITTEST_PROPERTIES)[0]};
			} else {
				Resource globalResource  = pathMatchingResourcePatternResolver.getResources(GLOBAL_PROPERTIES)[0];
				Resource profileResource = pathMatchingResourcePatternResolver.getResources(MessageFormat.format(PROFILE_PROPERTIES, activeProfile, activeProfile))[0];
				propertiesFileArray = new Resource[]{globalResource, profileResource};
			}

			// 解析 properties 文件，并保存 Properties 对象
			for (Resource resource : propertiesFileArray) {
				Properties  properties  = new Properties();
				InputStream inputStream = resource.getInputStream();
				properties.load(inputStream);
				inputStream.close();
				PropertyHolder.putAll(properties);
			}

			// 注册 DynamicPropertyPlaceholder，实现 Spring 对 xml 文件的${}支持
			boolean            ignoreResourceNotFound    = Boolean.parseBoolean(element.getAttribute(XSD_IGNORE_RESOURCE_NOT_FOUND));
			RootBeanDefinition placeHolderBeanDefinition = new RootBeanDefinition(DynamicPropertyPlaceholder.class);
			placeHolderBeanDefinition.getPropertyValues().add("ignoreResourceNotFound", ignoreResourceNotFound);
			placeHolderBeanDefinition.getPropertyValues().add("locations", propertiesFileArray);
			parserContext.getRegistry().registerBeanDefinition("propertyConfigurer", placeHolderBeanDefinition);
		} catch (IOException e) {
			LOGGER.error("parse place-holder is error!", e);
		}
		return null;
	}
}
