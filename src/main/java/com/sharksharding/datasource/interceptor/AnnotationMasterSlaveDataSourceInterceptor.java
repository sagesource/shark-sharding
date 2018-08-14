package com.sharksharding.datasource.interceptor;

import com.sharksharding.common.annotation.MasterSlave;
import com.sharksharding.common.holder.MasterSlaveDataSourceHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * <p>注解主从数据源拦截器</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class AnnotationMasterSlaveDataSourceInterceptor implements MethodInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationMasterSlaveDataSourceInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String debugInfo = "[" + invocation.toString() + "]";
		LOGGER.debug("enter into master/slave data source interceptor {}", debugInfo);

		// 获取实际执行的方法
		Method realMethod = MethodHelper.getRealMethod(invocation);

		// 主从逻辑
		MasterSlave masterSlaveAnno = realMethod.getAnnotation(MasterSlave.class);
		if (masterSlaveAnno != null) {
			MasterSlaveDataSourceHolder.putDataSource(masterSlaveAnno.type());
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("method:{}, choise master / slave :{}", realMethod, masterSlaveAnno.type());
		}

		// 执行逻辑
		try {
			return invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			MasterSlaveDataSourceHolder.clearDataSource();
		}
	}
}
