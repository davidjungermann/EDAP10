

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    
    private final JProgressBar mainProgressBar;

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();

        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        
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
        //System.out.println("message intercepted (N=" + n + ")...");
    	WorklistItem workListItem = new WorklistItem(n, "N: " + n);
    	JButton button = new JButton("LET'S GET CRACKIN'");
    	button.addActionListener(e -> {
    		moveItem(workListItem, button);
    		startCrackinG(n);
    	});
    	workListItem.add(button);
        workList.add(workListItem);
    }
    
    private void startCrackinG(BigInteger n){
    	
    	
    }
    
    private void moveItem(WorklistItem workListItem, JButton button){
    	workListItem.remove(workListItem);
    	progressList.add(workListItem);
    	workListItem.remove(button);
    }
}
