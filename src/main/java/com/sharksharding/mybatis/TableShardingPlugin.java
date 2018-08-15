package com.sharksharding.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

/**
 * <p> Mybatis 分表插件 </p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/15
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TableShardingPlugin implements Interceptor {
	private static Logger LOGGER = LoggerFactory.getLogger(TableShardingPlugin.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		String debugInfo = "[" + invocation.toString() + "]";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("get into mybatis table sharding plugin {}", debugInfo);
		}

		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		String           sql              = statementHandler.getBoundSql().getSql();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("original sql : {}", sql);
		}

		// 转换 SQL
		String targetSql = TableShardingSqlConvert.convert(sql, statementHandler);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("converted sql : {}", targetSql);
		}

		if (!sql.equals(targetSql)) {
			Field field = BoundSql.class.getDeclaredField("sql");
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, statementHandler.getBoundSql(), targetSql);
		}

		try {
			return invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			LOGGER.debug("get out mybatis table sharding plugin {}", debugInfo);
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}
}
