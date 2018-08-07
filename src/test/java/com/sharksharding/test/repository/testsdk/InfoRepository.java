package com.sharksharding.test.repository.testsdk;

import com.sharksharding.test.entity.InfoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoRepository {

	int insert(InfoEntity params);

	InfoEntity selectById(Long id);
}
