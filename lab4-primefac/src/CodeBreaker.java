import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
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
		w.enableErrorChecks();
		new Sniffer(this).start();
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) throws Exception {

		/*
		 * Most Swing operations (such as creating view elements) must be
		 * performed in the Swing EDT (Event Dispatch Thread).
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
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WorklistItem workListItem = new WorklistItem(n, "N: " + n);
				JButton button = new JButton("LET'S GET CRACKIN'");
				button.addActionListener(e -> {
					workListItemHandler(workListItem, n, message);
				});
				workListItem.add(button);
				workList.add(workListItem);
			}
		});
	}

	private void startCrackinG(String code, BigInteger n, ProgressTracker tracker, ProgressItem item) {

		Runnable crackerG = () -> {
			String cracked = Factorizer.crack(code, n, tracker);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					item.getTextArea().setText(cracked);
					JButton button = new JButton("Remove");
					button.addActionListener(e -> {
						progressList.remove(item);
						mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
						mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
					});
					item.add(button);
				}
			});
		};

		threadPool.execute(crackerG);
	}

	private void workListItemHandler(WorklistItem workListItem, BigInteger n, String message) {
		workList.remove(workListItem);
		ProgressItem item = new ProgressItem(n, "N: " + n);
		ProgressTracker tracker = new Tracker(item);
		progressList.add(item);
		startCrackinG(message, n, tracker, item);
		mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
	
	}

	private class Tracker implements ProgressTracker {
		private int totalProgress = 0;
		private int prevPercent = -1;
		private ProgressItem progressItem;

		/**
		 * Called by Factorizer to indicate progress. The total sum of ppmDelta
		 * from all calls will add upp to 1000000 (one million).
		 * 
		 * @param ppmDelta
		 *            portion of work done since last call, measured in ppm
		 *            (parts per million)
		 */
		public Tracker(ProgressItem progressItem) {
			this.progressItem = progressItem;
		}

		@Override
		public void onProgress(int ppmDelta) {
			totalProgress += ppmDelta;
			final int percent = totalProgress / 10000;

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progressItem.getProgressBar().setValue(progressItem.getProgressBar().getValue() + ppmDelta);
						mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta);
					}
				});
				System.out.println(percent);
				prevPercent = percent;
		
		}

	}

}
