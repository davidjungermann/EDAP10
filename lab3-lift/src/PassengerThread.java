
import java.util.Random;

import lift.LiftView;
import lift.Passenger;

public class PassengerThread extends Thread {

  private Passenger passenger;
  private LiftMonitor monitor;

  public PassengerThread(LiftView view, LiftMonitor monitor) {
    this.monitor = monitor;
    passenger = view.createPassenger();
  }

  @Override
  public void run() {

    try {
      waiting();
      passenger.begin();
      monitor.rideLift(passenger);
      passenger.end();
    } catch (InterruptedException e) {
      throw new Error(e);
    }
  }

  private void waiting() {

    try {
      Random rand = new Random();
      int wait = rand.nextInt(46);
      Thread.sleep(wait * 1000);
    } catch (InterruptedException e) {
      throw new Error(e);
    }
  }

}
