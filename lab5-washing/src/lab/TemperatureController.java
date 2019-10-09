package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {
	private final WashingIO io;
	private double currentTemp;
	private double temp;
	private double upperBound;
	private double lowerBound;
	private int dt;

	private static final double mu = 0.678;
	private static final double ml = 1.19048;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		dt = 10000;
		MessagingThread<WashingMessage> sender;

		try {
			while (true) {
				WashingMessage m = receive();
				if(m != null){				
					sender = m.getSender();
					System.out.println(sender);
					System.out.println(m.getCommand());
					System.out.println(m.getValue());
					switch (m.getCommand()) {
					case WashingMessage.TEMP_IDLE:
						System.out.println("io heat false in idle");
						io.heat(false);
						m = null;
						break;

					case WashingMessage.TEMP_SET:
						System.out.println("keeptemp");
						temp = m.getValue();
						upperBound = temp - mu;
						lowerBound = temp - ml;
						if(reachTemperature(m, sender)) keepTemperature(m);
						break;
					}
				}
			}
		} catch (InterruptedException e) {
			io.heat(false);
			throw new Error(e);
		}
	}
	
	private boolean reachTemperature(WashingMessage m, MessagingThread sender) throws InterruptedException{
		while (true) {
			m = receiveWithTimeout(dt / Wash.SPEEDUP);
			if (m != null) {
				if (m.getCommand() == WashingMessage.TEMP_SET) {
					temp = m.getValue();
					upperBound = temp - mu;
					lowerBound = temp - ml;
				} else {
					System.out.println("io heat false in reach");
					io.heat(false);
					return false;
				}
			}

			currentTemp = io.getTemperature();
			if (io.getWaterLevel() > 1) { // Check that there is
											// water in machine.
				if (lowerBound >= currentTemp) {
					io.heat(true);
				} else if (upperBound <= currentTemp) {
					io.heat(false);
				}
				if (currentTemp >= temp - 2 && currentTemp < temp) {
					sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
					return true;
				}
			}
		}
	}
	
	private void keepTemperature(WashingMessage m) throws InterruptedException{
		while (true) {
			m = receiveWithTimeout(dt / Wash.SPEEDUP);
			if (m != null) {
				if (m.getCommand() == WashingMessage.TEMP_SET) {
					temp = m.getValue();
					upperBound = temp - mu;
					lowerBound = temp - ml;
				} else {
					System.out.println("io heat false in keep");
					io.heat(false);
					return;
				}
			}

			currentTemp = io.getTemperature();
			if (io.getWaterLevel() > 1) { // Check that there is
											// water in machine.
				if (lowerBound >= currentTemp) {
					io.heat(true);
				} else if (upperBound <= currentTemp) {
					io.heat(false);
				}
			}
		}
	}
}
