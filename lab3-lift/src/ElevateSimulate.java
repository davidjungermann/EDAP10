import java.util.Random;
import java.util.concurrent.Semaphore;

import lift.LiftView;
import lift.Passenger;

public class ElevateSimulate {

	private static final int MAX_PASSENGER = 30;

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Runnable runnableLift = () -> {
			int currFloor = 0;
			int nextFloor;
			while (true) {
				try {
					nextFloor = elevatorState.determineNextFloor();			
					view.moveLift(currFloor, nextFloor);
					currFloor = nextFloor;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		Thread liftThread = new Thread(runnableLift);
		liftThread.start();		
		while (true) {
			nbrOfPassengers.acquire();
			Thread passengerThread = new Thread(runnablePassenger);
			passengerThread.start();
		}
	}
}
