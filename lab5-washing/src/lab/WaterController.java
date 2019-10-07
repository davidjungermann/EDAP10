package lab;

import wash.WashingIO;

public class WaterController extends MessagingThread<WashingMessage> {

  private WashingIO io;
  private static final int dt = 100;
  private static final double mu = -0.06;
  private static final double ml = 0.07;

  private double currentLevel;
  private double wantedLevel;
  private double upperBound;
  private double lowerBound;

  public WaterController(WashingIO io) {
    this.io = io;
  }

  @Override
  public void run() {
    try {
      while (true) {
        WashingMessage m = receive();
        while (m != null) {
          if (m.getCommand() == WashingMessage.WATER_IDLE) {
            m = null;
            break;
          }

          if (m.getCommand() == WashingMessage.WATER_FILL) {
            while (true) {
              m = receiveWithTimeout(dt / Wash.SPEEDUP);
              if (m != null) {
                wantedLevel = m.getValue();
                upperBound = wantedLevel + mu;
                lowerBound = wantedLevel - ml;
              } else {
                io.fill(false);
                io.drain(false);
                break;
              }
            }
            currentLevel = io.getWaterLevel();

            if (lowerBound >= currentLevel) {
              io.fill(true);
            } else if (upperBound <= currentLevel) {
              io.fill(false);
            }
            if (currentLevel >= wantedLevel - 2 && currentLevel < wantedLevel) {
              System.out.println(currentLevel + "Call: Temperature regulation");
              send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
            }
          }

          if (m.getCommand() == WashingMessage.WATER_DRAIN) {
            while (true) {
              m = receiveWithTimeout(dt / Wash.SPEEDUP);
              if (m != null) {
                wantedLevel = m.getValue();
                upperBound = wantedLevel + mu;
                lowerBound = wantedLevel - ml;
              } else {
                io.fill(false);
                io.drain(false);
                break;
              }
            }
            currentLevel = io.getWaterLevel();

            if (upperBound > currentLevel) {
              io.drain(false);
              io.fill(true);
            } else if (lowerBound < currentLevel) {
              io.fill(false);
              io.drain(true);
            }

            if (currentLevel >= wantedLevel - 0.5 && currentLevel <= wantedLevel + 0.5) {
              System.out.println("Call: Water fill");
              send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
              io.fill(false);
              io.drain(false);
              m = null;
              break;
            }
          }
          break;
        }
      }
    } catch (InterruptedException unexpected) {
      // we don't expect this thread to be interrupted,
      // so throw an error if it happens anyway
      throw new Error(unexpected);
    }
  }
}
