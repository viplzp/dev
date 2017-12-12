package demo.util;

import java.util.concurrent.CountDownLatch;

/**
 * 并发测试辅助类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class TimeTaskHelper {

	private TimeTaskHelper() {
	}

	/**
	 * 并发测试
	 */
	public static long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
		/**
		 * 一个启动信号，在 driver 为继续执行 worker 做好准备之前，它会阻止所有的 worker 继续执行。
		 */
		final CountDownLatch startSignal = new CountDownLatch(1);
		/**
		 * 一个完成信号，它允许 driver 在完成所有 worker 之前一直等待。
		 */
		final CountDownLatch doneSignal = new CountDownLatch(nThreads);

		for (int i = 0; i < nThreads; i++) {
			Thread t = new Thread() {
				public void run() {
					try {
						startSignal.await();/** 阻塞于此，一直到startSignal计数为0，再往下执行 */
						try {
							task.run();
						} finally {
							doneSignal.countDown();/**
													 * doneSignal 计数减一，直到最后一个线程结束
													 */
						}
					} catch (InterruptedException ignored) {
					}
				}
			};
			t.start();
		}
		long start = System.currentTimeMillis();
		startSignal.countDown();/** doneSignal 计数减一，为0，所有task开始并发执行run */
		doneSignal.await();/** 阻塞于此，一直到doneSignal计数为0，再往下执行 */
		long end = System.currentTimeMillis();
		return end - start;
	}

}
