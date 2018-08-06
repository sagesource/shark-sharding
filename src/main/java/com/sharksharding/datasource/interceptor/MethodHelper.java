package com.sharksharding.datasource.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

public class MethodHelper {

	public static Method getRealMethod(MethodInvocation invocation) {
		Class<?> targetClass  = AopUtils.getTargetClass(invocation.getThis()) ;
		Method specificMethod = ClassUtils.getMostSpecificMethod( invocation.getMethod(), targetClass);
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		return specificMethod;
	}

}
