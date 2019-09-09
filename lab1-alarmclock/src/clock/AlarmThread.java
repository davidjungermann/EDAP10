package clock;

import java.util.concurrent.Semaphore;

public class AlarmThread implements Runnable{

	private Semaphore alarmTrigger;
	private ClockOutput out;
	public AlarmThread(Semaphore alarmTrigger, ClockOutput out){
		this.alarmTrigger = alarmTrigger;
		this.out = out;
	}
	
	
	public void run() {
		while (true){
			
			try {
				System.out.println("jtajtaj");
				alarmTrigger.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("NufunkarDet");
			out.alarm();
		}
	}

}
