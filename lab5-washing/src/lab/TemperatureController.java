package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {

	WashingIO io;
	long dt;
	double mu;
	double ml;

	public TemperatureController(WashingIO io) {
		this.io = io;
		this.dt = 10000;
		this.mu = 0.678;
		this.ml = 0.19048;
	}

	@Override
	public void run() {
		while (true) {
			try {
				WashingMessage m = receive();
				if (m.getCommand() == m.TEMP_SET) {
					double goalTemp = m.getValue();
					while (true) {
						if (io.getTemperature() > goalTemp - mu) {
							io.heat(false);
						}
						if (io.getTemperature() < goalTemp + ml) {
							io.heat(true);
						}
						this.sleep(dt);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
