
import java.rmi.RemoteException;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kenneth
 */
public class MessageDialog{
    public final int YES = JOptionPane.YES_OPTION;
    public final int NO = JOptionPane.NO_OPTION;
    public final int CANCEL = JOptionPane.CANCEL_OPTION;
    public final int CLOSED = JOptionPane.CANCEL_OPTION;
    public final int OK = JOptionPane.OK_OPTION;
    private final javax.swing.JTextField username = new javax.swing.JTextField();
    private final javax.swing.JPasswordField password = new javax.swing.JPasswordField();
    
    public MessageDialog(){
        
    }
    
    public int confirmationSave(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to save?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public int confirmationDelete(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to delete?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public int confirmationCancel(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to cancel?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public int confirmationExit(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public int confirmationClose(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to close?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public int confirmationSubmit(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to submit?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public int confirmationBack(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to go back?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public int confirmationCheckout(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to checkout?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public void error(javax.swing.JFrame parent, String errorMessage){
       JOptionPane.showMessageDialog(parent, errorMessage, "Error", JOptionPane.ERROR_MESSAGE, null);
    }
    
    public void successful(javax.swing.JFrame parent){
        JOptionPane.showMessageDialog(parent, "Successful", "Successful", JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    public void unsuccessful(javax.swing.JFrame parent){
        JOptionPane.showMessageDialog(parent, "Unsuccessful", "Unsuccessful", JOptionPane.ERROR_MESSAGE, null);
    }
    
    public boolean login(javax.swing.JFrame parent, NPRInterface client) throws RemoteException{
        Object[] message = {
            "Username:", username,
            "Password:", password
        };
        username.setText("");
        password.setText("");
        while(true){
            if(JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,null) == 0){
                if(client.admin_login(new UserLogin(username.getText(),password.getText())) != null){
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Please Try Again.", "Invalid Login", JOptionPane.ERROR_MESSAGE, null);
                    password.setText("");
                }
            } else {
                return false;
            }
        }
    }
    
    public void sentMessages(javax.swing.JFrame parent, int sent, int unsent){
        JOptionPane.showMessageDialog(parent, "Done Sending Text Messages!\nSent Messages:\t"+sent+"\nUnsent Messages:\t"+unsent, "Message", JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    public void sentEmails(javax.swing.JFrame parent, int sent, int unsent){
        JOptionPane.showMessageDialog(parent, "Done Sending Emails!\nSent Emails:\t"+sent+"\nUnsent Emails:\t"+unsent, "Email", JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    public void message(javax.swing.JFrame parent){
        JOptionPane.showMessageDialog(parent, "Message Sent", "Message", JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    public void messageNotSent(javax.swing.JFrame parent){
        JOptionPane.showMessageDialog(parent, "Message not Sent", "Message", JOptionPane.ERROR_MESSAGE, null);
    }
    
    public void email(javax.swing.JFrame parent){
        JOptionPane.showMessageDialog(parent, "Email Sent", "Email", JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    public void emailNotSent(javax.swing.JFrame parent){
        JOptionPane.showMessageDialog(parent, "Email not Sent", "Email", JOptionPane.ERROR_MESSAGE, null);
    }
    
    public void serverError(javax.swing.JFrame parent){
        JOptionPane.showMessageDialog(null, "Server is not yet accessible.\nConsider running the server.", "Server Error", JOptionPane.ERROR_MESSAGE, null);
    }
    
    public int logout(javax.swing.JFrame parent){
        return JOptionPane.showConfirmDialog(parent, "Are you sure you want to logout this account?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    public void warning(javax.swing.JFrame parent, String message){
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE, null);
    }
    
    public void unsuccessful(javax.swing.JFrame parent, String message){
        JOptionPane.showMessageDialog(parent, message, "Unsuccessful", JOptionPane.ERROR_MESSAGE, null);
    }
    
    public void successful(javax.swing.JFrame parent, String message){
        JOptionPane.showMessageDialog(parent, message, "Successful", JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    public void warning(javax.swing.JFrame parent){
        JOptionPane.showMessageDialog(parent, "Warning", "Warning", JOptionPane.WARNING_MESSAGE, null);
    }
    
}
