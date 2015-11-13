
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
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
public class ResidentArchiveFrame extends javax.swing.JFrame {

    /**
     * Creates new form ResidentArchiveFrame
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
    public ResidentArchiveFrame(NPRInterface client, String user) throws IOException {

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
            for(ResidentImpl r : client.getAllResidentPersonal()){
                model.addRow(new Object[]{
                    r.getId(),
                    r.getFullName(),
                    r.getLastStatus(),
                    r.getTime()
                });
            }
            
            model = (DefaultTableModel) residentcontactinfotable.getModel();
            for(ResidentImpl r : client.getAllResidentContact()){
                model.addRow(new Object[]{
                    r.getFullName(),
                    r.getId(),
                    r.getBirthdate()
                });
            }
            
            model = (DefaultTableModel) residentaddresstable.getModel();
            for(ResidentImpl r : client.getAllResidentAddress()){
                model.addRow(new Object[]{
                    r.getFullName(),
                    r.getId(),
                    r.getBirthdate()
                });
            }
            
            model = (DefaultTableModel) residentstatustable.getModel();
            for(ResidentImpl r : client.getAllResidentStatus()){
                model.addRow(new Object[]{
                    r.getFullName(),
                    r.getId(),
                    r.getBirthdate()
                });
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(ReadmitArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(ResidentArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }

    }//endof

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
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        residentpersonalinfotable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        residentcontactinfotable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        residentaddresstable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        residentstatustable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        tabletoPDF1 = new javax.swing.JButton();
        tabletoExcel1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("residentarchive.png")).getImage());
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resident Archive", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 24))); // NOI18N

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
                "Resident ID", "Full Name", "Gender", "Birthdate"
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

        jTabbedPane2.addTab("Personal Information", jScrollPane2);

        residentcontactinfotable.setAutoCreateRowSorter(true);
        residentcontactinfotable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Full name", "Mobile No.", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        residentcontactinfotable.setGridColor(new java.awt.Color(204, 204, 204));
        residentcontactinfotable.getTableHeader().setResizingAllowed(false);
        residentcontactinfotable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(residentcontactinfotable);

        jTabbedPane2.addTab("Contact Information", jScrollPane3);

        residentaddresstable.setAutoCreateRowSorter(true);
        residentaddresstable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Full Name", "Address", "Zip Code"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        residentaddresstable.setGridColor(new java.awt.Color(204, 204, 204));
        residentaddresstable.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(residentaddresstable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Address", jPanel3);

        residentstatustable.setAutoCreateRowSorter(true);
        residentstatustable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Resident ID", "Full Name", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        residentstatustable.setGridColor(new java.awt.Color(204, 204, 204));
        residentstatustable.getTableHeader().setResizingAllowed(false);
        residentstatustable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(residentstatustable);

        jTabbedPane2.addTab("Status", jScrollPane4);

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 891, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(250, 250, 250)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/181 (2).jpg"))); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Prev.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        tabletoPDF1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pdficon.png"))); // NOI18N
        tabletoPDF1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabletoPDF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabletoPDF1ActionPerformed(evt);
            }
        });

        tabletoExcel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/excelicon.png"))); // NOI18N
        tabletoExcel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabletoExcel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabletoExcel1ActionPerformed(evt);
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
                        .addGap(13, 13, 13))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(28, 28, 28))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tabletoPDF1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tabletoExcel1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tabletoPDF1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabletoExcel1, javax.swing.GroupLayout.Alignment.LEADING))
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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        this.dispose();
        new ArchivesMenu(client,user).setVisible(true);
    }//GEN-LAST:event_jLabel1MouseClicked

    private void tabletoPDF1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabletoPDF1ActionPerformed
        // TODO add your handling code here:
        String title = "";
        if (jTabbedPane2.getSelectedIndex() == 0) {
            model = (DefaultTableModel) residentpersonalinfotable.getModel();
            title = "PersonalInformation";
        } else if (jTabbedPane2.getSelectedIndex() == 1) {
            model = (DefaultTableModel) residentcontactinfotable.getModel();
            title = "ContactInformation";
        } else if (jTabbedPane2.getSelectedIndex() == 2) {
            model = (DefaultTableModel) residentaddresstable.getModel();
            title = "Address";
        } else {
            model = (DefaultTableModel) residentstatustable.getModel();
            title = "Status";
        }
        
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path+"\\PDF\\"+title+"ResidentArchive.pdf"));
            PdfReader pdfReader = new PdfReader("ReportGeneration.pdf");
            document.open();
            PdfPTable tab = new PdfPTable(model.getColumnCount());
            
            for (int i = 0; i < model.getColumnCount(); i++) {
                tab.addCell(model.getColumnName(i));
            }
            
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    if(model.getValueAt(i, j) != null){
                        tab.addCell(model.getValueAt(i, j).toString());
                    } else {
                        tab.addCell("-");
                    }
                }
            }

            document.add(tab);
            pdfReader.close();
            document.close();

            JOptionPane.showMessageDialog(null, "Data saved at '" + path
                    + "\\PDF\\"+title+"ResidentArchive.pdf' successfully", "Message",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException ex) {
//            Logger.getLogger(ResidentArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        } catch (DocumentException | IOException ex) {
//            Logger.getLogger(ResidentArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_tabletoPDF1ActionPerformed

    private void tabletoExcel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabletoExcel1ActionPerformed
        // TODO add your handling code here:
        try {
            ExcelExporter exp = new ExcelExporter();
            if (jTabbedPane2.getSelectedIndex() == 0) {
                exp.fillData(residentpersonalinfotable, new File(path+"\\EXCEL\\PersonalInformationResidentArchive.xls"));
            }else if (jTabbedPane2.getSelectedIndex() == 1) {
                exp.fillData(residentcontactinfotable, new File(path+"\\EXCEL\\ContactInformationResidentArchive.xls"));
            }else if (jTabbedPane2.getSelectedIndex() == 2) {
                exp.fillData(residentaddresstable, new File(path+"\\EXCEL\\AddressResidentArchive.xls"));
            }else {
                exp.fillData(residentstatustable, new File(path+"\\EXCEL\\StatusResidentArchive.xls"));
            }
            JOptionPane.showMessageDialog(null, "Data saved at '" + path
                    + "\\EXCEL' successfully", "Message",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
//            ex.printStackTrace();
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_tabletoExcel1ActionPerformed

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
            java.util.logging.Logger.getLogger(ResidentArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResidentArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResidentArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResidentArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ResidentArchiveFrame(null, null).setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(ResidentArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable residentaddresstable;
    private javax.swing.JTable residentcontactinfotable;
    private javax.swing.JTable residentpersonalinfotable;
    private javax.swing.JTable residentstatustable;
    private javax.swing.JButton tabletoExcel1;
    private javax.swing.JButton tabletoPDF1;
    // End of variables declaration//GEN-END:variables

    private int CONCAT(String residentLname) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Number Integer(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void framesetVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void framesetSize(int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
