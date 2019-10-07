package lab;

import wash.WashingIO;

public class SpinController extends MessagingThread<WashingMessage> {

  public static final int SPIN_IDLE = 1; // barrel not rotating
  public static final int SPIN_LEFT = 2; // barrel rotating slowly, left
  public static final int SPIN_RIGHT = 3; // barrel rotating slowly, right
  public static final int SPIN_FAST = 4; // barrel rotating fast

  private final WashingIO io;

  public SpinController(WashingIO io) {
    this.io = io;
  }

  @Override
  public void run() {
    try {

      while (true) {
        // wait for up to a (simulated) minute for a WashingMessage
        WashingMessage message = receive();

        if (message.getCommand() == WashingMessage.SPIN_SLOW) {
          io.setSpinMode(SPIN_LEFT);
          SpinController.sleep(60000 / Wash.SPEEDUP);
          io.setSpinMode(SPIN_RIGHT);
        }

        if (message.getCommand() == WashingMessage.SPIN_FAST) {
          io.setSpinMode(SPIN_FAST);
        }

        if (message.getCommand() == WashingMessage.SPIN_OFF) {
          io.setSpinMode(SPIN_IDLE);
        }

        // if m is null, it means a minute passed and no message was received
        if (message != null) {
          System.out.println("got " + message);
        }

        // ... TODO ...
      }
    } catch (InterruptedException unexpected) {
      // we don't expect this thread to be interrupted,
      // so throw an error if it happens anyway
      throw new Error(unexpected);
    }
  }
}
