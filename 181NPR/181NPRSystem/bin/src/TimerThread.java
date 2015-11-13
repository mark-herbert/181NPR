
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
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
    protected boolean isRunning;
    
    protected JLabel timeLabel;
    protected JLabel dateLabel;
    protected JLabel dayLabel;
    
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    protected SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    protected SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    
    public TimerThread (JLabel timeLabel, JLabel dateLabel, JLabel dayLabel){
        this.timeLabel = timeLabel;
        this.dateLabel = dateLabel;
        this.dayLabel = dayLabel;
        this.isRunning = true;
    }
    
    public TimerThread (JLabel timeLabel, JLabel dateLabel){
        this.timeLabel = timeLabel;
        this.dateLabel = dateLabel;
        this.dayLabel = new JLabel();
        this.isRunning = true;
    }
    
    public void run(){
        while(isRunning){
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    Calendar currentDate = Calendar.getInstance();
                    Date currentTime = currentDate.getTime();
                    dateLabel.setText(dateFormat.format(currentTime));
                    dayLabel.setText(dayFormat.format(currentTime));
                    timeLabel.setText(timeFormat.format(currentTime));
                }
                
            });
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ex) {
                Logger.getLogger(TimerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }
    
}
