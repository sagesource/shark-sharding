package com.sharksharding.test.repository.testsdk;

import com.sharksharding.test.entity.LogEntity;
import org.springframework.stereotype.Repository;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/1
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Repository
public interface LogRepository {

	int insert(LogEntity params);

	LogEntity selectById(Long id);

}
