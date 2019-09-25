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
				view.moveLift(currFloor, currFloor + 1);
				currFloor = currFloor + 1;
			} else if (direction == Direction.DOWN) {
				view.moveLift(currFloor, currFloor - 1);
				currFloor = currFloor - 1;
			}
		}
		
		notifyAll();
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
			//if ((waitEntry[currFloor] == 0 || passengers == 4) && waitExit[currFloor] == 0) {
				notifyAll();
			//}
			wait();
			pressButton(passenger);
			waitForElevator(passenger);
		} else {
			enterElevator(passenger);
		}
	}

	private boolean isWrongDirection(Passenger passenger) {
		if (direction == Direction.STILL) {
			return false;
		} else if (direction == Direction.DOWN && passenger.getDestinationFloor() < currFloor) {
			return false;
		} else if (direction == Direction.UP && passenger.getDestinationFloor() > currFloor) {
			return false;
		}
		return true;
	}

	private void enterElevator(Passenger passenger) throws InterruptedException {
		passenger.enterLift();
		passengers++;
		waitEntry[passenger.getStartFloor()]--;
		waitExit[passenger.getDestinationFloor()]++;
		//if ((waitEntry[currFloor] == 0 || passengers == 4) && waitExit[currFloor] == 0) {
			notifyAll();
		//}
		waitToExit(passenger);
	}

	private void waitToExit(Passenger passenger) throws InterruptedException {
		while (currFloor != passenger.getDestinationFloor()) {
			wait();
		}
		passenger.exitLift();
		waitExit[passenger.getDestinationFloor()]--;
		passengers--;
		//if (waitEntry[currFloor] == 0 && waitExit[currFloor] == 0) {
			notifyAll();
		//}
	}

	private void chooseDirection() {
		
		/*if (currFloor == 6) {
			direction = Direction.DOWN;
		}
		if (currFloor == 0) {
			direction = Direction.UP;
		}
		*/
		
		if (currFloor == 6) {
			direction = Direction.STILL;
		}
		if (currFloor == 0) {
			direction = Direction.STILL;
		}
		
		if (direction == Direction.UP) {
			for (int i = currFloor + 1; i <= 6; i++) {
				if (waitExit[i] != 0) {
					return;
				}
			}
			for (int i = currFloor + 1; i <= 6; i++) {
				if (waitEntry[i] != 0) {
					return;
				}
			}
			direction = Direction.STILL;
			return;
		} else if (direction == Direction.DOWN) {
			for (int i = currFloor - 1; i >= 0; i--) {
				if (waitExit[i] != 0) {
					return;
				}
			}
			for (int i = currFloor - 1; i >= 0; i--) {
				if (waitEntry[i] != 0) {
					return;
				}
			}
			direction = Direction.STILL;
			return;
		}

		if (direction == Direction.STILL) {
			for (int i = currFloor - 1; i >= 0; i--) {
				if (waitExit[i] != 0) {
					direction = Direction.DOWN;
				}
			}
			for (int i = currFloor + 1; i <= 6; i++) {
				if (waitExit[i] != 0) {
					direction = Direction.UP;
				}
			}

			for (int i = currFloor - 1; i >= 0; i--) {
				if (waitEntry[i] != 0) {
					direction = Direction.DOWN;
				}
			}
			for (int i = currFloor + 1; i <= 6; i++) {
				if (waitEntry[i] != 0) {
					direction = Direction.UP;
				}
			}
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
