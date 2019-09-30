import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;

import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

  private final JPanel workList;
  private final JPanel progressList;
  private ExecutorService threadPool;
  private final JProgressBar mainProgressBar;

  // -----------------------------------------------------------------------

  private CodeBreaker() {
    StatusWindow w = new StatusWindow();

    workList = w.getWorkList();
    progressList = w.getProgressList();
    mainProgressBar = w.getProgressBar();
    threadPool = Executors.newFixedThreadPool(2);
    new Sniffer(this).start();
  }

  // -----------------------------------------------------------------------

  public static void main(String[] args) throws Exception {

	  

    /*
     * Most Swing operations (such as creating view elements) must be performed in
     * the Swing EDT (Event Dispatch Thread).
     * 
     * That's what SwingUtilities.invokeLater is for.
     */
	  
	  
	  
	  
    SwingUtilities.invokeLater(() -> new CodeBreaker());
  }

  // -----------------------------------------------------------------------

  /** Called by a Sniffer thread when an encrypted message is obtained. */
  @Override
  public void onMessageIntercepted(String message, BigInteger n) {
    // System.out.println("message intercepted (N=" + n + ")...");
    WorklistItem workListItem = new WorklistItem(n, "N: " + n);
    JButton button = new JButton("LET'S GET CRACKIN'");
    ProgressTracker tracker = new Tracker();
    button.addActionListener(e -> {
      moveItem(workListItem, button);
      startCrackinG(message, n, tracker);
    });
    workListItem.add(button);
    workList.add(workListItem);
  }

  
  
  private void startCrackinG(String code, BigInteger n, ProgressTracker tracker) {
	  
	  Runnable crackerG = () -> {		  
		  Factorizer.crack(code, n, tracker);
	  };
	  
	  threadPool.execute(crackerG);
    
  }

  private void moveItem(WorklistItem workListItem, JButton button) {
    workListItem.remove(workListItem);
    progressList.add(workListItem);
    workListItem.remove(button);
  }

  private static class Tracker implements ProgressTracker {
    private int totalProgress = 0;
    private int prevPercent = -1;

    /**
     * Called by Factorizer to indicate progress. The total sum of ppmDelta from all
     * calls will add upp to 1000000 (one million).
     * 
     * @param ppmDelta portion of work done since last call, measured in ppm (parts
     *                 per million)
     */
    @Override
    public void onProgress(int ppmDelta) {
      totalProgress += ppmDelta;
      int percent = totalProgress / 10000;
      if (percent != prevPercent) {
        System.out.println(percent + "%");
        prevPercent = percent;
      }
    }
    
    
    
  }

}
