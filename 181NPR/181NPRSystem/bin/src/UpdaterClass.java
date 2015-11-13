
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class UpdaterClass extends Thread implements Runnable{
    protected boolean isRunning;
    protected javax.swing.JTextField field;
    protected SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
    
    public UpdaterClass(javax.swing.JTextField origin){
        this.field = origin;
        isRunning = true;
    }
    @Override
    public void run(){
        while(isRunning){
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    Calendar currentDate = Calendar.getInstance();
                    Date currentTime = currentDate.getTime();
                    field.setText(format.format(currentTime)); //To change body of generated methods, choose Tools | Templates.
                }
                
            });
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException ex) {
                Logger.getLogger(UpdaterClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }
    
}
