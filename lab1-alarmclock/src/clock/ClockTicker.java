package clock;

import java.util.concurrent.Semaphore;

public class ClockTicker implements Runnable {

	private ClockOutput out;
	private TimeState timeState;
	private Semaphore alarmTrigger;
	private int alarmTime;
	private boolean alarmOn;
	
	public ClockTicker(ClockOutput out, TimeState timeState, Semaphore alarmTrigger) {
		this.out = out;
		this.timeState = timeState;
		this.alarmTrigger = alarmTrigger;
		alarmOn = false;
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
			String timeValue = timeState.getTime().toString().replace(":", "");
			if(timeValue.length() == 4){
				timeValue = timeValue + "00";
			}
			if(timeValue.contains(".")){
				
				timeValue = timeValue.substring(0, timeValue.indexOf("."));
			}

			out.displayTime((Integer.parseInt(timeValue)));
			
			System.out.println(timeState.getAlarmTime());
			System.out.println(timeState.getTime());
			
			if (timeState.getAlarmTime() != null && ((timeState.getTime().compareTo(timeState.getAlarmTime()) == 0 || alarmOn) && alarmTick <= alarmTime)) {
				alarmOn = true;
				alarmTick ++;
				alarmTrigger.release();
			} else if (alarmTick == alarmTime) {
				alarmOn = false;
				alarmTick = 0;
			}
			timeState.setTime(timeState.getTime().plusSeconds(1));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		}

	}

}
