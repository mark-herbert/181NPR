import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.exceptions.IllegalPdfSyntaxException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kenneth
 */
public class NPRImpl extends UnicastRemoteObject implements NPRInterface {

    private Connection connection;
    private final String username = "root";
    private final String password = "";
    private boolean isLogsUpdated = false;
    private javax.swing.JTextField label;

    public NPRImpl(javax.swing.JTextField label) throws RemoteException {
        this.label = label;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", username, password);
        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
    }

    @Override
    public boolean sendMessage(ArrayList<String> desc) throws RemoteException {
        try {
            String notificationType;
            String myPasscode = "myPasscode";
            String myUsername = "kenneth101795";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT fowizUsername, fowizPasscode FROM account");
            while (rs.next()) {
                myUsername = rs.getString("fowizUsername").trim();
                myPasscode = rs.getString("fowizPasscode").trim();
            }
            String toPhoneNumber = desc.get(0);
            String message = desc.get(1);
            switch (desc.get(2)) {
                case "Reservation":
                    notificationType = "Reservation Update";
                    break;
                case "Registration":
                    notificationType = "Successful Registration";
                    break;
                case "Visitor":
                    notificationType = "Visitor";
                    break;
                case "Due":
                    notificationType = "Due Date";
                    break;
                case "Balance":
                    //balance
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT billingBalance FROM billing WHERE residentIdnum = ?");
                    preparedStatement.setInt(1, Integer.parseInt(desc.get(3).trim()));
                    double bal = 0.0;
                    rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        bal = rs.getDouble("billingBalance");
                    }
                    //date today
                    String d = new SimpleDateFormat("MMM dd, yyyy").format(Calendar.getInstance().getTime());
                    //due date
                    long monthToday = 1L;
                    String dd = null;
                    if (Calendar.getInstance().getTime().getDate() <= 5) {
                        dd = new SimpleDateFormat("MMM 05, yyyy").format(Calendar.getInstance().getTime());
                    } else {
                        Calendar cal = Calendar.getInstance();
                        DateFormat df = new SimpleDateFormat("MMM 05, yyyy");
                        cal.add(Calendar.MONTH, 1);
                        dd = df.format(cal.getTime());
                    }
                    message = message.replace("<datetoday>", d);
                    message = message.replace("<balance>", (bal + ""));
                    message = message.replace("<duedate>", dd);
                    notificationType = "Balance";
                    break;
                case "Roommate":
                    notificationType = "New Roommate";
                    break;
                case "Curfew":
                    notificationType = "Violations";
                    break;
                default:
                    notificationType = "Others";
            }
            String myMessage = URLEncoder.encode(message, "UTF-8");

            String requestUrl = "http://cloud.fowiz.com/api/message_http_api.php?username=" + myUsername + "&phonenumber="
                    + toPhoneNumber + "&message=" + myMessage + "&passcode=" + myPasscode;
            URL url = new URL(requestUrl);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();

            if (uc.getResponseMessage().equals("OK")) {
                uc.disconnect();
                String query = "INSERT INTO notification (notificationTime, notificationDate, notificationMessage, notificationType, notificationContact, notificationMethod, "
                        + "residentIdnum, notificationTable) VALUES (?,?,?,?,?,?,?,?)";

                java.util.Date date = Calendar.getInstance().getTime();
                Date sqlDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(date)).getTime());
                Time sqlTime = new Time(new SimpleDateFormat("HH:mm:ss").parse(new SimpleDateFormat("HH:mm:ss").format(date)).getTime());
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setTime(1, sqlTime);
                preparedStatement.setDate(2, sqlDate);
                preparedStatement.setString(3, message);
                preparedStatement.setString(4, notificationType);
                preparedStatement.setString(5, desc.get(0));
                preparedStatement.setString(6, "text");
                preparedStatement.setInt(7, Integer.parseInt(desc.get(3).trim()));
                preparedStatement.setString(8, desc.get(4));
                preparedStatement.execute();
                return true;
            } else {
                uc.disconnect();
                return false;
            }
        } catch (IOException ex) {
            return false;
        } catch (SQLException ex) {
            return false;
        } catch (ParseException ex) {
            return false;
        }
    }

    @Override
    public boolean sendEmail(ArrayList<String> desc) throws RemoteException {

        // Create the email addresses involved
        try {

            String notificationType;
            String ms = desc.get(2);
            switch (desc.get(3)) {
                case "Reservation":
                    notificationType = "Reservation Update";
                    break;
                case "Registration":
                    notificationType = "Successful Registration";
                    break;
                case "Visitor":
                    notificationType = "Visitor";
                    break;
                case "Due":
                    notificationType = "Due Date";
                    break;
                case "Balance":
                    //balance
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT billingBalance FROM billing WHERE residentIdnum = ?");
                    preparedStatement.setInt(1, Integer.parseInt(desc.get(4).trim()));
                    double bal = 0.0;
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        bal = rs.getDouble("billingBalance");
                    }
                    //date today
                    String d = new SimpleDateFormat("MMM dd, yyyy").format(Calendar.getInstance().getTime());
                    //due date
                    long monthToday = 1L;
                    String dd = null;
                    if (Calendar.getInstance().getTime().getDate() <= 5) {
                        dd = new SimpleDateFormat("MMM 05, yyyy").format(Calendar.getInstance().getTime());
                    } else {
                        Calendar cal = Calendar.getInstance();
                        DateFormat df = new SimpleDateFormat("MMM 05, yyyy");
                        cal.add(Calendar.MONTH, 1);
                        dd = df.format(cal.getTime());
                    }
                    ms = ms.replace("<datetoday>", d);
                    ms = ms.replace("<balance>", (bal + ""));
                    ms = ms.replace("<duedate>", dd);
                    notificationType = "Balance";
                    break;
                case "Roommate":
                    notificationType = "New Roommate";
                    break;
                case "Curfew":
                    notificationType = "Violations";
                    break;
                default:
                    notificationType = "Others";
            }

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT emailAccount, emailPassword, emailPort FROM account");
            rs.next();
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", true); // added this line
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.user", rs.getString("emailAccount"));
            props.put("mail.smtp.password", rs.getString("emailPassword"));
            props.put("mail.smtp.port", rs.getString("emailPort"));
            props.put("mail.smtp.auth", true);

            Session session = Session.getInstance(props, null);
            MimeMessage message = new MimeMessage(session);

            System.out.println("Port: " + session.getProperty("mail.smtp.port"));

            InternetAddress from = new InternetAddress(rs.getString("emailAccount"));
            message.setSubject(desc.get(1));
            message.setFrom(from);
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(desc.get(0)));

            // Create a multi-part to combine the parts
            Multipart multipart = new MimeMultipart("alternative");

            // Create your text message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("181 North Place Residences");

            // Add the text part to the multipart
            multipart.addBodyPart(messageBodyPart);

            // Create the html part
            messageBodyPart = new MimeBodyPart();
            String htmlMessage = ms;
            messageBodyPart.setContent(htmlMessage, "text/html");

            // Add html part to multi part
            multipart.addBodyPart(messageBodyPart);

            // Associate multi-part with message
            message.setContent(multipart);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", rs.getString("emailAccount"), rs.getString("emailPassword"));
            System.out.println("Transport: " + transport.toString());
            transport.sendMessage(message, message.getAllRecipients());

            String query = "INSERT INTO notification (notificationTime, notificationDate, notificationMessage, notificationType, notificationContact, notificationMethod, "
                    + "residentIdnum, notificationTable) VALUES (?,?,?,?,?,?,?,?)";

            java.util.Date date = Calendar.getInstance().getTime();
            Date sqlDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(date)).getTime());
            Time sqlTime = new Time(new SimpleDateFormat("HH:mm:ss").parse(new SimpleDateFormat("HH:mm:ss").format(date)).getTime());
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setTime(1, sqlTime);
            preparedStatement.setDate(2, sqlDate);
            preparedStatement.setString(3, desc.get(1) + ": " + ms);
            preparedStatement.setString(4, notificationType);
            preparedStatement.setString(5, desc.get(0));
            preparedStatement.setString(6, "email");
            preparedStatement.setInt(7, Integer.parseInt(desc.get(4).trim()));
            preparedStatement.setString(8, desc.get(5));
            preparedStatement.execute();

        } catch (AddressException e) {
            // TODO Auto-generated catch block
            label.setText(e.getMessage());
            return false;
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            label.setText(e.getMessage());
            return false;
        } catch (ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return true; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<String> getRooms(int dorm) throws RemoteException {
        ArrayList<String> result = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM room WHERE roomDorm LIKE ?;");
            preparedStatement.setInt(1, dorm);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String res = resultSet.getString(2) + ","
                        + resultSet.getString(3) + ","
                        + resultSet.getString(4);
                result.add(res);
            }
        } catch (SQLException ex) {
            label.setText(ex.getMessage());
            return null;
        }
        return result; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getRoomDetails(String room) throws RemoteException {
        String[] details = new String[3];
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomStatus, roomType, roomDorm FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setInt(1, Integer.parseInt(room.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                details[0] = resultSet.getString("roomStatus");
                details[1] = resultSet.getString("roomType");
                details[2] = resultSet.getString("roomDorm");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return details; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateRoom(String type, String status, String room) throws RemoteException {
        try {
            int roomType = 0;
            int occupants = 0;
            String roomId = " ";
            String q = "SELECT Count(1) AS numberOfOccupants FROM resident WHERE roomIdnum = ? UNION "
                    + "SELECT Count(1) AS numberOfOccupants FROM reservation WHERE roomIdnum = ?";

            switch (type) {
                case "S":
                    roomType = 1;
                    break;
                case "D":
                    roomType = 2;
                    break;
                case "T":
                    roomType = 3;
                    break;
            }

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE room SET roomType = ? , roomStatus = ? WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, status);
            preparedStatement.setInt(3, Integer.parseInt(room.trim()));
            preparedStatement.execute();

            if (!status.equalsIgnoreCase("not available")) {
                preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber LIKE ?");
                preparedStatement.setString(1, room);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    roomId = rs.getString("roomIdnum");
                }

                preparedStatement = connection.prepareStatement(q);
                preparedStatement.setInt(1, Integer.parseInt(roomId));
                preparedStatement.setInt(2, Integer.parseInt(roomId));
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    occupants += rs.getInt("numberOfOccupants");
                }

                if (occupants == 0) {
                    q = "UPDATE room SET roomStatus = 'unoccupied' WHERE roomNumber LIKE ?";
                } else if (occupants > 0 && occupants < roomType) {
                    q = "UPDATE room SET roomStatus = 'partially occupied' WHERE roomNumber LIKE ?";
                } else if (occupants == roomType) {
                    q = "UPDATE room SET roomStatus = 'fully occupied' WHERE roomNumber LIKE ?";
                }

                preparedStatement = connection.prepareStatement(q);
                preparedStatement.setString(1, room);
                preparedStatement.execute();
            }

        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return true;
        }
        return false; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insertTenantReservation(ArrayList<String> reservationDetails) throws RemoteException {
        try {
            // the mysql insert statement
            String rumId = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` FROM room where roomNumber = ?");
            preparedStatement.setInt(1, Integer.parseInt(reservationDetails.get(0).trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rumId = resultSet.getString("roomIdnum");
            }

            preparedStatement = connection.prepareStatement("insert into reservation (roomIdnum, reservationLname, reservationFname,"
                    + " reservationMname, reservationHomeaddress, reservationZipCode, reservationMobileNo,"
                    + " reservationSchoolTerm, reservationEmail, reservationAyFrom,"
                    + " reservationAyTo, reservationOthers, reservationDatePaid, reservationStatus)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setString(1, rumId);
            preparedStatement.setString(2, reservationDetails.get(1).trim().substring(0, 1).toUpperCase() + reservationDetails.get(1).trim().substring(1, reservationDetails.get(1).trim().length()));
            preparedStatement.setString(3, reservationDetails.get(2).trim().substring(0, 1).toUpperCase() + reservationDetails.get(2).trim().substring(1, reservationDetails.get(2).trim().length()));
            if (reservationDetails.get(3).length() != 0) {
                preparedStatement.setString(4, reservationDetails.get(3).trim().substring(0, 1).toUpperCase() + reservationDetails.get(3).trim().substring(1, reservationDetails.get(3).trim().length()));
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (reservationDetails.get(4).length() != 0) {
                preparedStatement.setString(5, reservationDetails.get(4));
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (reservationDetails.get(5).length() != 0) {
                preparedStatement.setString(6, reservationDetails.get(5) + " ");
            } else {
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            preparedStatement.setString(7, reservationDetails.get(6) + " ");
            if (reservationDetails.get(7).length() != 0) {
                preparedStatement.setString(8, reservationDetails.get(7) + " ");
            } else {
                preparedStatement.setNull(8, Types.VARCHAR);
            }
            if (reservationDetails.get(8).length() != 0) {
                preparedStatement.setString(9, reservationDetails.get(8) + " ");
            } else {
                preparedStatement.setNull(9, Types.VARCHAR);
            }
            if (reservationDetails.get(9).length() != 0) {
                preparedStatement.setInt(10, Integer.parseInt(reservationDetails.get(9).trim()));
            } else {
                preparedStatement.setNull(10, Types.INTEGER);
            }
            if (reservationDetails.get(10).length() != 0) {
                preparedStatement.setInt(11, Integer.parseInt(reservationDetails.get(10).trim()));
            } else {
                preparedStatement.setNull(11, Types.INTEGER);
            }
            if (reservationDetails.get(11).length() != 0) {
                preparedStatement.setString(12, reservationDetails.get(11) + " ");
            } else {
                preparedStatement.setNull(12, Types.VARCHAR);
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = df.parse(reservationDetails.get(12).trim());
            java.sql.Date sDate = new java.sql.Date(utilDate.getTime());

            preparedStatement.setDate(13, sDate);
            preparedStatement.setString(14, "Pending");

            preparedStatement.execute();

//            preparedStatement = connection.prepareStatement("SELECT `reservationLname`, `reservationFname`, `reservationLname` "
//                    + "FROM reservation WHERE reservationLname LIKE ?");
//            preparedStatement.setString(1, reservationDetails.get(1).trim());
//            resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                int count = 0;
//                String rumnum = "";
//                preparedStatement = connection.prepareStatement("SELECT residentIdnum AS id FROM resident WHERE roomIdnum = ? "
//                        + "UNION SELECT reservationIdnum AS id FROM reservation WHERE roomIdnum = ? AND reservationStatus LIKE 'Pending'");
//                preparedStatement.setInt(1, Integer.parseInt(rumId.trim()));
//                preparedStatement.setInt(2, Integer.parseInt(rumId.trim()));
//                resultSet = preparedStatement.executeQuery();
//                while (resultSet.next()) {
//                    count++;
//                }
//                if (reservationDetails.get(13).trim().contains("Triple") & count == 1) {
//                    preparedStatement = connection.prepareStatement("UPDATE room SET roomStatus = 'partially occupied' WHERE roomIdnum = ?");
//                    preparedStatement.setInt(1, Integer.parseInt(rumId.trim()));
//                    preparedStatement.executeUpdate();
//                } else if (reservationDetails.get(13).trim().contains("Triple") & count == 2) {
//                    preparedStatement = connection.prepareStatement("UPDATE room SET roomStatus = 'partially occupied' WHERE roomIdnum = ?");
//                    preparedStatement.setInt(1, Integer.parseInt(rumId.trim()));
//                    preparedStatement.executeUpdate();
//                } else if (reservationDetails.get(13).trim().contains("Double") & count == 1) {
//                    preparedStatement = connection.prepareStatement("UPDATE room SET roomStatus = 'partially occupied' WHERE roomIdnum = ?");
//                    preparedStatement.setInt(1, Integer.parseInt(rumId.trim()));
//                    preparedStatement.executeUpdate();
//                } else {
//                    preparedStatement = connection.prepareStatement("UPDATE room SET roomStatus = 'fully occupied' WHERE roomIdnum = ?");
//                    preparedStatement.setInt(1, Integer.parseInt(rumId.trim()));
//                    preparedStatement.executeUpdate();
//                }
//            } else {
//                return false;
//            }
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return true;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateAdmin(AdminImpl adminImpl) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE admin SET adminLname=?, adminFname=?, adminMname=?, adminGender=?, adminBirthdate=?, adminEmail=?, adminPicture=? WHERE adminIdnum = ?");
            preparedStatement.setString(1, adminImpl.getLName());
            preparedStatement.setString(2, adminImpl.getFName());
            preparedStatement.setString(3, adminImpl.getMName());
            preparedStatement.setString(4, adminImpl.getGender());

            DateFormat oldFormat = new SimpleDateFormat("MMM dd, yyyy");
            java.util.Date utilDate = oldFormat.parse(adminImpl.getBirthdate());
            DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateF = newFormat.format(utilDate);
            utilDate = newFormat.parse(dateF);
            java.sql.Date sqlDate = new Date(utilDate.getTime());
            newFormat.format(sqlDate);

            preparedStatement.setDate(5, sqlDate);
            preparedStatement.setString(6, adminImpl.getEmail());
            Blob blob = connection.createBlob();
            if (adminImpl.getPicture() != null) {
                blob.setBytes(1, adminImpl.getPicture());
                preparedStatement.setBlob(7, blob);
            } else {
                preparedStatement.setNull(7, Types.BLOB);
            }
            preparedStatement.setInt(8, Integer.parseInt(adminImpl.getUser().trim()));
            return preparedStatement.execute();
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return true;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changePassword(UserLogin user) throws RemoteException {
        String pass = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT adminPassword FROM credentials WHERE adminIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(user.getUserId().trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                pass = resultSet.getString("adminPassword");
            }
            String key = "181npdavincicode"; // 128 bit key

            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            HexBinaryAdapter adapter = new HexBinaryAdapter();
            byte[] b = adapter.unmarshal(pass);

            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(b));
            
            if (decrypted.equals(user.getUser())) {
                preparedStatement = connection.prepareStatement("UPDATE credentials SET adminPassword=? WHERE adminIdnum=?");
                
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encrypted = cipher.doFinal(user.getPass().getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte bytes : encrypted) {
                    sb.append(String.format("%02x", bytes));
                }
                
                preparedStatement.setString(1, sb.toString());
                preparedStatement.setInt(2, Integer.parseInt(user.getUserId().trim()));
                return preparedStatement.execute();
            }
        } catch (SQLException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return true; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<String> getAvailableRoom() throws RemoteException {
        ArrayList<String> result = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT roomNumber FROM room WHERE roomType NOT LIKE 'SU' AND roomStatus NOT LIKE 'not available' AND roomStatus NOT LIKE 'fully occupied'");
            while (resultSet.next()) {
                result.add(resultSet.getString("roomNumber"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return result;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<String> getAllAdmin() throws RemoteException {
        ArrayList<String> result = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT CONCAT(adminFname, ' ',SUBSTRING(adminMname,1,1),'. ' , adminLname) AS admin_fullname FROM admin");
            while (resultSet.next()) {
                result.add(resultSet.getString("admin_fullname"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return result; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AdminImpl getAdminProfile(String name) throws RemoteException {
        AdminImpl adminImpl = new AdminImpl();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM admin WHERE CONCAT(adminFname, ' ',SUBSTRING(adminMname,1,1),'. ' , adminLname) LIKE ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                adminImpl.setLName(resultSet.getString("adminLname"));
                adminImpl.setFName(resultSet.getString("adminFname"));
                adminImpl.setMName(resultSet.getString("adminMname"));
                adminImpl.setEmail(resultSet.getString("adminEmail"));
                if(resultSet.getBlob("adminPicture") != null){
                    adminImpl.setPicture(resultSet.getBlob("adminPicture").getBytes(1, (int) resultSet.getBlob("adminPicture").length()));
                } else {
                    adminImpl.setPicture(null);
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return adminImpl;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String admin_login(UserLogin user) throws RemoteException {
        try {
            String key = "181npdavincicode";
            
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM credentials");
            while (resultSet.next()) {
                HexBinaryAdapter adapter = new HexBinaryAdapter();
                byte[] b = adapter.unmarshal(resultSet.getString("adminPassword"));

                // decrypt the text
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                String decrypted = new String(cipher.doFinal(b));
                if (resultSet.getString("adminUsername").equals(user.getUser()) && decrypted.equals(user.getPass())) {
                    return (resultSet.getInt("adminIdnum") + "");
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
//            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AdminImpl getUserAdmin(String user) throws RemoteException {
        AdminImpl adminImpl = new AdminImpl();
        try {
            isLogsUpdated = true;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT CONCAT(adminFname, ' ',SUBSTRING(adminMname,1,1),'. ' , adminLname) AS admin_fullname, "
                    + "adminLname, adminFname, adminMname, adminGender, adminBirthdate, adminEmail FROM admin WHERE adminIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(user.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                adminImpl.setLName(resultSet.getString("adminLname"));
                adminImpl.setFName(resultSet.getString("adminFname"));
                adminImpl.setMName(resultSet.getString("adminMname"));
                adminImpl.setGender(resultSet.getString("adminGender"));
                adminImpl.setEmail(resultSet.getString("adminEmail"));
                adminImpl.setBirthdate(resultSet.getDate("adminBirthdate").toString());
                adminImpl.setFullName(resultSet.getString("admin_fullname"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return adminImpl;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getProfilePicture(String user) throws RemoteException {
        int length = 0;
        Blob blobContainer = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT adminPicture FROM admin WHERE adminIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(user.trim()));//To change body of generated methods, choose Tools | Templates.
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getBlob("adminPicture") != null) {
                    blobContainer = resultSet.getBlob("adminPicture");
                    length = (int) blobContainer.length();
                    return blobContainer.getBytes(1, length);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insertRegistrationAdmin(AdminImpl adminImpl, UserLogin userLogin) throws RemoteException {
        boolean success = false;
        int admin_id = 0;
        try {
            Blob blob = connection.createBlob();
            if (adminImpl.getPicture() != null) {
                blob.setBytes(1, adminImpl.getPicture());
            } else {
                blob = null;
            }
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO admin (adminFname, adminMname, adminLname, adminGender, adminBirthdate, adminEmail, adminPicture) VALUES (?,?,?,?,?,?,?)");
            preparedStatement.setString(1, adminImpl.getFName());
            preparedStatement.setString(2, adminImpl.getMName());
            preparedStatement.setString(3, adminImpl.getLName());
            preparedStatement.setString(4, adminImpl.getGender());

            DateFormat oldFormat = new SimpleDateFormat("MMM dd, yyyy");
            java.util.Date utilDate = oldFormat.parse(adminImpl.getBirthdate());
            DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateF = newFormat.format(utilDate);
            utilDate = newFormat.parse(dateF);
            java.sql.Date sqlDate = new Date(utilDate.getTime());
            newFormat.format(sqlDate);

            preparedStatement.setDate(5, sqlDate);//date
            preparedStatement.setString(6, adminImpl.getEmail());
            preparedStatement.setBlob(7, blob);//dito ka magfocus
            success = preparedStatement.execute();

            preparedStatement = connection.prepareStatement("SELECT adminIdnum FROM admin WHERE adminFname LIKE ? AND adminMname LIKE ? AND adminLname LIKE ?");
            preparedStatement.setString(1, adminImpl.getFName());
            preparedStatement.setString(2, adminImpl.getMName());
            preparedStatement.setString(3, adminImpl.getLName());
            ResultSet r = preparedStatement.executeQuery();
            if (r.next()) {
                admin_id = r.getInt("adminIdnum");
            }

            preparedStatement = connection.prepareStatement("INSERT INTO credentials (adminIdnum,adminUsername,adminPassword) VALUES (?,?,?)");
            preparedStatement.setInt(1, admin_id);
            preparedStatement.setString(2, userLogin.getUser());

            String key = "181npdavincicode"; // 128 bit key

            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            
            byte[] encrypted = cipher.doFinal(userLogin.getPass().getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : encrypted) {
                sb.append(String.format("%02x", b));
            }
            preparedStatement.setString(3, sb.toString());
            success = preparedStatement.execute();
        } catch (SQLException | ParseException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return true;
        }
        return success;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String time_In_Out(String id) throws RemoteException {
        try {
            isLogsUpdated = true;
            boolean isEmpty = true;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE residentIdnum = ? AND status NOT LIKE 'Leave'");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                isEmpty = false;
            }
            if (isEmpty) {
                return null;
            }
            String status = "";
            preparedStatement = connection.prepareStatement("SELECT logsStatus FROM logs WHERE residentIdnum = ? ORDER BY logsIdnum DESC LIMIT 1");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                status = resultSet.getString("logsStatus");
            }

            Calendar current = Calendar.getInstance();

            java.util.Date thisDate = current.getTime();
            DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            String utilDate = newFormat.format(thisDate);
            thisDate = newFormat.parse(utilDate);
            java.sql.Date sqlDate = new Date(thisDate.getTime());
            newFormat.format(sqlDate);

            thisDate = current.getTime();
            newFormat = new SimpleDateFormat("HH:mm:ss");
            String utilTime = newFormat.format(thisDate);
            thisDate = newFormat.parse(utilTime);
            java.sql.Time sqlTime = new Time(thisDate.getTime());
            newFormat.format(sqlTime);

            if (status.isEmpty()) {
                preparedStatement = connection.prepareStatement("SELECT billingIdnum FROM billing WHERE residentIdnum = ? ORDER BY billingIdnum ASC LIMIT 1");
                preparedStatement.setInt(1, Integer.parseInt(id));
                resultSet = preparedStatement.executeQuery();
                String regId = null;
                while (resultSet.next()) {
                    regId = resultSet.getString("billingIdnum");
                }
                preparedStatement = connection.prepareStatement("UPDATE billing SET billingDatein=? WHERE residentIdnum = ?");
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setInt(2, Integer.parseInt(regId));
                preparedStatement.execute();
                status = "OUT";
            }

            switch (status) {
                case "IN":
                    preparedStatement = connection.prepareStatement("INSERT INTO logs (logsDate,logsTime,logsStatus,residentIdnum) VALUES (?,?,?,?)");
                    preparedStatement.setDate(1, sqlDate);
                    preparedStatement.setTime(2, sqlTime);
                    preparedStatement.setString(3, "OUT");
                    preparedStatement.setInt(4, Integer.parseInt(id.trim()));
                    if (!preparedStatement.execute()) {
                        return id;
                    } else {
                        return null;
                    }
                case "OUT":
                    preparedStatement = connection.prepareStatement("INSERT INTO logs (logsDate,logsTime,logsStatus,residentIdnum) VALUES (?,?,?,?)");
                    preparedStatement.setDate(1, sqlDate);
                    preparedStatement.setTime(2, sqlTime);
                    preparedStatement.setString(3, "IN");
                    preparedStatement.setInt(4, Integer.parseInt(id.trim()));
                    if (!preparedStatement.execute()) {
                        return id;
                    } else {
                        return null;
                    }
            }
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResidentImpl getResidentInformation(String id) throws RemoteException {
        try {
            String logsStatus = "";
            String name = "";
            int length = 0;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT logsStatus FROM logs WHERE residentIdnum = ? ORDER BY logsIdnum DESC LIMIT 1");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                logsStatus = resultSet.getString("logsStatus");
                break;
            }
            preparedStatement = connection.prepareStatement("SELECT CONCAT(residentFname, ' ' , residentLname) AS resident_fullname, "
                    + "picture FROM resident WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            resultSet = preparedStatement.executeQuery();

            ResidentImpl residentImpl = new ResidentImpl();

            while (resultSet.next()) {
                name = resultSet.getString("resident_fullname");
                if (resultSet.getBlob("picture") != null) {
                    length = (int) resultSet.getBlob("picture").length();
                    residentImpl.setPicture(resultSet.getBlob("picture").getBytes(1, length));
                } else {
                    residentImpl.setPicture(null);
                }
            }
            residentImpl.setFullName(name);
            residentImpl.setLastStatus(logsStatus);
            return residentImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<ResidentImpl> getAllLogs(String date) throws RemoteException {
        try {
            ArrayList<ResidentImpl> logs = new ArrayList<>();
            DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date thisDate = newFormat.parse(date);
            java.sql.Date sqlDate = new Date(thisDate.getTime());
            newFormat.format(sqlDate);
            if (true) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT resident.residentIdnum, CONCAT(residentFname, ' ' , residentLname) AS resident_fullname, "
                        + "logsStatus, logsTime FROM logs NATURAL JOIN resident WHERE logsDate LIKE ?  ORDER BY logsTime ASC");
                preparedStatement.setDate(1, sqlDate);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    newFormat = new SimpleDateFormat("hh:mm a");
                    String utilTime = newFormat.format(resultSet.getTime("logsTime"));
                    thisDate = newFormat.parse(utilTime);

                    logs.add(new ResidentImpl(resultSet.getString("residentIdnum"), resultSet.getString("resident_fullname"), resultSet.getString("logsStatus"), newFormat.format(thisDate)));
                }
                isLogsUpdated = false;
            }
            return logs;
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResidentImpl getNumber(String id) throws RemoteException {
        try {
            ResidentImpl residentImpl = new ResidentImpl();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT residentMobileNo, registrationFatherMobileNo, registrationMotherMobileNo, registrationGuardianMobileNo "
                    + "FROM resident LEFT JOIN registration ON resident.registrationIdnum = registration.registrationIdnum "
                    + "WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                residentImpl.setResidentContact(resultSet.getString("residentMobileNo"));
                residentImpl.setFatherContact(resultSet.getString("registrationFatherMobileNo"));
                residentImpl.setMotherContact(resultSet.getString("registrationMotherMobileNo"));
                residentImpl.setGuardianContact(resultSet.getString("registrationGuardianMobileNo"));
            }
            return residentImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResidentImpl getEmail(String id) throws RemoteException {
        try {
            ResidentImpl residentImpl = new ResidentImpl();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT residentEmail, registrationFatherEmail, registrationMotherEmail "
                    + "FROM resident LEFT JOIN registration ON resident.registrationIdnum = registration.registrationIdnum "
                    + "WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                residentImpl.setResidentContact(resultSet.getString("residentEmail"));
                residentImpl.setFatherContact(resultSet.getString("registrationFatherEmail"));
                residentImpl.setMotherContact(resultSet.getString("registrationMotherEmail"));
                residentImpl.setGuardianContact(null);
            }
            return residentImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<LogsImpl> getAllResidentsForNotif(String date) throws RemoteException {
        try {
            ArrayList<LogsImpl> id = new ArrayList<>();
            LogsImpl logsImpl = new LogsImpl();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT residentIdnum, CONCAT(residentFname, ' ' , residentLname) AS resident_fullname FROM resident ORDER BY residentIdnum ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id.add(new LogsImpl(resultSet.getString("residentIdnum"), resultSet.getString("resident_fullname")));
            }
            DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date thisDate = newFormat.parse(date);
            java.sql.Date sqlDate = new Date(thisDate.getTime());
            newFormat.format(sqlDate);
            for (int i = 0; i < id.size(); i++) {
                preparedStatement = connection.prepareStatement("SELECT logsTime FROM logs WHERE logsDate LIKE ? AND residentIdnum = ? AND logsStatus LIKE 'IN' ORDER BY logsTime DESC LIMIT 1");
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setInt(2, Integer.parseInt(id.get(i).getId()));
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    id.get(i).setTimeIn(resultSet.getTime("logsTime").toString());
                }
                preparedStatement = connection.prepareStatement("SELECT logsTime FROM logs WHERE logsDate LIKE ? AND residentIdnum = ? AND logsStatus LIKE 'OUT' ORDER BY logsIdnum DESC LIMIT 1");
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setInt(2, Integer.parseInt(id.get(i).getId()));
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    id.get(i).setTimeOut(resultSet.getTime("logsTime").toString());
                }
            }
            return id;
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<LogsImpl> getAllResidentForNotifSearch(String date, String search) throws RemoteException {
        try {
            ArrayList<LogsImpl> id = new ArrayList<>();
            LogsImpl logsImpl = new LogsImpl();
            String s = "%" + search + "%";
            String query = "SELECT residentIdnum, CONCAT(residentFname, ' ' , residentLname) AS resident_fullname "
                    + "FROM resident WHERE residentIdnum LIKE ? OR residentFname LIKE ? OR residentMname LIKE ? OR residentLname LIKE ? ORDER BY residentIdnum ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, s);
            preparedStatement.setString(2, s);
            preparedStatement.setString(3, s);
            preparedStatement.setString(4, s);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id.add(new LogsImpl(resultSet.getString("residentIdnum"), resultSet.getString("resident_fullname")));
            }
            DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date thisDate = newFormat.parse(date);
            java.sql.Date sqlDate = new Date(thisDate.getTime());
            newFormat.format(sqlDate);
            for (int i = 0; i < id.size(); i++) {
                preparedStatement = connection.prepareStatement("SELECT logsTime FROM logs WHERE logsDate LIKE ? AND residentIdnum = ? AND logsStatus LIKE 'IN' ORDER BY logsIdnum DESC LIMIT 1");
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setInt(2, Integer.parseInt(id.get(i).getId()));
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    id.get(i).setTimeIn(resultSet.getTime("logsTime").toString());
                }
                preparedStatement = connection.prepareStatement("SELECT logsTime FROM logs WHERE logsDate LIKE ? AND residentIdnum = ? AND logsStatus LIKE 'OUT' ORDER BY logsIdnum DESC LIMIT 1");
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setInt(2, Integer.parseInt(id.get(i).getId()));
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    id.get(i).setTimeOut(resultSet.getTime("logsTime").toString());
                }
            }
            return id;
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<ResidentImpl> getAllLogsSearch(String date, String search) throws RemoteException {
        try {
            String query = "SELECT resident.residentIdnum, CONCAT(residentFname, ' ' , residentLname) AS resident_fullname, "
                    + "logsStatus, logsTime FROM logs NATURAL JOIN resident WHERE logsDate = ? AND "
                    + "(resident.residentIdnum = ? OR residentFname LIKE ? OR residentMname LIKE ? OR residentLname LIKE ? OR logsStatus LIKE ?) ORDER BY logsIdnum ASC";
            String s = "%" + search.trim() + "%";
            ArrayList<ResidentImpl> logs = new ArrayList<>();
            DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date thisDate = newFormat.parse(date);
            java.sql.Date sqlDate = new Date(thisDate.getTime());
            newFormat.format(sqlDate);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, sqlDate);
            preparedStatement.setString(2, s);
            preparedStatement.setString(3, s);
            preparedStatement.setString(4, s);
            preparedStatement.setString(5, s);
            preparedStatement.setString(6, s);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                newFormat = new SimpleDateFormat("hh:mm a");
                String utilTime = newFormat.format(resultSet.getTime("logsTime"));
                thisDate = newFormat.parse(utilTime);

                logs.add(new ResidentImpl(resultSet.getString("residentIdnum"), resultSet.getString("resident_fullname"), resultSet.getString("logsStatus"), newFormat.format(thisDate)));
            }
            return logs;
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<RoomImpl> getNumberOfOccupants() throws RemoteException {
        ResultSet resultSet;
        ArrayList<RoomImpl> roomImpl = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.personPerRoom");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomImpl.add(new RoomImpl(resultSet.getString("roomIdnum"), resultSet.getString("roomNumber"), resultSet.getInt("numberOfResident")));
            }
            return roomImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<ResidentImpl> getOccupants(String roomNumber) throws RemoteException {
        ArrayList<ResidentImpl> roomImpl = new ArrayList<>();
        ResultSet resultSet;
        String query = "SELECT residentIdnum, CONCAT(residentFname, ' ' , residentLname) AS resident_fullname, picture "
                + "FROM resident NATURAL JOIN room "
                + "WHERE roomNumber LIKE ? AND status NOT LIKE 'Leave'";
        Blob blobContainer = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, roomNumber);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                blobContainer = resultSet.getBlob("picture");
                if (blobContainer != null) {
                    roomImpl.add(new ResidentImpl(resultSet.getString("residentIdnum"), resultSet.getString("resident_fullname"), blobContainer.getBytes(1, (int) blobContainer.length())));
                } else {
                    roomImpl.add(new ResidentImpl(resultSet.getString("residentIdnum"), resultSet.getString("resident_fullname"), null));
                }
            }
            return roomImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<ResidentImpl> searchTenant(String searchKey) throws RemoteException {
        try {
            ArrayList<ResidentImpl> residentImpl = new ArrayList<>();
            ResultSet resultSet;
            String skey = "%" + searchKey + "%";
            String query = "SELECT CONCAT(residentFname, ' ' , residentLname) AS fullName, logsStatus "
                    + "FROM (SELECT residentFname, residentMname, residentLname, logsStatus, resident.residentIdnum FROM resident LEFT JOIN logs "
                    + "ON resident.residentIdnum = logs.residentIdnum ORDER BY logsIdnum DESC) as t1 "
                    + "WHERE residentFname LIKE ? OR residentMname LIKE ? OR residentLName LIKE ? "
                    + "GROUP BY residentIdnum ORDER BY residentFname ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, skey);
            preparedStatement.setString(2, skey);
            preparedStatement.setString(3, skey);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                residentImpl.add(new ResidentImpl(null, resultSet.getString("fullname"), resultSet.getString("logsStatus"), null));
            }
            return residentImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertVisitor(VisitorImpl v) throws RemoteException {
        try {
            int id = 0;
            String query;
            PreparedStatement preparedStatement;
            String table = "resident";
            if ("administrator".equals(v.getResidentFullName())) {
                id = 00000001;
                table = "administrator";
            } else {
                query = "SELECT residentIdnum FROM resident WHERE CONCAT(residentFname, ' ' , residentLname) LIKE ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, v.getResidentFullName());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    id = Integer.parseInt(resultSet.getString("residentIdnum"));
                }
            }
            query = "INSERT INTO visitors (visitorsName, visitorsDate, visitorsTimein, visitorsPurpose, residentIdnum, visitorTable) "
                    + "VALUES (?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, v.getVisitorName());

            java.util.Date date = new java.util.Date();
            DateFormat old = new SimpleDateFormat("yyyy-MM-dd");
            date = old.parse(old.format(date));
            Date sqlDate = new Date(date.getTime());
            old.format(sqlDate);
            preparedStatement.setDate(2, sqlDate);

            java.util.Date daDate = new java.util.Date();
            DateFormat thisOld = new SimpleDateFormat("HH:mm:ss");
            daDate = thisOld.parse(thisOld.format(daDate));
            Time thisSqlDate = new Time(daDate.getTime());
            thisOld.format(thisSqlDate);
            preparedStatement.setTime(3, thisSqlDate);

            preparedStatement.setString(4, v.getPurpose());
            preparedStatement.setInt(5, id);
            preparedStatement.setString(6, table);

            return preparedStatement.execute();
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean logoutVisitor(VisitorImpl v) throws RemoteException {
        try {
            //        "UPDATE credentials SET adminPassword=? WHERE adminIdnum=?"
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE visitors SET visitorsTimeout=? WHERE visitorsName LIKE ?");

            java.util.Date daDate = new java.util.Date();
            DateFormat thisOld = new SimpleDateFormat("HH:mm:ss");
            daDate = thisOld.parse(thisOld.format(daDate));
            Time thisSqlDate = new Time(daDate.getTime());
            thisOld.format(thisSqlDate);

            preparedStatement.setTime(1, thisSqlDate);
            preparedStatement.setString(2, v.getVisitorName());
            return preparedStatement.execute();
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return false;
    }

    @Override
    public ArrayList<VisitorImpl> searchVisitor(String searchKey, boolean b) throws RemoteException {
        ArrayList<VisitorImpl> visitorImpl = new ArrayList<>();
        ResultSet resultSet;
        String sKey = "%" + searchKey + "%";
        String query;
        if (b) {
            query = "SELECT visitorsName, visitorsPurpose, CONCAT(residentFname, ' ' , residentLname) AS fullName, visitorsTimein, visitorTable "
                    + "FROM (SELECT * FROM visitors NATURAL JOIN resident ORDER BY visitorsIdnum DESC) AS t1 WHERE visitorsName LIKE ? AND visitorsTimeout IS null GROUP BY visitorsName";
        } else {
            query = "SELECT visitorsName, visitorsPurpose, CONCAT(residentFname, ' ' , residentLname) AS fullName, visitorsTimein, visitorTable "
                    + "FROM (SELECT * FROM visitors NATURAL JOIN resident ORDER BY visitorsIdnum DESC) AS t1 WHERE visitorsName LIKE ? GROUP BY visitorsName";
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sKey);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if ("administrator".equals(resultSet.getString("visitorTable"))) {
                    visitorImpl.add(new VisitorImpl(resultSet.getString("visitorsName"), null, "Administrator", resultSet.getString("visitorsTimein")));
                } else {
                    visitorImpl.add(new VisitorImpl(resultSet.getString("visitorsName"), null, resultSet.getString("fullname"), resultSet.getString("visitorsTimein")));
                }
            }
            return visitorImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertGroupVisit(VisitorImpl visitorImpl) throws RemoteException {
        try {
            String query = "SELECT residentIdnum FROM resident WHERE CONCAT(residentFname, ' ' , residentLname) LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, visitorImpl.getResidentFullName());
            int idNumber = 0;
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                idNumber = rs.getInt("residentIdnum");
            }
            query = "INSERT INTO visitors (visitorsName,visitorsDate,visitorsTimein,visitorsTimeout,residentIdnum,visitorsValidId,visitorsArea,visitorsStartDate) VALUES (?,?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, visitorImpl.getVisitorName());

            DateFormat current = new SimpleDateFormat("MMM dd, yyyy");
            DateFormat old = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = old.parse(old.format(current.parse(visitorImpl.getDate())));
            Date sqlDate = new Date(date.getTime());
            old.format(sqlDate);

            preparedStatement.setDate(2, sqlDate);

            DateFormat thisCurrent = new SimpleDateFormat("hh:mm:ss a");
            DateFormat thisOld = new SimpleDateFormat("HH:mm:ss");
            java.util.Date daDate = thisOld.parse(thisOld.format(thisCurrent.parse(visitorImpl.getTimeIn())));
            Time thisSqlDate = new Time(daDate.getTime());
            thisOld.format(thisSqlDate);

            preparedStatement.setTime(3, thisSqlDate);

            thisCurrent = new SimpleDateFormat("hh:mm:ss a");
            thisOld = new SimpleDateFormat("HH:mm:ss");
            daDate = thisOld.parse(thisOld.format(thisCurrent.parse(visitorImpl.getTimeOut())));
            thisSqlDate = new Time(daDate.getTime());
            thisOld.format(thisSqlDate);

            preparedStatement.setTime(4, thisSqlDate);
            preparedStatement.setInt(5, idNumber);
            preparedStatement.setString(6, visitorImpl.getValidId());
            preparedStatement.setString(7, visitorImpl.getArea());

            current = new SimpleDateFormat("MMM dd, yyyy");
            old = new SimpleDateFormat("yyyy-MM-dd");
            date = old.parse(old.format(current.parse(visitorImpl.getStartDate())));
            sqlDate = new Date(date.getTime());
            old.format(sqlDate);

            preparedStatement.setDate(8, sqlDate);
            return preparedStatement.execute();
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return Boolean.TRUE;
    }

    @Override
    public ArrayList<VisitorImpl> getGroupVisit() throws RemoteException {
        try {
            ArrayList<VisitorImpl> visitorImpl = new ArrayList<>();
            String query = "SELECT CONCAT(residentFname, ' ' , residentLname) AS resident_fullname, visitorsTimein, visitorsTimeout, "
                    + "visitorsArea, visitorCount FROM groupvisitcount NATURAL JOIN resident WHERE visitorsArea IS NOT NULL AND visitorsStartDate = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date sqlDate = new Date(dFormat.parse(dFormat.format(Calendar.getInstance().getTime())).getTime());

            preparedStatement.setDate(1, sqlDate);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String rFName = rs.getString("resident_fullname");
                String tIn = rs.getString("visitorsTimein");
                String tOut = rs.getString("visitorsTimeout");
                String area = rs.getString("visitorsArea");
                int count = rs.getInt("visitorCount");
                visitorImpl.add(new VisitorImpl(rFName, tIn, tOut, area, count));
            }
            return visitorImpl;
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<VisitorImpl> getGroupVisitNames(VisitorImpl visitorImpl) throws RemoteException {
        return null;
    }

    @Override
    public ArrayList<ResidentImpl> getAllResidentFingerprint() throws RemoteException {
        try {
            ArrayList<ResidentImpl> residentImpl = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM fingerprintOwner WHERE status NOT LIKE 'Leave'");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                residentImpl.add(new ResidentImpl(0, rs.getString("residentIdnum"), rs.getString("resident_fullname"), rs.getString("fingerprintData1"), rs.getString("fingerprintData2"), rs.getString("status")));
            }
            return residentImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean registerResidentFingerprint(ResidentImpl residentImpl) throws RemoteException {
        boolean insert = true;
        boolean updateResident = true;
        boolean updateRegistration = true;
        int registrationIdnum = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO biometrics (fingerprintData1,fingerprintData2,status,residentIdnum) VALUES (?,?,?,?)");
            preparedStatement.setString(1, residentImpl.getFingerprint1());
            preparedStatement.setString(2, residentImpl.getFingerprint2());
            preparedStatement.setString(3, "active");
            preparedStatement.setInt(4, Integer.parseInt(residentImpl.getId().trim()));

            insert = preparedStatement.execute();

            preparedStatement = connection.prepareStatement("SELECT registrationIdnum FROM resident WHERE residentIdnum=?");
            preparedStatement.setInt(1, Integer.parseInt(residentImpl.getId().trim()));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                registrationIdnum = rs.getInt("registrationIdnum");
            }

            preparedStatement = connection.prepareStatement("UPDATE registration SET registrationStatus = 'Active' WHERE registrationIdnum=?");
            preparedStatement.setInt(1, registrationIdnum);
            updateRegistration = preparedStatement.execute();

            preparedStatement = connection.prepareStatement("UPDATE resident SET status = 'Active' WHERE residentIdnum=?");
            preparedStatement.setInt(1, Integer.parseInt(residentImpl.getId().trim()));
            updateResident = preparedStatement.execute();

            if (updateRegistration || updateResident || insert) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return true;
    }

    @Override
    public ArrayList<VisitorImpl> getVisitorPerGroup(VisitorImpl visitorImpl) throws RemoteException {
        String query = "SELECT residentIdnum FROM resident WHERE CONCAT(residentFname, ' ' , residentLname) LIKE ?";
        try {
            ArrayList<VisitorImpl> implVisit = new ArrayList<>();
            int residentId = 0;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, visitorImpl.getResidentFullName());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                residentId = rs.getInt("residentIdnum");
                break;
            }

            query = "SELECT visitorsName, visitorsValidId FROM visitors WHERE visitorsArea LIKE ? AND residentIdnum = ? AND visitorsStartDate LIKE ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, visitorImpl.getArea());
            preparedStatement.setInt(2, residentId);
            java.util.Date d = Calendar.getInstance().getTime();
            Date sqlDate = new Date(d.getTime());
            preparedStatement.setDate(3, sqlDate);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                implVisit.add(new VisitorImpl(rs.getString("visitorsName"), rs.getString("visitorsValidId")));
            }
            return implVisit;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<FurnitureImpl> getFurniture(String roomId) throws RemoteException {
        ArrayList<FurnitureImpl> info = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `furnitureItemName`, `furnitureControlNo`, `furnitureIdnum` FROM furniture "
                    + "WHERE furnitureStatus = 'Available' AND roomIdnum = ? AND residentIdnum IS NULL AND transientIdnum IS NULL ORDER BY furnitureItemName ASC");
            preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                info.add(new FurnitureImpl(resultSet.getString("furnitureItemName"), resultSet.getString("furnitureControlNo"), resultSet.getString("furnitureIdnum")));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public ArrayList<ReservationImpl> getAllReservation() throws RemoteException {
        ArrayList<ReservationImpl> info = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `reservationLname`, `reservationFname`, `reservationMname`, `roomNumber`, `reservationDatePaid`  FROM reservation natural join room WHERE reservationStatus = 'Pending' ORDER BY reservationLname ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("reservationLname").trim() + ", " + resultSet.getString("reservationFname").trim();
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy");
                java.util.Date utilDate = originalFormat.parse(resultSet.getString("reservationDatePaid"));
                String formattedDate = targetFormat.format(utilDate);
                info.add(new ReservationImpl(name, resultSet.getString("roomNumber"), formattedDate));
            }
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public ArrayList<ReservationImpl> getAllReservationForSearchByDate(String date) throws RemoteException {
        ArrayList<ReservationImpl> info = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `reservationDatePaid`, `reservationLname`, `reservationFname`, `reservationMname`, `roomNumber`, `reservationDatePaid`  "
                    + "FROM reservation natural join room WHERE reservationStatus = 'Pending' AND reservationDatePaid LIKE ? "
                    + "ORDER BY reservationLname ASC");
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("reservationLname").trim()
                        + ", " + resultSet.getString("reservationFname").trim();
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy");
                java.util.Date utilDate = originalFormat.parse(resultSet.getString("reservationDatePaid"));
                String formattedDate = targetFormat.format(utilDate);
                info.add(new ReservationImpl(name, resultSet.getString("roomNumber"), formattedDate));
            }
            return info;
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public ArrayList<ReservationImpl> getAllReservationForSearchByName(String name) throws RemoteException {
        ArrayList<ReservationImpl> info = new ArrayList<>();
        try {
            String rName = "%" + name + "%";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `reservationLname`, `reservationFname`, `reservationMname`, `roomNumber`, `reservationDatePaid`  "
                    + "FROM reservation natural join room WHERE reservationStatus = 'Pending' AND (reservationLname LIKE ? OR reservationFname LIKE ?) ORDER BY reservationLname ASC");
            preparedStatement.setString(1, rName);
            preparedStatement.setString(2, rName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String fullname = resultSet.getString("reservationLname").trim()
                        + ", " + resultSet.getString("reservationFname").trim();
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy");
                java.util.Date utilDate = originalFormat.parse(resultSet.getString("reservationDatePaid"));
                String formattedDate = targetFormat.format(utilDate);
                info.add(new ReservationImpl(fullname, resultSet.getString("roomNumber"), formattedDate));
            }
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public ArrayList<ResidentImpl> getAllResident() throws RemoteException {
        ArrayList<ResidentImpl> info = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `residentLname`, `residentFname`, `status` "
                    + "FROM resident WHERE `status` != 'Leave' ORDER BY residentLname ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("residentLname").trim() + ", " + resultSet.getString("residentFname").trim();
                info.add(new ResidentImpl(name, resultSet.getString("status")));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public ArrayList<ResidentImpl> getAllResidentForSearch(String status, String name) throws RemoteException {
        ArrayList<ResidentImpl> info = new ArrayList<>();
        try {
            if (status.contains("Active")) {
                String rName = "%" + name + "%";
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT `residentLname`, `residentFname`, `status` "
                        + "FROM resident WHERE status = ? "
                        + "AND (residentLname LIKE ? "
                        + "OR residentFname LIKE ?) "
                        + " ORDER BY residentLname ASC");
                preparedStatement.setString(1, status);
                preparedStatement.setString(2, rName);
                preparedStatement.setString(3, rName);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String residentName = resultSet.getString("residentLname").trim() + ", " + resultSet.getString("residentFname").trim();
                    info.add(new ResidentImpl(residentName, resultSet.getString("status")));
                }
            } else {
                String rName = "%" + name + "%";
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT `residentLname`, `residentFname`, `status` "
                        + "FROM resident WHERE status != 'Leave' "
                        + "AND (residentLname LIKE ? "
                        + "OR residentFname LIKE ?) "
                        + " ORDER BY residentLname ASC");
                preparedStatement.setString(1, rName);
                preparedStatement.setString(2, rName);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String residentName = resultSet.getString("residentLname").trim() + ", " + resultSet.getString("residentFname").trim();
                    info.add(new ResidentImpl(residentName, resultSet.getString("status")));
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public ArrayList<String> getRooms() throws RemoteException {
        ArrayList<String> rooms = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomNumber` FROM room WHERE roomStatus != 'fully occupied' AND  roomType != 'SU'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (!rooms.contains(resultSet.getString("roomNumber"))) {
                    rooms.add(resultSet.getString("roomNumber"));
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rooms;
    }

    @Override
    public boolean insertGadget(ArrayList<String> gadgetDetails) throws RemoteException {
        boolean status = false;
        try {
            int c = 0;
            PreparedStatement preparedStatement;
//            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM gadget");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                c++;
//            }
            preparedStatement = connection.prepareStatement("INSERT INTO gadget (gadgetItemName, gadgetDescription,"
                    + " gadgetSerialNo, gadgetVoltage, gadgetWattage, gadgetRate, residentIdnum)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)");
            if (gadgetDetails.get(0).length() != 0) {
                preparedStatement.setString(1, gadgetDetails.get(0).trim());
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (gadgetDetails.get(1).length() != 0) {
                preparedStatement.setString(2, gadgetDetails.get(1).trim());
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (gadgetDetails.get(2).length() != 0) {
                preparedStatement.setString(3, gadgetDetails.get(2).trim());
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            if (gadgetDetails.get(3).length() != 0) {
                preparedStatement.setString(4, gadgetDetails.get(3).trim());
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (gadgetDetails.get(4).length() != 0) {
                preparedStatement.setString(5, gadgetDetails.get(4).trim());
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (gadgetDetails.get(5).length() != 0) {
                preparedStatement.setDouble(6, Double.parseDouble(gadgetDetails.get(5).trim()));
            } else {
                preparedStatement.setNull(6, Types.DOUBLE);
            }
            preparedStatement.setString(7, "99999999");
            return !preparedStatement.execute();
//            int c1 = 0;
//            preparedStatement = connection.prepareStatement("SELECT * FROM gadget");
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                c1++;
//            }
//            if (c == (c1 - 1)) {
//                status =  true;
//            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            status = false;
        }
        return status;
    }

    @Override
    public boolean deleteGadgetFromRegistration(String id) throws RemoteException {
        boolean status = false;
        try {
            int c = 0;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM gadget");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c++;
            }
            preparedStatement = connection.prepareStatement("DELETE FROM `gadget` WHERE `residentIdnum` = '99999999'");
            preparedStatement.execute();
            int c1 = 0;
            preparedStatement = connection.prepareStatement("SELECT * FROM gadget");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c1++;
            }
            if ((c + 1) == c1) {
                status = true;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateFurniture(String furnitureId) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = '99999999', furnitureStatus = 'Taken' WHERE furnitureIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(furnitureId.trim()));
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateFurnitureFromRegistration(String id) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ? WHERE residentIdnum = '99999999'");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateReservationStatus(String idNum) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE reservation set reservationStatus = 'Cancel' where reservationIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(idNum.trim()));
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateResidentStatus(String status, String idNum) throws RemoteException {
        boolean rStatus = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident NATURAL JOIN furniture WHERE `residentIdnum` = ?");
            preparedStatement.setInt(1, Integer.parseInt(idNum.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `furniture` SET `residentIdnum` = ? WHERE `furnitureIdnum` = ?");
                preparedStatement.setNull(1, Types.INTEGER);
                preparedStatement.setInt(2, Integer.parseInt(resultSet.getString("furnitureIdnum")));
                preparedStatement.executeUpdate();
                preparedStatement = connection.prepareStatement("UPDATE `furniture` SET `furnitureStatus` = 'Available' WHERE `furnitureIdnum` = ?");
                preparedStatement.setInt(1, Integer.parseInt(resultSet.getString("furnitureIdnum")));
                preparedStatement.executeUpdate();
            }

            preparedStatement = connection.prepareStatement("UPDATE `resident` SET `status` = ? WHERE `residentIdnum` = ?");
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, Integer.parseInt(idNum.trim()));
            rStatus = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            rStatus = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rStatus;
    }

    @Override
    public boolean updateRoomStatusFromReservation(String name) throws RemoteException {
        boolean status = false;
        int count = 0;
        try {
            String[] ReservationName = name.split(",");
            String roomId = "";
            String query = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `reservationIdnum`, `roomIdnum` FROM reservation WHERE reservationLname LIKE ? OR reservationFname LIKE ?");
            preparedStatement.setString(1, ReservationName[0].trim());
            preparedStatement.setString(2, ReservationName[1].trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM reservation NATURAL JOIN room WHERE roomIdnum = ? AND reservationStatus != 'Cancel'");
            preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                switch (resultSet.getString("roomType")) {
                    case "S":
                        preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?");
                        preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                        status = preparedStatement.executeUpdate() == 1;
                        break;

                    case "D":
                        count = 0;
                        preparedStatement = connection.prepareStatement("SELECT residentIdnum AS id FROM resident WHERE roomIdnum = ? "
                                + "UNION SELECT reservationIdnum AS id FROM reservation WHERE roomIdnum = ? AND reservationStatus LIKE 'Pending'");
                        preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                        preparedStatement.setInt(2, Integer.parseInt(roomId.trim()));
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            count++;
                        }
                        switch (count) {
                            case 1:
                                preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?");
                                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                                status = preparedStatement.executeUpdate() == 1;
                                break;

                            case 2:
                                preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'partially occupied' "
                                        + "WHERE roomIdnum = ?");
                                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                                status = preparedStatement.executeUpdate() == 1;
                                break;
                        }
                        break;

                    case "T":
                        count = 0;
                        preparedStatement = connection.prepareStatement("SELECT residentIdnum AS id FROM resident WHERE roomIdnum = ? "
                                + "UNION SELECT reservationIdnum AS id FROM reservation WHERE roomIdnum = ? AND reservationStatus LIKE 'Pending'");
                        preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                        preparedStatement.setInt(2, Integer.parseInt(roomId.trim()));
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            count++;
                        }
                        switch (count) {
                            case 1:
                                preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?");
                                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                                status = preparedStatement.executeUpdate() == 1;
                                break;

                            default:
                                preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'partially occupied' WHERE roomIdnum = ?");
                                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                                status = preparedStatement.executeUpdate() == 1;
                                break;
                        }
                        break;
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateRoomStatusFromResident(String name) throws RemoteException {
        boolean status = false;
        try {
            String[] ResidentName = name.split(",");
            String roomId = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `residentIdnum`, `residentFname`, `residentLname`, `roomIdnum` FROM resident WHERE residentLname LIKE ? OR residentFname LIKE ?");
            preparedStatement.setString(1, ResidentName[0].trim());
            preparedStatement.setString(2, ResidentName[1].trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }

            int count = 0;
            preparedStatement = connection.prepareStatement("SELECT residentIdnum AS id FROM resident WHERE roomIdnum = ? "
                    + "UNION SELECT reservationIdnum AS id FROM reservation WHERE roomIdnum = ? AND reservationStatus LIKE 'Pending'");
            preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
            preparedStatement.setInt(2, Integer.parseInt(roomId.trim()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count++;
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM resident NATURAL JOIN room WHERE roomIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                switch (resultSet.getString("roomType")) {
                    case "S":
                        preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?");
                        preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                        status = preparedStatement.executeUpdate() == 1;
                        break;

                    case "D":
                        switch (count) {
                            case 1:
                                preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?");
                                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                                status = preparedStatement.executeUpdate() == 1;
                                break;

                            case 2:
                                preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'partially occupied' WHERE roomIdnum = ?");
                                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                                status = preparedStatement.executeUpdate() == 1;
                                break;
                        }
                        break;

                    case "T":
                        switch (count) {
                            case 1:
                                preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?");
                                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                                status = preparedStatement.executeUpdate() == 1;
                                break;

                            default:
                                preparedStatement = connection.prepareStatement("UPDATE room set roomStatus = 'partially occupied' WHERE roomIdnum = ?");
                                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                                status = preparedStatement.executeUpdate() == 1;
                                break;
                        }
                        break;
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public int getFurnitureRowCount() throws RemoteException {
        int rows = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM furniture WHERE residentIdnum = '99999999'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rows++;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rows;
    }

    @Override
    public int getGadgetRowCount() throws RemoteException {
        int rows = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM gadget WHERE residentIdnum = '99999999'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rows++;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rows;
    }

    @Override
    public RegistrationImpl getReservationInfo(String reservationId) throws RemoteException {
        RegistrationImpl data = new RegistrationImpl();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `reservationLname`, `reservationFname`, `reservationMname`, `reservationHomeaddress`, `reservationMobileNo`, `reservationEmail`, `roomNumber`, `roomType` FROM reservation natural join room  WHERE reservationIdnum = '" + reservationId.trim() + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                data = new RegistrationImpl(resultSet.getString("reservation.reservationLname"),
                        resultSet.getString("reservation.reservationFname"),
                        resultSet.getString("reservation.reservationMname"),
                        resultSet.getString("reservation.reservationEmail"),
                        resultSet.getString("reservation.reservationMobileNo"),
                        resultSet.getString("reservation.reservationHomeaddress"),
                        resultSet.getString("roomNumber"),
                        resultSet.getString("roomType"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return data;
    }

    @Override
    public String getResidentId(String name) throws RemoteException {
        String id = "";
        try {
            String[] ResidentName = name.split(",");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT residentIdnum FROM resident WHERE residentLname LIKE ? AND residentFname LIKE ?");
            preparedStatement.setString(1, ResidentName[0].trim());
            preparedStatement.setString(2, ResidentName[1].trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("residentIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public String getRoomId(String room) throws RemoteException {
        String roomId = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, room.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return roomId;
    }

    @Override
    public String getRoomIdFromReservation(String name) throws RemoteException {
        String id = "";
        try {
            String[] ReservationName = name.split(",");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` FROM reservation WHERE reservationLname  LIKE ? OR reservationFname LIKE ?");
            preparedStatement.setString(1, ReservationName[0].trim());
            preparedStatement.setString(2, ReservationName[1].trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("roomIdnum") + " ";
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public String getRoomIdFromResident(String name) throws RemoteException {
        String id = "";
        try {
            String[] ResidentName = name.split(",");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM resident WHERE residentLname LIKE ? OR residentFname LIKE ?");
            preparedStatement.setString(1, ResidentName[0].trim());
            preparedStatement.setString(2, ResidentName[1].trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("roomIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public String getRoomType(String room) throws RemoteException {
        String roomType = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomType` FROM room WHERE roomNumber = ?");
            preparedStatement.setInt(1, Integer.parseInt(room.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomType = resultSet.getString("roomType");
            }

        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return roomType;
    }

    @Override
    public ArrayList<GadgetImpl> getGadget(String residentId) throws RemoteException {
        ArrayList<GadgetImpl> info = new ArrayList<>();
        GadgetImpl gadget;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `gadget` WHERE `residentIdnum` = ? ORDER BY `gadgetItemName` ASC");
            preparedStatement.setInt(1, Integer.parseInt(residentId.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                gadget = new GadgetImpl();
                gadget.setId(resultSet.getString("gadgetIdnum"));
                gadget.setItem_name(resultSet.getString("gadgetItemName"));
                if (resultSet.getString("gadgetDescription") != null) {
                    gadget.setDescription(resultSet.getString("gadgetDescription"));
                } else {
                    gadget.setDescription("");
                }
                if (resultSet.getString("gadgetSerialNo") != null) {
                    gadget.setSerial_number(resultSet.getString("gadgetSerialNo"));
                } else {
                    gadget.setSerial_number("");
                }
                if (resultSet.getString("gadgetVoltage") != null) {
                    gadget.setVoltage(resultSet.getString("gadgetVoltage"));
                } else {
                    gadget.setVoltage("");
                }
                if (resultSet.getString("gadgetWattage") != null) {
                    gadget.setWattage(resultSet.getString("gadgetWattage"));
                } else {
                    gadget.setWattage("");
                }
                if (resultSet.getString("gadgetRate") != null) {
                    gadget.setRate(Double.parseDouble(resultSet.getString("gadgetRate")));
                } else {
                    gadget.setRate(0.0);
                }
                info.add(gadget);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public ResidentImpl getResidentInfo(String residentId) throws RemoteException {
        ResidentImpl resident = new ResidentImpl();
        Blob blobContainer = null;
        int length = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident INNER JOIN registration ON (resident.registrationIdnum = registration.registrationIdnum) WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(residentId.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("residentLname") != null) {
                    resident.setLast_name(resultSet.getString("residentLname"));
                } else {
                    resident.setLast_name("");
                }
                if (resultSet.getString("residentFname") != null) {
                    resident.setFirst_name(resultSet.getString("residentFname"));
                } else {
                    resident.setFirst_name("");
                }
                if (resultSet.getString("residentMname") != null) {
                    resident.setMiddle_name(resultSet.getString("residentMname"));
                } else {
                    resident.setMiddle_name("");
                }
                resident.setGender(resultSet.getString("residentGender"));

                resident.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").format(resultSet.getDate("residentBirthdate")));
                if (resultSet.getString("residentEmail") != null) {
                    resident.setEmail(resultSet.getString("residentEmail"));
                } else {
                    resident.setEmail("");
                }
                if (resultSet.getString("residentMobileNo") != null) {
                    resident.setMobile_number(resultSet.getString("residentMobileNo"));
                } else {
                    resident.setMobile_number("");
                }
                if (resultSet.getString("residentHomeAddress") != null) {
                    resident.setAddress(resultSet.getString("residentHomeAddress"));
                } else {
                    resident.setAddress("");
                }
                if (resultSet.getString("residentZipCode") != null) {
                    resident.setZipcode(resultSet.getString("residentZipCode"));
                } else {
                    resident.setZipcode("");
                }
                if (resultSet.getBlob("picture") != null) {
                    blobContainer = resultSet.getBlob("picture");
                    length = (int) blobContainer.length();
                    resident.setPicture(blobContainer.getBytes(1, length));
                } else {
                    resident.setPicture(null);
                }

                if (resultSet.getString("registrationFatherName") != null) {
                    resident.setFatherName(resultSet.getString("registrationFatherName"));
                } else {
                    resident.setFatherName("");
                }
                if (resultSet.getString("registrationFatherLandline") != null) {
                    resident.setFatherLandLine(resultSet.getString("registrationFatherLandline"));
                } else {
                    resident.setFatherLandLine("");
                }
                if (resultSet.getString("registrationFatherMobileNo") != null) {
                    resident.setFatherContact(resultSet.getString("registrationFatherMobileNo"));
                } else {
                    resident.setFatherContact("");
                }
                if (resultSet.getString("registrationFatherEmail") != null) {
                    resident.setFatherEmail(resultSet.getString("registrationFatherEmail"));
                } else {
                    resident.setFatherEmail("");
                }

                if (resultSet.getString("registrationMotherName") != null) {
                    resident.setMotherName(resultSet.getString("registrationMotherName"));
                } else {
                    resident.setMotherName("");
                }
                if (resultSet.getString("registrationMotherLandline") != null) {
                    resident.setMotherLandLine(resultSet.getString("registrationMotherLandline"));
                } else {
                    resident.setMotherLandLine("");
                }
                if (resultSet.getString("registrationMotherMobileNo") != null) {
                    resident.setMotherContact(resultSet.getString("registrationMotherMobileNo"));
                } else {
                    resident.setMotherContact("");
                }
                if (resultSet.getString("registrationMotherEmail") != null) {
                    resident.setMotherEmail(resultSet.getString("registrationMotherEmail"));
                } else {
                    resident.setMotherEmail("");
                }

                if (resultSet.getString("registrationGuardianName") != null) {
                    resident.setGuardianName(resultSet.getString("registrationGuardianName"));
                } else {
                    resident.setGuardianName("");
                }
                if (resultSet.getString("registrationGuardianAddress") != null) {
                    resident.setGuardianAddress(resultSet.getString("registrationGuardianAddress"));
                } else {
                    resident.setGuardianAddress("");
                }
                if (resultSet.getString("registrationGuardianMobileNo") != null) {
                    resident.setGuardianContact(resultSet.getString("registrationGuardianMobileNo"));
                } else {
                    resident.setGuardianContact("");
                }
                if (resultSet.getString("registrationGuardianRelation") != null) {
                    resident.setGuardianRelation(resultSet.getString("registrationGuardianRelation"));
                } else {
                    resident.setGuardianRelation("");
                }

            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return resident;
    }

    @Override
    public ArrayList<FurnitureImpl> getFurnitureFromResident(String residentId) throws RemoteException {
        ArrayList<FurnitureImpl> info = new ArrayList<>();
        try {
            int c = 0;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `furnitureItemName`, `furnitureControlNo`, `furnitureIdnum` FROM `furniture` WHERE `residentIdnum` = ? ORDER BY `furnitureItemName` ASC");
            preparedStatement.setInt(1, Integer.parseInt(residentId.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                info.add(new FurnitureImpl(resultSet.getString("furnitureItemName"), resultSet.getString("furnitureControlNo"), resultSet.getString("furnitureIdnum")));
                c++;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public String getRoomIdFromResidentId(String residentId) throws RemoteException {
        String id = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` FROM resident WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(residentId.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getString("roomIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public boolean updateResident(ArrayList<String> residentDetails, DateFormat birthdateFormat) throws RemoteException {
        boolean status = false;
        try {
            DateFormat originalFormat = birthdateFormat;
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = originalFormat.parse(residentDetails.get(0));
            String formattedDate = targetFormat.format(utilDate);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            utilDate = df.parse(formattedDate);
            java.sql.Date sDate = new java.sql.Date(utilDate.getTime());
            String nDate = df.format(sDate);

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `resident` SET "
                    + "`residentLname` = ?, "
                    + "`residentFname` = ?, "
                    + "`residentMname` = ?, "
                    + "`residentHomeAddress` = ?, "
                    + "`residentMobileNo` = ?, "
                    + "`residentEmail` = ?, "
                    + "`residentGender` = ?, "
                    + "`residentBirthdate` = ?, "
                    + "`residentZipCode` = ? "
                    + "WHERE residentIdnum = ?");
            if (residentDetails.get(2).length() != 0) {
                preparedStatement.setString(1, residentDetails.get(2));
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (residentDetails.get(3).length() != 0) {
                preparedStatement.setString(2, residentDetails.get(3));
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (residentDetails.get(4).length() != 0) {
                preparedStatement.setString(3, residentDetails.get(4));
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            if (residentDetails.get(5).length() != 0) {
                preparedStatement.setString(4, residentDetails.get(5));
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (residentDetails.get(6).length() != 0) {
                preparedStatement.setString(5, residentDetails.get(6));
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (residentDetails.get(7).length() != 0) {
                preparedStatement.setString(6, residentDetails.get(7));
            } else {
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            if (residentDetails.get(8).length() != 0) {
                preparedStatement.setString(7, residentDetails.get(8));
            } else {
                preparedStatement.setNull(7, Types.VARCHAR);
            }
            preparedStatement.setDate(8, sDate);
            if (residentDetails.get(9).length() != 0) {
                preparedStatement.setString(9, residentDetails.get(9));
            } else {
                preparedStatement.setNull(9, Types.VARCHAR);
            }
            preparedStatement.setInt(10, Integer.parseInt(residentDetails.get(1)));
            status = preparedStatement.executeUpdate() == 1;

            preparedStatement = connection.prepareStatement("SELECT registrationIdnum FROM resident WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(residentDetails.get(1).trim()));
            ResultSet rs = preparedStatement.executeQuery();
            int regIdnum = 0;
            while (rs.next()) {
                regIdnum = rs.getInt("registrationIdnum");
            }

            //======================================================================================
            preparedStatement = connection.prepareStatement("UPDATE `registration` SET "
                    + "`registrationResidentMobileNo` = ?, "
                    + "`registrationResidentGender` = ?, "
                    + "`registrationResidentBirthdate` = ?, "
                    + "`registrationFatherMobileNo` = ?, "
                    + "`registrationFatherEmail` = ?, "
                    + "`registrationFatherLandline` = ?, "
                    + "`registrationMotherMobileNo` = ?, "
                    + "`registrationMotherEmail` = ?, "
                    + "`registrationMotherLandline` = ?, "
                    + "`registrationGuardianName` = ?, "
                    + "`registrationGuardianAddress` = ?, "
                    + "`registrationGuardianMobileNo` = ?, "
                    + "`registrationGuardianRelation` = ? "
                    + "WHERE registrationIdnum = ?");
            if (residentDetails.get(6).length() != 0) {
                preparedStatement.setString(1, residentDetails.get(6));
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (residentDetails.get(8).length() != 0) {
                preparedStatement.setString(2, residentDetails.get(8));
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            preparedStatement.setDate(3, sDate);

            if (residentDetails.get(11).length() != 0) {
                preparedStatement.setString(4, residentDetails.get(11));
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (residentDetails.get(12).length() != 0) {
                preparedStatement.setString(5, residentDetails.get(12));
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (residentDetails.get(13).length() != 0) {
                preparedStatement.setString(6, residentDetails.get(13));
            } else {
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            if (residentDetails.get(14).length() != 0) {
                preparedStatement.setString(7, residentDetails.get(14));
            } else {
                preparedStatement.setNull(7, Types.VARCHAR);
            }
            if (residentDetails.get(15).length() != 0) {
                preparedStatement.setString(8, residentDetails.get(15));
            } else {
                preparedStatement.setNull(8, Types.VARCHAR);
            }
            if (residentDetails.get(16).length() != 0) {
                preparedStatement.setString(9, residentDetails.get(16));
            } else {
                preparedStatement.setNull(9, Types.VARCHAR);
            }
            if (residentDetails.get(17).length() != 0) {
                preparedStatement.setString(10, residentDetails.get(17));
            } else {
                preparedStatement.setNull(10, Types.VARCHAR);
            }
            if (residentDetails.get(18).length() != 0) {
                preparedStatement.setString(11, residentDetails.get(18));
            } else {
                preparedStatement.setNull(11, Types.VARCHAR);
            }
            if (residentDetails.get(19).length() != 0) {
                preparedStatement.setString(12, residentDetails.get(19));
            } else {
                preparedStatement.setNull(12, Types.VARCHAR);
            }
            if (residentDetails.get(20).length() != 0) {
                preparedStatement.setString(13, residentDetails.get(20));
            } else {
                preparedStatement.setNull(13, Types.VARCHAR);
            }

            preparedStatement.setInt(14, Integer.parseInt(residentDetails.get(1).trim()));
            status = !preparedStatement.execute();

        } catch (ParseException | SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override //feeling ko may problema dito
    public boolean updateGadget(ArrayList<String> gadgetDetails) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE gadget SET "
                    + "`gadgetItemName` = ?, `gadgetDescription` = ?, `gadgetSerialNo` = ?, "
                    + "`gadgetVoltage` = ?, `gadgetWattage` = ?, "
                    + "`gadgetRate` = ?"
                    + " WHERE residentIdnum = ? AND gadgetIdnum = ?");
            preparedStatement.setInt(7, Integer.parseInt(gadgetDetails.get(0).trim()));
            preparedStatement.setInt(8, Integer.parseInt(gadgetDetails.get(7).trim()));

            if (gadgetDetails.get(1).length() != 0) {
                preparedStatement.setString(1, gadgetDetails.get(1));
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (gadgetDetails.get(2).length() != 0) {
                preparedStatement.setString(2, gadgetDetails.get(2));
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (gadgetDetails.get(3).length() != 0) {
                preparedStatement.setString(3, gadgetDetails.get(3));
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            if (gadgetDetails.get(4).length() != 0) {
                preparedStatement.setString(4, gadgetDetails.get(4));
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (gadgetDetails.get(5).length() != 0) {
                preparedStatement.setString(5, gadgetDetails.get(5));
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (gadgetDetails.get(6).length() != 0) {
                preparedStatement.setDouble(6, Double.parseDouble(gadgetDetails.get(6)));
            } else {
                preparedStatement.setNull(6, Types.DOUBLE);
            }
            
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean insertGadget(ArrayList<String> gadgetDetails, String residentId) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO gadget (gadgetItemName, gadgetDescription,"
                    + " gadgetSerialNo, gadgetVoltage, gadgetWattage, gadgetRate, residentIdnum)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)");
            if (gadgetDetails.get(0).length() != 0) {
                preparedStatement.setString(1, gadgetDetails.get(0).trim());
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (gadgetDetails.get(1).length() != 0) {
                preparedStatement.setString(2, gadgetDetails.get(1).trim());
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (gadgetDetails.get(2).length() != 0) {
                preparedStatement.setString(3, gadgetDetails.get(2).trim());
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            if (gadgetDetails.get(3).length() != 0) {
                preparedStatement.setString(4, gadgetDetails.get(3).trim());
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (gadgetDetails.get(4).length() != 0) {
                preparedStatement.setString(5, gadgetDetails.get(4).trim());
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (gadgetDetails.get(5) != null) {
                preparedStatement.setDouble(6, Double.parseDouble(gadgetDetails.get(5).trim()));
            } else {
                preparedStatement.setDouble(6, 0.00);
            }
            preparedStatement.setString(7, residentId);
            if (preparedStatement.execute()) {
                return false;
            }

        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean updateFurnitureFromEdit(String residentId, String furnitureId) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ? WHERE furnitureIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(residentId.trim()));
            preparedStatement.setInt(2, Integer.parseInt(furnitureId.trim()));
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateFurnitureToNull(String furnitureId) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ?, furnitureStatus = 'Available' WHERE furnitureIdnum = ?");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setInt(2, Integer.parseInt(furnitureId.trim()));
            status = !preparedStatement.execute();

        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean deleteGadget(String residentId) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM gadget WHERE residentIdnum = ?");
            preparedStatement.setString(1, residentId);
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean removeFurniture() throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ?, transientIdnum = ?, furnitureStatus = 'Available' "
                    + "WHERE residentIdnum = '99999999' OR transientIdnum = '99999999'");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setNull(2, Types.INTEGER);
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean removeGadget() throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM gadget WHERE residentIdnum = '99999999'");
            status = preparedStatement.execute();
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateResidentPhoto(ResidentImpl residentImpl) throws RemoteException {
        boolean status = false;
        try {
            Blob blob = connection.createBlob();
            int regIdnum = 0;
            if (residentImpl.getPicture() != null) {
                blob.setBytes(1, residentImpl.getPicture());
            } else {
                blob = null;
            }
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE resident SET picture = ? WHERE residentIdnum = ?");
            preparedStatement.setBlob(1, blob);
            preparedStatement.setInt(2, Integer.parseInt(residentImpl.getId()));
            status = preparedStatement.executeUpdate() == 1;

            preparedStatement = connection.prepareStatement("SELECT registrationIdnum FROM resident WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(residentImpl.getId()));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                regIdnum = rs.getInt("registrationIdnum");
            }

            preparedStatement = connection.prepareStatement("UPDATE registration SET registrationPicture = ? WHERE registrationIdnum = ?");
            preparedStatement.setBlob(1, blob);
            preparedStatement.setInt(2, regIdnum);
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return status;
    }

    @Override
    public ArrayList<ResidentImpl> getAllResidentFingerprint(String keyword) throws RemoteException {
        String query = "SELECT fingerprintOwner.residentIdnum, resident_fullname, fingerprintData1, fingerprintData2, fingerprintOwner.status "
                + "FROM fingerprintOwner NATURAL JOIN resident "
                + "WHERE fingerprintOwner.status NOT LIKE 'Leave' AND (residentLname LIKE ? OR residentMname LIKE ? OR residentFname LIKE ?)";
        String key = "%" + keyword + "%";
        try {
            ArrayList<ResidentImpl> residentImpl = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, key);
            preparedStatement.setString(3, key);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                residentImpl.add(new ResidentImpl(0, rs.getString("residentIdnum"), rs.getString("resident_fullname"), rs.getString("fingerprintData1"), rs.getString("fingerprintData2"), rs.getString("status")));
            }
            return residentImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertTenantRegistration(ArrayList<String> registrationDetails, byte[] pic) throws RemoteException {
        try {
            String roomId = "";
            String id = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` FROM room WHERE roomNumber = ?");
            preparedStatement.setInt(1, Integer.parseInt(registrationDetails.get(0).trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }
            resultSet = preparedStatement.executeQuery("SELECT * FROM registration");
            int c1 = 0;
            while (resultSet.next()) {
                c1++;
            }

            preparedStatement = connection.prepareStatement("INSERT INTO registration (registrationResidentCollege, registrationResidentCourse,"
                    + " registrationResidentYear, registrationResidentDept, registrationResidentBirthdate, registrationResidentGender,"
                    + " registrationResidentMobileNo, registrationResidentMobileNo2, registrationFatherName, registrationFatherLandline,"
                    + " registrationFatherMobileNo, registrationFatherEmail, registrationMotherName, registrationMotherLandline, "
                    + "registrationMotherMobileNo, registrationMotherEmail, registrationGuardianName, registrationGuardianAddress,"
                    + "registrationGuardianMobileNo, registrationGuardianRelation, registrationPicture, registrationStatus, reservationIdnum, roomIdnum, registrationDate)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)");

            if (registrationDetails.get(1).length() != 0) {
                preparedStatement.setString(1, registrationDetails.get(1));
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (registrationDetails.get(2).length() != 0) {
                preparedStatement.setString(2, registrationDetails.get(2));
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (registrationDetails.get(3).length() != 0) {
                preparedStatement.setString(3, registrationDetails.get(3));
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            if (registrationDetails.get(4).length() != 0) {
                preparedStatement.setString(4, registrationDetails.get(4));
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = df.parse(registrationDetails.get(5));
            java.sql.Date sDate = new java.sql.Date(utilDate.getTime());

            preparedStatement.setDate(5, sDate);
            if (registrationDetails.get(6).length() != 0) {
                preparedStatement.setString(6, registrationDetails.get(6));
            } else {
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            if (registrationDetails.get(7).length() != 0) {
                preparedStatement.setString(7, registrationDetails.get(7));
            } else {
                preparedStatement.setNull(7, Types.VARCHAR);
            }
            if (registrationDetails.get(8).length() != 0) {
                preparedStatement.setString(8, registrationDetails.get(8));
            } else {
                preparedStatement.setNull(8, Types.VARCHAR);
            }
            if (registrationDetails.get(9).length() != 0) {
                preparedStatement.setString(9, registrationDetails.get(9));
            } else {
                preparedStatement.setNull(9, Types.VARCHAR);
            }
            if (registrationDetails.get(10).length() != 0) {
                preparedStatement.setString(10, registrationDetails.get(10));
            } else {
                preparedStatement.setNull(10, Types.VARCHAR);
            }
            if (registrationDetails.get(11).length() != 0) {
                preparedStatement.setString(11, registrationDetails.get(11));
            } else {
                preparedStatement.setNull(11, Types.VARCHAR);
            }
            if (registrationDetails.get(12).length() != 0) {
                preparedStatement.setString(12, registrationDetails.get(12));
            } else {
                preparedStatement.setNull(12, Types.VARCHAR);
            }
            if (registrationDetails.get(13).length() != 0) {
                preparedStatement.setString(13, registrationDetails.get(13));
            } else {
                preparedStatement.setNull(13, Types.VARCHAR);
            }
            if (registrationDetails.get(14).length() != 0) {
                preparedStatement.setString(14, registrationDetails.get(14));
            } else {
                preparedStatement.setNull(14, Types.VARCHAR);
            }
            if (registrationDetails.get(15).length() != 0) {
                preparedStatement.setString(15, registrationDetails.get(15));
            } else {
                preparedStatement.setNull(15, Types.VARCHAR);
            }
            if (registrationDetails.get(16).length() != 0) {
                preparedStatement.setString(16, registrationDetails.get(16));
            } else {
                preparedStatement.setNull(16, Types.VARCHAR);
            }
            if (registrationDetails.get(17).length() != 0) {
                preparedStatement.setString(17, registrationDetails.get(17));
            } else {
                preparedStatement.setNull(17, Types.VARCHAR);
            }
            if (registrationDetails.get(18).length() != 0) {
                preparedStatement.setString(18, registrationDetails.get(18));
            } else {
                preparedStatement.setNull(18, Types.VARCHAR);
            }
            if (registrationDetails.get(19).length() != 0) {
                preparedStatement.setString(19, registrationDetails.get(19));
            } else {
                preparedStatement.setNull(19, Types.VARCHAR);
            }
            if (registrationDetails.get(20).length() != 0) {
                preparedStatement.setString(20, registrationDetails.get(20));
            } else {
                preparedStatement.setNull(20, Types.VARCHAR);
            }

            Blob blobContainer = connection.createBlob();
            if (pic != null) {
                blobContainer.setBytes(1, pic);
                preparedStatement.setBlob(21, blobContainer);
            } else {
                preparedStatement.setNull(21, Types.BLOB);
            }
            preparedStatement.setString(22, "Not Active");
            id = registrationDetails.get(22);
            preparedStatement.setString(23, id);
            preparedStatement.setString(24, roomId);
            Date date = new Date(Calendar.getInstance().getTime().getTime());
            preparedStatement.setDate(25, date);
            boolean status = !preparedStatement.execute();

            if (status) {
                preparedStatement = connection.prepareStatement("UPDATE reservation set reservationStatus = 'Registered' where reservationIdnum = ?");
                preparedStatement.setInt(1, Integer.parseInt(registrationDetails.get(22).trim()));
                preparedStatement.executeUpdate();
            } else {
                return false;
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM registration NATURAL JOIN reservation WHERE registration.reservationIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            resultSet = preparedStatement.executeQuery();

            String lname = "";
            String fname = "";
            while (resultSet.next()) {
                preparedStatement = connection.prepareStatement("INSERT INTO resident(residentLname, residentFname, residentMname, residentGender,"
                        + "residentZipCode, residentMobileNo, residentEmail, residentBirthdate,"
                        + "residentHomeAddress, picture, status, registrationIdnum, roomIdnum) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                preparedStatement.setString(1, registrationDetails.get(23).trim());
                lname = registrationDetails.get(23).trim();
                preparedStatement.setString(2, registrationDetails.get(24).trim());
                fname = registrationDetails.get(24).trim();
                preparedStatement.setString(3, registrationDetails.get(25).trim());
                preparedStatement.setString(4, registrationDetails.get(6).trim());
                if (resultSet.getString("reservationZipCode") != null) {
                    preparedStatement.setString(5, resultSet.getString("reservationZipCode"));
                } else {
                    preparedStatement.setNull(5, Types.VARCHAR);
                }
                if (resultSet.getString("registrationResidentMobileNo") != null) {
                    preparedStatement.setString(6, resultSet.getString("registrationResidentMobileNo"));
                } else {
                    preparedStatement.setNull(6, Types.VARCHAR);
                }
                if (registrationDetails.get(27).trim() != null || !registrationDetails.get(27).trim().isEmpty()) {
                    preparedStatement.setString(7, registrationDetails.get(27).trim());
                } else {
                    preparedStatement.setNull(7, Types.VARCHAR);
                }
                if (resultSet.getString("registrationResidentBirthdate") != null) {
                    preparedStatement.setString(8, resultSet.getString("registrationResidentBirthdate"));
                } else {
                    preparedStatement.setNull(8, Types.VARCHAR);
                }
                if (registrationDetails.get(26).trim() != null || !registrationDetails.get(26).trim().isEmpty()) {
                    preparedStatement.setString(9, resultSet.getString("reservationHomeaddress"));
                } else {
                    preparedStatement.setNull(9, Types.VARCHAR);
                }
                if (resultSet.getBlob("registrationPicture") != null) {
                    preparedStatement.setBlob(10, resultSet.getBlob("registrationPicture"));
                } else {
                    preparedStatement.setNull(10, Types.BLOB);
                }

                preparedStatement.setString(11, "Not Active");
                preparedStatement.setString(12, resultSet.getString("registrationIdnum"));
                preparedStatement.setString(13, resultSet.getString("roomIdnum"));
                if (preparedStatement.execute()) {
                    System.out.println("Failed");
                    return false;
                }
            }
            int registrationId = 0;
            preparedStatement = connection.prepareStatement("SELECT `registrationIdnum` FROM registration NATURAL JOIN reservation WHERE reservationIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                registrationId = resultSet.getInt("registrationIdnum");
            }
            int residentId = 0;
            preparedStatement = connection.prepareStatement("SELECT `residentIdnum` FROM resident WHERE registrationIdnum = ?");
            preparedStatement.setInt(1, registrationId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                residentId = resultSet.getInt("residentIdnum");
            }
            preparedStatement = connection.prepareStatement("SELECT * FROM furniture WHERE residentIdnum = '99999999'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE furniture SET furnitureStatus = 'Taken' WHERE residentIdnum = '99999999'");
                preparedStatement.executeUpdate();
                preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ? WHERE residentIdnum = '99999999'");
                preparedStatement.setInt(1, residentId);
                preparedStatement.executeUpdate();
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM gadget WHERE residentIdnum = '99999999'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE gadget SET residentIdnum = ? WHERE residentIdnum = '99999999'");
                preparedStatement.setInt(1, residentId);
                preparedStatement.executeUpdate();
            }

            resultSet = preparedStatement.executeQuery("SELECT * FROM registration");
            int c4 = 0;
            while (resultSet.next()) {
                c4++;
            }
//            if (c3 == (c4-1)) {
//                preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE residentLname LIKE ? AND residentFname LIKE ?");
//                preparedStatement.setString(1, lname);
//                preparedStatement.setString(2, fname);
//                resultSet = preparedStatement.executeQuery();
//                int c = 0;
//                while(resultSet.next()){
//                    c++;
//                }
//                if (c == 0) {
//                    return false;
//                }
//            }
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String getEmailTransientOrReservation(String id, String table) throws RemoteException {
        String query = null;
        if (table.equals("reservation")) {
            query = "SELECT reservationEmail FROM reservation WHERE reservationIdnum = ?";
        } else {
            query = "SELECT transientEmail FROM transient WHERE transientIdnum = ?";
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public String getNumberTransientOrReservation(String id, String table) throws RemoteException {
        String query = null;
        if (table.equals("reservation")) {
            query = "SELECT reservationMobileNo FROM reservation WHERE reservationIdnum = ?";
        } else {
            query = "SELECT transientMobileno FROM transient WHERE transientIdnum = ?";
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<GadgetImpl> getAllGadgetFromRoom(String roomNumber) throws RemoteException {
        try {
            ArrayList<GadgetImpl> gadgetImpl = new ArrayList<>();
            String id = "";
            String query = "SELECT roomIdnum FROM room WHERE roomNumber LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, roomNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                id = rs.getString("roomIdnum");
            }
            query = "SELECT gadgetSerialNo AS serialNumber, gadgetItemName AS itemName, "
                    + "CONCAT(residentFname, ' ' , residentLname) AS residentFullname "
                    + "FROM gadget NATURAL JOIN resident WHERE roomIdnum LIKE ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                gadgetImpl.add(new GadgetImpl(rs.getString("itemName"), rs.getString("serialNumber"), rs.getString("residentFullname")));
            }
            return gadgetImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<FurnitureImpl> getAllFurnitureFromRoom(String roomNumber) throws RemoteException {
        try {
            ArrayList<FurnitureImpl> furnitureImpl = new ArrayList<>();
            String id = "";
            String query = "SELECT roomIdnum FROM room WHERE roomNumber LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, roomNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                id = rs.getString("roomIdnum");
            }

            query = "SELECT furnitureControlNo AS serialNumber, furnitureItemName AS itemName, "
                    + "CONCAT(residentFname, ' ' , residentLname) AS residentFullname "
                    + "FROM furniture NATURAL JOIN resident WHERE roomIdnum LIKE ? AND furnitureStatus LIKE 'Taken'";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                furnitureImpl.add(new FurnitureImpl(rs.getString("itemName"), rs.getString("serialNumber"), rs.getString("residentFullname")));
            }
            return furnitureImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<ReservationImpl> getAllReservationsFromRoom(String roomNumber) throws RemoteException {
        try {
            String id = "";
            ArrayList<ReservationImpl> reservationImpl = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, roomNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                id = rs.getString("roomIdnum");
            }

            String query = "SELECT CONCAT(reservationFname, ' ' , reservationLname) AS reservationFullname "
                    + "FROM reservation WHERE roomIdnum LIKE ? AND reservationStatus LIKE 'pending'";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                reservationImpl.add(new ReservationImpl(rs.getString("reservationFullname")));
            }
            return reservationImpl;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateResidentFingerprint(ResidentImpl residentImpl) throws RemoteException {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("UPDATE biometrics SET fingerprintData1 = ?,fingerprintData2 = ? WHERE residentIdnum = ?");
            preparedStatement.setString(1, residentImpl.getFingerprint1());
            preparedStatement.setString(2, residentImpl.getFingerprint2());
            preparedStatement.setInt(3, Integer.parseInt(residentImpl.getId().trim()));
            return !preparedStatement.execute();
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }

        return false;
    }

    @Override
    public String getResidentIdnum(String name) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT residentIdnum FROM resident WHERE CONCAT(residentFname, ' ' , residentLname) LIKE ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return rs.getString("residentIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<NotificationImpl> getAllNotifsByDate(String date) throws RemoteException {
        try {
            ArrayList<NotificationImpl> notificationImpl = new ArrayList<>();
            String q = "SELECT * FROM ( SELECT notificationTime, notificationDate, notificationMethod, notificationType, notificationTable, "
                    + "CONCAT(residentFname, ' ' , residentLname) as fullname "
                    + "FROM notification LEFT JOIN resident ON notification.residentIdnum = resident.residentIdnum "
                    + "WHERE notificationTable LIKE 'resident' "
                    + "UNION "
                    + "SELECT notificationTime, notificationDate, notificationMethod, notificationType, notificationTable, "
                    + "CONCAT(transientFname, ' ' , transientLname) as fullname "
                    + "FROM notification INNER JOIN transient ON notification.residentIdnum = transient.transientIdnum "
                    + "WHERE notificationTable LIKE 'transient' "
                    + "UNION "
                    + "SELECT notificationTime, notificationDate, notificationMethod, notificationType, notificationTable, "
                    + "CONCAT(reservationFname, ' ' , reservationLname) as fullname "
                    + "FROM notification INNER JOIN reservation ON notification.residentIdnum = reservation.reservationIdnum "
                    + "WHERE notificationTable LIKE 'reservation' ) "
                    + "AS notificationTab WHERE notificationDate LIKE ? ORDER BY notificationTime DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(q);
            java.util.Date d = new SimpleDateFormat("MMM dd, yyyy").parse(date);
            Date sqlDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(d)).getTime());
            preparedStatement.setDate(1, sqlDate);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                notificationImpl.add(new NotificationImpl(rs.getString("notificationTime"), rs.getString("notificationType"), rs.getString("fullname"), rs.getString("notificationMethod"), rs.getString("notificationTable")));
            }
            return notificationImpl;
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public NotificationImpl getMessageDetails(ArrayList<String> list) throws RemoteException {
        try {
            String query = "";
            switch (list.get(4).trim()) {
                case "resident":
                    query = "SELECT * FROM notification NATURAL JOIN resident WHERE notificationTime = ? AND notificationDate = ? AND notificationType LIKE ? AND "
                            + "notificationMethod LIKE ? AND CONCAT(residentFname, ' ' , residentLname) LIKE ?";
                    break;
                case "reservation":
                    query = "SELECT * FROM notification NATURAL JOIN reservation WHERE notificationTime = ? AND notificationDate = ? AND notificationType LIKE ? AND "
                            + "notificationMethod LIKE ? AND CONCAT(reservationFname, ' ' , reservationLname) LIKE ?";
                    break;
                case "transient":
                    query = "SELECT * FROM notification NATURAL JOIN transient WHERE notificationTime = ? AND notificationDate = ? AND notificationType LIKE ? AND "
                            + "notificationMethod LIKE ? AND CONCAT(transientFname, ' ' , transientLname) LIKE ?";
                    break;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            java.util.Date d = new SimpleDateFormat("MMM dd, yyyy").parse(list.get(5));
            Date sqlDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(d)).getTime());

            java.util.Date t = new SimpleDateFormat("hh:mm:ss a").parse(list.get(0));
            Time sqlTime = new Time(new SimpleDateFormat("HH:mm:ss").parse(new SimpleDateFormat("HH:mm:ss").format(t)).getTime());

            preparedStatement.setTime(1, sqlTime);
            preparedStatement.setDate(2, sqlDate);
            preparedStatement.setString(3, list.get(3));
            preparedStatement.setString(4, list.get(2));
            preparedStatement.setString(5, list.get(1));
            ResultSet rs = preparedStatement.executeQuery();
            NotificationImpl notifImpl = null;
            while (rs.next()) {
                notifImpl = new NotificationImpl(rs.getString("notificationIdnum"), rs.getString("notificationMessage"), rs.getString("notificationContact"));
            }
            return notifImpl;
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<String> getResidentNames() throws RemoteException {
        ArrayList<String> names = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `residentLname`, `residentFname`  FROM `resident` ORDER BY `residentLname` ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                names.add(resultSet.getString("residentLname") + ", " + resultSet.getString("residentFname"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return names;
    }

    @Override
    public ArrayList<TransientImpl> getAllTransient() throws RemoteException {
        ArrayList<TransientImpl> transients = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `transient` WHERE `transientStatus` != 'Checkout' ORDER BY `transientLname` ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("transientLname").trim()
                        + ", " + resultSet.getString("transientFname").trim();
                transients.add(new TransientImpl(name, resultSet.getString("transientBalance"),
                        resultSet.getString("transientArrival"), resultSet.getString("transientDeparture"),
                        resultSet.getString("transientTotalNoDays"), resultSet.getString("transientStatus")));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return transients;
    }

    @Override
    public ArrayList<TransientImpl> getAllTransientByName(String status, String keyword) throws RemoteException {
        ArrayList<TransientImpl> transients = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `transient` "
                    + "WHERE `transientStatus` LIKE ? AND (`transientLname` LIKE ? OR `transientFname` LIKE ?) ORDER BY `transientLname` ASC");
            preparedStatement.setString(1, "%" + status + "%");
            preparedStatement.setString(2, "%" + keyword + "%");
            preparedStatement.setString(3, "%" + keyword + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("transientLname").trim()
                        + ", " + resultSet.getString("transientFname").trim();
                transients.add(new TransientImpl(name, resultSet.getString("transientBalance"),
                        resultSet.getString("transientArrival"), resultSet.getString("transientDeparture"),
                        resultSet.getString("transientTotalNoDays"), resultSet.getString("transientStatus")));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return transients;
    }

    @Override
    public ArrayList<TransientImpl> getAllTransientByStatus(String status) throws RemoteException {
        ArrayList<TransientImpl> transients = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `transient` WHERE `transientStatus` != 'Checkout' AND `transientStatus` LIKE ?  ORDER BY `transientLname` ASC");
            preparedStatement.setString(1, "%" + status + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("transientLname").trim()
                        + ", " + resultSet.getString("transientFname").trim();
                transients.add(new TransientImpl(name, resultSet.getString("transientBalance"),
                        resultSet.getString("transientArrival"), resultSet.getString("transientDeparture"),
                        resultSet.getString("transientTotalNoDays"), resultSet.getString("transientStatus")));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return transients;
    }

    @Override
    public boolean insertTransient(ArrayList<String> transientDetail) throws RemoteException {
        boolean status = true;
        try {
            String lname = transientDetail.get(0).trim();
            String fname = transientDetail.get(1).trim();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transient");
            ResultSet resultSet = preparedStatement.executeQuery();
            int c1 = 0;
            while (resultSet.next()) {
                c1++;
            }
            preparedStatement = connection.prepareStatement("INSERT INTO transient (transientLname, transientFname,"
                    + " transientMobileNo, transientAddress, transientEmail, transientDateEntered,  transientRelation,"
                    + " transientTotalAmount, transientAmountpaid, transientBalance, transientNoreservedRoom, "
                    + " transientReservedRoomNo, transientArrival, transientDeparture, transientTotalNoDays, "
                    + " transientNoAdditionalGuest, transientNamesAdditionalGuest, transientNoExtraBed, transientBedCharge,"
                    + " transientChargePerPerson, transientStatus, residentIdnum, transientDiscount)"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            preparedStatement.setString(1, lname);
            preparedStatement.setString(2, fname);
            preparedStatement.setString(3, transientDetail.get(2).trim());
            if (transientDetail.get(3).length() != 0) {
                preparedStatement.setString(4, transientDetail.get(3).trim());
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (transientDetail.get(4).length() != 0) {
                preparedStatement.setString(5, transientDetail.get(4).trim());
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (transientDetail.get(5).length() != 0) {
                preparedStatement.setString(6, transientDetail.get(5).trim());
            } else {
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            if (transientDetail.get(6).length() != 0) {
                preparedStatement.setString(7, transientDetail.get(6).trim());
            } else {
                preparedStatement.setNull(7, Types.VARCHAR);
            }
            if (transientDetail.get(7).length() != 0) {
                preparedStatement.setDouble(8, Double.parseDouble(transientDetail.get(7).trim()));
            } else {
                preparedStatement.setDouble(8, 0.0);
            }
            if (transientDetail.get(8).length() != 0) {
                preparedStatement.setDouble(9, Double.parseDouble(transientDetail.get(8).trim()));
            } else {
                preparedStatement.setDouble(9, 0.0);
            }
            if (transientDetail.get(9).length() != 0) {
                preparedStatement.setDouble(10, Double.parseDouble(transientDetail.get(9).trim()));
            } else {
                preparedStatement.setDouble(10, 0.0);
            }
            if (transientDetail.get(10).length() != 0) {
                preparedStatement.setInt(11, Integer.parseInt(transientDetail.get(10).trim()));
            } else {
                preparedStatement.setInt(11, 0);
            }
            if (transientDetail.get(11).length() != 0) {
                preparedStatement.setString(12, transientDetail.get(11).trim());
            } else {
                preparedStatement.setNull(12, Types.VARCHAR);
            }
            if (transientDetail.get(12).length() != 0) {
                preparedStatement.setString(13, transientDetail.get(12).trim());
            } else {
                preparedStatement.setNull(13, Types.VARCHAR);
            }
            if (transientDetail.get(13).length() != 0) {
                preparedStatement.setString(14, transientDetail.get(13).trim());
            } else {
                preparedStatement.setNull(14, Types.VARCHAR);
            }
            if (transientDetail.get(14).length() != 0) {
                preparedStatement.setInt(15, Integer.parseInt(transientDetail.get(14).trim()));
            } else {
                preparedStatement.setInt(15, 0);
            }
            if (transientDetail.get(15).length() != 0) {
                preparedStatement.setInt(16, Integer.parseInt(transientDetail.get(15).trim()));
            } else {
                preparedStatement.setInt(16, 0);
            }
            if (transientDetail.get(16).length() != 0) {
                preparedStatement.setString(17, transientDetail.get(16).trim());
            } else {
                preparedStatement.setNull(17, Types.VARCHAR);
            }
            if (transientDetail.get(17).length() != 0) {
                preparedStatement.setInt(18, Integer.parseInt(transientDetail.get(17).trim()));
            } else {
                preparedStatement.setInt(18, 0);
            }
            if (transientDetail.get(18).length() != 0) {
                preparedStatement.setDouble(19, Double.parseDouble(transientDetail.get(18).trim()));
            } else {
                preparedStatement.setDouble(19, 0.0);
            }
            if (transientDetail.get(19).length() != 0) {
                preparedStatement.setDouble(20, Double.parseDouble(transientDetail.get(19).trim()));
            } else {
                preparedStatement.setDouble(20, 0.0);
            }
            preparedStatement.setString(21, "Active");

            if (transientDetail.get(20).length() != 0) {
                preparedStatement.setInt(22, Integer.parseInt(transientDetail.get(20).trim()));
            } else {
                preparedStatement.setInt(22, 0);
            }
            preparedStatement.setDouble(23, Double.parseDouble(transientDetail.get(21).trim()));
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("SELECT transientIdnum FROM transient WHERE transientFname LIKE ? AND transientLname LIKE ?");
            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, lname);
            ResultSet rs = preparedStatement.executeQuery();
            String transientIdnum = null;
            while (rs.next()) {
                transientIdnum = rs.getString("transientIdnum");
            }

            preparedStatement = connection.prepareStatement("UPDATE furniture SET transientIdnum = ?, furnitureStatus = 'Taken' WHERE transientIdnum LIKE '99999999'");
            preparedStatement.setInt(1, Integer.parseInt(transientIdnum));
            preparedStatement.execute();
//            preparedStatement = connection.prepareStatement("SELECT * FROM transient");
//            resultSet = preparedStatement.executeQuery();
//            int c2 = 0;
//            while (resultSet.next()) {
//                c2++;
//            }
//
//            if (c1 != (c2 - 1)) {
//                String transientId = "";
//                preparedStatement = connection.prepareStatement("SELECT * FROM transient "
//                        + "WHERE transientLname LIKE ? "
//                        + "OR transientFname LIKE ?");
//                preparedStatement.setString(1, "%" + lname + "%");
//                preparedStatement.setString(2, "%" + fname + "%");
//                resultSet = preparedStatement.executeQuery();
//                while (resultSet.next()) {
//                    transientId = resultSet.getString("transientIdnum") + " ";
//                }
//                preparedStatement = connection.prepareStatement("DELETE FROM transient WHERE transientIdnum = ?");
//                preparedStatement.setString(1, transientId.trim());
//                status = preparedStatement.executeUpdate() == 1;
//                System.out.println("Dito may mali...");
//                status = false;
//            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean isSaved(String firstName, String lastName) throws RemoteException {
        boolean status = true;
        try {
            String name = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT transientIdnum FROM transient "
                    + "WHERE transientStatus NOT LIKE '%Checkout%' AND transientLname LIKE ? "
                    + "AND transientFname LIKE ?");
            preparedStatement.setString(1, "%" + lastName.trim() + "%");
            preparedStatement.setString(2, "%" + firstName.trim() + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString("transientIdnum");
            }
            if (name.length() != 0) {
                status = false;
            } else {
                status = true;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean isTransientThere(String transientId) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transient "
                    + "WHERE transientIdnum = ?");
            preparedStatement.setString(1, transientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            int c = 0;
            while (resultSet.next()) {
                c++;
            }
            status = c != 0;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean removeFurniture(String room) throws RemoteException {
        boolean status = false;
        try {
            String roomId = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` "
                    + "FROM `room` WHERE `roomNumber` = ?");
            preparedStatement.setString(1, room.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }
            preparedStatement = connection.prepareStatement("UPDATE `furniture` SET `transientIdnum` = ?, `furnitureStatus` = 'Available' WHERE `roomIdnum` = ? AND `furnitureStatus` LIKE 'Taken' && transientIdnum LIKE '99999999'");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setInt(2, Integer.parseInt(roomId.trim()));
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateFurnitureFromTransient(String id) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT transientReservedRoomNo FROM transient WHERE transientIdnum = ? AND transientStatus NOT LIKE 'Checkout'");
            preparedStatement.setInt(1, Integer.parseInt(id.trim()));
            ResultSet rs = preparedStatement.executeQuery();
            String roomId = "";
            while (rs.next()) {
                roomId = rs.getString("transientReservedRoomNo");
            }
            for (String room : roomId.split(",")) {
                preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber LIKE ?");
                preparedStatement.setString(1, room.trim());
                rs = preparedStatement.executeQuery();
                String r = "";
                while (rs.next()) {
                    r = rs.getString("roomIdnum");
                }
                preparedStatement = connection.prepareStatement("UPDATE `furniture` SET `transientIdnum` = ? WHERE roomIdnum = ? AND furnitureStatus LIKE 'Available'");
                preparedStatement.setInt(1, Integer.parseInt(id.trim()));
                preparedStatement.setInt(2, Integer.parseInt(r.trim()));
                status = !preparedStatement.execute();
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateRoomStatusFromTransient(String roomNum, String status) throws RemoteException {
        boolean rStatus = true;
        try {
            String roomId = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` "
                    + "FROM `room` WHERE `roomNumber` = ?");
            preparedStatement.setString(1, roomNum.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
                break;
            }

            preparedStatement = connection.prepareStatement("SELECT "
                    + "(SELECT DISTINCT COUNT(reservationIdnum) "
                    + "FROM  room  JOIN reservation ON room.roomIdnum = reservation.roomIdnum "
                    + "WHERE room.roomIdnum = ? AND reservationStatus = 'Pending') + "
                    + "(SELECT DISTINCT COUNT(residentIdnum) "
                    + "FROM  room  JOIN resident ON room.roomIdnum = resident.roomIdnum "
                    + "WHERE room.roomIdnum = ?) AS count");
            preparedStatement.setInt(1, Integer.parseInt(roomId));
            preparedStatement.setInt(2, Integer.parseInt(roomId));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (!status.trim().equals("fully occupied")) {
                int count = resultSet.getInt("count");
                preparedStatement = connection.prepareStatement("SELECT roomType, roomStatus, numberOfResident FROM room NATURAL JOIN personperroom WHERE roomNumber LIKE ?");
                preparedStatement.setString(1, roomNum);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    switch (resultSet.getString("roomType")) {
                        case "T":
                            if (count == 3) {
                                status = "fully occupied";
                            } else if (count < 3 && count > 0) {
                                status = "partially occupied";
                            } else {
                                status = "unoccupied";
                            }
                            break;
                        case "D":
                            
                System.out.println(count+" here");
                            if (count == 2) {
                                status = "fully occupied";
                            } else if (count == 1) {
                                status = "partially occupied";
                            } else {
                                status = "unoccupied";
                            }
                            break;
                        case "S":
                            if (count == 1) {
                                status = "fully occupied";
                            } else {
                                status = "unoccupied";
                            }
                            break;
                        case "SU":
                            status = "unoccupied";
                            break;
                    }
                }
            }

            PreparedStatement statement = connection.prepareStatement("UPDATE `room` set `roomStatus` = ? "
                    + "WHERE `roomIdnum` = ?");
            statement.setString(1, status);
            statement.setString(2, roomId.trim());
            rStatus = !statement.execute();
        } catch (SQLException ex) {
            rStatus = false;
            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rStatus;
    }

    @Override
    public boolean updateTransient(ArrayList<String> transientDetail, String transientId) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transient WHERE transientIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(transientId.trim()));
            ResultSet rs = preparedStatement.executeQuery();
            double lastPayment = 0.0;
            while (rs.next()) {
                lastPayment = rs.getDouble("transientAmountpaid");
            }

            preparedStatement = connection.prepareStatement("UPDATE `transient` SET "
                    + "`transientTotalAmount` = ?, "
                    + "`transientAmountpaid` = ?, "
                    + "`transientBalance` = ?, "
                    + "`transientNoreservedRoom` = ?, "
                    + "`transientReservedRoomNo` = ?, "
                    + "`transientDateEntered` = ?, "
                    + "`transientArrival` = ?, "
                    + "`transientDeparture` = ?, "
                    + "`transientTotalNoDays` = ?, "
                    + "`transientNoAdditionalGuest` = ?, "
                    + "`transientNoExtraBed` = ?, "
                    + "`transientNamesAdditionalGuest` = ?, "
                    + "`transientChargePerPerson` = ?, "
                    + "`transientBedcharge` = ?, "
                    + "`transientStatus` = ?, "
                    + "`transientDiscount` = ? "
                    + "WHERE `transientIdnum` = ?");
            if (transientDetail.get(0).length() != 0) {
                preparedStatement.setDouble(1, Double.parseDouble(transientDetail.get(0)));
            } else {
                preparedStatement.setDouble(1, 0.0);
            }
            if (transientDetail.get(1).length() != 0) {
                preparedStatement.setDouble(2, Double.parseDouble(transientDetail.get(1)) + lastPayment);
            } else {
                preparedStatement.setDouble(2, 0.0);
            }
            if (transientDetail.get(2).length() != 0) {
                preparedStatement.setDouble(3, Double.parseDouble(transientDetail.get(2)));
            } else {
                preparedStatement.setDouble(3, 0.0);
            }
            if (transientDetail.get(3).length() != 0) {
                preparedStatement.setInt(4, Integer.parseInt(transientDetail.get(3)));
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (transientDetail.get(4).length() != 0) {
                preparedStatement.setString(5, transientDetail.get(4));
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (transientDetail.get(5).length() != 0) {
                preparedStatement.setString(6, transientDetail.get(5));
            } else {
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            if (transientDetail.get(6).length() != 0) {
                preparedStatement.setString(7, transientDetail.get(6));
            } else {
                preparedStatement.setNull(7, Types.VARCHAR);
            }
            if (transientDetail.get(7).length() != 0) {
                preparedStatement.setString(8, transientDetail.get(7));
            } else {
                preparedStatement.setNull(8, Types.VARCHAR);
            }
            if (transientDetail.get(8).length() != 0) {
                preparedStatement.setString(9, transientDetail.get(8));
            } else {
                preparedStatement.setNull(9, Types.VARCHAR);
            }
            if (transientDetail.get(9).length() != 0) {
                preparedStatement.setString(10, transientDetail.get(9));
            } else {
                preparedStatement.setNull(10, Types.VARCHAR);
            }
            if (transientDetail.get(10).length() != 0) {
                preparedStatement.setString(11, transientDetail.get(10));
            } else {
                preparedStatement.setNull(11, Types.VARCHAR);
            }
            if (transientDetail.get(11).length() != 0) {
                preparedStatement.setString(12, transientDetail.get(11));
            } else {
                preparedStatement.setNull(12, Types.VARCHAR);
            }
            if (transientDetail.get(12).length() != 0) {
                preparedStatement.setString(13, transientDetail.get(12));
            } else {
                preparedStatement.setNull(13, Types.VARCHAR);
            }
            if (transientDetail.get(13).length() != 0) {
                preparedStatement.setString(14, transientDetail.get(13));
            } else {
                preparedStatement.setNull(14, Types.VARCHAR);
            }
            if (transientDetail.get(14).length() != 0) {
                preparedStatement.setString(15, transientDetail.get(14));
            } else {
                preparedStatement.setNull(15, Types.VARCHAR);
            }
            preparedStatement.setDouble(16, Double.parseDouble(transientDetail.get(15)));
            
            if (transientId.length() != 0) {
                preparedStatement.setString(17, transientId);
            }
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateTransientStatus(String rStatus, String transientId) throws RemoteException {
        boolean status;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `transient` SET `transientStatus` = ? "
                    + "WHERE `transientIdnum` = ?");
            preparedStatement.setString(1, rStatus);
            preparedStatement.setString(2, transientId);
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public int getFurnitureRowCount(String id) throws RemoteException {
        int rows = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `furniture` WHERE `residentIdnum` = ?");
            preparedStatement.setString(1, id.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rows++;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rows;
    }

    @Override
    public String getRelationToResident(String transientId) throws RemoteException {
        String relation = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `transient` WHERE `transientIdnum` = ?");
            preparedStatement.setString(1, transientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                relation = resultSet.getString("transientRelation");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return relation;
    }

    @Override
    public String getResidentName(String transientId) throws RemoteException {
        String name = "";
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT `residentIdnum` FROM `transient` WHERE `transientIdnum` = ?");
            preparedStatement1.setString(1, transientId);
            ResultSet resultSet = preparedStatement1.executeQuery();
            String residentIdnum = "";
            while (resultSet.next()) {
                residentIdnum = resultSet.getString("residentIdnum");
            }
            PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM `resident` WHERE `residentIdnum` = ?");
            preparedStatement2.setString(1, residentIdnum);
            resultSet = preparedStatement2.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString("residentLname") + ", " + resultSet.getString("residentFname");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return name;
    }

    @Override
    public String getRoomNumber(String name) throws RemoteException {
        String room = "";
        try {
            String[] rName = name.split(",");
            String lastName = rName[0].trim();
            String firstName = rName[1].trim();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomNumber` FROM `resident` NATURAL JOIN `room` "
                    + "WHERE `residentLname` LIKE ? AND `residentFname` LIKE ?");
            preparedStatement.setString(1, lastName);
            preparedStatement.setString(2, firstName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                room = resultSet.getString("roomNumber");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return room;
    }

    @Override
    public String getTransientId(String transientName) throws RemoteException {
        String id = "";
        try {
            String[] name = transientName.split(",");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `transient` "
                    + "WHERE `transientLname` LIKE ? "
                    + "AND `transientFname` LIKE ?");
            preparedStatement.setString(1, name[0].trim());
            preparedStatement.setString(2, name[1].trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("transientIdnum") + " ";
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public String[] getRoomsFromTransient(String id) throws RemoteException {
        String a = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `transientReservedRoomNo` FROM `transient` WHERE `transientIdnum` = ?");
            preparedStatement.setString(1, id.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                a = resultSet.getString("transientReservedRoomNo");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        String[] rooms = a.split(",");
        return rooms;
    }

    @Override
    public TransientImpl getTransient(String transientId) throws RemoteException {
        TransientImpl transientDetails = new TransientImpl();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transient "
                    + "WHERE transientIdnum = ?");
            preparedStatement.setString(1, transientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("transientLname") != null) {
                    transientDetails.setLast_name(resultSet.getString("transientLname"));
                }
                if (resultSet.getString("transientFname") != null) {
                    transientDetails.setFirst_name(resultSet.getString("transientFname"));
                }
                if (resultSet.getString("transientLname") != null) {
                    transientDetails.setMobile_number(resultSet.getString("transientMobileNo"));
                }
                if (resultSet.getString("transientAddress") != null) {
                    transientDetails.setAddress(resultSet.getString("transientAddress"));
                }
                if (resultSet.getString("transientEmail") != null) {
                    transientDetails.setEmail(resultSet.getString("transientEmail"));
                }
                if (resultSet.getString("transientChargePerPerson") != null) {
                    transientDetails.setChargePerson(resultSet.getString("transientChargePerPerson"));
                }
                if (resultSet.getString("transientBedcharge") != null) {
                    transientDetails.setBedCharge(resultSet.getString("transientBedcharge"));
                }
                if (resultSet.getString("transientAmountPaid") != null) {
                    transientDetails.setAmountPaid(resultSet.getString("transientAmountPaid"));
                }
                if (resultSet.getString("transientTotalAmount") != null) {
                    transientDetails.setTotalAmount(resultSet.getString("transientTotalAmount"));
                }
                if (resultSet.getString("transientBalance") != null) {
                    transientDetails.setBalance(resultSet.getString("transientBalance"));
                }
                if (resultSet.getString("transientNoreservedRoom") != null) {
                    transientDetails.setNumberOfRooms(resultSet.getString("transientNoreservedRoom"));
                }
                if (resultSet.getString("transientReservedRoomNo") != null) {
                    transientDetails.setReservedRooms(resultSet.getString("transientReservedRoomNo"));
                }
                if (resultSet.getString("transientTotalNoDays") != null) {
                    transientDetails.setTotalDays(resultSet.getString("transientTotalNoDays"));
                }
                if (resultSet.getString("transientNoExtraBed") != null) {
                    transientDetails.setExtraBeds(resultSet.getString("transientNoExtraBed"));
                }
                if (resultSet.getString("transientDateEntered") != null) {
                    transientDetails.setDateEntered(resultSet.getString("transientDateEntered"));
                }
                if (resultSet.getString("transientArrival") != null) {
                    transientDetails.setArrival(resultSet.getString("transientArrival"));
                }
                if (resultSet.getString("transientDeparture") != null) {
                    transientDetails.setDeparture(resultSet.getString("transientDeparture"));
                }
                if (resultSet.getString("transientRelation") != null) {
                    transientDetails.setRelation(resultSet.getString("transientRelation"));
                }
                if (resultSet.getString("transientNoAdditionalGuest") != null) {
                    transientDetails.setNoAdditionalGuest(resultSet.getString("transientNoAdditionalGuest"));
                }
                if (resultSet.getString("transientNamesAdditionalGuest") != null) {
                    transientDetails.setAdditionalGuest(resultSet.getString("transientNamesAdditionalGuest"));
                }
                transientDetails.setDiscount(resultSet.getString("transientDiscount"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return transientDetails;
    }

    @Override
    public boolean updateTransientFurniture(String furnitureId) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET transientIdnum = '99999999' WHERE furnitureIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(furnitureId.trim()));
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateTransientFurnitureToNull(String transientId) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET transientIdnum = ? WHERE transientIdnum = ?");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setInt(2, Integer.parseInt(transientId.trim()));
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public ArrayList<FurnitureImpl> getFurnitureFromTransient(String residentId) throws RemoteException {
        ArrayList<FurnitureImpl> info = new ArrayList<>();
        try {
            int c = 0;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `furnitureItemName`, `furnitureControlNo`, `furnitureIdnum` FROM `furniture` WHERE `transientIdnum` = ? ORDER BY `furnitureItemName` ASC");
            preparedStatement.setInt(1, Integer.parseInt(residentId.trim()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                info.add(new FurnitureImpl(resultSet.getString("furnitureItemName"), resultSet.getString("furnitureControlNo"), resultSet.getString("furnitureIdnum")));
                c++;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return info;
    }

    @Override
    public RatesImpl getRates() throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transientRate");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return new RatesImpl(rs.getDouble(1), rs.getDouble(3), rs.getDouble(2), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public String getStatus(String room) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomStatus FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, room);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return rs.getString("roomStatus");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<String> getTransientFromRoom(String roomNumber) throws RemoteException {
        try {
            ArrayList<String> names = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT CONCAT(transientFname, ' ', transientLname) AS fullname FROM transient WHERE transientReservedRoomNo LIKE ? AND transientStatus NOT LIKE 'Checkout'");
            preparedStatement.setString(1, "%" + roomNumber.trim() + "%");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("fullname"));
            }
            return names;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<FurnitureImpl> getTransientFurnitureFromRoom(String name, String roomNum) throws RemoteException {
        try {
            String idnum = null;
            String roomId = null;
            ArrayList<FurnitureImpl> furn = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT transientIdnum FROM transient WHERE CONCAT(transientFname, ' ', transientLname) LIKE ? AND transientStatus NOT LIKE 'Checkout'");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                idnum = rs.getString("transientIdnum");
            }

            preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, roomNum);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                roomId = rs.getString("roomIdnum");
            }
            if (idnum != null) {
                preparedStatement = connection.prepareStatement("SELECT * FROM furniture WHERE transientIdnum = ? AND roomIdnum = ?");
                preparedStatement.setInt(1, Integer.parseInt(idnum.trim()));
                preparedStatement.setInt(2, Integer.parseInt(roomId));
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    furn.add(new FurnitureImpl(rs.getString("furnitureItemName"), rs.getString("furnitureControlNo"), rs.getString("furnitureIdnum")));
                }
            }
            return furn;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public boolean saveReservationToPdf(ReservationImpl reservationImpl) throws RemoteException {
        
        try {
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            String path="";
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } finally {
                br.close();
            }
            // TODO add your handling code here:
            Document doc = new Document();
            FileOutputStream fos = new FileOutputStream(path + "\\reservation\\" + reservationImpl.getlName().trim() + ", " + reservationImpl.getfName().trim() + "ReservationForm.pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);
            PdfReader pdfReader = new PdfReader("ReservationForm.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                //Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 10);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getDatePaid().trim(), 435, 655, 0);
                if (reservationImpl.getmName().trim().isEmpty()) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getlName().trim() + ", " + reservationImpl.getfName(), 265, 625, 0);
                } else {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getlName().trim() + ", " + reservationImpl.getfName()
                            + " " + reservationImpl.getmName().charAt(0) + ".", 265, 625, 0);
                }
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getAddress().trim(), 265, 600, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getZipCode().trim(), 265, 580, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getmNumber().trim(), 265, 565, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getEmail().trim(), 265, 550, 0);
                content.setFontAndSize(bf, 12);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Room No.:" + reservationImpl.getRoom().trim(), 125, 370, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getRoomType().trim(), 325, 370, 0);
//                content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getsTerm().trim(), 125, 310, 0);
                if(reservationImpl.getsTerm() != null) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getsTerm().trim(), 125, 310, 0);
                    if (!reservationImpl.getAyTo().trim().isEmpty() & !reservationImpl.getAyFrom().trim().isEmpty()) {
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getAyFrom().trim() + " - " + reservationImpl.getAyTo().trim(), 325, 310, 0);
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, " - ", 435, 310, 0);
                    } else {
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, " - ", 325, 310, 0);
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, " - ", 435, 310, 0);
                    }
                } else {
                    if (reservationImpl.getOthers().trim().isEmpty()) {
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, " - ", 325, 310, 0);
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, " - ", 435, 310, 0);
                    } else {
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, " - ", 325, 310, 0);
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getOthers().trim(), 435, 310, 0);
                    }
                }
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, reservationImpl.getDatePaid().trim(), 265, 210, 0);
                content.endText();
            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();
            pdfWriter.close();
        } catch (DocumentException|FileNotFoundException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (IOException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } 
        return true;
    }

    @Override
    public boolean saveRegistrationToPdf(RegistrationImpl registrationImpl, ResidentImpl residentImpl) throws RemoteException {
        try {
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            String path="";
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } finally {
                br.close();
            }
            String gend = "";
            Document doc = new Document();
            FileOutputStream fos = new FileOutputStream(path + "\\registration\\" + residentImpl.getLName().trim() + ", " + residentImpl.getFName().trim() + "RegistrationResidentForm.pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);

            PdfReader pdfReader = new PdfReader("RegistrationResidentForm.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                if (residentImpl.getPicture() != null) {
                    Image image1 = Image.getInstance(residentImpl.getPicture());
                    image1.scaleAbsolute(95, 105);
                    image1.setAbsolutePosition(465f, 635f);
                    content.addImage(image1);
                }

                //Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 10);
                //last name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, residentImpl.getLName().trim(), 175, 610, 0);
                //first name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, residentImpl.getFName().trim(), 325, 610, 0);
                //middle name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, residentImpl.getMName().trim(), 470, 610, 0);
                //college
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getCollege().trim(), 180, 582, 0);
                //Course and year
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getCourse().trim() + " - " + registrationImpl.getYear().trim(), 180, 568, 0);
                //department
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getDepartment().trim(), 400, 582, 0);
                //Sex
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, residentImpl.getGender().trim(), 400, 568, 0);
                //Home address
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getAddress().trim(), 110, 540, 0);
                //resident mobile no1
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getMobile_number().trim(), 187, 485, 0);
                //resident mobile no2
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getMobile_number2().trim(), 187, 470, 0);
                //student email
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getEmail().trim(), 397, 485, 0);
                //fathers name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getFatherName().trim(), 110, 443, 0);
                //fathers area code
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getFatherAreaCode().trim(), 300, 443, 0);
                //father landline
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getFatherPhone().trim(), 355, 443, 0);
                //father mobile
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getFatherMobile().trim(), 490, 443, 0);
                //father email
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getFatherEmail().trim(), 110, 430, 0);
                //mother name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getMotherName().trim(), 110, 402, 0);
                //mother area code
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getMotherAreaCode().trim(), 300, 402, 0);
                //mother landline
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getMotherPhone().trim(), 355, 402, 0);
                //mother mobile
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getMotherMobile().trim(), 490, 402, 0);
                //mother email
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getMotherEmail().trim(), 110, 387, 0);
                //guardian name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getGuardianName().trim(), 170, 335, 0);
                //guardian contact number
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getGuardianMobile().trim(), 470, 335, 0);
                //guardian address
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getGuardianAddress().trim(), 125, 322, 0);
                //guardian relation
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, registrationImpl.getGuardianRelation().trim(), 170, 307, 0);
                content.setFontAndSize(bf, 12);
                //room details
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Room No.: " + registrationImpl.getRoom_number().trim() + " - " + registrationImpl.getRoom_type().trim(), 355, 235, 0);
                content.endText();

            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();
            pdfWriter.close();
        } catch (DocumentException|FileNotFoundException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (IOException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean saveFurnitureToPdf(ArrayList<String> info, ArrayList<FurnitureImpl> furnitureImpl) throws RemoteException {
        try {
            // TODO add your handling code here:
            Document doc = new Document();
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            String path="";
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } finally {
                br.close();
            }
            FileOutputStream fos = new FileOutputStream(path + "\\furniture\\" + info.get(0) + "InventoryForm.pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);

            PdfReader pdfReader = new PdfReader("InventoryForm.pdf");
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            float added = 0;
            float rowSize = 175;
            float columnSize = 560;
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                //Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 10);
                //room number
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(0).trim(), 185, 650, 0);
                //room type
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(2).trim(), 185, 635, 0);
                //resident
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(1).trim(), 185, 620, 0);
                for (FurnitureImpl f : furnitureImpl) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, f.getItem_name().trim(), rowSize - 60, columnSize + added, 0);
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, f.getControl_number().trim(), rowSize + 30, columnSize + added, 0);
                    added -= 15;
                }

                content.endText();
            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();
            pdfWriter.close();
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (DocumentException | IOException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean saveGadgetToPdf(ArrayList<String> info, ArrayList<GadgetImpl> gadgetImpl) throws RemoteException {
        try {
            // TODO add your handling code here:
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            String path="";
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } finally {
                br.close();
            }
            Document doc = new Document();
            FileOutputStream fos = new FileOutputStream(path + "\\gadget\\" + info.get(0).trim() + "GadgetForm.pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);

            PdfReader pdfReader = new PdfReader("GadgetForm.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            float rowSize = 175;
            float columnSize = 495;
            float added = 0;
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                //Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 10);
                //room number
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(1).trim(), 240, 630, 0);
                //room type
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(2).trim(), 240, 615, 0);
                //resident
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(0).trim(), 240, 600, 0);
                for (GadgetImpl g : gadgetImpl) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, g.getItem_name().trim(), rowSize - 60, columnSize + added, 0);
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, g.getDescription().trim(), rowSize + 65, columnSize + added, 0);
                    added -= 15;
                }

                content.endText();
            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();
            pdfWriter.close();
        } catch (DocumentException | FileNotFoundException | IllegalPdfSyntaxException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (IOException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } 
        return true;
    }

    @Override
    public boolean saveTransientToPdf(TransientImpl transientImpl, ArrayList<String> names) throws RemoteException {
        try {
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            String path="";
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } finally {
                br.close();
            }

            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            FileOutputStream fos = new FileOutputStream(path + "\\transient\\" + transientImpl.getLast_name().trim() + ", " + transientImpl.getFirst_name().trim() + "TransientForm.pdf");
            PdfWriter.getInstance(doc, fos);

            PdfReader pdfReader = new PdfReader("TransientForm.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
            float varPosition = 0;
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                //Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 10);
                varPosition = 15;
                //last name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getLast_name().trim() + ", " + transientImpl.getFirst_name().trim(), 100, 661 + varPosition, 0);
                //first name 
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()), 400, 661 + varPosition, 0);
                if (!transientImpl.getFull_name().isEmpty()) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getFull_name().trim(), 125, 633 + varPosition, 0);
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getRelation().trim(), 470, 633 + varPosition, 0);
                } else {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "N / A", 125, 633 + varPosition, 0);
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "N / A", 470, 633 + varPosition, 0);
                }
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getMobile_number().trim(), 135, 590 + varPosition, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getEmail().trim(), 400, 590 + varPosition, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getAddress().trim(), 175, 577 + varPosition, 0);
                varPosition = 35;
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getReservedRooms().trim(), 135, 507 + varPosition, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getArrival().trim(), 135, 493 + varPosition, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getDeparture().trim(), 435, 493 + varPosition, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, transientImpl.getNoAdditionalGuest().trim(), 180, 478 + varPosition, 0);
                for (int count = 0; count < names.size(); count++) {
                    float x = 50;
                    if (count <= 3) {
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, names.get(0).trim(), x, 435 + varPosition, 0);
                        varPosition -= 13;
                        if (count == 3) {
                            varPosition = 35;
                        }
                    } else if (count >= 4 & count <= 7) {
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, names.get(0).trim(), x + 200, 435 + varPosition, 0);
                        varPosition -= 13;
                        if (count == 7) {
                            varPosition = 35;
                        }
                    } else {
                        content.showTextAligned(PdfContentByte.ALIGN_LEFT, names.get(0).trim(), x + 400, 435 + varPosition, 0);
                        varPosition -= 13;
                    }
                }
                content.endText();
            }
            pdfStamper.close();
            pdfReader.close();
        } catch (DocumentException | FileNotFoundException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (IOException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<FurnitureImpl> getFurnituresFromAdmin() throws RemoteException {
        ArrayList<FurnitureImpl> furniture = new ArrayList<FurnitureImpl>();
        try {
            String resident = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT CONCAT (residentFname, \" \", residentLname) "
                    + "AS residentName, CONCAT (transientFname, \" \", transientLname) "
                    + "AS transientName, roomNumber, furnitureControlNo, "
                    + "furnitureItemName, furnitureBrand, furniturePurchasedate "
                    + "FROM furniture LEFT JOIN room  ON room.roomIdnum = furniture.roomIdnum "
                    + "LEFT JOIN resident ON furniture.residentIdnum = resident.residentIdnum "
                    + "AND resident.roomIdnum = furniture.roomIdnum LEFT JOIN transient "
                    + "ON furniture.transientIdnum = transient.transientIdnum");
            ResultSet resultSet = preparedStatement.executeQuery();
            int c = 0;
            while (resultSet.next()) {
                c++;
                FurnitureImpl f = new FurnitureImpl();
                if (resultSet.getString("residentName") != null) {
                    f.setResident(resultSet.getString("residentName"));
                } else if (resultSet.getString("transientName") != null) {
                    f.setResident(resultSet.getString("residentName"));
                }
                f.setItem_name(resultSet.getString("furnitureItemName"));
                f.setControl_number(resultSet.getString("furnitureControlNo"));
                if (resultSet.getString("furnitureBrand") != null) {
                    f.setBrand(resultSet.getString("furnitureBrand"));
                }
                if (resultSet.getString("furniturePurchasedate") != null) {
                    f.setPurchaseDate(resultSet.getString("furniturePurchasedate"));
                }
                if (resultSet.getString("roomNumber") != null) {
                    f.setRoom(resultSet.getString("roomNumber"));
                }
                furniture.add(f);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return furniture;
    }

    @Override
    public ArrayList<String> getFurnituresItems() throws RemoteException {
        ArrayList<String> items = new ArrayList<String>();
        try {
            String resident = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT furnitureItemName FROM furniture ORDER BY furnitureItemName ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                items.add(resultSet.getString("furnitureItemName"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return items;
    }

    @Override
    public ArrayList<String> getAllRooms() throws RemoteException {
        ArrayList<String> rooms = new ArrayList<String>();
        try {
            String resident = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT roomNumber FROM room;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rooms.add(resultSet.getString("roomNumber"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rooms;
    }

    @Override
    public boolean insertFurniture(ArrayList<String> furnitureInfo) throws RemoteException {
        boolean status = true;
        try {
            int c = 0;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM furniture");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c++;
            }
            String roomId = "";
            preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber = ?");
            if (furnitureInfo.get(5) != null) {
                preparedStatement.setString(1, furnitureInfo.get(5).trim());
            } else {
                preparedStatement.setString(1, "");
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }
            preparedStatement = connection.prepareStatement("INSERT INTO furniture (furnitureControlNo, furnitureColor,"
                    + " furnitureBrand, furniturePurchasedate, furnitureItemName, roomIdnum, furnitureStatus)"
                    + " VALUES (?, ?, ?, ?, ?, ?, 'Available')");
            if (furnitureInfo.get(0) != null) {
                preparedStatement.setString(1, furnitureInfo.get(0).trim());
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (furnitureInfo.get(1) != null) {
                preparedStatement.setString(2, furnitureInfo.get(1).trim());
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (furnitureInfo.get(2) != null) {
                preparedStatement.setString(3, furnitureInfo.get(2).trim());
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            if (furnitureInfo.get(3) != null) {
                DateFormat oldFormat = new SimpleDateFormat("MMMM d, yyyy");
                java.util.Date utilDate = oldFormat.parse(furnitureInfo.get(3));
                DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateF = newFormat.format(utilDate);
                utilDate = newFormat.parse(dateF);
                Date sqlDate = new Date(utilDate.getTime());
                newFormat.format(sqlDate);
                preparedStatement.setDate(4, sqlDate);
            } else {
                preparedStatement.setNull(4, Types.DATE);
            }
            if (furnitureInfo.get(4) != null) {
                preparedStatement.setString(5, furnitureInfo.get(4).trim());
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (roomId.length() != 0) {
                preparedStatement.setDouble(6, Double.parseDouble(roomId.trim()));
            } else {
                preparedStatement.setNull(6, Types.DOUBLE);
            }
            preparedStatement.execute();
            int c1 = 0;
            preparedStatement = connection.prepareStatement("SELECT * FROM furniture");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c1++;
            }
            if (c != (c1 - 1)) {
                status = false;
            }
        } catch (SQLException | ParseException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateFurniture(String controlNumber, ArrayList<String> furnitureInfo) throws RemoteException {
        boolean status = true;
        try {
            System.out.println("furniturectrlNo: " + controlNumber);
            String roomId = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber = ?");
            preparedStatement.setString(1, furnitureInfo.get(5));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }
            String furnitureId = "";
            preparedStatement = connection.prepareStatement("SELECT furnitureIdnum FROM furniture "
                    + "WHERE furnitureControlNo LIKE ?");
            preparedStatement.setString(1, "%" + controlNumber + "%");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                furnitureId = resultSet.getString("furnitureIdnum");
            }
            System.out.println("furnitureId: " + furnitureId);
            preparedStatement = connection.prepareStatement("UPDATE furniture "
                    + "SET furnitureControlNo = ?, "
                    + "furnitureColor = ?, "
                    + "furnitureBrand = ?, "
                    + "furniturePurchasedate = ?, "
                    + "furnitureItemName = ?, "
                    + "roomIdnum = ? "
                    + "WHERE furnitureIdnum = ?");

            preparedStatement.setString(1, furnitureInfo.get(0));
            if (furnitureInfo.get(1) != null) {
                preparedStatement.setString(2, furnitureInfo.get(1));
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (furnitureInfo.get(2) != null) {
                preparedStatement.setString(3, furnitureInfo.get(2));
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            if (furnitureInfo.get(3) != null) {
                if (!furnitureInfo.get(3).equals("")) {
                    DateFormat oldFormat = new SimpleDateFormat("MMMM d, yyyy");
                    java.util.Date utilDate = oldFormat.parse(furnitureInfo.get(3));
                    DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String dateF = newFormat.format(utilDate);
                    utilDate = newFormat.parse(dateF);
                    Date sqlDate = new Date(utilDate.getTime());
                    newFormat.format(sqlDate);
                    preparedStatement.setDate(4, sqlDate);
                } else {
                    preparedStatement.setNull(4, Types.DATE);
                }
            } else {
                preparedStatement.setNull(4, Types.DATE);
            }

            preparedStatement.setString(5, furnitureInfo.get(4));
            if (furnitureInfo.get(5) != null) {
                if (!roomId.trim().isEmpty()) {
                    preparedStatement.setInt(6, Integer.parseInt(roomId));
                } else {
                    preparedStatement.setNull(6, Types.INTEGER);
                }
            } else {
                preparedStatement.setNull(6, Types.INTEGER);
            }
            preparedStatement.setString(7, furnitureId);
            status = !preparedStatement.execute();
        } catch (SQLException | ParseException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public ArrayList<FurnitureImpl> getAllFurnituresForSearch(String item, String name) throws RemoteException {
        ArrayList<FurnitureImpl> furniture = new ArrayList<FurnitureImpl>();
        try {
            String resident = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT CONCAT (residentFname, \" \", residentLname) "
                    + "AS residentName, CONCAT (transientFname, \" \", transientLname) "
                    + "AS transientName, roomNumber, furnitureControlNo, furnitureItemName, furnitureBrand, "
                    + "furniturePurchasedate FROM furniture LEFT JOIN room  ON room.roomIdnum = furniture.roomIdnum "
                    + "LEFT JOIN resident ON furniture.residentIdnum = resident.residentIdnum "
                    + "AND resident.roomIdnum = furniture.roomIdnum LEFT JOIN transient "
                    + "ON furniture.transientIdnum = transient.transientIdnum WHERE "
                    + "(residentFname LIKE ? OR residentLname LIKE ? OR transientFname LIKE ? OR transientLname LIKE ? "
                    + "OR furnitureControlNo LIKE ? OR furnitureBrand LIKE ? OR roomNumber LIKE ?) "
                    + "AND furnitureItemName LIKE ? ORDER BY roomNumber ASC");
            preparedStatement.setString(1, "%" + name + "%");
            preparedStatement.setString(2, "%" + name + "%");
            preparedStatement.setString(3, "%" + name + "%");
            preparedStatement.setString(4, "%" + name + "%");
            preparedStatement.setString(5, "%" + name + "%");
            preparedStatement.setString(6, "%" + name + "%");
            preparedStatement.setString(7, "%" + name + "%");

            if (item.equals("All")) {
                preparedStatement.setString(8, "%%");
            } else {
                preparedStatement.setString(8, "%" + item + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            int c = 0;
            while (resultSet.next()) {
                c++;
                FurnitureImpl f = new FurnitureImpl();
                if (resultSet.getString("residentName") != null) {
                    f.setResident(resultSet.getString("residentName"));
                } else if (resultSet.getString("transientName") != null) {
                    f.setResident(resultSet.getString("residentName"));
                }
                f.setItem_name(resultSet.getString("furnitureItemName"));
                f.setControl_number(resultSet.getString("furnitureControlNo"));
                if (resultSet.getString("furnitureBrand") != null) {
                    f.setBrand(resultSet.getString("furnitureBrand"));
                }
                if (resultSet.getString("furniturePurchasedate") != null) {
                    f.setPurchaseDate(resultSet.getString("furniturePurchasedate"));
                }
                if (resultSet.getString("roomNumber") != null) {
                    f.setRoom(resultSet.getString("roomNumber"));
                }
                furniture.add(f);
            }
//            System.out.println("Count: " + c);
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return furniture;
    }

    @Override
    public boolean deleteFurniture(String controlNumber) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM furniture WHERE furnitureControlNo = ?");
            preparedStatement.setString(1, controlNumber);
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public ArrayList<String> getFurnitureFromCtrlNo(String controlNumber) throws RemoteException {
        ArrayList<String> item = new ArrayList<>();
        try {
            String resident = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT furnitureItemName, furnitureColor, furnitureBrand, "
                    + "furniturePurchasedate, roomNumber FROM furniture LEFT JOIN room ON furniture.roomIdnum = room.roomIdnum "
                    + "WHERE furnitureControlNo LIKE ?");
            preparedStatement.setString(1, "%" + controlNumber + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                item.add(resultSet.getString("furnitureItemName"));
                item.add(resultSet.getString("furnitureColor"));
                item.add(controlNumber);
                item.add(resultSet.getString("furniturePurchasedate"));
                item.add(resultSet.getString("furnitureBrand"));
                item.add(resultSet.getString("roomNumber"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return item;
    }

    @Override
    public Accounts getAccounts() throws RemoteException {
        Accounts account = new Accounts();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                account.setEmailAccount(resultSet.getString("emailAccount"));
                account.setEmailPassword(resultSet.getString("emailPassword"));
                account.setEmailPort(resultSet.getString("emailPort"));
                account.setFowizPasscode(resultSet.getString("fowizPasscode"));
                account.setFowizUsername(resultSet.getString("fowizUsername"));
                account.setFurniturePath(resultSet.getString("furnitureDefault"));
                account.setGadgetPath(resultSet.getString("gadgetDefault"));
                account.setRegistrationPath(resultSet.getString("registrationDefault"));
                account.setReservationPath(resultSet.getString("reservationDefault"));
                account.setTransientPath(resultSet.getString("transientDefault"));
                account.setBiometrics(resultSet.getString("biometric"));
                account.setCurfew(resultSet.getString("curfew"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return account;
    }

    @Override
    public boolean updateAccounts(Accounts accounts) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE account SET "
                    + "emailAccount = ?, "
                    + "emailPassword = ?, "
                    + "emailPort = ?, "
                    + "fowizPasscode = ?, "
                    + "fowizUsername = ?, "
                    + "biometric = ?,"
                    + "curfew = ?");
            preparedStatement.setString(1, accounts.getEmailAccount());
            preparedStatement.setString(2, accounts.getEmailPassword());
            preparedStatement.setString(3, accounts.getEmailPort());
            preparedStatement.setString(4, accounts.getFowizPasscode());
            preparedStatement.setString(5, accounts.getFowizUsername());
            preparedStatement.setString(6, accounts.getBiometrics());
            
            Time sqlTime = new Time(new SimpleDateFormat("HH:mm:ss").parse(accounts.getCurfew()).getTime());
            preparedStatement.setTime(7, sqlTime);
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        } catch (ParseException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public double getRoomRate(String roomType) throws RemoteException {
        double rate = 0.0;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            switch (roomType) {
                case "Single Room":
                    preparedStatement = connection.prepareStatement("SELECT `rentRoomSingleSharing`  FROM rate");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rentRoomSingleSharing");
                    }
                    break;

                case "Double-Sharing Room":
                    preparedStatement = connection.prepareStatement("SELECT `rentRoomDoubleSharing`  FROM rate");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rentRoomDoubleSharing");
                    }
                    break;

                case "Triple-Sharing Room":
                    preparedStatement = connection.prepareStatement("SELECT `rentRoomTripleSharing`  FROM rate");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rentRoomTripleSharing");
                    }
                    break;

                default:
                    preparedStatement = connection.prepareStatement("SELECT `rentRoomMasterSuite`  FROM rate");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rentRoomMasterSuite");
                    }
                    break;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rate;
    }

    @Override
    public boolean updateRoomRate(double rate, String roomType) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            switch (roomType) {
                case "Single Room":
                    preparedStatement = connection.prepareStatement("UPDATE rate SET rentRoomSingleSharing = ?");
                    preparedStatement.setDouble(1, rate);
                    status = preparedStatement.executeUpdate() == 1;
                    break;

                case "Double-Sharing Room":
                    preparedStatement = connection.prepareStatement("UPDATE rate SET rentRoomDoubleSharing = ?");
                    preparedStatement.setDouble(1, rate);
                    status = preparedStatement.executeUpdate() == 1;
                    break;

                case "Triple-Sharing Room":
                    preparedStatement = connection.prepareStatement("UPDATE rate SET rentRoomTripleSharing = ?");
                    preparedStatement.setDouble(1, rate);
                    status = preparedStatement.executeUpdate() == 1;
                    break;

                default:
                    preparedStatement = connection.prepareStatement("UPDATE rate SET rentRoomMasterSuite = ?");
                    preparedStatement.setDouble(1, rate);
                    status = preparedStatement.executeUpdate() == 1;
                    break;
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public double[] getShuttleRate() throws RemoteException {
        double[] rates = new double[2];
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rate");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rates[0] = resultSet.getDouble("shuttleRateDaily");
                rates[1] = resultSet.getDouble("shuttleRateMonthly");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rates;
    }

    @Override
    public boolean updateShuttleRate(double[] rates) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement;
            if (rates[1] == 0.0) {
                preparedStatement = connection.prepareStatement("UPDATE rate SET shuttleRateDaily = ?");
                preparedStatement.setDouble(1, rates[0]);
                status = preparedStatement.executeUpdate() == 1;
            } else if (rates[0] == 0.0) {
                preparedStatement = connection.prepareStatement("UPDATE rate SET shuttleRateMonthly = ?");
                preparedStatement.setDouble(1, rates[1]);
                status = preparedStatement.executeUpdate() == 1;
            } else {
                preparedStatement = connection.prepareStatement("UPDATE rate SET shuttleRateDaily = ?, shuttleRateMonthly = ?");
                preparedStatement.setDouble(1, rates[0]);
                preparedStatement.setDouble(2, rates[1]);
                status = preparedStatement.executeUpdate() == 1;
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateFurniture(String furnitureIdnum, String residentIdnum) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ?, furnitureStatus = 'Taken' WHERE furnitureIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(residentIdnum.trim()));
            preparedStatement.setInt(2, Integer.parseInt(furnitureIdnum.trim()));
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public String getReservationId(String name, String room) throws RemoteException {
        String id = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber = ?");
            preparedStatement.setString(1, room.trim());
            ResultSet rs = preparedStatement.executeQuery();
            String roomIdnum = "";
            while (rs.next()) {
                roomIdnum = rs.getString("roomIdnum");
            }
            String[] ReservationName = name.split(",");
            preparedStatement = connection.prepareStatement("SELECT reservationIdnum FROM reservation WHERE reservationLname LIKE ? AND reservationFname LIKE ? AND roomIdnum = ?");
            preparedStatement.setString(1, ReservationName[0].trim());
            preparedStatement.setString(2, ReservationName[1].trim());
            preparedStatement.setInt(3, Integer.parseInt(roomIdnum));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("reservationIdnum") + " ";
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public String getReservationId(String name) throws RemoteException {
        String id = "";
        try {
            String[] ReservationName = name.split(",");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT reservationIdnum FROM reservation WHERE reservationLname LIKE ? AND reservationFname LIKE ?");
            preparedStatement.setString(1, ReservationName[0].trim());
            preparedStatement.setString(2, ReservationName[1].trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("reservationIdnum") + " ";
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public boolean deleteGadget(String residentId, GadgetImpl gad) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM gadget WHERE residentIdnum = ? AND gadgetItemName LIKE ?");
            preparedStatement.setInt(1, Integer.parseInt(residentId));
            preparedStatement.setString(2, gad.getItem_name());
            return !preparedStatement.execute();
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
    }

    @Override
    public double[] getTransientRate() throws RemoteException {
        double[] rates = new double[6];
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transientrate");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rates[0] = resultSet.getDouble("transientRegularRate");
                rates[1] = resultSet.getDouble("transientFamilyRate");
                rates[2] = resultSet.getDouble("transientRExtraBed");
                rates[3] = resultSet.getDouble("transientFExtraBedRate");
                rates[4] = resultSet.getDouble("transientPrivateSuite");
                rates[5] = resultSet.getDouble("transientPrivateExtraRate");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rates;
    }

    @Override
    public boolean updateTransientRate(double[] rates) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement;
            if (status && rates[0] != 0.0) {
                preparedStatement = connection.prepareStatement("UPDATE transientrate SET transientRegularRate = ?");
                preparedStatement.setDouble(1, rates[0]);
                status = preparedStatement.executeUpdate() == 1;
            }
            if (status && rates[1] != 0.0) {
                preparedStatement = connection.prepareStatement("UPDATE transientrate SET transientFamilyRate = ?");
                preparedStatement.setDouble(1, rates[1]);
                status = preparedStatement.executeUpdate() == 1;
            }
            if (status && rates[2] != 0.0) {
                preparedStatement = connection.prepareStatement("UPDATE transientrate SET transientRExtraBed = ?");
                preparedStatement.setDouble(1, rates[2]);
                status = preparedStatement.executeUpdate() == 1;
            }
            if (status && rates[3] != 0.0) {
                preparedStatement = connection.prepareStatement("UPDATE transientrate SET transientFExtraBedRate = ?");
                preparedStatement.setDouble(1, rates[3]);
                status = preparedStatement.executeUpdate() == 1;
            }
            if (status && rates[4] != 0.0) {
                preparedStatement = connection.prepareStatement("UPDATE transientrate SET transientPrivateSuite = ?");
                preparedStatement.setDouble(1, rates[4]);
                status = preparedStatement.executeUpdate() == 1;
            }
            if (status && rates[5] != 0.0) {
                preparedStatement = connection.prepareStatement("UPDATE transientrate SET transientPrivateExtraRate = ?");
                preparedStatement.setDouble(1, rates[5]);
                status = preparedStatement.executeUpdate() == 1;
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean saveTransientBillingToPdf(String transientIdnum, String amountPaid, String admin, String controlN, String mode, String remarks) throws RemoteException {
        try {
            // TODO add your handling code here:
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            String path="";
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } finally {
                br.close();
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transient WHERE transientIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(transientIdnum.trim()));
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();

            Document doc = new Document();
            FileOutputStream fos = new FileOutputStream(path + "\\transientBilling\\" + rs.getString("transientFname") + " " + rs.getString("transientLname") + "CheckoutDetails.pdf");
            PdfWriter.getInstance(doc, fos);

            PdfReader pdfReader = new PdfReader("Payment Remittance.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                //Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 11);

                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Control Number: " + controlN, 300, 260, 0);
                if (mode.equals("cash")) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 346, 218, 0);
                } else if (mode.equals("cheque")) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 347, 198, 0);
                } else {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 347, 178, 0);
                }

                content.showTextAligned(PdfContentByte.ALIGN_LEFT, remarks, 330, 140, 0);
                //Date
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()), 460, 260, 0);
                //Resident name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, rs.getString("transientFname") + " " + rs.getString("transientLname"), 90, 250, 0);
                //Purpose of payment
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Transient Checkout", 125, 223, 0);
                //Room Details
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, rs.getString("transientReservedRoomNo"), 125, 183, 0);
                //Amount
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, amountPaid, 125, 158, 0);
                //Received By admin  name!
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, admin, 125, 105, 0);
                content.endText();

            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();

            return true;
        } catch (DocumentException | SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (IOException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<BillingImpl> getAllResidentBilling() throws RemoteException {
        ArrayList<BillingImpl> billingResident = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT CONCAT(residentLname,', ',residentFname) AS name, "
                    + "registrationDate, billingBalance, billingDatePaid, billingDatein FROM "
                    + "(SELECT resident.residentIdnum, residentLname, residentFname, registrationDate, "
                    + "billingBalance, billingDatePaid, billingDatein "
                    + "FROM resident LEFT JOIN billing ON resident.residentIdnum = billing.residentIdnum "
                    + "LEFT JOIN registration ON registration.registrationIdnum = resident.registrationIdnum "
                    + "WHERE status NOT LIKE 'Leave' AND billingStatus NOT LIKE 'Replaced' "
                    + "ORDER BY billingDatein DESC, billingDatePaid ASC) as t1 GROUP BY name "
                    + "ORDER BY billingDatePaid DESC, billingDatein DESC) AS t1 ORDER BY name");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BillingImpl bill = new BillingImpl();
                if (resultSet.getString("name") != null) {
                    bill.setFull_name(resultSet.getString("name").trim());
                }
                if (resultSet.getString("registrationDate") != null) {
                    bill.setRegistrationDate(resultSet.getString("registrationDate"));
                }
                if (resultSet.getString("billingBalance") != null) {
                    bill.setBalance(resultSet.getString("billingBalance"));
                } else {
                    bill.setBalance("0.00");
                }
                if (resultSet.getString("billingDatePaid") != null) {
                    bill.setDatePaid(resultSet.getString("billingDatePaid").trim());
                }
                billingResident.add(bill);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billingResident;
    }

    @Override
    public ArrayList<BillingImpl> getAllResidentBillingByStatus(String status) throws RemoteException {
        ArrayList<BillingImpl> billingResident = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            if (status.equalsIgnoreCase("Paid")) {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM (SELECT CONCAT(residentLname,', ',residentFname) AS name, "
                        + "registrationDate, billingBalance, billingDatePaid, billingDatein FROM "
                        + "(SELECT resident.residentIdnum, residentLname, residentFname, registrationDate, "
                        + "billingBalance, billingDatePaid, billingDatein "
                        + "FROM resident LEFT JOIN billing ON resident.residentIdnum = billing.residentIdnum "
                        + "LEFT JOIN registration ON registration.registrationIdnum = resident.registrationIdnum "
                        + "WHERE status NOT LIKE 'Leave' AND billingStatus NOT LIKE 'Replaced' "
                        + "ORDER BY billingDatein DESC, billingDatePaid ASC) as t1 GROUP BY name "
                        + "ORDER BY billingDatePaid DESC, billingDatein DESC) AS t1 WHERE billingBalance = ? ORDER BY name");
                preparedStatement.setDouble(1, 0.00);
            } else {
                preparedStatement = connection.prepareStatement("SELECT * FROM (SELECT CONCAT(residentLname,', ',residentFname) AS name, "
                        + "registrationDate, billingBalance, billingDatePaid, billingDatein FROM "
                        + "(SELECT resident.residentIdnum, residentLname, residentFname, registrationDate, "
                        + "billingBalance, billingDatePaid, billingDatein "
                        + "FROM resident LEFT JOIN billing ON resident.residentIdnum = billing.residentIdnum "
                        + "LEFT JOIN registration ON registration.registrationIdnum = resident.registrationIdnum "
                        + "WHERE status NOT LIKE 'Leave' AND billingStatus NOT LIKE 'Replaced' "
                        + "ORDER BY billingDatein DESC, billingDatePaid ASC) as t1 GROUP BY name "
                        + "ORDER BY billingDatePaid DESC, billingDatein DESC) AS t1 WHERE billingBalance != ? ORDER BY name");
                preparedStatement.setDouble(1, 0.00);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BillingImpl bill = new BillingImpl();
                if (resultSet.getString("name") != null) {
                    bill.setFull_name(resultSet.getString("name").trim());
                }
                if (resultSet.getString("registrationDate") != null) {
                    bill.setRegistrationDate(resultSet.getString("registrationDate"));
                }
                if (resultSet.getString("billingBalance") != null) {
                    bill.setBalance(resultSet.getString("billingBalance"));
                } else {
                    bill.setBalance("0.00");
                }
                if (resultSet.getString("billingDatePaid") != null) {
                    bill.setDatePaid(resultSet.getString("billingDatePaid").trim());
                }
                billingResident.add(bill);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billingResident;
    }

    @Override
    public ArrayList<BillingImpl> getAllResidentBillingByName(String status, String keyword) throws RemoteException {
        ArrayList<BillingImpl> billingResident = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            if (status.equalsIgnoreCase("Paid")) {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM (SELECT CONCAT(residentLname,', ',residentFname) AS name, "
                        + "registrationDate, billingBalance, billingDatePaid, billingDatein FROM "
                        + "(SELECT resident.residentIdnum, residentLname, residentFname, registrationDate, "
                        + "billingBalance, billingDatePaid, billingDatein "
                        + "FROM resident LEFT JOIN billing ON resident.residentIdnum = billing.residentIdnum "
                        + "LEFT JOIN registration ON registration.registrationIdnum = resident.registrationIdnum "
                        + "WHERE (resident.residentLname LIKE ? OR resident.residentFname LIKE ?) "
                        + "AND (status NOT LIKE 'Leave' AND billingStatus NOT LIKE 'Replaced') "
                        + "ORDER BY billingDatein DESC, billingDatePaid ASC) as t1 GROUP BY name "
                        + "ORDER BY billingDatePaid DESC, billingDatein DESC) AS t1 WHERE billingBalance = 0.00 ORDER BY name");
                preparedStatement.setString(1, "%" + keyword + "%");
                preparedStatement.setString(2, "%" + keyword + "%");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    BillingImpl bill = new BillingImpl();
                    if (resultSet.getString("name") != null) {
                        bill.setFull_name(resultSet.getString("name").trim());
                    }
                    if (resultSet.getString("registrationDate") != null) {
                        bill.setRegistrationDate(resultSet.getString("registrationDate"));
                    }
                    if (resultSet.getString("billingBalance") != null) {
                        bill.setBalance(resultSet.getString("billingBalance"));
                    } else {
                        bill.setBalance("0.00");
                    }
                    if (resultSet.getString("billingDatePaid") != null) {
                        bill.setDatePaid(resultSet.getString("billingDatePaid").trim());
                    }
                    billingResident.add(bill);
                }
            } else if (status.equalsIgnoreCase("Unpaid")) {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM (SELECT CONCAT(residentLname,', ',residentFname) AS name, "
                        + "registrationDate, billingBalance, billingDatePaid, billingDatein FROM "
                        + "(SELECT resident.residentIdnum, residentLname, residentFname, registrationDate,"
                        + "billingBalance, billingDatePaid, billingDatein "
                        + "FROM resident LEFT JOIN billing ON resident.residentIdnum = billing.residentIdnum "
                        + "LEFT JOIN registration ON registration.registrationIdnum = resident.registrationIdnum "
                        + "WHERE (resident.residentLname LIKE ? OR resident.residentFname LIKE ?) "
                        + "AND (status NOT LIKE 'Leave' AND billingStatus NOT LIKE 'Replaced') "
                        + "ORDER BY billingDatein DESC, billingDatePaid ASC) as t1 GROUP BY name "
                        + "ORDER BY billingDatePaid DESC, billingDatein DESC) AS t1 WHERE billingBalance != 0.00 ORDER BY name");
                preparedStatement.setString(1, "%" + keyword + "%");
                preparedStatement.setString(2, "%" + keyword + "%");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    BillingImpl bill = new BillingImpl();
                    if (resultSet.getString("name") != null) {
                        bill.setFull_name(resultSet.getString("name").trim());
                    }
                    if (resultSet.getString("registrationDate") != null) {
                        bill.setRegistrationDate(resultSet.getString("registrationDate"));
                    }
                    if (resultSet.getString("billingBalance") != null) {
                        bill.setBalance(resultSet.getString("billingBalance"));
                    } else {
                        bill.setBalance("0.00");
                    }
                    if (resultSet.getString("billingDatePaid") != null) {
                        bill.setDatePaid(resultSet.getString("billingDatePaid").trim());
                    }
                    billingResident.add(bill);
                }
            } else {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM (SELECT CONCAT(residentLname,', ',residentFname) AS name, "
                        + "registrationDate, billingBalance, billingDatePaid, billingDatein FROM "
                        + "(SELECT resident.residentIdnum, residentLname, residentFname, registrationDate,"
                        + "billingBalance, billingDatePaid, billingDatein "
                        + "FROM resident LEFT JOIN billing ON resident.residentIdnum = billing.residentIdnum "
                        + "LEFT JOIN registration ON registration.registrationIdnum = resident.registrationIdnum "
                        + "WHERE (resident.residentLname LIKE ? OR resident.residentFname LIKE ?) "
                        + "AND (status NOT LIKE 'Leave' AND billingStatus NOT LIKE 'Replaced') "
                        + "ORDER BY billingDatein DESC, billingDatePaid ASC) as t1 GROUP BY name "
                        + "ORDER BY billingDatePaid DESC, billingDatein DESC) AS t1 ORDER BY name");
                preparedStatement.setString(1, "%" + keyword + "%");
                preparedStatement.setString(2, "%" + keyword + "%");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    BillingImpl bill = new BillingImpl();
                    if (resultSet.getString("name") != null) {
                        bill.setFull_name(resultSet.getString("name").trim());
                    }
                    if (resultSet.getString("registrationDate") != null) {
                        bill.setRegistrationDate(resultSet.getString("registrationDate"));
                    }
                    if (resultSet.getString("billingBalance") != null) {
                        bill.setBalance(resultSet.getString("billingBalance"));
                    } else {
                        bill.setBalance("0.00");
                    }
                    if (resultSet.getString("billingDatePaid") != null) {
                        bill.setDatePaid(resultSet.getString("billingDatePaid").trim());
                    }
                    billingResident.add(bill);
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billingResident;
    }

    @Override
    public ArrayList<String> getResidentRoomDetail(String residentId) throws RemoteException {
        ArrayList<String> roomDetails = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT CONCAT (residentFname, \" \", residentLname) AS residentName, "
                    + "roomNumber, roomType, roomDorm FROM resident LEFT JOIN room ON resident.roomIdnum = room.roomIdnum "
                    + "WHERE residentIdnum = ?");
            preparedStatement.setString(1, residentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomDetails.add(resultSet.getString("residentName"));
                roomDetails.add(resultSet.getString("roomNumber"));
                switch (resultSet.getString("roomType")) {
                    case "T":
                        roomDetails.add("Triple Sharing Room");
                        break;
                    case "D":
                        roomDetails.add("Double Sharing Room");
                        break;
                    case "S":
                        roomDetails.add("Single Room");
                        break;
                    default:
                        roomDetails.add("Suite");
                        break;
                }
                roomDetails.add(resultSet.getString("roomDorm"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return roomDetails;
    }

    @Override
    public ArrayList<String> getRoomDetail(String roomNumber) throws RemoteException {
        ArrayList<String> roomDetails = new ArrayList<>();
        try {
            String resident = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomType, roomDorm "
                    + "FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, roomNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                switch (resultSet.getString("roomType")) {
                    case "T":
                        roomDetails.add("Triple Sharing Room");
                        break;
                    case "D":
                        roomDetails.add("Double Sharing Room");
                        break;
                    default:
                        roomDetails.add("Single Room");
                        break;
                }
                roomDetails.add(resultSet.getString("roomDorm"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return roomDetails;
    }

    @Override
    public boolean updateRoomStatus(String oldRoom, String newRoom, String residentId) throws RemoteException {
        boolean status = false;
        try {

            String roomId = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomIdnum "
                    + "FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, "%" + newRoom + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }
            int count = 0;

            preparedStatement = connection.prepareStatement("UPDATE resident "
                    + "SET roomIdnum = ? "
                    + "WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(roomId));
            preparedStatement.setString(2, residentId);
            status = preparedStatement.executeUpdate() == 1;
            
            preparedStatement = connection.prepareStatement("SELECT registrationIdnum FROM resident WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(residentId.trim()));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            
            preparedStatement = connection.prepareStatement("UPDATE registration SET roomIdnum=? WHERE registrationIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(roomId));
            preparedStatement.setInt(2, resultSet.getInt("registrationIdnum"));
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("SELECT "
                    + "(SELECT DISTINCT COUNT(reservationIdnum) "
                    + "FROM  room  JOIN reservation ON room.roomIdnum = reservation.roomIdnum "
                    + "WHERE room.roomIdnum = ? AND reservationStatus = 'Pending') + "
                    + "(SELECT DISTINCT COUNT(residentIdnum) "
                    + "FROM  room  JOIN resident ON room.roomIdnum = resident.roomIdnum "
                    + "WHERE room.roomIdnum = ?) AS count");
            preparedStatement.setString(1, roomId);
            preparedStatement.setString(2, roomId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("count");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM room "
                    + "WHERE roomIdnum = ?");
            preparedStatement.setString(1, roomId);
            resultSet = preparedStatement.executeQuery();
//            System.out.println(count);
            while (resultSet.next()) {
                switch (resultSet.getString("roomType")) {
                    case "S":
                        switch (count) {
                            case 1:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "SET roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                            default:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "SET roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                        }
                        break;

                    case "D":
                        switch (count) {
                            case 2:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;

                            case 1:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'partially occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;

                            default:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                        }
                        break;

                    case "T":
                        switch (count) {
                            case 3:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                            case 1:
                            case 2:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'partially occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;

                            default:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                        }
                        break;
                }
            }

            if (status) {
                roomId = "";
                preparedStatement = connection.prepareStatement("SELECT roomIdnum "
                        + "FROM room WHERE roomNumber LIKE ?");
                preparedStatement.setString(1, "%" + oldRoom + "%");
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    roomId = resultSet.getString("roomIdnum");
                }

                count = 0;
                preparedStatement = connection.prepareStatement("SELECT "
                        + "(SELECT DISTINCT COUNT(reservationIdnum) "
                        + "FROM  room  JOIN reservation ON room.roomIdnum = reservation.roomIdnum "
                        + "WHERE room.roomIdnum = ? AND reservationStatus = 'Pending') + "
                        + "(SELECT DISTINCT COUNT(residentIdnum) "
                        + "FROM  room  JOIN resident ON room.roomIdnum = resident.roomIdnum "
                        + "WHERE room.roomIdnum = ?) AS count");
                preparedStatement.setString(1, roomId);
                preparedStatement.setString(2, roomId);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    count = resultSet.getInt("count");
                }

                preparedStatement = connection.prepareStatement("SELECT roomType FROM room WHERE roomIdnum = ?");
                preparedStatement.setString(1, roomId);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    switch (resultSet.getString("roomType")) {
                        case "S":
                            switch (count) {
                                case 1:
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "SET roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                    break;
                                default:
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "SET roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                    break;
                            }
                            break;

                        case "D":
                            switch (count) {
                                case 2:
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                    break;

                                case 1:
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'partially occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                    break;

                                default:
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                    break;
                            }
                            break;

                        case "T":
                            switch (count) {
                                case 3:
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                    break;
                                case 1:
                                case 2:
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'partially occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                    break;

                                default:
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                    break;
                            }
                            break;
                    }
                }
            }

            preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ? WHERE roomIdnum = ?");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setInt(2, Integer.parseInt(roomId.trim()));
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public ArrayList<String> getResidentAndRoomInfo(String residentId) throws RemoteException {
        ArrayList<String> billing = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT CONCAT(residentFname, \" \" ,residentLname) AS residentName, "
                    + "residentHomeAddress, roomNumber, roomType  "
                    + "FROM resident NATURAL JOIN room WHERE residentIdnum = ?");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                billing.add(resultSet.getString("residentName"));
                billing.add(resultSet.getString("residentHomeAddress"));
                billing.add(resultSet.getString("roomNumber"));
                billing.add(resultSet.getString("roomType"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billing;
    }

    @Override
    public ArrayList<String> getResidentAndBillingInfo(String residentId) throws RemoteException {
        ArrayList<String> billing = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            String query = "";
            preparedStatement = connection.prepareStatement("SELECT * FROM (SELECT * FROM billing ORDER BY billingDatePaid ASC) as t1 WHERE residentIdnum = ? GROUP BY residentIdnum ORDER BY billingDatein DESC, billingDatePaid DESC");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                billing.add(resultSet.getString("billingDatein"));
                billing.add(resultSet.getString("billingDateout"));
                billing.add(resultSet.getString("billingRateType"));
                billing.add(resultSet.getString("billingNoofDays"));
                billing.add(resultSet.getString("billingTotalRoomrate"));
                billing.add(resultSet.getString("billingTotalShuttleRate"));
                billing.add(resultSet.getString("billingTotalGadgetRate"));
                billing.add(resultSet.getString("billingAdditionalFee"));
                billing.add(resultSet.getString("billingTotalAmount"));
                billing.add(resultSet.getString("billingAmountPaid"));
                billing.add(resultSet.getString("billingBalance"));
                billing.add(resultSet.getString("billingRemarks"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billing;
    }

    @Override
    public ArrayList<String> getBillingAndShuttleInfo(String residentId) throws RemoteException {
        ArrayList<String> billing = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.billing NATURAL JOIN shuttle WHERE residentIdnum = ?");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                billing.add(resultSet.getString("shuttleRateType"));
                billing.add(resultSet.getString("shuttleNoRides"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billing;
    }

    @Override
    public ArrayList<String[]> getResidentAndGadgetInfo(String residentId) throws RemoteException {
        ArrayList<String[]> billing = new ArrayList<>();

        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT gadget.gadgetItemName, gadget.gadgetRate "
                    + "FROM gadget JOIN resident ON gadget.residentIdnum = resident.residentIdnum "
                    + "WHERE gadget.residentIdnum = ?");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String[] info = new String[2];
                info[0] = resultSet.getString("gadgetItemName");
                info[1] = resultSet.getString("gadgetRate");
                billing.add(info);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billing;
    }

    @Override
    public boolean insertBillingGadget(String billingId, String gadgetId, boolean isChecked) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO billinggadget(billingIdnum, gadgetIdnum, isChecked) "
                    + "VALUES (?,?,?)");
            if (billingId.trim().length() != 0) {
                preparedStatement.setString(1, billingId.trim());
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (gadgetId.trim().length() != 0) {
                preparedStatement.setString(2, gadgetId.trim());
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            preparedStatement.setBoolean(3, isChecked);
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public ArrayList<BillingGadgetImpl> getBillingGadget(String billingId) throws RemoteException {
        ArrayList<BillingGadgetImpl> billingGadget = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT gadgetItemName, gadgetRate, isChecked FROM billing NATURAL JOIN billinggadget NATURAL JOIN gadget WHERE billinggadget.billingIdnum = ?");
            preparedStatement.setString(1, billingId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BillingGadgetImpl bill = new BillingGadgetImpl();
                bill.setItemName(resultSet.getString("gadgetItemName"));
                bill.setGadgetRate(resultSet.getDouble("gadgetRate"));
                bill.setIsSelected(resultSet.getBoolean("isChecked"));
                billingGadget.add(bill);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billingGadget;
    }

    @Override
    public boolean insertBilling(ArrayList<String> billingInfo) throws RemoteException {
        boolean status = true;
        try {
            int c = 0;
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            
            preparedStatement = connection.prepareStatement("UPDATE billing SET billingStatus = ? WHERE residentIdnum = ? AND billingStatus LIKE 'Unpaid'");
            preparedStatement.setString(1, "Replaced");
            preparedStatement.setInt(2, Integer.parseInt(billingInfo.get(10).trim()));
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("SELECT * FROM billing");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c++;
            }

            preparedStatement = connection.prepareStatement(
                    "INSERT INTO billing (billingDatePaid, billingDateIn, billingDateOut, "
                    + "billingRateType, billingNoofDays, billingRoomrate, billingShuttleRatetype, "
                    + "billingShuttleRate, billingGadgetRate, billingRemarks, residentIdnum, "
                    + "roomIdnum, billingTotalAmount, billingBalance, billingTotalRoomrate, "
                    + "billingTotalShuttlerate, billingTotalGadgetrate, billingAdditionalfee, billingStatus) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            if (billingInfo.get(0) != null) {
                preparedStatement.setString(1, billingInfo.get(0));
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (billingInfo.get(1) != null) {
                preparedStatement.setString(2, billingInfo.get(1));

            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (billingInfo.get(2) != null) {
                preparedStatement.setString(3, billingInfo.get(2));

            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            if (billingInfo.get(3) != null) {
                preparedStatement.setString(4, billingInfo.get(3));

            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if (billingInfo.get(4) != null) {
                preparedStatement.setString(5, billingInfo.get(4));

            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (billingInfo.get(5) != null) {
                preparedStatement.setString(6, billingInfo.get(5));

            } else {
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            if (billingInfo.get(6) != null) {
                preparedStatement.setString(7, billingInfo.get(6));

            } else {
                preparedStatement.setNull(7, Types.VARCHAR);
            }
            if (billingInfo.get(7) != null) {
                preparedStatement.setString(8, billingInfo.get(7));

            } else {
                preparedStatement.setNull(8, Types.VARCHAR);
            }
            if (billingInfo.get(8) != null) {
                preparedStatement.setString(9, billingInfo.get(8));

            } else {
                preparedStatement.setNull(9, Types.VARCHAR);
            }
            if (billingInfo.get(9) != null) {
                preparedStatement.setString(10, billingInfo.get(9));

            } else {
                preparedStatement.setNull(10, Types.VARCHAR);
            }
            if (billingInfo.get(10) != null) {
                preparedStatement.setString(11, billingInfo.get(10));

            } else {
                preparedStatement.setNull(11, Types.VARCHAR);
            }
            if (billingInfo.get(11) != null) {
                preparedStatement.setString(12, billingInfo.get(11));
            } else {
                preparedStatement.setNull(12, Types.VARCHAR);
            }
            if (billingInfo.get(12) != null) {
                preparedStatement.setString(13, billingInfo.get(12));
            } else {
                preparedStatement.setNull(13, Types.VARCHAR);
            }
            if (billingInfo.get(13) != null) {
                preparedStatement.setString(14, billingInfo.get(13));
            } else {
                preparedStatement.setNull(14, Types.VARCHAR);
            }
            if (billingInfo.get(14) != null) {
                preparedStatement.setString(15, billingInfo.get(14));
            } else {
                preparedStatement.setNull(15, Types.VARCHAR);
            }
            if (billingInfo.get(15) != null) {
                preparedStatement.setString(16, billingInfo.get(15));
            } else {
                preparedStatement.setNull(16, Types.VARCHAR);
            }
            if (billingInfo.get(16) != null) {
                preparedStatement.setString(17, billingInfo.get(16));
            } else {
                preparedStatement.setNull(17, Types.VARCHAR);
            }
            if (billingInfo.get(17) != null) {
                preparedStatement.setString(18, billingInfo.get(17));
            } else {
                preparedStatement.setNull(18, Types.VARCHAR);
            }
            preparedStatement.setString(19, billingInfo.get(18).trim());

            preparedStatement.execute();
            int c1 = 0;
            preparedStatement = connection.prepareStatement("SELECT * FROM billing");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c1++;
            }
            if (c != (c1 - 1)) {
                status = false;
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public String getBillingId(String residentId) throws RemoteException {
        String id = "";
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT billingIdnum FROM billing WHERE residentIdnum = ?");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("billingIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public String getGadgetId(String itemName, String residentId) throws RemoteException {
        String id = "";
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT DISTINCT gadgetIdnum FROM gadget WHERE residentIdnum = ? AND gadgetItemName = ? GROUP BY residentIdnum");
            preparedStatement.setString(1, residentId);
            preparedStatement.setString(2, itemName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("gadgetIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return id;
    }

    @Override
    public boolean updateRoomStatus(String oldRomm, String newRoom) throws RemoteException {
        try {
            //update old room
            String roomId = null;
            String query = "UPDATE room SET roomStatus = 'unoccupied' WHERE roomIdnum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, oldRomm.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum").trim();
            }
            int count = 0;
            preparedStatement = connection.prepareStatement("SELECT "
                    + "(SELECT DISTINCT COUNT(reservationIdnum) "
                    + "FROM  room  JOIN reservation ON room.roomIdnum = reservation.roomIdnum "
                    + "WHERE room.roomIdnum = ? AND reservationStatus = 'Pending') + "
                    + "(SELECT DISTINCT COUNT(residentIdnum) "
                    + "FROM  room  JOIN resident ON room.roomIdnum = resident.roomIdnum "
                    + "WHERE room.roomIdnum = ?) AS count");
            preparedStatement.setString(1, roomId);
            preparedStatement.setString(2, roomId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("count");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM room "
                    + "WHERE roomIdnum = ?");
            preparedStatement.setString(1, roomId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                switch (resultSet.getString("roomType")) {
                    case "S":
                        switch (count) {
                            case 1:
                                query = "UPDATE room set roomStatus = 'fully occupied' WHERE roomIdnum = ?";
                                break;

                            default:
                                query = "UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?";
                                break;
                        }
                        break;

                    case "D":
                        switch (count) {
                            case 2:
                                query = "UPDATE room set roomStatus = 'fully occupied' WHERE roomIdnum = ?";
                                break;

                            case 1:
                                query = "UPDATE room set roomStatus = 'partially occupied' WHERE roomIdnum = ?";
                                break;

                            default:
                                query = "UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?";
                                break;
                        }
                        break;

                    case "T":
                        switch (count) {
                            case 3:
                                query = "UPDATE room set roomStatus = 'fully occupied' WHERE roomIdnum = ?";
                                break;

                            case 1:
                            case 2:
                                query = "UPDATE room set roomStatus = 'partially occupied' WHERE roomIdnum = ?";
                                break;

                            default:
                                query = "UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?";
                                break;
                        }
                        break;
                }
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, roomId);
                preparedStatement.executeUpdate();
            }

            //update new room
            preparedStatement = connection.prepareStatement("SELECT roomIdnum FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, newRoom.trim());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum").trim();
            }
            count = 0;
            preparedStatement = connection.prepareStatement("SELECT "
                    + "(SELECT DISTINCT COUNT(reservationIdnum) "
                    + "FROM  room  JOIN reservation ON room.roomIdnum = reservation.roomIdnum "
                    + "WHERE room.roomIdnum = ? AND reservationStatus = 'Pending') + "
                    + "(SELECT DISTINCT COUNT(residentIdnum) "
                    + "FROM  room  JOIN resident ON room.roomIdnum = resident.roomIdnum "
                    + "WHERE room.roomIdnum = ?) AS count");
            System.out.println("ID: " + roomId);
            preparedStatement.setString(1, roomId);
            preparedStatement.setString(2, roomId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("count");
            }
            preparedStatement = connection.prepareStatement("SELECT * FROM room "
                    + "WHERE roomIdnum = ?");
            preparedStatement.setString(1, roomId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                switch (resultSet.getString("roomType")) {
                    case "S":
                        switch (count) {
                            case 1:
                                query = "UPDATE room set roomStatus = 'fully occupied' WHERE roomIdnum = ?";
                                break;
                            default:
                                query = "UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?";
                                break;
                        }
                        break;

                    case "D":
                        switch (count) {
                            case 2:
                                query = "UPDATE room set roomStatus = 'fully occupied' WHERE roomIdnum = ?";
                                break;

                            case 1:
                                query = "UPDATE room set roomStatus = 'partially occupied' WHERE roomIdnum = ?";
                                break;

                            default:
                                query = "UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?";
                                break;
                        }
                        break;

                    case "T":
                        switch (count) {
                            case 3:
                                query = "UPDATE room set roomStatus = 'fully occupied' WHERE roomIdnum = ?";
                                break;
                            case 1:
                            case 2:
                                query = "UPDATE room set roomStatus = 'partially occupied' WHERE roomIdnum = ?";
                                break;

                            default:
                                query = "UPDATE room set roomStatus = 'unoccupied' WHERE roomIdnum = ?";
                                break;
                        }
                        break;
                }
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, roomId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean updateFurnitureTosNull() throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ?, furnitureStatus = 'Available' WHERE residentIdnum = ?");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setInt(2, Integer.parseInt("99999999"));
            status = !preparedStatement.execute();

        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public MessageTemplate getMessageTemplates() throws RemoteException {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM messageTemplate");
            while (rs.next()) {
                return new MessageTemplate(rs.getString("reservation7days"), rs.getString("reservation3days"), rs.getString("successfulRegistration"), rs.getString("visitorInform"),
                        rs.getString("rentDueDate"), rs.getString("balanceAmount"), rs.getString("newRoommate"), rs.getString("curfewNotice"));
            }

        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return null;
    }

    @Override
    public double getPreviousBalance(String id, String prev) throws RemoteException {
        double balance = 0.0;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT billingBalance FROM billing WHERE residentIdnum = ? AND billingStatus = 'Paid' ORDER BY billingDatePaid DESC LIMIT 1");
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                balance = resultSet.getDouble("billingBalance");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return balance;
    }

    @Override
    public String getDatein(String id, String date) throws RemoteException {
        String d = "";
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT billingDatein FROM billing WHERE residentIdnum = ? AND billingDatePaid = ?");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, date);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                d = resultSet.getString("billingDatein");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return d;
    }

    @Override
    public boolean insertShuttle(ArrayList<String> info) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("INSERT INTO shuttle (shuttleDate, shuttleNoRides, residentIdnum, shuttleStatus) VALUES (?,?,?,?)");
            preparedStatement.setString(1, info.get(0));
            preparedStatement.setInt(2, Integer.parseInt(info.get(1)));
            preparedStatement.setInt(3, Integer.parseInt(info.get(2)));
            preparedStatement.setString(4, "unpaid");
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateShuttle(String id) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("SELECT * FROM shuttle WHERE residentIdnum = ? AND shuttleStatus = 'unpaid'");
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            int c = 0;
            while (resultSet.next()) {
                c++;
            }
            while (c != 0) {
                preparedStatement = connection.prepareStatement("UPDATE shuttle set shuttleStatus = 'paid' WHERE residentIdnum = ? AND shuttleStatus = 'unpaid'");
                preparedStatement.setString(1, id);
                if (preparedStatement.executeUpdate() == 1) {
                    status = false;
                    break;
                }
                c--;
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean deleteShuttle(String id) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM shuttle WHERE residentIdnum = ? AND shuttleStatus = 'unpaid'");
            preparedStatement.setString(1, id);
            if (preparedStatement.execute()) {
                status = false;
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean isSaved(ArrayList<String> info) throws RemoteException {
        boolean status = false;
        try {
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("SELECT * FROM shuttle WHERE shuttleDate = ? AND shuttleStatus = 'unpaid' AND shuttleNoRides = ? AND residentIdnum = ?");
            preparedStatement.setString(1, info.get(0));
            preparedStatement.setString(2, info.get(1));
            preparedStatement.setString(3, info.get(2));
            ResultSet resultSet = preparedStatement.executeQuery();
            int c = 0;
            while (resultSet.next()) {
                c++;
            }
            if (c != 0) {
                status = true;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public ArrayList<String> getResidentAndBillingInfo(String residentId, String dateIn) throws RemoteException {
        ArrayList<String> billing = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT * FROM billing NATURAL JOIN resident WHERE residentIdnum = ? AND billingDatePaid = ? ORDER BY billingDatePaid DESC, billingDatePaid DESC");
            preparedStatement.setString(1, residentId);
            preparedStatement.setDate(2, new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateIn).getTime()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                billing.add(resultSet.getString("billingDatein"));
                billing.add(resultSet.getString("billingDateout"));
                billing.add(resultSet.getString("billingRateType"));
                billing.add(resultSet.getString("billingNoofDays"));
                billing.add(resultSet.getString("billingTotalRoomrate"));
                billing.add(resultSet.getString("billingTotalShuttleRate"));
                billing.add(resultSet.getString("billingTotalGadgetRate"));
                billing.add(resultSet.getString("billingAdditionalFee"));
                billing.add(resultSet.getString("billingTotalAmount"));
                billing.add(resultSet.getString("billingAmountPaid"));
                billing.add(resultSet.getString("billingBalance"));
                billing.add(resultSet.getString("billingRemarks"));
                billing.add(resultSet.getString("billingShuttleRate"));
            }
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return billing;
    }

    @Override
    public String getBillingShuttleType(String residentId) throws RemoteException {
        String shuttleType = "";
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT * FROM billing NATURAL JOIN resident WHERE residentIdnum = ? AND billingStatus LIKE 'Unpaid' ORDER BY billingDatePaid DESC, billingDatePaid DESC LIMIT 1");
            preparedStatement.setInt(1, Integer.parseInt(residentId));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                shuttleType = resultSet.getString("billingShuttleRatetype");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return shuttleType;
    }

    @Override
    public double getBillingShuttleRiderate(String residentId) throws RemoteException {
        double rideRate = 0.0;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT * FROM billing NATURAL JOIN resident WHERE residentIdnum = ? ORDER BY billingDatePaid DESC, billingDatePaid DESC");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rideRate = resultSet.getDouble("billingShuttleRate");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rideRate;
    }

    @Override
    public int getTotalRides(String residentIdnum) throws RemoteException {
        int c = 0;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT shuttleNoRides FROM shuttle WHERE residentIdnum = ? AND shuttleStatus = 'unpaid'");
            preparedStatement.setString(1, residentIdnum);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c += resultSet.getDouble("shuttleNoRides");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return c;
    }

    @Override
    public ArrayList<ShuttleImpl> getRides(String residentIdnum) throws RemoteException {
        ArrayList<ShuttleImpl> rides = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT shuttleDate, shuttleNoRides FROM shuttle WHERE residentIdnum = ? AND shuttleStatus = 'unpaid'");
            preparedStatement.setString(1, residentIdnum);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rides.add(new ShuttleImpl(new SimpleDateFormat("MMM dd, yyyy").format(resultSet.getDate("shuttleDate")), resultSet.getInt("shuttleNoRides")));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return rides;
    }

    @Override
    public boolean updateBilling(ArrayList<String> billingInfo) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `billing` SET "
                    + "`billingDatePaid` = ?, " //1
                    + "`billingRemarks` = ?, " //2
                    + "`billingTotalRoomrate` = ?, " //3
                    + "`billingTotalShuttlerate` = ?, " //4
                    + "`billingTotalGadgetrate` = ?, " //5
                    + "`billingAdditionalfee` = ?, " //6
                    + "`billingTotalAmount` = ?, " //7
                    + "`billingAmountPaid` = ?, " //8
                    + "`billingStatus` = ?, " //9
                    + "`billingBalance` = ?, " //10
                    + "`adminIdnum` = ?, "
                    + "`billingModeOfPayment` = ? " //11
                    + "WHERE residentIdnum = ? AND billingStatus LIKE 'Unpaid'"); //12
            if (billingInfo.get(0) != null) {
                preparedStatement.setString(1, billingInfo.get(0));
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (billingInfo.get(1) != null) {
                preparedStatement.setString(2, billingInfo.get(1));
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (billingInfo.get(2) != null) {
                preparedStatement.setDouble(3, Double.parseDouble(billingInfo.get(2)));
            } else {
                preparedStatement.setNull(3, Types.DOUBLE);
            }
            if (billingInfo.get(3) != null) {
                preparedStatement.setDouble(4, Double.parseDouble(billingInfo.get(3)));
            } else {
                preparedStatement.setNull(4, Types.DOUBLE);
            }
            if (billingInfo.get(4) != null) {
                preparedStatement.setDouble(5, Double.parseDouble(billingInfo.get(4)));
            } else {
                preparedStatement.setNull(5, Types.DOUBLE);
            }
            if (billingInfo.get(5) != null) {
                preparedStatement.setDouble(6, Double.parseDouble(billingInfo.get(5)));
            } else {
                preparedStatement.setNull(6, Types.DOUBLE);
            }
            if (billingInfo.get(6) != null) {
                preparedStatement.setDouble(7, Double.parseDouble(billingInfo.get(6)));
            } else {
                preparedStatement.setNull(7, Types.DOUBLE);
            }
            if (billingInfo.get(7) != null) {
                preparedStatement.setDouble(8, Double.parseDouble(billingInfo.get(7)));
            } else {
                preparedStatement.setNull(8, Types.DOUBLE);
            }
            preparedStatement.setString(9, "paid");
            if (billingInfo.get(8) != null) {
                preparedStatement.setDouble(10, Double.parseDouble(billingInfo.get(8)));
            } else {
                preparedStatement.setNull(10, Types.DOUBLE);
            }
            preparedStatement.setInt(11, Integer.parseInt(billingInfo.get(13).trim()));
            preparedStatement.setString(12, billingInfo.get(12));
            preparedStatement.setInt(13, Integer.parseInt(billingInfo.get(9)));
            status = preparedStatement.execute();

            if(!status){
                preparedStatement = connection.prepareStatement("SELECT billingIdnum FROM billing WHERE residentIdnum = ? ORDER BY BillingDatePaid DESC LIMIT 1");
                preparedStatement.setInt(1, Integer.parseInt(billingInfo.get(9)));
                ResultSet rs = preparedStatement.executeQuery();
                rs.next();
                preparedStatement = connection.prepareStatement("INSERT INTO controlNumber (controlNumber, type, id) VALUES (?,?,?)");
                preparedStatement.setInt(1, Integer.parseInt(billingInfo.get(10)));
                preparedStatement.setString(2, billingInfo.get(11).trim());
                preparedStatement.setInt(3, rs.getInt("billingIdnum"));
                preparedStatement.execute();
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public String getAdminName(String id) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT CONCAT(adminFname, ' ', adminLname) as name FROM admin WHERE adminIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(id));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
        return null;
    }

    @Override
    public boolean saveStatementOfAccountToPDF(ArrayList<String> info) throws RemoteException {
        try {
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            String path="";
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } finally {
                br.close();
            }

            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            FileOutputStream fos = new FileOutputStream(path + "\\residentStatementOfAccount\\" + info.get(0) + "statementOfAccounts.pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);

            PdfReader pdfReader = new PdfReader("statementOfAccounts.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(info.get(6));

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 10);
                //month
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, new SimpleDateFormat("MMMM").format(date), 205, 625, 0);
                //name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(0), 125, 610, 0);
                //roomNumber
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(1), 130, 598, 0);
                //monthly rate
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(2), 205, 560, 0);
                //Shuttle rate
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(3), 205, 545, 0);
                //gadget rate
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(4), 205, 530, 0);
                //additional fee
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(5), 205, 518, 0);
                //total amount
                Double totalAmount = Double.parseDouble(info.get(2)) + Double.parseDouble(info.get(3)) + Double.parseDouble(info.get(4));
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, totalAmount + "", 205, 490, 0);
                //due date

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MONTH, Calendar.MONTH - 1);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(7), 130, 463, 0);

                content.endText();

            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();
            pdfWriter.close();

            return true;
        } catch (DocumentException | ParseException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (IOException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean savePaymentRemittanceToPDF(ArrayList<String> info) throws RemoteException {
        try {
            // TODO add your handling code here:
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            String path="";
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } finally {
                br.close();
            }

            Document doc = new Document();
            FileOutputStream fos = new FileOutputStream(path + "\\residentBilling\\" + info.get(0) + "Payment Remittance.pdf");
            PdfWriter.getInstance(doc, fos);
            
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM billing WHERE residentIdnum = ? AND billingStatus LIKE 'unpaid'");
            preparedStatement.setInt(1, Integer.parseInt(info.get(7)));
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            

            PdfReader pdfReader = new PdfReader("Payment Remittance.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                //Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 11);

                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Control Number: " + info.get(4), 300, 260, 0);
                if (info.get(5).equals("cash")) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 346, 218, 0);
                } else if (info.get(5).equals("cheque")) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 347, 198, 0);
                } else {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 347, 178, 0);
                }

                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(6), 330, 140, 0);
                //Date
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()), 460, 260, 0);
                //Resident name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(0), 90, 250, 0);
                //Purpose of payment
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Resident Monthly Billing", 90, 223, 0);
                //Room Details
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(1), 125, 183, 0);
                //Amount
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(2), 125, 158, 0);
                //Received By admin  name!
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(3), 125, 105, 0);
                content.endText();

            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();

            return true;
        } catch (DocumentException | SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        } catch (IOException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateTransientInfo(ArrayList<String> info, String transientIdnum) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE transient SET transientModeOfPayment = ?, transientControlNumber = ?, "
                    + "transientRemarks = ? WHERE transientIdnum = ?");
            preparedStatement.setString(1, info.get(0));
            preparedStatement.setString(2, info.get(1));
            preparedStatement.setString(3, info.get(2));
            preparedStatement.setInt(4, Integer.parseInt(transientIdnum.trim()));
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("INSERT INTO controlNumber (controlNumber,type,id) VALUES (?, ?, ?)");
            preparedStatement.setInt(1, Integer.parseInt(info.get(1)));
            preparedStatement.setString(2, "transient");
            preparedStatement.setInt(3, Integer.parseInt(transientIdnum.trim()));
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
    }

    @Override
    public String getLastControllNumber() throws RemoteException {
        String last = new SimpleDateFormat("yy").format(Calendar.getInstance().getTime()) + "0000";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT controlNumber FROM controlNumber ORDER BY controlNumber DESC LIMIT 1");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (rs.getString("controlNumber") != null) {
                    return rs.getString("controlNumber");
                } else {
                    return last;
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }

        return last;
    }

    @Override
    public ArrayList<AdminImpl> getAllAdminAccounts() throws RemoteException {
        Statement statement;
        ArrayList<AdminImpl> admin = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM admin");
            while (rs.next()) {
                admin.add(new AdminImpl(rs.getString("adminFname") + rs.getString("adminLname"), rs.getString("adminGender"), rs.getString("adminBirthdate"), rs.getString("adminEmail")));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
        return admin;
    }

    @Override
    public ArrayList<ResidentImpl> getAllResidentBillingInfo() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident JOIN billing ON resident.residentIdnum = billing.residentIdnum");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentLname") + ", " + resultSet.getString("residentFname"), resultSet.getString("billingAmountPaid"), resultSet.getString("billingDatePaid"), true));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<TransientImpl> getAllTransientBillingInfo() throws RemoteException {
        ArrayList<TransientImpl> trans = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT transientDeparture, transientLname, transientFname, transientAmountPaid FROM transient");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                trans.add(new TransientImpl(resultSet.getString("transientLname") + ", " + resultSet.getString("transientFname"), resultSet.getString("transientDeparture"), resultSet.getString("transientAmountPaid")));
            }
            return trans;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getResidentBillingSummary() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            String query = "SELECT * FROM (SELECT residentFname, residentLname, billingIdnum, billingAmountPaid, billingModeOfPayment, adminIdnum, billingDatePaid "
                    + "FROM billing LEFT JOIN resident ON (billing.residentIdnum = resident.residentIdnum) "
                    + "WHERE billingStatus = 'Paid' AND adminIdnum IS NOT null) AS t1 RIGHT JOIN controlnumber ON (t1.billingIdnum = controlnumber.id) WHERE controlnumber.type LIKE 'resident'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResidentImpl impl = null;
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                impl = new ResidentImpl();
                impl.setfName(resultSet.getString("residentFname"));
                impl.setLName(resultSet.getString("residentLname"));
                impl.setAddress(resultSet.getString("billingAmountPaid"));
                impl.setBirthdate(resultSet.getString("billingModeOfPayment"));
                impl.setGender(resultSet.getString("adminIdnum"));
                impl.setFullName(resultSet.getString("controlNumber"));
                impl.setId(resultSet.getString("billingDatePaid"));
                res.add(impl);
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<FurnitureImpl> getAllFurniture() throws RemoteException {
        ArrayList<FurnitureImpl> furn = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM furniture");
            while (rs.next()) {
                furn.add(new FurnitureImpl(rs.getString("furnitureItemName"), rs.getString("furnitureControlNo"), rs.getString("furnitureColor"), rs.getString("furnitureBrand"), new SimpleDateFormat("MMMM dd, yyyy").format(rs.getDate("furniturePurchaseDate"))));
            }
            return furn;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getAllLeavedResidentPersonal() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE status LIKE 'Leave'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentIdnum"), resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"),
                        resultSet.getString("residentGender"), resultSet.getString("residentBirthdate")));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getAllLeavedResidentContact() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE status LIKE 'Leave'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"), resultSet.getString("residentmobileNo"), resultSet.getString("residentEmail"), true));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getAllLeavedResidentAddress() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE status LIKE 'Leave'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"), resultSet.getString("residentHomeAddress"), resultSet.getString("residentZipCode"), true));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getAllLeavedResidentStatus() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE status LIKE 'Leave'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentIdnum"), resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"), resultSet.getString("status"), true));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<RegistrationImpl> getAllRegistrationPersonal() throws RemoteException {
        ArrayList<RegistrationImpl> reg = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.reservation INNER JOIN registration ON reservation.reservationIdnum = registration.reservationIdnum;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RegistrationImpl regImpl = new RegistrationImpl();
                regImpl.setLast_name(resultSet.getString("registrationIdnum"));
                regImpl.setFirst_name(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                regImpl.setYear(resultSet.getString("registrationResidentBirthdate"));
                regImpl.setCollege(resultSet.getString("registrationResidentGender"));
                regImpl.setCourse(resultSet.getString("registrationResidentCollege"));
                regImpl.setAddress(resultSet.getString("registrationResidentCourse") + " " + resultSet.getString("registrationResidentYear"));
                regImpl.setDepartment(resultSet.getString("registrationResidentDept"));

                reg.add(regImpl);
            }
            return reg;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<RegistrationImpl> getAllRegistrationContact() throws RemoteException {
        ArrayList<RegistrationImpl> reg = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.reservation INNER JOIN registration ON reservation.reservationIdnum = registration.reservationIdnum INNER JOIN room ON registration.roomIdnum = room.roomIdnum;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RegistrationImpl regImpl = new RegistrationImpl();
                regImpl.setFirst_name(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                regImpl.setMobile_number(resultSet.getString("registrationResidentMobileNo"));
                regImpl.setMobile_number2(resultSet.getString("registrationResidentMobileNo2"));
                regImpl.setEmail(resultSet.getString("reservationEmail"));
                regImpl.setRoom_number(resultSet.getString("roomNumber"));

                reg.add(regImpl);
            }
            return reg;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<RegistrationImpl> getAllRegistrationFather() throws RemoteException {
        ArrayList<RegistrationImpl> reg = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.reservation INNER JOIN registration ON reservation.reservationIdnum = registration.reservationIdnum INNER JOIN room ON registration.roomIdnum = room.roomIdnum;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RegistrationImpl regImpl = new RegistrationImpl();
                regImpl.setFirst_name(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                regImpl.setFatherName(resultSet.getString("registrationFatherName"));
                regImpl.setFatherAreaCode(resultSet.getString("registrationFatherLandline"));
                regImpl.setFatherMobile(resultSet.getString("registrationFatherMobileNo"));
                regImpl.setFatherEmail(resultSet.getString("registrationFatherEmail"));

                reg.add(regImpl);
            }
            return reg;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<RegistrationImpl> getAllRegistrationMother() throws RemoteException {
        ArrayList<RegistrationImpl> reg = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.reservation INNER JOIN registration ON reservation.reservationIdnum = registration.reservationIdnum INNER JOIN room ON registration.roomIdnum = room.roomIdnum;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RegistrationImpl regImpl = new RegistrationImpl();
                regImpl.setFirst_name(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                regImpl.setMotherName(resultSet.getString("registrationMotherName"));
                regImpl.setMotherAreaCode(resultSet.getString("registrationMotherLandline"));
                regImpl.setMotherMobile(resultSet.getString("registrationMotherMobileNo"));
                regImpl.setMotherEmail(resultSet.getString("registrationMotherEmail"));
                reg.add(regImpl);
            }
            return reg;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<RegistrationImpl> getAllRegistrationGuardian() throws RemoteException {
        ArrayList<RegistrationImpl> reg = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.reservation INNER JOIN registration ON reservation.reservationIdnum = registration.reservationIdnum INNER JOIN room ON registration.roomIdnum = room.roomIdnum;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RegistrationImpl regImpl = new RegistrationImpl();
                regImpl.setFirst_name(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                regImpl.setGuardianMobile(resultSet.getString("registrationGuardianMobileNo"));
                regImpl.setGuardianName(resultSet.getString("registrationGuardianName"));
                regImpl.setGuardianRelation(resultSet.getString("registrationGuardianRelation"));

                reg.add(regImpl);
            }
            return reg;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<RegistrationImpl> getAllRegistrationGuardianAddress() throws RemoteException {
        ArrayList<RegistrationImpl> reg = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.reservation INNER JOIN registration ON reservation.reservationIdnum = registration.reservationIdnum INNER JOIN room ON registration.roomIdnum = room.roomIdnum;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RegistrationImpl regImpl = new RegistrationImpl();
                regImpl.setFirst_name(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                regImpl.setGuardianName(resultSet.getString("registrationGuardianName"));
                regImpl.setGuardianAddress(resultSet.getString("registrationGuardianAddress"));

                reg.add(regImpl);
            }
            return reg;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ReservationImpl> getAllReservationPersonal() throws RemoteException {
        ArrayList<ReservationImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM reservation;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ReservationImpl resImpl = new ReservationImpl();
                resImpl.setlName(resultSet.getString("reservationIdnum"));
                resImpl.setFullName(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                resImpl.setsTerm(resultSet.getString("reservationSchoolTerm"));
                resImpl.setAyFrom(resultSet.getString("reservationAyFrom"));
                resImpl.setAyTo(resultSet.getString("reservationAyTo"));

                res.add(resImpl);
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ReservationImpl> getAllReservationContact() throws RemoteException {
        ArrayList<ReservationImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM reservation;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ReservationImpl resImpl = new ReservationImpl();
                resImpl.setFullName(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                resImpl.setmNumber(resultSet.getString("reservationMobileNo"));
                resImpl.setEmail(resultSet.getString("reservationEmail"));

                res.add(resImpl);
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ReservationImpl> getAllReservationAddress() throws RemoteException {
        ArrayList<ReservationImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM reservation;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ReservationImpl resImpl = new ReservationImpl();
                resImpl.setFullName(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                resImpl.setAddress(resultSet.getString("reservationHomeaddress"));
                resImpl.setZipCode(resultSet.getString("reservationZipCode"));

                res.add(resImpl);
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ReservationImpl> getAllReservationStatus() throws RemoteException {
        ArrayList<ReservationImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `reservation` INNER JOIN room ON reservation.roomIdnum = room.roomIdnum;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ReservationImpl resImpl = new ReservationImpl();
                resImpl.setFullName(resultSet.getString("reservationFname") + " " + resultSet.getString("reservationMname") + " " + resultSet.getString("reservationLname"));
                resImpl.setDatePaid(resultSet.getString("reservationDatePaid"));
                resImpl.setRemarks(resultSet.getString("reservationStatus"));
                resImpl.setRoom(resultSet.getString("roomNumber"));
                resImpl.setOthers(resultSet.getString("reservationOthers"));

                res.add(resImpl);
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getAllResidentPersonal() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE status NOT LIKE 'Leave'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentIdnum"), resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"),
                        resultSet.getString("residentGender"), resultSet.getString("residentBirthdate")));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getAllResidentContact() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE status NOT LIKE 'Leave'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"), resultSet.getString("residentmobileNo"), resultSet.getString("residentEmail"), true));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getAllResidentAddress() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE status NOT LIKE 'Leave'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"), resultSet.getString("residentHomeAddress"), resultSet.getString("residentZipCode"), true));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<ResidentImpl> getAllResidentStatus() throws RemoteException {
        ArrayList<ResidentImpl> res = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resident WHERE status NOT LIKE 'Leave'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(new ResidentImpl(resultSet.getString("residentIdnum"), resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"), resultSet.getString("status"), true));
            }
            return res;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<RoomImpl> getAllRoom() throws RemoteException {
        ArrayList<RoomImpl> room = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM room;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RoomImpl r = new RoomImpl();
                r.setRoomDorm(Integer.parseInt(resultSet.getString("roomDorm")));
                r.setRoomId(resultSet.getString("roomDorm"));
                r.setRoomNumber(resultSet.getString("roomNumber"));
                r.setRoomStatus(resultSet.getString("roomStatus"));
                r.setRoomType(resultSet.getString("roomType"));

                room.add(r);
            }
            return room;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<LogsImpl> getAllTimeInOut() throws RemoteException {
        ArrayList<LogsImpl> log = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `logs`INNER JOIN resident ON logs.residentIdnum = resident.residentIdnum;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LogsImpl logImpl = new LogsImpl();
                logImpl.setDate(resultSet.getString("logsDate"));
                logImpl.setFullName(resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"));
                logImpl.setTimeIn(resultSet.getString("logsTime"));
                logImpl.setStatus(resultSet.getString("logsStatus"));

                log.add(logImpl);
            }
            return log;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<TransientImpl> getAllTransientInfo() throws RemoteException {
        ArrayList<TransientImpl> trans = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM 181nprdb.transient");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TransientImpl transImpl = new TransientImpl();
                transImpl.setStatus(resultSet.getString("transientIdnum"));
                transImpl.setFull_name(resultSet.getString("transientFname") + " " + resultSet.getString("transientLname"));
                transImpl.setMobile_number(resultSet.getString("transientMobileNo"));
                transImpl.setEmail(resultSet.getString("transientEmail"));
                transImpl.setAddress(resultSet.getString("transientAddress"));
                transImpl.setReservedRooms(resultSet.getString("transientReservedRoomNo"));

                trans.add(transImpl);
            }
            return trans;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<TransientImpl> getAllTransientCheckinDetails() throws RemoteException {
        ArrayList<TransientImpl> trans = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT *, transientFname, transientLname, transientReservedRoomNo, CONCAT(residentFname, \" \", residentMname, \" \", residentLname) AS Relation FROM 181nprdb.transient "
                    + "INNER JOIN resident "
                    + "ON transient.transientRelation = resident.residentIdnum ");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                TransientImpl transImpl = new TransientImpl();
                transImpl.setDateEntered(resultSet.getString("transientDateEntered"));
                transImpl.setFull_name(resultSet.getString("transientFname") + " " + resultSet.getString("transientLname"));
                transImpl.setArrival(resultSet.getString("transientArrival"));
                transImpl.setDeparture(resultSet.getString("transientDeparture"));
                transImpl.setStatus(resultSet.getString("registrationIdnum"));
                transImpl.setReservedRooms(resultSet.getString("transientReservedRoomNo"));
                transImpl.setRelation(resultSet.getString("Relation"));

                trans.add(transImpl);
            }
            return trans;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<TransientImpl> getAllTransientGuest() throws RemoteException {
        ArrayList<TransientImpl> trans = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transient");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                TransientImpl transImpl = new TransientImpl();
                transImpl.setFull_name(resultSet.getString("transientFname") + " " + resultSet.getString("transientLname"));
                transImpl.setAdditionalGuest(resultSet.getString("transientNamesAdditionalGuest"));

                trans.add(transImpl);
            }
            return trans;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<VisitorImpl> getAllVisitor() throws RemoteException {
        ArrayList<VisitorImpl> vist = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM visitors");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                VisitorImpl vi = new VisitorImpl();
                vi.setDate(resultSet.getString("visitorsDate"));
                vi.setVisitorId(resultSet.getString("visitorsIdnum"));
                vi.setVisitorName(resultSet.getString("visitorsName"));
                vi.setTimeIn(resultSet.getString("visitorsTimein"));
                vi.setTimeOut(resultSet.getString("visitorsTimeout"));
                vi.setPurpose(resultSet.getString("visitorsPurpose"));
                vi.setResidentId(resultSet.getString("residentIdnum"));

                vist.add(vi);
            }
            return vist;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public int getCountYearlyReportReservation(String year) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(reservationIdnum) as count "
                    + "FROM reservation WHERE YEAR(reservationDatePaid) = ?");
            preparedStatement.setInt(1, Integer.parseInt(year));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return 0;
        }
        return 0;
    }

    @Override
    public int getCountYearlyReportRegistration(String year) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(registrationIdnum) as count "
                    + "FROM registration WHERE YEAR(registrationDate) = ?");
            preparedStatement.setInt(1, Integer.parseInt(year));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return 0;
        }
        return 0;
    }

    @Override
    public int getCountMonthlyReportReservation(String month, String year) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(reservationIdnum) as count "
                    + "FROM reservation WHERE MONTH(reservationDatePaid) = ? AND YEAR(reservationDatePaid) = ?");
            preparedStatement.setInt(1, Integer.parseInt(month));
            preparedStatement.setInt(2, Integer.parseInt(year));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return 0;
        }
        return 0;
    }

    @Override
    public int getCountMonthlyReportRegistration(String month, String year) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(registrationIdnum) as count "
                    + "FROM registration WHERE MONTH(registrationDate) = ? AND YEAR(registrationDate) = ?");
            preparedStatement.setInt(1, Integer.parseInt(month));
            preparedStatement.setInt(2, Integer.parseInt(year));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return 0;
        }
        return 0;
    }

    @Override
    public String getMonthlyBillingReport(String month, String year) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT billingDatePaid as count "
                    + "FROM billing WHERE MONTH(billingDatePaid) = ? AND YEAR(billingDatePaid) = ?");
            preparedStatement.setInt(1, Integer.parseInt(month));
            preparedStatement.setInt(2, Integer.parseInt(year));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("billingDatePaid");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
        return null;
    }

    @Override
    public int getDailyReport(String week, String id) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(logs.residentIdnum) as count FROM resident JOIN logs ON resident.residentIdnum = logs.residentIdnum WHERE WEEK(logsDate) = ? AND resident.residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(week));
            preparedStatement.setInt(2, Integer.parseInt(id));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return 0;
        }
        return 0;
    }

    @Override
    public boolean residentHasPreviousStatement(String residentIdnum) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT billingIdnum FROM billing WHERE residentIdnum = ? AND billingStatus LIKE 'UNPAID'");
            preparedStatement.setInt(1, Integer.parseInt(residentIdnum));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<String> getBillingInfo(String residentIdnum) throws RemoteException {
        ArrayList<String> info = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM billing WHERE residentIdnum = ? AND billingStatus LIKE 'Unpaid'");
            preparedStatement.setInt(1, Integer.parseInt(residentIdnum));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                info.add(rs.getString("billingIdnum")); //0
                info.add(rs.getString("billingDatePaid")); //1
                info.add(rs.getString("billingDatein")); //2
                info.add(rs.getString("billingDateout")); //3
                info.add(rs.getString("billingRateType")); //4
                info.add(rs.getString("billingNoofDays")); //5
                info.add(rs.getString("billingRoomRate")); //6
                info.add(rs.getString("billingShuttleRateType")); //7
                info.add(rs.getString("billingShuttleRate")); //8
                info.add(rs.getString("billingGadgetRate")); //9
                info.add(rs.getString("billingRemarks")); //10
                info.add(rs.getString("billingBalance")); //11
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
        return info;
    }

    @Override
    public boolean updateResidentBilling(ArrayList<String> info) throws RemoteException {
        boolean status = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `billing` SET "
                    + "`billingDatePaid` = ?, " //1
                    + "`billingRemarks` = ?, " //2
                    + "`billingTotalRoomrate` = ?, " //3
                    + "`billingTotalShuttlerate` = ?, " //4
                    + "`billingTotalGadgetrate` = ?, " //
                    + "`billingAdditionalfee` = ?, " //6
                    + "`billingTotalAmount` = ?, " //7
                    + "`billingAmountPaid` = ?, " //8
                    + "`billingStatus` = ?, " //9
                    + "`billingAmountPaid` = ?, " //10
                    + "`billingBalance` = ?, " //11
                    + "`billingModeOfPayment` = ? " //12
                    + "WHERE residentIdnum = ?");           //13
            if (info.get(0) != null) {
                preparedStatement.setString(1, info.get(0));
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (info.get(1) != null) {
                preparedStatement.setString(2, info.get(1));
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (info.get(2) != null) {
                preparedStatement.setDouble(3, Double.parseDouble(info.get(2)));
            } else {
                preparedStatement.setNull(3, Types.DOUBLE);
            }
            if (info.get(3) != null) {
                preparedStatement.setDouble(4, Double.parseDouble(info.get(3)));
            } else {
                preparedStatement.setNull(4, Types.DOUBLE);
            }
            if (info.get(4) != null) {
                preparedStatement.setDouble(5, Double.parseDouble(info.get(4)));
            } else {
                preparedStatement.setNull(5, Types.DOUBLE);
            }
            if (info.get(5) != null) {
                preparedStatement.setDouble(6, Double.parseDouble(info.get(5)));
            } else {
                preparedStatement.setNull(6, Types.DOUBLE);
            }
            if (info.get(6) != null) {
                preparedStatement.setDouble(7, Double.parseDouble(info.get(6)));
            } else {
                preparedStatement.setNull(7, Types.DOUBLE);
            }
            if (info.get(7) != null) {
                preparedStatement.setDouble(8, Double.parseDouble(info.get(7)));
            } else {
                preparedStatement.setNull(8, Types.DOUBLE);
            }
            preparedStatement.setString(9, "paid");
            if (info.get(8) != null) {
                preparedStatement.setDouble(10, Double.parseDouble(info.get(8)));
            } else {
                preparedStatement.setNull(10, Types.DOUBLE);
            }
            if (info.get(9) != null) {
                preparedStatement.setDouble(11, Double.parseDouble(info.get(9)));
            } else {
                preparedStatement.setNull(11, Types.DOUBLE);
            }
            System.out.println(info.get(12));
            preparedStatement.setString(12, info.get(12));
            preparedStatement.setInt(13, Integer.parseInt(info.get(10)));
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public boolean updateTransientBill(String transientIdnum, Double amountPaid, String adminId) throws RemoteException {
        try {
            String query = "UPDATE transient SET transientAmountPaid = ?, transientBalance = ?, adminIdnum = ? WHERE transientIdnum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, amountPaid);
            preparedStatement.setDouble(2, 0.00);
            preparedStatement.setInt(3, Integer.parseInt(adminId.trim()));
            preparedStatement.setInt(4, Integer.parseInt(transientIdnum.trim()));
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<TransientImpl> getTransientBillingSummary() throws RemoteException {
        ArrayList<TransientImpl> info = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transient WHERE transientStatus LIKE 'Checkout'");
            ResultSet rs = preparedStatement.executeQuery();
            TransientImpl impl;
            while(rs.next()){
                impl = new TransientImpl();
                impl.setFull_name(rs.getString("transientLname") + ", " + rs.getString("transientFname"));
                impl.setAmountPaid(rs.getString("transientAmountPaid"));
                impl.setEmail(rs.getString("adminIdnum"));
                impl.setBalance(rs.getString("controlNumber"));
                impl.setAddress(rs.getString("transientModeOfPayment"));
                
                info.add(impl);
            }
            return info;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<TransientImpl> getTransientBillingInfo() throws RemoteException {
        ArrayList<TransientImpl> info = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transient WHERE transientStatus LIKE 'Checkout'");
            ResultSet rs = preparedStatement.executeQuery();
            TransientImpl impl;
            while(rs.next()){
                impl = new TransientImpl();
                impl.setFull_name(rs.getString("transientLname") + ", " + rs.getString("transientFname"));
                impl.setAmountPaid(rs.getString("transientAmountPaid"));
                impl.setEmail(rs.getString("adminIdnum"));
                impl.setBalance(rs.getString("billingDeparture"));
                
                info.add(impl);
            }
            return info;
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateBillingFromChangeRoom(String residentIdnum) throws RemoteException {
        try {
            String query = "SELECT resident.roomIdnum, roomType FROM room RIGHT JOIN resident ON (resident.roomIdnum = room.roomIdnum) WHERE residentIdnum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(residentIdnum.trim()));
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            String roomType = rs.getString("roomType");
            String roomIdnum = rs.getString("roomIdnum");
            double roomRate = 0.0;
            preparedStatement = connection.prepareStatement("SELECT * FROM rate");
            rs = preparedStatement.executeQuery();
            rs.next();
            switch(roomType){
                case "T":
                    roomRate = rs.getDouble("rentRoomTripleSharing");
                    break;
                case "D":
                    roomRate = rs.getDouble("rentRoomDoubleSharing");
                    break;
                case "S":
                    roomRate = rs.getDouble("rentRoomSingleSharing");
                    break;
                default:
            }
            query = "SELECT billingDatein, billingDateout, billingBalance FROM billing WHERE residentIdnum = ? ORDER BY billingDatePaid DESC LIMIT 1";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(residentIdnum.trim()));
            rs = preparedStatement.executeQuery();
            rs.next();
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(rs.getDate("billingDatein"));
            
            Calendar today = Calendar.getInstance();
            int l = today.getTime().getDate() - cal.getTime().getDate();
            double amount = l * (roomRate/30.0);
            query = "INSERT INTO billing (billingDatePaid, billingDatein, billingDateout, billingBalance, billingStatus, residentIdnum, roomIdnum) VALUES (?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, new Date(Calendar.getInstance().getTime().getTime()));
            preparedStatement.setDate(2, new Date(cal.getTime().getTime()));
            preparedStatement.setDate(3, new Date(Calendar.getInstance().getTime().getTime()));
            preparedStatement.setDouble(4, amount + rs.getDouble("billingBalance"));
            preparedStatement.setString(5, "Paid");
            preparedStatement.setInt(6, Integer.parseInt(residentIdnum.trim()));
            preparedStatement.setInt(7, Integer.parseInt(roomIdnum.trim()));
            return !preparedStatement.execute();
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return Boolean.TRUE;
    }

    @Override
    public double getResidentBalance(String idnum) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT billingBalance FROM "
                    + "(SELECT resident.residentIdnum, billingBalance "
                    + "FROM resident LEFT JOIN billing ON resident.residentIdnum = billing.residentIdnum "
                    + "LEFT JOIN registration ON registration.registrationIdnum = resident.registrationIdnum "
                    + "WHERE billingStatus NOT LIKE 'Replaced' "
                    + "ORDER BY billingDatePaid DESC, billingIdnum DESC) as t1 WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(idnum.trim()));
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            System.out.println(rs.getDouble("billingBalance"));
            return rs.getDouble("billingBalance");
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
            return 0.0;
        }
    }

    @Override
    public ArrayList<LogsImpl> getAllResidentLogs() throws RemoteException {
            try {
                ArrayList<LogsImpl> logs = new ArrayList<>();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `logs`INNER JOIN resident ON logs.residentIdnum = resident.residentIdnum;");
                ResultSet resultSet = preparedStatement.executeQuery();
                LogsImpl impl = null;
                while (resultSet.next()) {
                    impl = new LogsImpl();
                    impl.setDate(resultSet.getString("logsDate"));
                    impl.setFullName(resultSet.getString("residentFname") + " " + resultSet.getString("residentMname") + " " + resultSet.getString("residentLname"));
                    impl.setTimeIn(resultSet.getString("logsTime"));
                    impl.setStatus(resultSet.getString("logsStatus"));
                    logs.add(impl);
                }
                return logs;
            } catch (SQLException ex) {
//                Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
                label.setText(ex.getMessage());
                return null;
            }
    }

    @Override
    public boolean updateRoomStatus(String roomNum) throws RemoteException {
        boolean status = true;
        int count = 0;
        try {
            String roomId = "";
            String query = "";
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, roomNum);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
                break;
            }
            
            preparedStatement = connection.prepareStatement("SELECT "
                    + "(SELECT DISTINCT COUNT(reservationIdnum) "
                    + "FROM  room  JOIN reservation ON room.roomIdnum = reservation.roomIdnum "
                    + "WHERE room.roomIdnum = ? AND reservationStatus = 'Pending') + "
                    + "(SELECT DISTINCT COUNT(residentIdnum) "
                    + "FROM  room  JOIN resident ON room.roomIdnum = resident.roomIdnum "
                    + "WHERE room.roomIdnum = ?) AS count");
            preparedStatement.setString(1, roomId);
            preparedStatement.setString(2, roomId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("count");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM room "
                    + "WHERE roomIdnum = ?");
            preparedStatement.setString(1, roomId);
            resultSet = preparedStatement.executeQuery();
//            System.out.println(count);
            while (resultSet.next()) {
                switch (resultSet.getString("roomType")) {
                    case "S":
                        switch (count) {
                            case 1:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "SET roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                            default:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "SET roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                        }
                        break;

                    case "D":
                        switch (count) {
                            case 2:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;

                            case 1:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'partially occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;

                            default:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                        }
                        break;

                    case "T":
                        switch (count) {
                            case 3:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'fully occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                            case 1:
                            case 2:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'partially occupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;

                            default:
                                if (status) {
                                    preparedStatement = connection.prepareStatement("UPDATE room "
                                            + "set roomStatus = 'unoccupied' "
                                            + "WHERE roomIdnum = ?");
                                    preparedStatement.setString(1, roomId);
                                    status = preparedStatement.executeUpdate() == 1;
                                }
                                break;
                        }
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
            label.setText(ex.getMessage());
        }
        return status;
    }

    @Override
    public String getResidentStatus(String idnum) throws RemoteException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT status FROM resident WHERE residentIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(idnum.trim()));
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return rs.getString("status").trim();
        } catch (SQLException ex) {
            Logger.getLogger(NPRImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
