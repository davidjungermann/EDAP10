package clock;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class ClockTicker implements Runnable {

	private ClockOutput out;
	private Semaphore mutex;
	private ClockInput in;
	private TimeState timeState;
	
	public ClockTicker(ClockOutput out, ClockInput in, Semaphore mutex, TimeState timeState) {
		this.in = in;
		this.out = out;
		this.mutex = mutex;
		this.timeState = timeState;
	}

	public ClockTicker() {

	}
	

	public void run() {
		long intervalTime = 1000;
		long startTime = System.currentTimeMillis();
		long sleepTime;
		while (true) {
			
			sleepTime = (intervalTime - (System.currentTimeMillis() - startTime) % intervalTime);
			try {
				Thread.sleep(sleepTime);
			String timeValue = timeState.getTime().toString().replace(":", "");
			if(timeValue.length() == 4){
				timeValue = timeValue + "00";
			}
			if(timeValue.contains(".")){
				
				timeValue = timeValue.substring(0, timeValue.indexOf("."));
			}

				out.displayTime((Integer.parseInt(timeValue)));
				timeState.setTime(timeState.getTime().plusSeconds(1));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
		}

	}

}
