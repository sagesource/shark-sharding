package com.sharksharding.test.repository.tableshardsdk;

import com.sharksharding.test.entity.DictEntity;
import org.springframework.stereotype.Repository;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Repository
public interface DictRepository {

	void insert(DictEntity params);

	DictEntity selectById(Long id);
}
