import lift.LiftView;
import lift.Passenger;

public class LiftMonitor {

  private int current, next, load, waitingTotal;
  private int[] waitEntry, waitExit;
  private boolean isMoving;
  private boolean movingUp;

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
    waitEntry[p.getStartFloor()]++;
    waitingTotal++;
    notifyAll();
    while (p.getStartFloor() != current || load > 3 || isMoving) {
      wait();
    }

    p.enterLift();
    waitingTotal--;
    load++;
    waitEntry[p.getStartFloor()]--;
    waitExit[p.getDestinationFloor()]++;
    notifyAll();

    while (p.getDestinationFloor() != current || isMoving) {
      wait();
    }
    p.exitLift();
    load--;
    waitExit[p.getDestinationFloor()]--;
    notifyAll();
  }

  private synchronized int calculateNext() {
    if (current == 0) {
      movingUp = true;
    } else if (current == 6) {
      movingUp = false;
    }

    if (movingUp) {
      next = current + 1;
      current = next;
    }

    if (!movingUp) {
      next = current - 1;
      current = next;
    }
    return next;
  }

}
