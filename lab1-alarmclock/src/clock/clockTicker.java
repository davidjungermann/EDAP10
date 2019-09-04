package clock;

import java.time.LocalTime;

public class clockTicker implements Runnable{

	private ClockOutput out;
		
		public clockTicker(ClockOutput out){
			this.out = out;
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
			out.displayTime((Integer.parseInt(timer)));
			sleepTime = (intervalTime - (System.currentTimeMillis() - t0) % intervalTime);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			
	}

}
