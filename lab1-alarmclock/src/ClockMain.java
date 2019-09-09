import java.util.concurrent.Semaphore;

import clock.ClockInput;
import clock.ClockInput.UserInput;
import clock.ClockOutput;
import clock.ClockTicker;
import emulator.AlarmClockEmulator;

public class ClockMain {

<<<<<<< HEAD
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        

        out.displayTime(1337);   // arbitrary time: just an example

        Semaphore sem = in.getSemaphore();
        Semaphore updateTime = new Semaphore(1);
        clockTicker ct = new clockTicker(out, updateTime, in);
        Thread clockCounter = new Thread(ct);
        //clockCounter.start();
        
        while (true) {
            sem.acquire();                       // wait for user input
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int value = userInput.getValue();
            int i = 0244;
            System.out.println("choice = " + choice + "  value=" + value);
            System.out.println(i);
        }
    }
=======
	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();

		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();

		Semaphore sem = in.getSemaphore();
		Semaphore mutex = new Semaphore(1);
		ClockTicker ct = new ClockTicker(out, in, mutex);
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

			//System.out.println("choice = " + choice + " value=" + value);
			System.out.println("HH: " + hh);
			System.out.println("MM: " + mm);
			System.out.println("SS: " + ss);
			

		}
	}
>>>>>>> 33c24ec30146fb0b9dd1079c2693e79964561b13
}
