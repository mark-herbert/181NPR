
import java.rmi.RemoteException;
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
public class SendNotifLogs extends javax.swing.JDialog {

    /**
     * Creates new form GuardianNumber
     */
    private final javax.swing.JTextField field;
    private final NPRInterface client;
    private ResidentImpl residentImpl;
    private final String id;
    private final String medium;
    private final String table;
    public SendNotifLogs(java.awt.Frame parent, boolean modal, javax.swing.JTextField field, NPRInterface client, String id, String medium, String table) {
        super(parent, modal);
        initComponents();
        this.field = field;
        this.client = client;
        this.id = id;
        this.medium = medium;
        this.table = table;
        getContacts();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        radio = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        father = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        mother = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        guardian = new javax.swing.JRadioButton();
        ok = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        resident = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        customRadio = new javax.swing.JRadioButton();
        custom = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        jLabel1.setText("CONTACT PERSONS");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Father", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N

        father.setBackground(new java.awt.Color(255, 255, 255));
        radio.add(father);
        father.setFont(new java.awt.Font("Rondalo", 0, 18)); // NOI18N
        father.setText("XXXXXXXXXXX");
        father.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(father, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(father)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mother", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N

        mother.setBackground(new java.awt.Color(255, 255, 255));
        radio.add(mother);
        mother.setFont(new java.awt.Font("Rondalo", 0, 18)); // NOI18N
        mother.setText("XXXXXXXXXXX");
        mother.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mother, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mother)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Guardian", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N

        guardian.setBackground(new java.awt.Color(255, 255, 255));
        radio.add(guardian);
        guardian.setFont(new java.awt.Font("Rondalo", 0, 18)); // NOI18N
        guardian.setText("XXXXXXXXXXX");
        guardian.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guardian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(guardian)
        );

        ok.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        ok.setText("OK");
        ok.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resident", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N

        resident.setBackground(new java.awt.Color(255, 255, 255));
        radio.add(resident);
        resident.setFont(new java.awt.Font("Rondalo", 0, 18)); // NOI18N
        resident.setSelected(true);
        resident.setText("XXXXXXXXXXX");
        resident.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resident, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(resident)
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Custom", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N

