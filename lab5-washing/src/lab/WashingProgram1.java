package lab;

import wash.WashingIO;

public class WashingProgram1 extends MessagingThread<WashingMessage> {

	private WashingIO io;
	private MessagingThread<WashingMessage> temp;
	private MessagingThread<WashingMessage> water;
	private MessagingThread<WashingMessage> spin;
	private WashingMessage m;

	public WashingProgram1(WashingIO io, MessagingThread<WashingMessage> temp, MessagingThread<WashingMessage> water,
			MessagingThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
	}

	@Override
	public void run() {
		try {
			// Lock the hatch, let water into the machine, heat to 40◦C, keep
			// the
			// temperature for 30 minutes, drain, rinse 5 times 2 minutes in
			// cold water,
			// centrifuge for 5
			// minutes and unlock the hatch

			io.lock(true);
			water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));

			receive();
			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
			spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));

			receive();
			Thread.sleep(30 * 60000 / Wash.SPEEDUP);
			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));

			int i = 0;
			while (true) {
				water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
				m = receive();
				if (m.getCommand() == WashingMessage.ACKNOWLEDGMENT && m.getSender() == water) {
					water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
					i++;
					Thread.sleep(2 * 60000 / Wash.SPEEDUP);
					receive();
					if (i == 5) {
						i = 0;
						break;
					}
				}
			}

			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			while (receive().getCommand() != WashingMessage.ACKNOWLEDGMENT) {
			}
			io.lock(false);

			spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
			Thread.sleep(5 * 60000 / Wash.SPEEDUP);

			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			io.lock(false);
			System.out.println("washing program 1 finished");

		} catch (InterruptedException e) {
			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			try {
				WashingMessage ack = receive();
				if (ack.getCommand() == WashingMessage.ACKNOWLEDGMENT) {
					io.lock(false);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			System.out.println("washing program terminated");
		}
	}
}
