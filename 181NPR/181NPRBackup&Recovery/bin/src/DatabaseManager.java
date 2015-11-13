
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kenneth
 */
public class DatabaseManager {
    
    private String password;
    private String path = "C:\\wamp\\bin\\mysql\\mysql5.6.17\\bin\\mysqldump";
    private String username;

    public DatabaseManager(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public boolean backup(javax.swing.JProgressBar bar, String savePath){
        try {
            bar.setValue(20);
            bar.update(bar.getGraphics());
            
            Process process = null;

            Thread.sleep(100);
            bar.setValue(40);
            bar.update(bar.getGraphics());
            
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HHmmssa");
            Date date = new Date();
            String filePath = dateFormat.format(date) + ".sql";
            
            Thread.sleep(100);
            bar.setValue(60);
            bar.update(bar.getGraphics());

            String batchCommand = path + " -u " + username + " --password=" + password + " --add-drop-database -B 181nprdb -r \"" + savePath + "\\" + filePath + "\"";
            System.out.println(batchCommand);
            
            Thread.sleep(100);
            bar.setValue(80);
            bar.update(bar.getGraphics());

            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(batchCommand);
            int processComplete = process.waitFor();
            
            Thread.sleep(100);
            bar.setValue(100);
            bar.update(bar.getGraphics());
            
            return processComplete == 0;
        } catch (IOException | InterruptedException ex) {
//            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(null, ex.getMessage());
        }
        return false;
    }
    
    public boolean importSript(javax.swing.JProgressBar bar, File file){
//        mysql -u username -p database_name < file.sql
        try {
            bar.setValue(33);
            bar.update(bar.getGraphics());
            
            Process process = null;
            String batchCommand = "cmd.exe /c cd C:\\wamp\\bin\\mysql\\mysql5.6.17\\bin & mysql -u " + username + " --password=" + password + " 181nprdb < " + file.toString();
            System.out.println(batchCommand);
            
            Thread.sleep(100);
            bar.setValue(66);
            bar.update(bar.getGraphics());
            
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(batchCommand);
            int processComplete = process.waitFor();
            System.out.println("Status: " + processComplete);
            
            Thread.sleep(100);
            bar.setValue(100);
            bar.update(bar.getGraphics());
            
            return processComplete == 0;
        } catch (IOException | InterruptedException ex) {
//            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(null, ex.getMessage());
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}
