package com.sharksharding.datasource.interceptor;

import com.sharksharding.common.Utils;
import com.sharksharding.common.holder.RepositoryShardingDataSourceHolder;
import com.sharksharding.common.annotation.RepositorySharding;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/8
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class RepositoryShardingDataSourceInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {
	private static Logger LOGGER = LoggerFactory.getLogger(RepositoryShardingDataSourceInterceptor.class);

	private ApplicationContext applicationContext;
	private ParameterNameDiscoverer paraNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String debugInfo = "[" + invocation.toString() + "]";
		LOGGER.debug("enter into repository sharding data source interceptor {}", debugInfo);

		// 获取实际执行的方法
		Method             realMethod = MethodHelper.getRealMethod(invocation);
		RepositorySharding rsAnno     = realMethod.getAnnotation(RepositorySharding.class);

		// 分库策略准备
		if (rsAnno != null) {
			Object[] args      = invocation.getArguments();
			String[] paraNames = paraNameDiscoverer.getParameterNames(realMethod);

			// 分库策略表达式
			String strategy = rsAnno.strategy();
			// 根据分库策略表达式，计算 shardingKey
			Object shardingObj = Utils.getSpelValue(args, paraNames, strategy, applicationContext);
			if (shardingObj == null) {
				throw new IllegalArgumentException("repository sharding strategy value is null");
			}

			String shardingKey = shardingObj.toString();
			RepositoryShardingDataSourceHolder.putRepoShardingKey(shardingKey);
		}

		// 执行逻辑
		try {
			return invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			RepositoryShardingDataSourceHolder.removeRepoShardingKey();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
