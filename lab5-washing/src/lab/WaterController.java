package lab;

import wash.WashingIO;

public class WaterController extends MessagingThread<WashingMessage> {
  private final WashingIO io;
  private double currentLevel;
  private double wantedLevel;
  private double upperBound;
  private double lowerBound;
  private int dt;

  private static final double margin = 0.06;

  public WaterController(WashingIO io) {
    this.io = io;
  }

  @Override
  public void run() {
    dt = 100;
    MessagingThread<WashingMessage> sender;

    try {
      while (true) {
        WashingMessage m = receive();

        while (m != null) {
          sender = m.getSender();

          switch (m.getCommand()) {

          case WashingMessage.WATER_IDLE:
            m = null;
            break;

          case WashingMessage.WATER_FILL:
            wantedLevel = m.getValue();
            upperBound = wantedLevel - margin;
            lowerBound = wantedLevel + margin;

            while (true) {
              m = receiveWithTimeout(dt / Wash.SPEEDUP);

              if (m != null) {
                if (m.getCommand() == WashingMessage.WATER_FILL) {
                  io.fill(false);
                  io.drain(false);
                  wantedLevel = m.getValue();
                  upperBound = wantedLevel - margin;
                  lowerBound = wantedLevel + margin;
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

              if (currentLevel >= wantedLevel - margin && currentLevel <= wantedLevel + margin) {
                sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                io.fill(false);
                io.drain(false);
                m = null;
                break;
              }
            }
            break;

          case WashingMessage.WATER_DRAIN:
            io.drain(true);
            while (true) {
              m = receiveWithTimeout((long) ((((io.getWaterLevel() / 0.2) - 1) * 1000) / Wash.SPEEDUP));
              if (m != null && m.getCommand() != WashingMessage.WATER_DRAIN) {
                io.drain(false);
                break;
              }
              currentLevel = io.getWaterLevel();
              if (currentLevel == 0) {
                io.drain(false);
                sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                m = null;
                break;
              }
            }
            break;
          }
        }
      }
    } catch (InterruptedException e) {
      throw new Error(e);
    }

  }
}