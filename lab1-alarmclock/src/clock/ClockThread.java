package clock;

import java.util.concurrent.Semaphore;

public class ClockThread implements Runnable {

	private ClockOutput out;
	private Monitor timeState;
	private Semaphore alarmTrigger;
	private int alarmTime;
	private Semaphore mutex;

	public ClockThread(ClockOutput out, Monitor timeState, Semaphore alarmTrigger, Semaphore mutex) {
		this.out = out;
		this.timeState = timeState;
		this.alarmTrigger = alarmTrigger;
		this.mutex = mutex;
		alarmTime = 20;
	}

	public void run() {
		long intervalTime = 1000;
		long startTime = System.currentTimeMillis();
		long sleepTime;
		int alarmTick = 0;
		while (true) {
			sleepTime = (intervalTime - (System.currentTimeMillis() - startTime) % intervalTime);
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				timeState.updateTime();
		}
	}
}
