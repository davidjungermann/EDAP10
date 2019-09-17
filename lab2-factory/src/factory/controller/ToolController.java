package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.swingview.Factory;

public class ToolController {
  private final DigitalSignal conveyor, press, paint;
  private final long pressingMillis, paintingMillis;

  public ToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis,
      long paintingMillis) {
    this.conveyor = conveyor;
    this.press = press;
    this.paint = paint;
    this.pressingMillis = pressingMillis;
    this.paintingMillis = paintingMillis;
  }

  public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
    if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
      conveyor.off();
      press();
      conveyor.on();
    }
  }

  public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
    if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
      conveyor.off();
      paint();
      conveyor.on();
    }
  }

  private void paint() throws InterruptedException {
    paint.on();
    waitOutside(paintingMillis);
    paint.off();
    waitOutside(paintingMillis);
  }

  private void press() throws InterruptedException {
    press.on();
    waitOutside(pressingMillis);
    press.off();
    waitOutside(pressingMillis);
  }

  private void waitOutside(long millis) throws InterruptedException {
    long timeToWakeUp = System.currentTimeMillis() + millis;
    while (System.currentTimeMillis() < timeToWakeUp) {
      long dt = timeToWakeUp - System.currentTimeMillis();
      wait(dt);
    }
  }

  // -----------------------------------------------------------------------

  public static void main(String[] args) {
    Factory factory = new Factory();
    factory.startSimulation();
  }
}
