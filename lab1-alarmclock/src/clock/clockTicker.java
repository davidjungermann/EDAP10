package clock;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class clockTicker implements Runnable{

	private ClockOutput out;
	private Semaphore updateTime;
	private ClockInput in;
	private long TimeDiff;
		public clockTicker(ClockOutput out, Semaphore updateTime, ClockInput in){
			this.in = in;
			this.out = out;
			this.updateTime = updateTime;
		}
		public clockTicker(){

		}
	
	public void run() {
		long intervalTime = 1000;
		long t0 = System.currentTimeMillis();
		long sleepTime;
		
		
		while (true){
			String timer = LocalTime.now().toString().replace(":", "");
			timer = timer.substring(0, timer.indexOf("."));
			try {
				updateTime.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			out.displayTime((Integer.parseInt(timer)));
			updateTime.release();
			sleepTime = (intervalTime - (System.currentTimeMillis() - t0) % intervalTime);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			
	}

}
