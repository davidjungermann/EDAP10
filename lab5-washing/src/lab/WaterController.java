package lab;

import wash.WashingIO;

public class WaterController extends MessagingThread<WashingMessage> {

	WashingIO io;

	public WaterController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		while (true) {

			try {
				
				WashingMessage m = receive();
				
				while (m != null) {
					
					if (m.getCommand() == WashingMessage.WATER_FILL) {
						io.lock(true);
						io.fill(true);
						this.sleep(2000);
						io.fill(false);
						//io.fill(false);
						m = null;
						break;
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
