import lift.LiftView;
import lift.Passenger;

public class LiftMonitor {

  private int current, next, load, waitingTotal;
  private int[] waitEntry, waitExit;
  private boolean isMoving;

  public LiftMonitor() {
    current = 0;
    load = 0;
    next = 0;
    waitEntry = new int[7];
    waitExit = new int[7];
    isMoving = false;
  }

  public synchronized int moveLift(LiftView lv) throws InterruptedException {

    while (load < 4 && waitEntry[current] > 0 || waitExit[current] > 0 || waitingTotal < 1 && load == 0) {
      wait();
    }
    isMoving = true;
    return calculateNext();

  }

  public synchronized int getCurrent() {
    return current;
  }

  public synchronized void updateFloors() {
    current = next;
    isMoving = false;
    notifyAll();
  }

  public synchronized void rideLift(Passenger p) throws InterruptedException {
    waitEntry[p.getStartFloor()] = waitEntry[p.getStartFloor()] + 1;
    waitingTotal++;
    notifyAll();
    while (p.getStartFloor() != current || load > 3 || isMoving) {
      wait();
    }

    p.enterLift();
    waitingTotal--;
    load++;
    waitEntry[p.getStartFloor()] = waitEntry[p.getStartFloor()] - 1;
    waitExit[p.getDestinationFloor()] = waitExit[p.getDestinationFloor()] + 1;
    notifyAll();

    while (p.getDestinationFloor() != current || isMoving) {
      wait();
    }
    p.exitLift();
    load--;
    waitExit[p.getDestinationFloor()] = waitExit[p.getDestinationFloor()] - 1;
    notifyAll();
  }

  private synchronized int calculateNext() {
    if (waitEntry[current] == 0 && load == 0) {
      int max = 0;
      for (int i = 0; i < 7; i++) {
        if (waitEntry[i] >= max && waitEntry[i] > 0) {
          max = waitEntry[i];
          next = i;
        }
      }
    } else {
      int max = 0;
      for (int i = 0; i < 7; i++) {
        if (waitExit[i] >= max && waitExit[i] > 0) {
          max = waitExit[i];
          next = i;
        }
      }
    }
    return next;
  }

}
