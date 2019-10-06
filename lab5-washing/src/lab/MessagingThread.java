package lab;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class MessagingThread<M> extends Thread {

  private BlockingQueue<M> queue = new LinkedBlockingDeque<>();

  /**
   * Called by another thread, to send a message to this thread.
   * 
   * @throws InterruptedException
   */
  public void send(M message) {
    queue.add(message);
  }

  /** Returns the first message in the queue, or blocks if none available. */
  protected M receive() throws InterruptedException {
    return queue.take();
  }

  /**
   * Returns the first message in the queue, or blocks up to 'timeout'
   * milliseconds if none available. Returns null if no message is obtained within
   * 'timeout' milliseconds.
   * 
   * @throws InterruptedException
   */
  protected M receiveWithTimeout(long timeout) throws InterruptedException {
    return queue.poll(timeout, TimeUnit.MILLISECONDS);
  }
}