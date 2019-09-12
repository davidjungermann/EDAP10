package clock;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class Monitor {

	private int alarmTick;
	private LocalTime time;
	private LocalTime alarmTime;
	private boolean alarmOn;
	private boolean alarmSounding;
	private Semaphore mutex;
	private Semaphore alarmTrigger;
	private ClockOutput out;

	public Monitor(Semaphore mutex, Semaphore alarmTrigger, ClockOutput out) {
		this.alarmTrigger = alarmTrigger;
		this.out = out;
		this.mutex = mutex;
		time = LocalTime.now();

	}

	public void setTime(LocalTime time) {
		this.time = time.withNano(00);
	}

	public LocalTime getTime() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LocalTime time = this.time;
		mutex.release();
		return time;
	}

	public void setAlarmTime(LocalTime time) {
		this.alarmTime = time;
	}

	public LocalTime getAlarmTime() {
		LocalTime time = this.alarmTime;
		return time;
	}

	public void toggleAlarm() {
		this.alarmOn = !alarmOn;
	}

	public boolean isAlarmOn() {
		return this.alarmOn;
	}

	private boolean alarmSounding() {
		return alarmSounding;
	}

	private void setAlarmSounding(boolean bool) {
		alarmSounding = bool;
	}
	
	private void resetAlarmTick() {
		alarmTick = 0;
	}

	private int incrementAlarmTick() {
		return alarmTick++;
	}
	public void checkAlarm() {
		try {
			mutex.acquire();
			if (isAlarmOn()) {
				if (alarmSounding() && incrementAlarmTick() < 20) {
					alarmTrigger.release();
				} else {
					setAlarmSounding(false);
					resetAlarmTick();
				}
				if (getAlarmTime() != null && ((getTime().compareTo(getAlarmTime()) == 0))) {
					setAlarmSounding(true);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mutex.release();
	}

	public void abortAlarm() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setAlarmSounding(false);
		resetAlarmTick();
		mutex.release();
	}
	
	public void updateTime() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String timeValue = time.toString().replace(":", "");
		if (timeValue.length() == 4) {
			timeValue = timeValue + "00";
		}
		if (timeValue.contains(".")) {
			timeValue = timeValue.substring(0, timeValue.indexOf("."));
		}
		
		out.displayTime((Integer.parseInt(timeValue)));
		time = time.plusSeconds(1);
		mutex.release();
		checkAlarm();
	}

}
