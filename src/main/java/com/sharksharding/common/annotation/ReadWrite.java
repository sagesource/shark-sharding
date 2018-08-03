package com.sharksharding.common.annotation;

import com.sharksharding.enums.ReadWriteType;

/**
 * <p> 读写分离注解 </p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public @interface ReadWrite {

	/**
	 * 读写分离类型
	 *
	 * @return
	 */
	ReadWriteType type() default ReadWriteType.MASTER;

}
