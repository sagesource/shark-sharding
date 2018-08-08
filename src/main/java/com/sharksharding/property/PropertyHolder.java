package com.sharksharding.property;

import com.sharksharding.common.Constants;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Properties;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/8
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class PropertyHolder {

	private static Properties properties = new Properties();
	// 系统和环境变量
	private static Properties sysProps   = new Properties();
	private static PropertyPlaceholderHelper propertyPlaceholderHelper;

	static {
		sysProps.putAll(System.getenv());
		sysProps.putAll(System.getProperties());

		propertyPlaceholderHelper = new PropertyPlaceholderHelper(
				PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX,
				PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX,
				PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR, true);
	}

	public static synchronized void putAll(Properties valueProperties) {
		if (valueProperties != null) {
			Properties tmpProperties = new Properties();
			for (Map.Entry<Object, Object> valuetEntry : valueProperties.entrySet()) {
				String value = (String) valuetEntry.getValue();
				// 使用本地环境变量值替换 ${}
				if (StringUtils.hasText(value) && value.contains(Constants.ARTIFACT_ALL_PREFIX)) {
					value = propertyPlaceholderHelper.replacePlaceholders(value, sysProps);
				}
				tmpProperties.put(valuetEntry.getKey(), value);
			}

			properties.putAll(tmpProperties);
		}
	}

	public static Properties getProperties() {
		return properties;
	}

	public static String getProperty(String property) {
		return properties.getProperty(property);
	}
}
