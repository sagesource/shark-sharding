package com.sharksharding.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <p> Namespaces 处理</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class JdbcMatrixNamespaceHandlerSupport extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("matrix-datasource", new MatrixDatasourceBeanDefinitionParser());
	}
}
