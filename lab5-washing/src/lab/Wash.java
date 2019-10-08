package lab;

import simulator.WashingSimulator;
import wash.WashingIO;

public class Wash {

  // simulation speed-up factor:
  // 50 means the simulation is 50 times faster than real time
  public static final int SPEEDUP = 50;
  private static Thread currentThread;

  public static void main(String[] args) throws InterruptedException {
    WashingSimulator sim = new WashingSimulator(SPEEDUP);

    WashingIO io = sim.startSimulation();

    TemperatureController temp = new TemperatureController(io);
    WaterController water = new WaterController(io);
    SpinController spin = new SpinController(io);

    temp.start();
    water.start();
    spin.start();

    while (true) {
      int n = io.awaitButton();
      System.out.println("user selected program " + n);

      switch (n) {
      case 0:
        currentThread.interrupt();
        break;
      case 1:
        currentThread = new WashingProgram1(io, temp, water, spin);
        currentThread.start();
        break;
      case 2:
        currentThread = new WashingProgram2(io, temp, water, spin);
        currentThread.start();
        break;
      case 3:
        currentThread = new WashingProgram3(io, temp, water, spin);
        currentThread.start();
        break;
      }
    }
  }
};
