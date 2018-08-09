package com.sharksharding.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>数据源配置</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MatrixPoolConfigMetaModel {

	private Map<String, String> properties = new LinkedHashMap<>();

	public void addProperty(String key, String value) {
		this.properties.put(key, value);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

}
