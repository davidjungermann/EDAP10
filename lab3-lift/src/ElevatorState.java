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
	private int nextFloor;
	private int passengers;
	private Semaphore exitMutex;
	private Semaphore enterMutex;

	public ElevatorState(LiftView view) {
		this.view = view;
		this.currFloor = 0;
		this.nextFloor = 0;
		this.direction = Direction.STILL;
		this.waitEntry = new int[7];
		this.waitExit = new int[7];
		this.passengers = 0;
		this.exitMutex = new Semaphore(1);
		this.enterMutex = new Semaphore(1);
	}
	
	
	public synchronized int determineNextFloor() throws InterruptedException{
		currFloor = nextFloor;
		return waitForEnter();
	}
	
	public synchronized int waitForEnter() throws InterruptedException {
		while ((waitEntry[currFloor] != 0 && passengers != 4) || waitExit[currFloor] != 0
				|| direction == Direction.STILL) {
			System.out.println(currFloor);
			System.out.println(direction);
			chooseDirection();
			wait();
		}
		notifyAll();
		System.out.println(currFloor);
		System.out.println(direction);
		return chooseDirection();
	}

	public synchronized void pressButton(Passenger passenger) throws InterruptedException {
		notifyAll();
		waitEntry[passenger.getStartFloor()]++;
		waitForElevator(passenger);
	}

	private synchronized void waitForElevator(Passenger passenger) throws InterruptedException {
		while (passenger.getStartFloor() != currFloor || passengers == 4) {
			wait();
		}
		if (isWrongDirection(passenger)) {
			waitEntry[passenger.getStartFloor()]--;
			notifyAll();
			wait();
			pressButton(passenger);
		} else {
			passengers++;
			waitEntry[passenger.getStartFloor()]--;
			waitExit[passenger.getDestinationFloor()]++;
			notifyAll();
		}
	}

	private synchronized boolean isWrongDirection(Passenger passenger) {
		if (direction == Direction.STILL) {
			return false;
		} else if (direction == Direction.DOWN && passenger.getDestinationFloor() < currFloor) {
			return false;
		} else if (direction == Direction.UP && passenger.getDestinationFloor() > currFloor) {
			return false;
		}
		return true;
	}

	public synchronized void enterElevator(Passenger passenger) throws InterruptedException {
		passenger.enterLift();
		waitToExit(passenger);
	}

	private synchronized void waitToExit(Passenger passenger) throws InterruptedException {
		notifyAll();
		while (currFloor != passenger.getDestinationFloor()) {
			wait();
		}
		waitExit[passenger.getDestinationFloor()]--;
		passengers--;
		notifyAll();
	}

	public synchronized void exit(Passenger passenger) throws InterruptedException {
		passenger.exitLift();
	}

	public synchronized int getFloor() {
		return currFloor;
	}

	public synchronized void setFloor(int floor) {
		currFloor = floor;
		notifyAll();
	}

	public synchronized int chooseDirection() throws InterruptedException {
		
		if (currFloor == 6) {
			direction = Direction.STILL;
		}
		if (currFloor == 0) {
			direction = Direction.STILL;
		}
		
		if (direction == Direction.STILL) {
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
		} else {
			
			if (direction == Direction.UP) {
				for (int i = currFloor + 1; i <= 6; i++) {
					if (waitExit[i] != 0) {
						break;
					}
					if (i == 6){
						direction = Direction.STILL;
					}
				}
				if (direction == Direction.STILL){			
					for (int i = currFloor + 1; i <= 6; i++) {
						if (waitEntry[i] != 0) {
							direction = Direction.UP;
						}
					}
				}
			} else if (direction == Direction.DOWN) {
				for (int i = currFloor - 1; i >= 0; i--) {
					if (waitExit[i] != 0) {
						break;
					}
					if (i == 0){
						direction = Direction.STILL;
					}
				}
				if (direction == Direction.STILL) {	
					for (int i = currFloor - 1; i >= 0; i--) {
						if (waitEntry[i] != 0) {
							direction = Direction.DOWN;
						}
					}	
				}
			}
		}
		
		if(direction == Direction.UP) {
			nextFloor = currFloor + 1;
		}
		if(direction == Direction.DOWN) {
			nextFloor = currFloor - 1;
		}
		System.out.println("currFloor: " + currFloor);
		System.out.println("nextFloor: " + nextFloor);
		System.out.println("Direction: " + direction);
		
		return nextFloor;
	}
}
