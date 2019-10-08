package lab;

import wash.WashingIO;

public class WashingProgram2 extends MessagingThread<WashingMessage> {

  private WashingIO io;
  private MessagingThread<WashingMessage> temp;
  private MessagingThread<WashingMessage> water;
  private MessagingThread<WashingMessage> spin;
  private WashingMessage m;

  public WashingProgram2(WashingIO io, MessagingThread<WashingMessage> temp, MessagingThread<WashingMessage> water,
      MessagingThread<WashingMessage> spin) {
    this.io = io;
    this.temp = temp;
    this.water = water;
    this.spin = spin;
  }

  @Override
  public void run() {
    try {
      int i = 0;

      // Like program 1, but with a 15 minute pre-wash in 40◦C. The main
      // wash should be performed in 60◦C.
      io.lock(true);
      water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
      spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));

      receive();
      temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));

      receive();
      Thread.sleep(15 * 60000 / Wash.SPEEDUP);
      temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));

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
      spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));

      receive();
      water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
      spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));

      receive();
      temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 60));

      receive();
      Thread.sleep(30 * 60000 / Wash.SPEEDUP);
      temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));

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

      receive();
      spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
      Thread.sleep(5 * 60000 / Wash.SPEEDUP);

      spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
      io.lock(false);
      System.out.println("washing program 2 finished");

    } catch (InterruptedException e) {
      temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
      water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
      spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
      System.out.println("washing program terminated");
    }
  }
}
