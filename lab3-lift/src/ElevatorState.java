import java.util.concurrent.Semaphore;

import lift.LiftView;
import lift.Passenger;

public class ElevatorState {

  private int currFloor;
  private LiftView view;

  private static enum Direction {
    UP, STILL, DOWN
  }

  private Direction direction;
  private int[] waitEntry;
  private int[] waitExit;
  private int passengers;
  private Semaphore mutex;

  public ElevatorState(LiftView view) {
    this.view = view;
    this.currFloor = 0;
    this.direction = Direction.STILL;
    this.waitEntry = new int[7];
    this.waitExit = new int[7];
    this.passengers = 0;
    this.mutex = new Semaphore(1);
  }

  public synchronized void move() throws InterruptedException {
    waitForButtonPress();
    chooseDirection();
    waitForEnter();
    chooseDirection();
    if (direction != Direction.STILL) {
      if (direction == Direction.UP) {
        moveLift(currFloor, currFloor + 1);
        // view.moveLift(currFloor, currFloor + 1);
        currFloor = currFloor + 1;
      } else if (direction == Direction.DOWN) {
        moveLift(currFloor, currFloor - 1);
        // view.moveLift(currFloor, currFloor - 1);
        currFloor = currFloor - 1;
      }
    }
    notifyAll();
  }

  private void moveLift(int currFloor, int nextFloor) {
    view.moveLift(currFloor, nextFloor);
  }

  private void waitForEnter() throws InterruptedException {
    while ((waitEntry[currFloor] != 0 && passengers != 4) || waitExit[currFloor] != 0) {
      wait();
    }
  }

  public void pressButton(Passenger passenger) throws InterruptedException {
    mutex.acquire();
    waitEntry[passenger.getStartFloor()]++;
    mutex.release();
  }

  public synchronized void waitForElevator(Passenger passenger) throws InterruptedException {
    notifyAll();
    while (passenger.getStartFloor() != currFloor || passengers == 4) {
      wait();
    }
    if (isWrongDirection(passenger)) {
      waitEntry[passenger.getStartFloor()]--;
      notifyAll();
      wait();
      pressButton(passenger);
      waitForElevator(passenger);
    } else {
      enterElevator(passenger);
    }
  }

  private boolean isWrongDirection(Passenger passenger) {
    return false;
  }

  private void enterElevator(Passenger passenger) throws InterruptedException {
    passenger.enterLift();
    passengers++;
    waitEntry[passenger.getStartFloor()]--;
    waitExit[passenger.getDestinationFloor()]++;
    notifyAll();
    waitToExit(passenger);
  }

  private void waitToExit(Passenger passenger) throws InterruptedException {
    while (currFloor != passenger.getDestinationFloor()) {
      wait();
    }
    passenger.exitLift();
    waitExit[passenger.getDestinationFloor()]--;
    passengers--;
    notifyAll();
  }

  private void chooseDirection() {

    if (currFloor == 6) {
      direction = Direction.DOWN;
    }
    if (currFloor == 0) {
      direction = Direction.UP;
    }
  }

  private void waitForButtonPress() throws InterruptedException {
    boolean noQueue = true;
    while (noQueue) {
      for (int floor : waitEntry) {
        if (floor != 0) {
          noQueue = false;
        }
      }
      for (int floor : waitExit) {
        if (floor != 0) {
          noQueue = false;
        }
      }
      if (noQueue) {
        wait();
      }
    }
  }
}
