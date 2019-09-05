package clock;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class ClockTicker implements Runnable {

	private ClockOutput out;
	private Semaphore mutex;
	private ClockInput in;

	public ClockTicker(ClockOutput out, ClockInput in, Semaphore mutex) {
		this.in = in;
		this.out = out;
		this.mutex = mutex;
	}

	public ClockTicker() {

	}

	public void run() {
		long intervalTime = 1000;
		long startTime = System.currentTimeMillis();
		long sleepTime;

		while (true) {
			String timeValue = LocalTime.now().toString().replace(":", "");
			timeValue = timeValue.substring(0, timeValue.indexOf("."));
			try {
				mutex.acquire();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			out.displayTime((Integer.parseInt(timeValue)));
			mutex.release();
			sleepTime = (intervalTime - (System.currentTimeMillis() - startTime) % intervalTime);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

	}

}
