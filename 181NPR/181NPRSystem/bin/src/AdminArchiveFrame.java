
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Joseph Madrid
 */
public class AdminArchiveFrame extends javax.swing.JFrame {

    /**
     * Creates new form AdminArchiveFrame
     */
    private String username = "root";
    private String password = "";
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private DefaultTableModel model;
    private final NPRInterface client;
    private final String user;
    private String path = "";
    public AdminArchiveFrame(NPRInterface client, String user) {
        initComponents();

        this.client = client;
        this.user = user;
        
        try {
            BufferedReader br = new BufferedReader(new FileReader("dir\\defaults.txt"));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                path = sb.toString().trim();
            } catch (IOException ex) {
//                Logger.getLogger(AdminArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, ex.getMessage());
            } finally {
                br.close();
            }
            model = (DefaultTableModel) residentpersonalinfotable.getModel();
            model.getDataVector().removeAllElements();
            model.fireTableDataChanged();
            for (AdminImpl admin : client.getAllAdminAccounts()) {
                model.addRow(new Object[]{
                    admin.getFullName(),
                    admin.getGender(),
                    admin.getBirthdate(),
                    admin.getEmail()
                });
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(AdminArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(AdminArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        } catch (IOException ex){
//            Logger.getLogger(AdminArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        residentpersonalinfotable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        tabletoPDF = new javax.swing.JButton();
        tabletoExcel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("adminarchive.png")).getImage());
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Admin Archive", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 24))); // NOI18N

        jButton1.setText("Export to Excel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Export to PDF");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        residentpersonalinfotable.setAutoCreateRowSorter(true);
        residentpersonalinfotable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Full Name", "Gender", "Birthdate", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        residentpersonalinfotable.setGridColor(new java.awt.Color(204, 204, 204));
        residentpersonalinfotable.getTableHeader().setResizingAllowed(false);
        residentpersonalinfotable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(residentpersonalinfotable);
        if (residentpersonalinfotable.getColumnModel().getColumnCount() > 0) {
            residentpersonalinfotable.getColumnModel().getColumn(0).setPreferredWidth(200);
            residentpersonalinfotable.getColumnModel().getColumn(1).setPreferredWidth(45);
            residentpersonalinfotable.getColumnModel().getColumn(2).setPreferredWidth(90);
            residentpersonalinfotable.getColumnModel().getColumn(3).setPreferredWidth(150);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/181 (2).jpg"))); // NOI18N

        tabletoPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pdficon.png"))); // NOI18N
        tabletoPDF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabletoPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabletoPDFActionPerformed(evt);
            }
        });

        tabletoExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/excelicon.png"))); // NOI18N
        tabletoExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabletoExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabletoExcelActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Prev.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
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
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(tabletoPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tabletoExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 785, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(19, 19, 19))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tabletoPDF, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabletoExcel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void tabletoPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabletoPDFActionPerformed
        // TODO add your handling code here:
        Document document = new Document();
                try {
                        PdfWriter.getInstance(document, new FileOutputStream(path+"\\PDF\\AdminArchive.pdf"));
                        document.open();
            
                        PdfPTable tab=new PdfPTable(model.getColumnCount());
                        for(int i = 0; i<model.getColumnCount();i++){
                                tab.addCell(model.getColumnName(i));
                            }
                        for(int i = 0;i<model.getRowCount();i++){
                                for(int j = 0; j<model.getColumnCount();j++){
                                        tab.addCell(model.getValueAt(i, j).toString());
                                    }
                            }
                        document.add(tab);
            
                        JOptionPane.showMessageDialog(null, "Data saved at " + path +
                                    "'\\PDF\\AdminArchive.pdf' successfully", "Message",
                                    JOptionPane.INFORMATION_MESSAGE);
                        document.close();
            
                    } catch (FileNotFoundException | DocumentException ex) {
//                        Logger.getLogger(TransientArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
                        new MessageDialog().error(this, ex.getMessage());
                    }
        
    }//GEN-LAST:event_tabletoPDFActionPerformed

    private void tabletoExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabletoExcelActionPerformed
        // TODO add your handling code here:
        try {
            ExcelExporter exp = new ExcelExporter();
            exp.fillData(residentpersonalinfotable, new File(path+"\\EXCEL\\AdminArchive.xls"));
            JOptionPane.showMessageDialog(null, "Data saved at " + path
                    + "'\\EXCEL\\AdminArchive.xls' successfully", "Message",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
//            ex.printStackTrace();
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_tabletoExcelActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        this.dispose();
        new ArchivesMenu(client,user).setVisible(true);
    }//GEN-LAST:event_jLabel1MouseClicked

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminArchiveFrame(null,null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable residentpersonalinfotable;
    private javax.swing.JButton tabletoExcel;
    private javax.swing.JButton tabletoPDF;
    // End of variables declaration//GEN-END:variables
}
