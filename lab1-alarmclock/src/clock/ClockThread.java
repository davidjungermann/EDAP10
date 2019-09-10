package clock;

import java.util.concurrent.Semaphore;

public class ClockThread implements Runnable {

	private ClockOutput out;
	private TimeState timeState;
	private Semaphore alarmTrigger;
	private int alarmTime;

	public ClockThread(ClockOutput out, TimeState timeState, Semaphore alarmTrigger) {
		this.out = out;
		this.timeState = timeState;
		this.alarmTrigger = alarmTrigger;
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
				if (timeValue.length() == 4) {
					timeValue = timeValue + "00";
				}
				if (timeValue.contains(".")) {

					timeValue = timeValue.substring(0, timeValue.indexOf("."));
				}

				out.displayTime((Integer.parseInt(timeValue)));

				if (timeState.isAlarmOn()) {
					if (timeState.getAlarmTime() != null
							&& ((timeState.getTime().compareTo(timeState.getAlarmTime()) == 0))) {
						timeState.setAlarmSounding(true);
						alarmTick = 0;
					}
					if (timeState.alarmSounding() && alarmTick <= alarmTime) {
						alarmTick++;
						alarmTrigger.release();
					} else if (alarmTick == alarmTime) {
						timeState.setAlarmSounding(false);
					}
				}
				timeState.setTime(timeState.getTime().plusSeconds(1));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}
