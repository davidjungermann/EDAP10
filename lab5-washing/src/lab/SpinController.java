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
        WashingMessage m = receive();
        while (m != null) {
          if (m.getCommand() == WashingMessage.SPIN_OFF) {
            io.setSpinMode(SPIN_IDLE);
            m = null;
            break;
          }

          if (m.getCommand() == WashingMessage.SPIN_SLOW) {
            while (true) {
              io.setSpinMode(SPIN_LEFT);
              m = receiveWithTimeout(60000 / Wash.SPEEDUP);
              if (m != null && m.getCommand() != 2) {
                break;
              }
              io.setSpinMode(SPIN_RIGHT);
              m = receiveWithTimeout(60000 / Wash.SPEEDUP);
              if (m != null && m.getCommand() != 2) {
                break;
              }
            }
          }
          if (m.getCommand() == WashingMessage.SPIN_FAST) {
            io.setSpinMode(SPIN_FAST);
            m = null;
            break;
          }

        }
      }
    } catch (InterruptedException unexpected) {
      // we don't expect this thread to be interrupted,
      // so throw an error if it happens anyway
      throw new Error(unexpected);
    }
  }
}
