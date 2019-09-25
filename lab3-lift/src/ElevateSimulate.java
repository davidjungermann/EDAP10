import java.util.Random;
import java.util.concurrent.Semaphore;

import lift.LiftView;
import lift.Passenger;

public class ElevateSimulate {

	private static final int MAX_PASSENGER = 10;

	public static void main(String[] args) throws InterruptedException {

		LiftView view = new LiftView();
		Random rand = new Random();
		Semaphore nbrOfPassengers = new Semaphore(MAX_PASSENGER);
		ElevatorState elevatorState = new ElevatorState(view);

		Runnable passengerThread = () -> {
			Passenger passenger = view.createPassenger();
			try {
				Thread.sleep(rand.nextInt(45000));
			passenger.begin();
			elevatorState.pressButton(passenger);
			elevatorState.waitForElevator(passenger);
			passenger.end();
			nbrOfPassengers.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Runnable liftThread = () -> {
			while (true) {
				try {
					elevatorState.move();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		Thread shaftThread = new Thread(liftThread);
		shaftThread.start();
		while (true) {
			nbrOfPassengers.acquire();
			Thread rideThread = new Thread(passengerThread);
			rideThread.start();
		}

	}
}
