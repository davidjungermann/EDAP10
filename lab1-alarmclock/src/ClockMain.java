import java.util.concurrent.Semaphore;

import clock.ClockInput;
import clock.ClockInput.UserInput;
import clock.ClockOutput;
import clock.ClockTicker;
import emulator.AlarmClockEmulator;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        

        out.displayTime(1337);   // arbitrary time: just an example

        Semaphore sem = in.getSemaphore();
        Semaphore mutex = new Semaphore(1);
        ClockTicker ct = new ClockTicker(out, in, mutex);
        Thread clockCounter = new Thread(ct);
        clockCounter.start();
        
        while (true) {
            sem.acquire();                       // wait for user input
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int value = userInput.getValue();

            System.out.println("choice = " + choice + "  value=" + value);
        }
    }
}
