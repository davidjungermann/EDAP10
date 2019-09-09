import java.time.LocalTime;
import java.util.concurrent.Semaphore;

import clock.ClockInput;
import clock.ClockInput.UserInput;
import clock.ClockOutput;
import clock.ClockTicker;
import clock.TimeState;
import emulator.AlarmClockEmulator;

public class ClockMain {

	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();
		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();
		Semaphore sem = in.getSemaphore();
		Semaphore mutex = new Semaphore(1);
		TimeState timeState = new TimeState(mutex);
		ClockTicker ct = new ClockTicker(out, in, mutex, timeState);
		Thread clockCounter = new Thread(ct);
		clockCounter.start();

		while (true) {
			sem.acquire(); // wait for user input
			UserInput userInput = in.getUserInput();
			int choice = userInput.getChoice();
			int value = userInput.getValue();
			int ss = value % 100;
			value /= 100;
			int mm = value % 100;
			value /= 100;
			int hh = value % 100;

			if (choice == 1 && value != 999999) {
				timeState.setTime(LocalTime.of(hh, mm, ss));
			}

			if (choice == 2 && value != 999999) {
				timeState.setAlarmTime(LocalTime.of(hh, mm, ss));
			}
			System.out.println("choice = " + choice + " value=" + value);
			System.out.println("HH: " + hh);
			System.out.println("MM: " + mm);
			System.out.println("SS: " + ss);

		}
	}
}
