package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {
  private final WashingIO io;

  public TemperatureController(WashingIO io) {
    this.io = io;
  }

  @Override
  public void run() {
    double actualTemp = 0;
    double wantedTemp = 0;
    double upperBound = 0;
    double lowerBound = 0;
    boolean called = false;
    int dt = 10000;
    MessagingThread<WashingMessage> sender;

    try {
      while (true) {
        WashingMessage m = receive();

        while (m != null) {
          sender = m.getSender();
          called = false;

          switch (m.getCommand()) {
          case 4:
            m = null;
            break;
          case 5:
            wantedTemp = m.getValue();
            upperBound = wantedTemp - 0.678;
            lowerBound = wantedTemp - 1.79048;

            while (true) {
              m = receiveWithTimeout(dt / Wash.SPEEDUP);
              if (m != null) {
                if (m.getCommand() == 5) {
                  wantedTemp = m.getValue();
                  upperBound = wantedTemp - 0.678;
                  lowerBound = wantedTemp - 1.79048;
                  called = false;
                } else {
                  io.heat(false);
                  break;
                }
              }

              actualTemp = io.getTemperature();

              if (lowerBound >= actualTemp) {
                io.heat(true);
              } else if (upperBound <= actualTemp) {
                io.heat(false);
              }

              if (!called && actualTemp >= wantedTemp - 2 && actualTemp < wantedTemp) {
                System.out.println(actualTemp + "Call: Temp regulated");
                called = true;
                sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
              }
            }
            break;
          default:
            throw new Error("Invalid value: " + m.getCommand());
          }
        }
      }
    } catch (InterruptedException e) {
      throw new Error(e);
    }
  }

}