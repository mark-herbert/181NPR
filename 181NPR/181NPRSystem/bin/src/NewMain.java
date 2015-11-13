
import java.security.Key;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;



public class NewMain {

    private Connection connection;
    private final String username = "root";
    private final String password = "";
    public static void main(String[] argv) {
        new NewMain().run();
    }
    public void run() {
      try {
          try {
              Class.forName("com.mysql.jdbc.Driver");
              connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", username, password);
          } catch (ClassNotFoundException | SQLException ex) {
              Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
//              label.setText(ex.getMessage());
          }
          String text = "admin";
          String key = "181npdavincicode"; // 128 bit key

          // Create key and cipher
          Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
         Cipher cipher = Cipher.getInstance("AES");
 
         // encrypt the text
         cipher.init(Cipher.ENCRYPT_MODE, aesKey);
         byte[] encrypted = cipher.doFinal(text.getBytes());
         StringBuilder sb = new StringBuilder();
         for(byte b : encrypted){
             sb.append(String.format("%02x", b));
         }
         System.out.println(sb.toString());
         PreparedStatement ps = connection.prepareStatement("UPDATE credentials SET adminPassword=?");
         ps.setString(1, sb.toString());
         ps.executeUpdate();
         
//         HexBinaryAdapter adapter = new HexBinaryAdapter();
//         byte[] b = adapter.unmarshal(sb.toString());
//         
// 
//         // decrypt the text
//         cipher.init(Cipher.DECRYPT_MODE, aesKey);
//         String decrypted = new String(cipher.doFinal(b));
//         System.out.println(decrypted);
      }catch(Exception e) {
         e.printStackTrace();
      }
    }

}
