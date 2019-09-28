import lift.LiftView;

public class LiftRide {

  public static void main(String[] args) {
    LiftView view = new LiftView();
    LiftMonitor monitor = new LiftMonitor();
    for (int i = 0; i < 20; i++) {
      Thread passengerThread = new PassengerThread(view, monitor);
      passengerThread.start();
    }

    while (true) {
      try {
        int current = monitor.getCurrent();
        int next = monitor.moveLift(view);
        view.moveLift(current, next);
        monitor.updateFloors();
      } catch (InterruptedException e) {
        throw new Error(e);
      }
    }
  }
}
