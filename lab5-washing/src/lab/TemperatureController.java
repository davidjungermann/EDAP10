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

        while (m != null) {
          sender = m.getSender();

          switch (m.getCommand()) {

          case WashingMessage.TEMP_IDLE:
            m = null;
            break;

          case WashingMessage.TEMP_SET:
            temp = m.getValue();
            upperBound = temp - mu;
            lowerBound = temp - ml;

            while (true) {
              m = receiveWithTimeout(dt / Wash.SPEEDUP);
              if (m != null) {
                if (m.getCommand() == WashingMessage.TEMP_SET) {
                  temp = m.getValue();
                  upperBound = temp - mu;
                  lowerBound = temp - ml;
                } else {
                  io.heat(false);
                  break;
                }
              }

              currentTemp = io.getTemperature();

              if (lowerBound >= currentTemp) {
                io.heat(true);
              } else if (upperBound <= currentTemp) {
                io.heat(false);
              }

              if (currentTemp >= temp - 2 && currentTemp < temp) {
                sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
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