package com.sharksharding.datasource.interceptor;

import com.sharksharding.common.Utils;
import com.sharksharding.common.annotation.TableSharding;
import com.sharksharding.common.holder.TableShardingDataSourceHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>分表拦截器</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/14
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class TableShardingDataSourceInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {
	private static Logger LOGGER = LoggerFactory.getLogger(TableShardingDataSourceInterceptor.class);

	private ApplicationContext applicationContext;
	private ParameterNameDiscoverer paraNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String debugInfo = "[" + invocation.toString() + "]";
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("enter into table sharding data source interceptor {}", debugInfo);

		// 获取实际执行的方法
		Method        realMethod = invocation.getMethod();
		TableSharding tsAnno     = realMethod.getAnnotation(TableSharding.class);

		// 分表策略处理
		if (tsAnno != null) {
			// 转换参数列表 因为 mybatis 是基于接口 aop，参数名称默认获取到的是 args0 形式
			// 分表插件因此需要作用在 DAO 层
			List<String>   paramNameList         = new ArrayList<String>();
			Annotation[][] annotationDyadicArray = realMethod.getParameterAnnotations();
			if (ArrayUtils.isNotEmpty(annotationDyadicArray)) {
				for (Annotation[] annotations : annotationDyadicArray) {
					if (ArrayUtils.isNotEmpty(annotations)) {
						for (Annotation anno : annotations) {
							if (anno instanceof Param) {
								paramNameList.add(((Param) anno).value());
								break;
							}

							if (anno instanceof org.apache.ibatis.annotations.Param) {
								paramNameList.add(((org.apache.ibatis.annotations.Param) anno).value());
								break;
							}
						}
					}
				}
			} else {
				LOGGER.debug("parameter annotations is empty {}", debugInfo);
			}

			Object[] args      = invocation.getArguments();
			String[] paraNames = new String[paramNameList.size()];
			// 分表策略表达式
			String strategy = tsAnno.strategy();
			// 分表 Key
			String shardingKey = tsAnno.shardingKey();
			// 根据分表策略表达式计算 shardingKey 的值
			Object shardingObj = Utils.getSpelValue(args, paramNameList.toArray(paraNames), strategy, applicationContext);
			if (shardingObj == null) {
				throw new IllegalArgumentException("table sharding strategy value is null");
			}

			String shardingValue = shardingObj.toString();
			TableShardingDataSourceHolder.putTableShardingKey(shardingKey, shardingValue);
		}

		// 执行逻辑
		try {
			return invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			TableShardingDataSourceHolder.removeTableShardingKey();
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
