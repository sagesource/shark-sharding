package com.sharksharding.mybatis;

import com.sharksharding.common.Utils;
import com.sharksharding.common.holder.TableShardingDataSourceHolder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>分表 SQL 转换</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/15
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class TableShardingSqlConvert {
	private static Logger LOGGER = LoggerFactory.getLogger(TableShardingSqlConvert.class);

	/**
	 * 转换 SQL
	 *
	 * @param sql
	 * @param statementHandler
	 * @return
	 */
	public static String convert(String sql, StatementHandler statementHandler) {
		String name = getMatchTableName(sql);
		if (StringUtils.isEmpty(name)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("table sharding name is blank, not replace sql");
			}
			return Utils.trimSql(sql);
		}

		Map<String, String> shardingMapper = TableShardingDataSourceHolder.getTableShardingKey();
		if (shardingMapper == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("table sharding strategy is null, not replace sql");
			}
			return Utils.trimSql(sql);
		}

		// 替换 SQL
		String shardingSql = sql;
		for (Map.Entry<String, String> valueEntry : shardingMapper.entrySet()) {
			shardingSql = shardingSql.replaceAll(getRegexTableName(valueEntry.getKey()), valueEntry.getValue());
		}
		return Utils.trimSql(shardingSql);
	}

	public static String getRegexTableName(String tableName) {
		Assert.hasText(tableName);
		return "\\$\\[" + tableName + "\\]\\$";
	}

	public static String getMatchTableName(String sql) {
		Pattern pattern = Pattern.compile("\\$\\[.*?\\]\\$");
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			String name = matcher.group();
			return name.substring(2, name.length() - 2);
		}
		return null;
	}
}