        customRadio.setBackground(new java.awt.Color(255, 255, 255));
        radio.add(customRadio);
        customRadio.setFont(new java.awt.Font("Rondalo", 0, 18)); // NOI18N
        customRadio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        customRadio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                customRadioStateChanged(evt);
            }
        });
        customRadio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customRadioMouseClicked(evt);
            }
        });

        custom.setFont(new java.awt.Font("Rondalo", 0, 18)); // NOI18N
        custom.setEnabled(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(customRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(custom)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(custom)
                    .addComponent(customRadio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ok))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 263, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ok)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        // TODO add your handling code here:
        if(resident.isSelected()){
            field.setText(resident.getText());
        } else if(father.isSelected()){
            field.setText(father.getText());
        } else if(mother.isSelected()){
            field.setText(mother.getText());
        } else if(guardian.isSelected()){
            field.setText(guardian.getText());
        } else {
            field.setText(custom.getText());
        }
        if(customRadio.isSelected() && custom.getText().isEmpty()){
            new MessageDialog().error(null, "Invalid Contact.");
        } else {
            dispose();
        }
    }//GEN-LAST:event_okActionPerformed

    private void customRadioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customRadioMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_customRadioMouseClicked

    private void customRadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_customRadioStateChanged
        // TODO add your handling code here:
        if(customRadio.isSelected()){
            custom.setEnabled(true);
        } else {
            custom.setEnabled(false);
        }
    }//GEN-LAST:event_customRadioStateChanged

    private void getContacts(){
        switch(medium){
            case "SMS":
                SMS();
                break;
            case "EMAIL":
                EMAIL();
                break;
        }
        if(resident.getText().equals(field.getText())){
            resident.setSelected(true);
        } else if(father.getText().equals(field.getText())){
            father.setSelected(true);
        } else if(mother.getText().equals(field.getText())){
            mother.setSelected(true);
        } else if(guardian.getText().equals(field.getText())){
            guardian.setSelected(true);
        } else if(!field.getText().trim().isEmpty()){
            customRadio.setSelected(true);
            custom.setEnabled(true);
            custom.setText(field.getText());
        }
    }
    
    public void SMS(){
        try {
            if(table.equals("resident")){
                residentImpl = client.getNumber(id);

                if(residentImpl.getGuardianContact() == null || residentImpl.getGuardianContact().isEmpty()){
                   guardian.setEnabled(false);
                   guardian.setSelected(false);
                } else {
                   guardian.setText(residentImpl.getGuardianContact());
                   guardian.setSelected(true);
                }

                if(residentImpl.getMotherContact() == null || residentImpl.getMotherContact().isEmpty()){
                   mother.setEnabled(false);
                   mother.setSelected(false);
                } else {
                   mother.setText(residentImpl.getMotherContact());
                   mother.setSelected(true);
                }

                if(residentImpl.getFatherContact() == null || residentImpl.getFatherContact().isEmpty()){
                   father.setEnabled(false);
                   father.setSelected(false);
                } else {
                   father.setText(residentImpl.getFatherContact());
                   father.setSelected(true);
                }

                if(residentImpl.getResidentContact() == null || residentImpl.getResidentContact().isEmpty()){
                   resident.setEnabled(false);
                   resident.setSelected(false);
                } else {
                   resident.setText(residentImpl.getResidentContact());
                   resident.setSelected(true);
                }
            } else {
                String contact = client.getNumberTransientOrReservation(id, table);
                resident.setEnabled(false);
                father.setEnabled(false);
                mother.setEnabled(false);
                guardian.setEnabled(false);
                if(contact != null){
                    resident.setEnabled(true);
                    resident.setText(contact);
                    resident.setSelected(true);
                }
                customRadio.setSelected(true);
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(SendNotifLogs.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(null, ex.getMessage());
        }
    }
    
    public void EMAIL(){
        try {
            if(table.equals("resident")){
                residentImpl = client.getEmail(id);

                if(residentImpl.getGuardianContact() == null){
                   guardian.setEnabled(false);
                   guardian.setSelected(false);
                } else {
                   guardian.setText(residentImpl.getGuardianContact());
                   guardian.setSelected(true);
                }

                if(residentImpl.getMotherContact() == null){
                   mother.setEnabled(false);
                   mother.setSelected(false);
                } else {
                   mother.setText(residentImpl.getMotherContact());
                   mother.setSelected(true);
                }

                if(residentImpl.getFatherContact() == null){
                   father.setEnabled(false);
                   father.setSelected(false);
                } else {
                   father.setText(residentImpl.getFatherContact());
                   father.setSelected(true);
                }

                if(residentImpl.getResidentContact() == null){
                   resident.setEnabled(false);
                   resident.setSelected(false);
                } else {
                   resident.setText(residentImpl.getResidentContact());
                   resident.setSelected(true);
                }
            } else {
                String contact = client.getEmailTransientOrReservation(id, table);
                resident.setEnabled(false);
                father.setEnabled(false);
                mother.setEnabled(false);
                guardian.setEnabled(false);
                if(contact != null){
                    resident.setEnabled(true);
                    resident.setText(contact);
                    resident.setSelected(true);
                }
                customRadio.setSelected(true);
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(SendNotifLogs.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(null, ex.getMessage());
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
            java.util.logging.Logger.getLogger(SendNotifLogs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SendNotifLogs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SendNotifLogs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SendNotifLogs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SendNotifLogs dialog = new SendNotifLogs(new javax.swing.JFrame(), true,new javax.swing.JTextField(),null,null,null,null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField custom;
    private javax.swing.JRadioButton customRadio;
    private javax.swing.JRadioButton father;
    private javax.swing.JRadioButton guardian;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButton mother;
    private javax.swing.JButton ok;
    private javax.swing.ButtonGroup radio;
    private javax.swing.JRadioButton resident;
    // End of variables declaration//GEN-END:variables
}
