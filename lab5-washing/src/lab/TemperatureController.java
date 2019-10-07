package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {

  private WashingIO io;
  private static final int dt = 10000;
  private static final double mu = 0.678;
  private static final double ml = -0.19048;

  private double currentTemp;
  private double wantedTemp;
  private double upperBound;
  private double lowerBound;
  public MessagingThread<WashingMessage> sender;

  public TemperatureController(WashingIO io) {
    this.io = io;
  }

  @Override
  public void run() {
    try {
      while (true) {
        WashingMessage m = receive();
        while (m != null) {
          sender = m.getSender();
          if (m.getCommand() == WashingMessage.TEMP_IDLE) {
            m = null;
            break;
          }

          if (m.getCommand() == WashingMessage.TEMP_SET) {
            while (true) {
              m = receiveWithTimeout(dt / Wash.SPEEDUP);
              if (m != null) {
                wantedTemp = m.getValue();
                upperBound = wantedTemp - mu;
                lowerBound = wantedTemp + ml;
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
            if (currentTemp >= wantedTemp - 2 && currentTemp < wantedTemp) {
              System.out.println(currentTemp + "Call: Temperature regulation");
              send(new WashingMessage(sender, WashingMessage.ACKNOWLEDGMENT));
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
