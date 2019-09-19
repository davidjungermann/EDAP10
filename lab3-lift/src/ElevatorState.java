import lift.LiftView;
import lift.Passenger;

public class ElevatorState {

	private int currFloor;
	private LiftView view;
	private boolean direction; // False going down, true going up
	private int[] waitEntry;
	private int[] waitExit;
	private int passengers;
	
	public ElevatorState(LiftView view) {
		this.view = view;
		this.currFloor = 0;
		this.direction = true;
		this.waitEntry = new int[7];
		this.waitExit = new int[7];
		this.passengers = 0;
	}

	public synchronized void move() throws InterruptedException {
		waitForButtonPress();
		direction = chooseDirection();
		if (direction) {
			view.moveLift(currFloor, currFloor + 1);
			currFloor = currFloor + 1;
		} else {
			view.moveLift(currFloor, currFloor - 1);
			currFloor = currFloor - 1;
		}
		direction = chooseDirection();
		notifyAll();
		waitForEnter();
	}
	
	
	private void waitForEnter() throws InterruptedException {
		while ((waitEntry[currFloor] != 0 && passengers != 4) || waitExit[currFloor] != 0) {
			wait();
		}
	}
	
	public synchronized void pressButton(Passenger passenger) throws InterruptedException{
		waitEntry[passenger.getStartFloor()]++;
		notifyAll();
		waitForElevator(passenger);
	}
	
	private void waitForElevator(Passenger passenger) throws InterruptedException {
		while(passenger.getStartFloor() != currFloor || passengers == 4) {
			wait();
		}
		if(isWrongDirection(passenger)){
			waitEntry[passenger.getStartFloor()]--;
			notifyAll();
			wait();
			pressButton(passenger);
		} else{			
		enterElevator(passenger);
		notifyAll();
		}
	}
	
	private boolean isWrongDirection(Passenger passenger){
		if(direction) {
			if (passenger.getDestinationFloor() > currFloor){				
				return false;
			}
		} else {
			if (passenger.getDestinationFloor() < currFloor){				
				return false;
			}
		}
		return true;
	}
	
	private void enterElevator(Passenger passenger) throws InterruptedException{
		passenger.enterLift();
		passengers++;
		waitEntry[passenger.getStartFloor()]--;
		waitExit[passenger.getDestinationFloor()]++;
		notifyAll();
		waitToExit(passenger);
	}
	
	private void waitToExit(Passenger passenger) throws InterruptedException {
		while(currFloor != passenger.getDestinationFloor()) {
			wait();
		}
		passenger.exitLift();
		waitExit[passenger.getDestinationFloor()]--;
		passengers--;
		notifyAll();
	}
	
	
	private boolean chooseDirection() {
		if (currFloor == 6) {
			return false;
		}
		if (currFloor == 0) {
			return true;
		}
		if (direction) {
			for(int i = currFloor; i <= 6; i++) {
				if (waitExit[i] != 0) {
					return direction;
				}
			}
		} else if (!direction && passengers != 4){
			for(int i = currFloor; i >= 0; i--) {
				if (waitExit[i] != 0) {
					return direction;
				}
			}
		}
		if (passengers != 4) {
			if (direction) {
				for(int i = currFloor; i <= 6; i++) {
					if (waitEntry[i] != 0) {
						return direction;
					}
				}
			} else if (!direction){
				for(int i = currFloor; i >= 0; i--) {
					if (waitEntry[i] != 0) {
						return direction;
					}
				}
			}
		}
		if (passengers != 4){			
			return !direction;
		}
		return direction;
	}
	private void waitForButtonPress() throws InterruptedException{
		boolean noQueue = true;
		while(noQueue){
			for(int floor : waitEntry) {
				if(floor != 0){
					noQueue = false;
				}
			} 
			for(int floor : waitExit) {
				if(floor != 0){
					noQueue = false;
				}
			} 
			if (noQueue) {
				wait();				
			}
		}
	}
}
