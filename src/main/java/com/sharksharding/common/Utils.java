package com.sharksharding.common;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class Utils {

	/**
	 * 解析 Spel 表达式
	 *
	 * @param args
	 * @param paraNames
	 * @param key
	 * @param beanFactory
	 * @return
	 */
	public static Object getSpelValue(Object[] args, String[] paraNames, String key, BeanFactory beanFactory) {
		Assert.hasText(key);

		ExpressionParser          ep      = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		if (beanFactory != null) {
			context.setBeanResolver(new BeanFactoryResolver(beanFactory));
		}

		if (!ArrayUtils.isEmpty(args) && !ArrayUtils.isEmpty(paraNames)) {
			if (args.length != paraNames.length) {
				throw new IllegalArgumentException("args length must be equal to paraNames length");
			}

			for (int i = 0; i < paraNames.length; i++) {
				context.setVariable(paraNames[i], args[i]);
			}
		}

		return ep.parseExpression(key).getValue(context);
	}

	public static String trimSql(String sql) {
		Assert.hasText(sql);
		String targetSql = StringUtils.replace(sql, "\n", " ");
		targetSql = StringUtils.replace(targetSql, "\t", " ");
		targetSql = targetSql.replaceAll(" +", " ");
		return targetSql.trim();
	}
}
