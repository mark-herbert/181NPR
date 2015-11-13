
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kenneth
 */
public class TimerThread extends Thread implements Runnable{
    private final javax.swing.JTextField field;
    public TimerThread (javax.swing.JTextField field){
        this.field = field;
    }
    
    public void run(){
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                field.setText("update");
            }

        });
        try {
            Thread.sleep(600000L);
        } catch (InterruptedException ex) {
            Logger.getLogger(TimerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
