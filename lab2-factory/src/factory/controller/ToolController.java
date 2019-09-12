package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.swingview.Factory;

public class ToolController {
  private final DigitalSignal conveyor, press, paint;
  private final long pressingMillis, paintingMillis;
  private boolean isPressing;
  private boolean isPainting;

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
      press.on();
      Thread.sleep(pressingMillis);
      press.off();
      Thread.sleep(pressingMillis); // press needs this time to retract
      conveyor.on();
    }
  }

  public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
    if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
      conveyor.off();
      paint.on();
      Thread.sleep(paintingMillis);
      paint.off();
      Thread.sleep(paintingMillis);
      conveyor.on();
    }
  }

  // -----------------------------------------------------------------------

  public static void main(String[] args) {
    Factory factory = new Factory();
    factory.startSimulation();
  }
}
