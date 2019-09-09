package clock;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class TimeState {

	private LocalTime time;
	private LocalTime alarmTime;
	private boolean alarmOn;

	Semaphore mutex;

	public TimeState(Semaphore mutex) {
		this.mutex = mutex;
		time = LocalTime.now();

	}

	public void setTime(LocalTime time) {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.time = time.withNano(00);
		mutex.release();
	}

	public LocalTime getTime() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LocalTime time = this.time;
		mutex.release();
		return time;
	}

	public void setAlarmTime(LocalTime time) {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.alarmTime = time;
		mutex.release();
	}

	public LocalTime getAlarmTime() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LocalTime time = this.alarmTime;
		mutex.release();
		return time;
	}

	public void toggleAlarm() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.alarmOn = !alarmOn;
		mutex.release();
	}

	public boolean isAlarmOn() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean alarmOn = this.alarmOn;
		mutex.release();
		return alarmOn;
	}

}
