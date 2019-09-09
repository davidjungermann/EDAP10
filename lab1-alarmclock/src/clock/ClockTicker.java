package clock;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class ClockTicker implements Runnable {

	private ClockOutput out;
	private Semaphore mutex;
	private ClockInput in;
	private LocalTime time;
	
	public ClockTicker(ClockOutput out, ClockInput in, Semaphore mutex, LocalTime time) {
		this.in = in;
		this.out = out;
		this.mutex = mutex;
		this.time = time;
	}

	public ClockTicker() {

	}
	
	public void setTime(LocalTime time){
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.time = time;
		mutex.release();
	}

	public void run() {
		long intervalTime = 1000;
		long startTime = System.currentTimeMillis();
		long sleepTime;
		while (true) {
			
			try {
				mutex.acquire();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String timeValue = time.toString().replace(":", "");
			if(timeValue.length() == 4){
				timeValue = timeValue + "00";
			}
			if(timeValue.contains(".")){
				
				timeValue = timeValue.substring(0, timeValue.indexOf("."));
			}
			mutex.release();
			sleepTime = (intervalTime - (System.currentTimeMillis() - startTime) % intervalTime);
			try {
				Thread.sleep(sleepTime);
				out.displayTime((Integer.parseInt(timeValue)));
				time = time.plusSeconds(1);	
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
		}

	}

}
