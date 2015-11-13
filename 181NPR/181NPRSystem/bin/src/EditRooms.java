
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kenneth
 */
public class EditRooms extends javax.swing.JFrame {

    /**
     * Creates new form EditRooms
     * @param client
     * @param room
     */
    private final NPRInterface client;
    private final String user;
    private final javax.swing.JTextField field;
    private final MessageDialog md = new MessageDialog();
    
    public EditRooms(NPRInterface client, String room, String user, javax.swing.JTextField field) {
        initComponents();
        roomNumber.setText(room);
        this.client = client;
        this.user = user;
        this.field = field;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fromType = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        save = new javax.swing.JButton();
        toType = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        fromStatus = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        toStatus = new javax.swing.JComboBox();
        cancel = new javax.swing.JButton();
        roomNumber = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dorm = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("icons/181NPR.png")).getImage());
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Rondalo", 1, 24)); // NOI18N
        jLabel1.setText("EDIT ROOM");

        jLabel2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel2.setText("Room Number:");

        jLabel3.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel3.setText("Room Type");

        fromType.setFont(new java.awt.Font("Rondalo", 2, 11)); // NOI18N
        fromType.setText("Room Type Label");

        jLabel5.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        jLabel5.setText("From:");

        jLabel6.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        jLabel6.setText("To:");

        jLabel8.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel8.setText("Room Type");

        save.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        save.setText("SAVE");
        save.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        toType.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        toType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Master Suite", "Single Sharing Room", "Double Sharing Room", "Triple Sharing Room" }));
        toType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel7.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel7.setText("Room Status");

        fromStatus.setFont(new java.awt.Font("Rondalo", 2, 11)); // NOI18N
        fromStatus.setText("Room Status Label");

        jLabel10.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel10.setText("Room Status");

        toStatus.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        toStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Not Available", "Unoccupied" }));
        toStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cancel.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        cancel.setText("CANCEL");
        cancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        roomNumber.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        roomNumber.setText("200");

        jLabel4.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel4.setText("Room Dorm:");

        dorm.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        dorm.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        dorm.setText("1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addComponent(cancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(save))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(fromType, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fromStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel10)
                                            .addGap(6, 6, 6)
                                            .addComponent(toStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel8)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(toType, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(roomNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dorm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(roomNumber))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(dorm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromType)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromStatus)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(toType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(toStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save)
                    .addComponent(cancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            // TODO add your handling code here:
            String[] details = client.getRoomDetails(roomNumber.getText());
            dorm.setText(details[2]);
            fromStatus.setText(details[0]);
            fromType.setText(type(details[1]));
            if(details[0].equals("partially occupied") || details[0].equals("fully occupied")){
                toStatus.setEnabled(false);
                toStatus.setSelectedIndex(1);
            }
            int occupants = client.getOccupants(roomNumber.getText().trim()).size() + client.getAllReservationsFromRoom(roomNumber.getText().trim()).size();
            if(("Triple Sharing Room".equals(fromType.getText()) && occupants == 2) || ("Double Sharing Room".equals(fromType.getText()) && occupants == 2)){
                toType.removeAllItems();
                toType.addItem("Double Sharing Room");
                toType.addItem("Triple Sharing Room");
            }
            if(occupants > 0){
                toType.removeItem("Master Suite");
            }
            toType.setSelectedItem(type(details[1]));
            if(details[0].equalsIgnoreCase("Unoccupied")){
                toStatus.setSelectedItem("Unoccupied");
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(EditRooms.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_formWindowOpened

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_cancelActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        try {
            // TODO add your handling code here:
            String status = "";
            if(toStatus.isEnabled()){
                status = toStatus.getSelectedItem().toString();
            } else {
                status = fromStatus.getText();
            }
            if(md.confirmationSave(this) == md.YES){
                if(!client.updateRoom(typeMnemonics(toType.getSelectedItem().toString()), status, roomNumber.getText())){
                    md.successful(this);
                    field.setText(roomNumber.getText());
                    this.dispose();
                } else{
                    md.unsuccessful(this);
                }
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(EditRooms.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_saveActionPerformed

    private String type(String type){
        String t = "";
        switch(type){
            case "SU":
                t = "Master Suite";
                break;
            case "S":
                t = "Single Sharing Room";
                break;
            case "D":
                t = "Double Sharing Room";
                break;
            case "T":
                t = "Triple Sharing Room";
                break;
        }
        return t;
    }
    
    private String typeMnemonics(String type){
        String t = "";
        switch(type){
            case "Master Suite":
                t = "SU";
                break;
            case "Single Sharing Room":
                t = "S";
                break;
            case "Double Sharing Room":
                t = "D";
                break;
            case "Triple Sharing Room":
                t = "T";
                break;
        }
        return t;
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
            java.util.logging.Logger.getLogger(EditRooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditRooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditRooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditRooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditRooms(null, null, null, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JLabel dorm;
    private javax.swing.JLabel fromStatus;
    private javax.swing.JLabel fromType;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel roomNumber;
    private javax.swing.JButton save;
    private javax.swing.JComboBox toStatus;
    private javax.swing.JComboBox toType;
    // End of variables declaration//GEN-END:variables
}
