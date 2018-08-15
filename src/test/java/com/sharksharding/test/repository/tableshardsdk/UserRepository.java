package com.sharksharding.test.repository.tableshardsdk;

import com.sharksharding.common.annotation.TableSharding;
import com.sharksharding.test.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Repository
public interface UserRepository {

	// 使用TableSharding时，方法参数必须需要@Param 注解
	@TableSharding(shardingKey = "user_sharding_key", strategy = "T(com.sharksharding.test.strategy.UserShardingStrategy).strategy(#params.createTime)")
	void insert(@Param("params") UserEntity params);

	@TableSharding(shardingKey = "user_sharding_key", strategy = "T(com.sharksharding.test.strategy.UserShardingStrategy).strategyByUserId(#userId)")
	UserEntity selectByUserId(@Param("userId") String userId);

	@TableSharding(shardingKey = "user_sharding_key", strategy = "T(com.sharksharding.test.strategy.UserShardingStrategy).strategyByUserId(#userId)")
	Map selectByJoin(@Param("userId") String userId);
}
