package clock;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class ClockTicker implements Runnable {

	private ClockOutput out;
	private Semaphore mutex;
	private ClockInput in;
	private long TimeDiff;

	public ClockTicker(ClockOutput out, ClockInput in, Semaphore mutex) {
		this.in = in;
		this.out = out;
		this.mutex = mutex;
	}

	public ClockTicker() {

	}

	public void run() {
		long intervalTime = 1000;
		long t0 = System.currentTimeMillis();
		long sleepTime;

		while (true) {
			String timer = LocalTime.now().toString().replace(":", "");
			timer = timer.substring(0, timer.indexOf("."));
			try {
				mutex.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			out.displayTime((Integer.parseInt(timer)));
			mutex.release();
			sleepTime = (intervalTime - (System.currentTimeMillis() - t0) % intervalTime);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
