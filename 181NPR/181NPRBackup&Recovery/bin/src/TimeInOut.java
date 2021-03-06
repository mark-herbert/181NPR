
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kenneth
 */
public class TimeInOut extends javax.swing.JFrame {

    /**
     * Creates new form TimeInOut
     */
    private Connection connection;
    private String username = "root";
    private String password = "";
    private PreparedStatement preparedStatement;
    private ArrayList<String[]> list = new ArrayList<>();
    private String currentName = null;
    private boolean iterate = false;
    public TimeInOut() {
        initComponents();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TimeInOut.class.getName()).log(Level.SEVERE, null, ex);
        }
        getResidents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        id = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        status = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        hour = new javax.swing.JSpinner();
        minute = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        second = new javax.swing.JSpinner();
        a = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        date = new datechooser.beans.DateChooserCombo();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        Default = new javax.swing.JButton();
        name = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        jLabel1.setText("TIME-IN/OUT");

        jLabel2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel2.setText("Name");

        jLabel3.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel3.setText("ID");

        id.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        id.setText("00000001");

        jLabel5.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel5.setText("Previous Status");

        status.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        status.setText("OUT");

        jLabel7.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel7.setText("Time");
        jLabel7.setToolTipText("");

        hour.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        hour.setModel(new javax.swing.SpinnerNumberModel(12, 1, 12, 1));
        hour.setToolTipText("Hour");
        hour.setEditor(new JSpinner.DefaultEditor(hour));

        minute.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        minute.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        minute.setToolTipText("Minute");
        minute.setEditor(new JSpinner.DefaultEditor(minute));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText(":");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText(":");

        second.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        second.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        second.setToolTipText("Second");
        second.setEditor(new JSpinner.DefaultEditor(second));

        a.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        a.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AM", "PM" }));
        a.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel8.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel8.setText("Date");

        date.setNothingAllowed(false);
        date.setFormat(2);
        date.setFieldFont(new java.awt.Font("Rondalo", java.awt.Font.PLAIN, 14));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back.png"))); // NOI18N
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jButton1.setText("SAVE");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Default.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        Default.setText("Default");
        Default.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Default.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DefaultActionPerformed(evt);
            }
        });

        name.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        name.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        name.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                nameItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel8))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(name, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addComponent(hour, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minute, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(second, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(a, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Default)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hour)
                    .addComponent(a)
                    .addComponent(minute)
                    .addComponent(second)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel12)
                        .addComponent(jLabel13)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(Default))
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        // TODO add your handling code here:
        new Home().setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel9MouseClicked

    private void nameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_nameItemStateChanged
        try {
            // TODO add your handling code here:
            if(iterate){
                if(name.getItemCount() > 0){
                    for(String[] l : list){
                        System.out.println(name.getSelectedItem());
                        if(name.getSelectedItem().equals(l[0])){
                            id.setText(l[1]);
                            System.out.println(l[0]);
                        }
                    }
                    String q = "SELECT logsStatus FROM logs WHERE residentIdnum = ? ORDER BY logsDate DESC, logsTime DESC LIMIT 1";
                    preparedStatement = connection.prepareStatement(q);
                    preparedStatement.setInt(1, Integer.parseInt(id.getText()));
                    ResultSet rs = preparedStatement.executeQuery();
                    while(rs.next()){
                        status.setText(rs.getString("logsStatus"));
                        break;
                    }
                    currentName = name.getSelectedItem().toString();
                }
                iterate = false;
            } else {
                iterate = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TimeInOut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_nameItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            String newStatus = null;
            if(status.getText().equals("OUT")){
                newStatus = "IN";
            } else {
                newStatus = "OUT";
            }
            //Date Format from Util Date to Sql Date
            Date dF = date.getDateFormat().parse(date.getText());
            DateFormat nDF = new SimpleDateFormat("yyyy-MM-dd");
            String d = nDF.format(dF);
            java.sql.Date sqlDate = new java.sql.Date(nDF.parse(d).getTime());
            nDF.format(sqlDate);
            
            //Time Format from Util Time to Sql Time
            DateFormat current = new SimpleDateFormat("hh:mm:ss a");
            DateFormat sqlFormat = new SimpleDateFormat("HH:mm:ss");
            Date tF = current.parse(hour.getValue()+":"+minute.getValue()+":"+second.getValue()+" "+a.getSelectedItem());
            java.sql.Time sqlTime = new java.sql.Time(sqlFormat.parse(sqlFormat.format(tF)).getTime());
            sqlFormat.format(sqlTime);
            // TODO add your handling code here:
            String q = "INSERT INTO logs (logsDate,logsTime,logsStatus,residentIdnum) VALUES (?,?,?,?)";
            preparedStatement = connection.prepareStatement(q);
            preparedStatement.setDate(1, sqlDate);
            preparedStatement.setTime(2, sqlTime);
            preparedStatement.setString(3, newStatus);
            preparedStatement.setInt(4, Integer.parseInt(id.getText()));
            if(!preparedStatement.execute()){
                JOptionPane.showMessageDialog(null, "Successful","Successful", JOptionPane.INFORMATION_MESSAGE);
                name.removeAllItems();
                getResidents();
                name.setSelectedItem(currentName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TimeInOut.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Unsuccessful: SQL Problems","Unsuccessful", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            Logger.getLogger(TimeInOut.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Unsuccessful: Parsing Problem","Unsuccessful", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void DefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DefaultActionPerformed
        // TODO add your handling code here:
        name.setSelectedIndex(0);
        hour.setValue(12);
        minute.setValue(0);
        second.setValue(0);
        a.setSelectedIndex(0);
    }//GEN-LAST:event_DefaultActionPerformed

    private void getResidents(){
        try {
            String q = "SELECT residentIdnum, CONCAT(residentLname, ', ',residentFname) AS resident_fullname FROM resident ORDER BY resident_fullname";
            preparedStatement = connection.prepareStatement(q);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                list.add(new String[]{rs.getString("resident_fullname"),rs.getString("residentIdnum")});
            }
            
            for(String[] l : list){
                name.addItem(l[0]);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TimeInOut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TimeInOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TimeInOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TimeInOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TimeInOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TimeInOut().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Default;
    private javax.swing.JComboBox a;
    private datechooser.beans.DateChooserCombo date;
    private javax.swing.JSpinner hour;
    private javax.swing.JLabel id;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner minute;
    private javax.swing.JComboBox name;
    private javax.swing.JSpinner second;
    private javax.swing.JLabel status;
    // End of variables declaration//GEN-END:variables
}
