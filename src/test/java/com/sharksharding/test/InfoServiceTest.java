package com.sharksharding.test;

import com.sharksharding.test.service.InfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CyclicBarrier;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/1
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class InfoServiceTest extends BaseTest {

	@Autowired
	private InfoService infoService;

	@Test
	public void saveInfoNoTransaction_test() {
		infoService.saveInfoNoTransaction();
	}

	@Test
	public void saveInfoDefault_test() {
		infoService.saveInfoDefault();
	}

	@Test
	public void saveInfoByReadOnly_test() {
		infoService.saveInfoByReadOnly();
	}

	@Test
	public void findNoTransactionNonMS_test() {
		infoService.findNoTransactionNonMS();
	}

	@Test
	public void findNoTransactionByMaster_test() {
		infoService.findNoTransactionByMaster();
	}

	@Test
	public void findNoTransactionBySlave_test() {
		infoService.findNoTransactionBySlave();
	}

	@Test
	public void findByTransactionNonMS_test() {
		infoService.findByTransactionNonMS();
	}

	@Test
	public void findByTransactionByMaster_test() {
		infoService.findByTransactionByMaster();
	}

	@Test
	public void findByTransactionBySlave_test() {
		infoService.findByTransactionBySlave();
	}

	@Test
	public void saveMultiNoTransaction_test() {
		infoService.saveMultiNoTransaction();
	}

	@Test
	public void saveMultiDefault_test() {
		infoService.saveMultiDefault();
	}

	@Test
	public void flowBussines() {
		infoService.saveInfoDefault();
		infoService.findNoTransactionBySlave();
	}

	private static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

	@Test
	public void threadFlowBusiness() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.currentThread().setName("THREAD-saveInfoDefault");
					cyclicBarrier.await();
					infoService.saveInfoDefault();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.currentThread().setName("THREAD-findNoTransactionBySlave");
					cyclicBarrier.await();
					infoService.findNoTransactionBySlave();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.currentThread().setName("THREAD-flowBussines");
					cyclicBarrier.await();
					infoService.saveInfoDefault();
					infoService.findNoTransactionBySlave();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		while (true) ;
	}

	/*
	 * 多线程并发场景，多个线程分别读写
	 * 只读事务+查询+更新
	 */
}


