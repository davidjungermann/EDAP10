import java.util.concurrent.Semaphore;

import clock.ClockInput;
import clock.ClockInput.UserInput;
import clock.ClockOutput;
import clock.clockTicker;
import emulator.AlarmClockEmulator;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        clockTicker ct = new clockTicker(out);
        Thread clockCounter = new Thread(ct);
        clockCounter.start();

        out.displayTime(1337);   // arbitrary time: just an example

        Semaphore sem = in.getSemaphore();

        while (true) {
            sem.acquire();                        // wait for user input

            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int value = userInput.getValue();

            System.out.println("choice = " + choice + "  value=" + value);

        }
       
    }
}
