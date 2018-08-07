package com.sharksharding.common.annotation;

import com.sharksharding.enums.MasterSlaveType;

import java.lang.annotation.*;

/**
 * <p> 读写分离注解 </p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MasterSlave {

	/**
	 * 读写分离类型
	 *
	 * @return
	 */
	MasterSlaveType type() default MasterSlaveType.MASTER;

}
