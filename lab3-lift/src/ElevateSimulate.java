import java.util.Random;
import java.util.concurrent.Semaphore;

import lift.LiftView;
import lift.Passenger;

public class ElevateSimulate {

  private static final int MAX_PASSENGER = 20;

  public static void main(String[] args) throws InterruptedException {

    LiftView view = new LiftView();
    Random rand = new Random();
    Semaphore nbrOfPassengers = new Semaphore(MAX_PASSENGER);
    ElevatorState elevatorState = new ElevatorState(view);

    Runnable runnablePassenger = () -> {
      Passenger passenger = view.createPassenger();
      try {
        Thread.sleep(rand.nextInt(45000));
        passenger.begin();
        elevatorState.pressButton(passenger);
        elevatorState.enterElevator(passenger);
        elevatorState.exit(passenger);
        passenger.end();
        nbrOfPassengers.release();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };
    Runnable runnableLift = () -> {
      while (true) {
        try {
          elevatorState.move();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    Thread liftThread = new Thread(runnableLift);
    liftThread.start();
    while (true) {
      Thread.sleep(rand.nextInt(2000));
      nbrOfPassengers.acquire();
      Thread passengerThread = new Thread(runnablePassenger);
      passengerThread.start();
    }
  }
}
