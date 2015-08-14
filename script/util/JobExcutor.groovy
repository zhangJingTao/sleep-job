package util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class JobExcutor {

  ExecutorService executorService;
  Semaphore semaphore;

  /**
   * 初始化线程池
   * @param poolSize 线程数
   * @param queueSize 队列大小
   */
  public JobExcutor(int poolSize, int queueSize) {
    executorService = Executors.newFixedThreadPool(poolSize);
    semaphore = new Semaphore(queueSize + poolSize);
  }

  /**
   * 用线程池中的线程执行一个闭包，如果线程和队列占满，block调用线程直到池中有空余线程
   * @param closure 要执行的闭包
   * @throws Exception
   */
  def run(closure) throws Exception {
    semaphore.acquire();
    try {
      executorService.execute(new Runnable() {
        public void run() {
          try {
            closure();
          } finally {
            semaphore.release();
          }
        }
      });
    } catch (Exception e) {
      semaphore.release();
      throw e;
    }
  }

  /**
   * wait for all task finished
   * @return
   */
  def await() {
    executorService.shutdown()
    executorService.awaitTermination(10, TimeUnit.MINUTES)
  }
}
