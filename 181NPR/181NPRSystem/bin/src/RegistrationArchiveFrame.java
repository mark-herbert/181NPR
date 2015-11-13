
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
public class RegistrationArchiveFrame extends javax.swing.JFrame {

    /**
     * Creates new form RegistrationArchiveFrame
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
    public RegistrationArchiveFrame(NPRInterface client, String user) throws IOException {
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
            model = (DefaultTableModel) regpersonalinfotable.getModel();
            for(RegistrationImpl r : client.getAllRegistrationPersonal()){
                model.addRow(new Object[]{
                    r.getLast_name(),
                    r.getFirst_name(),
                    r.getYear(),
                    r.getCollege(),
                    r.getCourse(),
                    r.getAddress(),
                    r.getDepartment()
                
                });
            }
            
            model = (DefaultTableModel) regcontactinfotable.getModel();
            for(RegistrationImpl r : client.getAllRegistrationContact()){
                model.addRow(new Object[]{
                    r.getFirst_name(),
                    r.getMobile_number(),
                    r.getMobile_number2(),
                    r.getEmail(),
                    r.getRoom_number()
                });
            }
            
            model = (DefaultTableModel) regfathersinfotable.getModel();
            for(RegistrationImpl r : client.getAllRegistrationFather()){
                model.addRow(new Object[]{
                    r.getFirst_name(),
                    r.getFatherName(),
                    r.getFatherAreaCode(),
                    r.getFatherMobile(),
                    r.getFatherEmail()
                });
            }
            
            model = (DefaultTableModel) regmothersinfotable.getModel();
            for(RegistrationImpl r : client.getAllRegistrationMother()){
                model.addRow(new Object[]{
                    r.getFirst_name(),
                    r.getMotherName(),
                    r.getMotherAreaCode(),
                    r.getMotherMobile(),
                    r.getMotherEmail()
                });
            }
            
            model = (DefaultTableModel) regguardiansinfotable.getModel();
            for(RegistrationImpl r : client.getAllRegistrationGuardian()){
                model.addRow(new Object[]{
                    r.getFirst_name(),
                    r.getGuardianName(),
                    r.getGuardianMobile(),
                    r.getGuardianRelation()
                });
            }
            
            model = (DefaultTableModel) guardianaddresstable.getModel();
            for(RegistrationImpl r : client.getAllRegistrationGuardianAddress()){
                model.addRow(new Object[]{
                    r.getFirst_name(),
                    r.getGuardianName(),
                    r.getGuardianAddress()
                });
            }
        } catch (RemoteException | FileNotFoundException ex) {
//            Logger.getLogger(RegistrationArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
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
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        regpersonalinfotable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        regcontactinfotable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        regfathersinfotable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        regmothersinfotable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        regguardiansinfotable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        guardianaddresstable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        tabletoPDF = new javax.swing.JButton();
        tabletoExcel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("registrationarchive.png")).getImage());
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registration Archive", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 24))); // NOI18N

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

        regpersonalinfotable.setAutoCreateRowSorter(true);
        regpersonalinfotable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "REG ID", "Full Name", "Birthdate", "Gender", "College", "Course & Year", "Department"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        regpersonalinfotable.setGridColor(new java.awt.Color(204, 204, 204));
        regpersonalinfotable.getTableHeader().setResizingAllowed(false);
        regpersonalinfotable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(regpersonalinfotable);

        jTabbedPane2.addTab("Personal Information", jScrollPane1);

        regcontactinfotable.setAutoCreateRowSorter(true);
        regcontactinfotable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Full Name", "Mobile 1", "Mobile 2", "Email", "Room"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        regcontactinfotable.setGridColor(new java.awt.Color(204, 204, 204));
        regcontactinfotable.getTableHeader().setResizingAllowed(false);
        regcontactinfotable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(regcontactinfotable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Contact Information", jPanel3);

        regfathersinfotable.setAutoCreateRowSorter(true);
        regfathersinfotable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Full Name", "Father's Name", "Landline", "Mobile", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        regfathersinfotable.setGridColor(new java.awt.Color(204, 204, 204));
        regfathersinfotable.getTableHeader().setResizingAllowed(false);
        regfathersinfotable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(regfathersinfotable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Father Information", jPanel4);

        regmothersinfotable.setAutoCreateRowSorter(true);
        regmothersinfotable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Full Name", "Mother's Name", "Landline", "Mobile", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        regmothersinfotable.setGridColor(new java.awt.Color(204, 204, 204));
        regmothersinfotable.getTableHeader().setResizingAllowed(false);
        regmothersinfotable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(regmothersinfotable);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Mother Information", jPanel5);

        regguardiansinfotable.setAutoCreateRowSorter(true);
        regguardiansinfotable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Full Name", "Guardian's Name", "Mobile", "Relation"
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
        regguardiansinfotable.setGridColor(new java.awt.Color(204, 204, 204));
        regguardiansinfotable.getTableHeader().setResizingAllowed(false);
        regguardiansinfotable.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(regguardiansinfotable);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Guardian Information", jPanel6);

        guardianaddresstable.setAutoCreateRowSorter(true);
        guardianaddresstable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Full Name", "Guardian's Name", "Address"
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
        guardianaddresstable.setGridColor(new java.awt.Color(204, 204, 204));
        guardianaddresstable.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(guardianaddresstable);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Guardian Address", jPanel7);

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
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 893, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(123, 123, 123)
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
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(19, 19, 19))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tabletoPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabletoExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addGap(11, 11, 11)
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
        // TODO add your handling code here:
        String title = "";
        if (jTabbedPane2.getSelectedIndex() == 0) {
            model = (DefaultTableModel) regpersonalinfotable.getModel();
            title = "PersonalInformation";
        } else if (jTabbedPane2.getSelectedIndex() == 1) {
            model = (DefaultTableModel) regcontactinfotable.getModel();
            title = "ContactInformation";
        } else if (jTabbedPane2.getSelectedIndex() == 2) {
            model = (DefaultTableModel) regfathersinfotable.getModel();
            title = "FatherInformation";
        } else if (jTabbedPane2.getSelectedIndex() == 3) {
            model = (DefaultTableModel) regmothersinfotable.getModel();
            title = "MotherInformation";
        } else if (jTabbedPane2.getSelectedIndex() == 4) {
            model = (DefaultTableModel) regguardiansinfotable.getModel();
            title = "GuardianInformation";
        } else if (jTabbedPane2.getSelectedIndex() == 5) {
            model = (DefaultTableModel) guardianaddresstable.getModel();
            title = "GuardianAddress";
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path+"\\PDF\\"+title+"RegistrationArchive.pdf"));
            
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

            JOptionPane.showMessageDialog(null, "Data saved at " + path
                    + "'\\PDF\\"+ title+ "RegistrationArchive.pdf' successfully", "Message",
                    JOptionPane.INFORMATION_MESSAGE);
            document.close();

        } catch (FileNotFoundException | DocumentException ex) {
//            Logger.getLogger(RegistrationArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_tabletoPDFActionPerformed

    private void tabletoExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabletoExcelActionPerformed
        // TODO add your handling code here:
        try {
            ExcelExporter exp = new ExcelExporter();
            if (jTabbedPane2.getSelectedIndex() == 0) {
                exp.fillData(regpersonalinfotable, new File(path+"\\EXCEL\\PersonalInformationRegistrationArchive.xls"));
            } else if (jTabbedPane2.getSelectedIndex() == 1) {
                exp.fillData(regcontactinfotable, new File(path+"\\EXCEL\\ContactInformationRegistrationArchive.xls"));
            } else if (jTabbedPane2.getSelectedIndex() == 2) {
                exp.fillData(regfathersinfotable, new File(path+"\\EXCEL\\FatherInformationRegistrationArchive.xls"));
            } else if (jTabbedPane2.getSelectedIndex() == 3) {
                exp.fillData(regmothersinfotable, new File(path+"\\EXCEL\\MotherInformationRegistrationArchive.xls"));
            } else if (jTabbedPane2.getSelectedIndex() == 4) {
                exp.fillData(regguardiansinfotable, new File(path+"\\EXCEL\\GuardianInformationRegistrationArchive.xls"));
            } else if (jTabbedPane2.getSelectedIndex() == 5) {
                exp.fillData(guardianaddresstable, new File(path+"\\EXCEL\\GuardianAddressRegistrationArchive.xls"));
            }
            JOptionPane.showMessageDialog(null, "Data saved at " + path
                    + "'\\EXCEL' successfully", "Message",
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
            java.util.logging.Logger.getLogger(RegistrationArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistrationArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistrationArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistrationArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new RegistrationArchiveFrame(null,null).setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(RegistrationArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable guardianaddresstable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable regcontactinfotable;
    private javax.swing.JTable regfathersinfotable;
    private javax.swing.JTable regguardiansinfotable;
    private javax.swing.JTable regmothersinfotable;
    private javax.swing.JTable regpersonalinfotable;
    private javax.swing.JButton tabletoExcel;
    private javax.swing.JButton tabletoPDF;
    // End of variables declaration//GEN-END:variables
}
