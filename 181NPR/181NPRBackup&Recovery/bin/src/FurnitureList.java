
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mark Herbert Cabuang
 */
public class FurnitureList extends javax.swing.JFrame {

    /**
     * Creates new form FurnitureList
     */
    private DefaultTableModel modelRight;
    private DefaultTableModel modelLeft;
    private DefaultTableModel tableModel = new DefaultTableModel();
    private int row = 0;
    private String id = "";
    private String name = "";
    private String roomNumber = "";
    private String roomType = "";
    private boolean test = false;
    private final MessageDialog md = new MessageDialog();
    private final String origin;
    private Connection connection;
    
    public FurnitureList() {
        origin = null;
        initComponents();
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Backup and Recovery.png")));
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }
    
    public FurnitureList(String[] rooms) {
        initComponents();
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Backup and Recovery.png")));
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        origin = "transient";
        test = true;
        ArrayList<String> roomIds = new ArrayList<>();
        try{
            for (String room : rooms) {
                roomIds.add(getRoomId(room.trim()));
            }
            tableModel = (DefaultTableModel) furnitureItemTable.getModel();
            tableModel.getDataVector().removeAllElements();
            tableModel.fireTableDataChanged();
            for(String roomId : roomIds){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT `furnitureItemName`, `furnitureControlNo`, `furnitureIdnum` FROM furniture "+
                        "WHERE furnitureStatus = 'Available' AND roomIdnum = ? AND residentIdnum IS NULL AND transientIdnum IS NULL ORDER BY furnitureItemName ASC");
                preparedStatement.setInt(1, Integer.parseInt(roomId.trim()));
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    tableModel.addRow(new Object[]{
                        resultSet.getString("furnitureItemName"),
                        resultSet.getString("furnitureControlNo"),
                        resultSet.getString("furnitureIdnum")
                    });
                }
            }
            furnitureItemTable.setModel(tableModel);
            modelRight = (DefaultTableModel) dormFurnitureTable.getModel();
            modelRight.getDataVector().removeAllElements();
            modelRight.fireTableDataChanged();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `furnitureItemName`, `furnitureControlNo`, `furnitureIdnum` FROM `furniture` WHERE `transientIdnum` = ? ORDER BY `furnitureItemName` ASC");
            preparedStatement.setInt(1, Integer.parseInt("99999999"));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                modelRight.addRow(new Object[]{
                    resultSet.getString("furnitureItemName"),
                    resultSet.getString("furnitureControlNo"),
                    resultSet.getString("furnitureIdnum")
                });
            }
        } catch (SQLException ex){
//            Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }
    
    public FurnitureList(String id, String name, String roomNumber, String roomType) {
        initComponents();
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Backup and Recovery.png")));
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        this.id = id;
        this.name = name;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        origin = "resident";
        try {
            tableModel = (DefaultTableModel) furnitureItemTable.getModel();
            tableModel.getDataVector().removeAllElements();
            tableModel.fireTableDataChanged();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `furnitureItemName`, `furnitureControlNo`, `furnitureIdnum` FROM furniture "+
                    "WHERE furnitureStatus = 'Available' AND roomIdnum = ? AND residentIdnum IS NULL AND transientIdnum IS NULL ORDER BY furnitureItemName ASC");
            preparedStatement.setInt(1, Integer.parseInt(getRoomId(roomNumber)));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                    resultSet.getString("furnitureItemName"),
                    resultSet.getString("furnitureControlNo"),
                    resultSet.getString("furnitureIdnum")
                });
            }
            furnitureItemTable.setModel(tableModel);
            modelRight = (DefaultTableModel) dormFurnitureTable.getModel();
            modelRight.getDataVector().removeAllElements();
            modelRight.fireTableDataChanged();
            preparedStatement = connection.prepareStatement("SELECT `furnitureItemName`, `furnitureControlNo`, `furnitureIdnum` FROM `furniture` WHERE `residentIdnum` = ? ORDER BY `furnitureItemName` ASC");
            preparedStatement.setInt(1, Integer.parseInt("99999999"));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                modelRight.addRow(new Object[]{
                    resultSet.getString("furnitureItemName"),
                    resultSet.getString("furnitureControlNo"),
                    resultSet.getString("furnitureIdnum")
                });
            }
        } catch (SQLException ex) {
//            Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        furnitureItemTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        dormFurnitureTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "List of Furniture", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Rondalo", 1, 24))); // NOI18N

        furnitureItemTable.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        furnitureItemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Name", "Control Number", "Furniture ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        furnitureItemTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(furnitureItemTable);
        if (furnitureItemTable.getColumnModel().getColumnCount() > 0) {
            furnitureItemTable.getColumnModel().getColumn(0).setResizable(false);
            furnitureItemTable.getColumnModel().getColumn(1).setResizable(false);
            furnitureItemTable.getColumnModel().getColumn(2).setResizable(false);
        }

        dormFurnitureTable.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        dormFurnitureTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Name", "Control Number", "Furniture ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dormFurnitureTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(dormFurnitureTable);
        if (dormFurnitureTable.getColumnModel().getColumnCount() > 0) {
            dormFurnitureTable.getColumnModel().getColumn(0).setResizable(false);
            dormFurnitureTable.getColumnModel().getColumn(1).setResizable(false);
            dormFurnitureTable.getColumnModel().getColumn(2).setResizable(false);
        }

        addButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        addButton.setText(">");
        addButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        removeButton.setText("<");
        removeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Prev.png"))); // NOI18N
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // TODO add your handling code here:
        modelRight =(DefaultTableModel) dormFurnitureTable.getModel();
        if(furnitureItemTable.getSelectedRowCount() > 0){
            for(int index : furnitureItemTable.getSelectedRows()){
                Object[] row = new Object[3];
                row[0] = furnitureItemTable.getValueAt(index, 0);
                row[1] = furnitureItemTable.getValueAt(index, 1);
                row[2] = furnitureItemTable.getValueAt(index, 2);
                if(origin.equals("resident")){
                    if(!updateFurniture(row[2].toString())){
                        md.error(this, "Update Error");
                    }
                } else {
                    if(!updateTransientFurniture(row[2].toString())){
                        md.error(this, "Update Error");
                    }
                }
                modelRight.addRow(row);
            }
            int totalRows = furnitureItemTable.getSelectedRowCount();
            modelLeft =(DefaultTableModel) furnitureItemTable.getModel();
            while(totalRows > 0){
                modelLeft.removeRow(furnitureItemTable.getSelectedRow());
                totalRows--;
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // TODO add your handling code here:
        modelLeft =(DefaultTableModel) furnitureItemTable.getModel();
        if(dormFurnitureTable.getSelectedRowCount() > 0){
            for(int index : dormFurnitureTable.getSelectedRows()){
                Object[] row = new Object[3];
                row[0] = dormFurnitureTable.getValueAt(index, 0);
                row[1] = dormFurnitureTable.getValueAt(index, 1);
                row[2] = dormFurnitureTable.getValueAt(index, 2);
                if(origin.equals("resident")){
                    if(!updateFurnitureToNull(row[2].toString())){
                        md.error(this, "Update Error");
                    }
                } else {
                    if(!updateTransientFurnitureToNull(row[2].toString())){
                        md.error(this, "Update Error");
                    }
                }
                modelLeft.addRow(row);
            }
            int totalRows = dormFurnitureTable.getSelectedRowCount();
            modelRight =(DefaultTableModel) dormFurnitureTable.getModel();
            while(totalRows > 0){
                modelRight.removeRow(dormFurnitureTable.getSelectedRows()[totalRows-1]);
                totalRows--;
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        // TODO add your handling code here:
        if (md.confirmationExit(this) == md.YES) {
             // yes option
            if("resident".equals(origin)){
                modelRight = (DefaultTableModel) dormFurnitureTable.getModel();
                new InventoryForm(modelRight, id, name, roomNumber, roomType).setVisible(true);
                this.dispose();
            } else {
                dispose();
            }
        }
    }//GEN-LAST:event_jLabel2MouseClicked

    private String getRoomId(String room){
        String roomId = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `roomIdnum` FROM room WHERE roomNumber LIKE ?");
            preparedStatement.setString(1, room.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomId = resultSet.getString("roomIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return roomId;
    }
    
    private boolean updateFurniture(String item){
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = '99999999' WHERE furnitureIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(item.trim()));
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
//            Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return status;
    }
    
    private boolean updateTransientFurniture(String item){
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET transientIdnum = '99999999' WHERE furnitureIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(item.trim()));
            status = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
//            Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return status;
    }
    
    private boolean updateFurnitureToNull(String item){
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET residentIdnum = ?, furnitureStatus = 'Available' WHERE furnitureIdnum = ?");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setInt(2, Integer.parseInt(item.trim()));
            status = preparedStatement.executeUpdate() == 1;
            
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return status;
    }
    
    private boolean updateTransientFurnitureToNull(String item){
        boolean status = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE furniture SET transientIdnum = ? WHERE transientIdnum = ?");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setInt(2, Integer.parseInt(item.trim()));
            status = !preparedStatement.execute();
        } catch (SQLException ex) {
//            Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return status;
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
            java.util.logging.Logger.getLogger(FurnitureList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FurnitureList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FurnitureList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FurnitureList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FurnitureList().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTable dormFurnitureTable;
    private javax.swing.JTable furnitureItemTable;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

}
