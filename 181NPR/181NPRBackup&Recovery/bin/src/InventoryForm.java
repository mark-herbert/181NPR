
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.exceptions.IllegalPdfSyntaxException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jefren
 */
public class InventoryForm extends javax.swing.JFrame {

    /**
     * Creates new form InventoryForm
     */
    private DefaultTableModel modelTop;
    private DefaultTableModel modelBottom;
    private Object[] test;
    private String id = "";
    private String name = "";
    private String roomNumber = "";
    private String roomType = "";
    private boolean status = false;
    private final MessageDialog md = new MessageDialog();
    private Connection connection;
    
    public InventoryForm() {
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
    
    public InventoryForm(String id, String name, String roomNumber, String roomType) {
        this.id = id;
        this.name = name;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Backup and Recovery.png")));
        initComponents();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        setInfo();
        
        try {
            modelTop = (DefaultTableModel) roomInventoryTable.getModel();
            modelTop.getDataVector().removeAllElements();
            modelTop.fireTableDataChanged();
            
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `furnitureItemName`, `furnitureControlNo`, `furnitureIdnum` FROM `furniture` WHERE `residentIdnum` = ? ORDER BY `furnitureItemName` ASC");
            preparedStatement.setInt(1, Integer.parseInt("99999999"));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                modelTop.addRow(new Object[]{
                    resultSet.getString("furnitureItemName"),
                    resultSet.getString("furnitureControlNo"),
                    resultSet.getString("furnitureIdnum")
                });
            }
        } catch (SQLException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        
        
    }

    private void setInfo() {
        nameLabel.setText(this.name);
        roomNumberLabel.setText(this.roomNumber);
        roomTypeLabel.setText(this.roomType);
    }
    
    public InventoryForm(DefaultTableModel table, String id, String name, String roomNumber, String roomType) {
        this.id = id;
        this.name = name;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Backup and Recovery.png")));
        initComponents();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        setInfo();
        roomInventoryTable.setModel(table);
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
        inventoryFormPanel = new javax.swing.JPanel();
        roomInventoryLabel = new javax.swing.JLabel();
        roomInventoryScrollPane = new javax.swing.JScrollPane();
        roomInventoryTable = new javax.swing.JTable();
        gadgetLabel = new javax.swing.JLabel();
        gadgetInventoryScrollPane = new javax.swing.JScrollPane();
        gadgetInventoryTable = new javax.swing.JTable();
        numberOfGadgetLabel = new javax.swing.JLabel();
        gadgetSpinner = new javax.swing.JSpinner();
        canceGadgetlButton = new javax.swing.JButton();
        saveGadgetButton = new javax.swing.JButton();
        roomInventoryButton = new javax.swing.JButton();
        previewGadgetButton = new javax.swing.JButton();
        previewRoomInventoryButton = new javax.swing.JButton();
        clearInventoryButton = new javax.swing.JButton();
        nameLabelOnly = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        roomNumberLabelOnly = new javax.swing.JLabel();
        roomTypeLabelOnly = new javax.swing.JLabel();
        roomNumberLabel = new javax.swing.JLabel();
        roomTypeLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Inventory Form");
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        inventoryFormPanel.setBackground(new java.awt.Color(255, 255, 255));
        inventoryFormPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Inventory Form", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Rondalo", 0, 18))); // NOI18N

        roomInventoryLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        roomInventoryLabel.setText("Room Inventory:");

        roomInventoryTable.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        roomInventoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Name", "Control Number", "Furniture Id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        roomInventoryTable.setColumnSelectionAllowed(true);
        roomInventoryTable.getTableHeader().setReorderingAllowed(false);
        roomInventoryScrollPane.setViewportView(roomInventoryTable);
        roomInventoryTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        gadgetLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        gadgetLabel.setText("Appliance and Gadget Inventory:");

        gadgetInventoryTable.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        gadgetInventoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Name", "Description", "Serial Number", "Voltage", "Wattage", "Monthly Charge"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        gadgetInventoryTable.getTableHeader().setReorderingAllowed(false);
        gadgetInventoryScrollPane.setViewportView(gadgetInventoryTable);

        numberOfGadgetLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        numberOfGadgetLabel.setText("Number of Gadgets/Appliance:");

        gadgetSpinner.setFont(new java.awt.Font("Rondalo", 0, 11)); // NOI18N
        gadgetSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 15, 1));
        gadgetSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(gadgetSpinner, ""));
        gadgetSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                gadgetSpinnerStateChanged(evt);
            }
        });

        canceGadgetlButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        canceGadgetlButton.setText("Cancel");
        canceGadgetlButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        canceGadgetlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canceGadgetlButtonActionPerformed(evt);
            }
        });

        saveGadgetButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        saveGadgetButton.setText("Save");
        saveGadgetButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        saveGadgetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGadgetButtonActionPerformed(evt);
            }
        });

        roomInventoryButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        roomInventoryButton.setText("Room Inventory");
        roomInventoryButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        roomInventoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomInventoryButtonActionPerformed(evt);
            }
        });

        previewGadgetButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        previewGadgetButton.setText("Save as PDF (Gadget)");
        previewGadgetButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        previewGadgetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewGadgetButtonActionPerformed(evt);
            }
        });

        previewRoomInventoryButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        previewRoomInventoryButton.setText("Save as PDF (Furniture)");
        previewRoomInventoryButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        previewRoomInventoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewRoomInventoryButtonActionPerformed(evt);
            }
        });

        clearInventoryButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        clearInventoryButton.setText("Clear");
        clearInventoryButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clearInventoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearInventoryButtonActionPerformed(evt);
            }
        });

        nameLabelOnly.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        nameLabelOnly.setText("Name:");

        nameLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        nameLabel.setText("fullname");

        roomNumberLabelOnly.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        roomNumberLabelOnly.setText("Room No. :");

        roomTypeLabelOnly.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        roomTypeLabelOnly.setText("Room Type:");

        roomNumberLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        roomNumberLabel.setText("Room No");

        roomTypeLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        roomTypeLabel.setText("Room Type");

        javax.swing.GroupLayout inventoryFormPanelLayout = new javax.swing.GroupLayout(inventoryFormPanel);
        inventoryFormPanel.setLayout(inventoryFormPanelLayout);
        inventoryFormPanelLayout.setHorizontalGroup(
            inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inventoryFormPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roomInventoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
                    .addComponent(gadgetInventoryScrollPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inventoryFormPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(previewGadgetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saveGadgetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(canceGadgetlButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inventoryFormPanelLayout.createSequentialGroup()
                        .addComponent(roomInventoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(previewRoomInventoryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(clearInventoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7))
                    .addGroup(inventoryFormPanelLayout.createSequentialGroup()
                        .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(roomTypeLabelOnly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameLabelOnly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roomNumberLabelOnly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roomNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roomTypeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)))
                    .addGroup(inventoryFormPanelLayout.createSequentialGroup()
                        .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gadgetLabel)
                            .addComponent(roomInventoryLabel)
                            .addGroup(inventoryFormPanelLayout.createSequentialGroup()
                                .addComponent(numberOfGadgetLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gadgetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        inventoryFormPanelLayout.setVerticalGroup(
            inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inventoryFormPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabelOnly)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roomNumberLabelOnly)
                    .addComponent(roomNumberLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roomTypeLabelOnly)
                    .addComponent(roomTypeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roomInventoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roomInventoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inventoryFormPanelLayout.createSequentialGroup()
                        .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clearInventoryButton)
                            .addComponent(previewRoomInventoryButton))
                        .addGap(11, 11, 11))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inventoryFormPanelLayout.createSequentialGroup()
                        .addComponent(roomInventoryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(gadgetLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfGadgetLabel)
                    .addComponent(gadgetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gadgetInventoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inventoryFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(canceGadgetlButton)
                    .addComponent(saveGadgetButton)
                    .addComponent(previewGadgetButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inventoryFormPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inventoryFormPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void gadgetSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_gadgetSpinnerStateChanged
        int row = Integer.parseInt("" + gadgetSpinner.getValue());
        modelBottom =(DefaultTableModel) gadgetInventoryTable.getModel();
        modelBottom.setNumRows(row);
        gadgetInventoryTable.setModel(modelBottom);
    }//GEN-LAST:event_gadgetSpinnerStateChanged

    private void saveGadgetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGadgetButtonActionPerformed
        if (md.confirmationSave(this) == md.YES) {
            status = true;
            modelBottom = (DefaultTableModel) gadgetInventoryTable.getModel();
            ArrayList<String> gadgetInfo;
            for (int count = 0; count < modelBottom.getRowCount(); count++) {
                gadgetInfo = new ArrayList<>();
                if(modelBottom.getValueAt(count, 0) != null && modelBottom.getValueAt(count, 1) != null && modelBottom.getValueAt(count, 2) != null){
                    gadgetInfo.add(modelBottom.getValueAt(count, 0).toString());
                    gadgetInfo.add(modelBottom.getValueAt(count, 1).toString());
                    gadgetInfo.add(modelBottom.getValueAt(count, 2).toString());
                    if(modelBottom.getValueAt(count, 3) != null){
                        gadgetInfo.add(modelBottom.getValueAt(count, 3).toString());
                    } else {
                        gadgetInfo.add("0");
                    }
                    if(modelBottom.getValueAt(count, 4) != null){
                        gadgetInfo.add(modelBottom.getValueAt(count, 4).toString());
                    } else {
                        gadgetInfo.add("0");
                    }
                    if(modelBottom.getValueAt(count, 5) != null){
                        gadgetInfo.add(modelBottom.getValueAt(count, 5).toString());
                    } else {
                        gadgetInfo.add("0");
                    }
                    if(!insertGadget(gadgetInfo)){
                        md.unsuccessful(this);
                        status = false;
                        break;
                    }
                }
            }
            if(status){
                    md.successful(this);
                }
        }
    }//GEN-LAST:event_saveGadgetButtonActionPerformed

    private void roomInventoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomInventoryButtonActionPerformed
        new FurnitureList(id, name, roomNumber, roomType).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_roomInventoryButtonActionPerformed

    private void previewRoomInventoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewRoomInventoryButtonActionPerformed
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
            FileOutputStream fos = new FileOutputStream(path+"\\furniture\\"+nameLabel.getText().trim() + "InventoryForm.pdf");
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
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, nameLabel.getText().trim(), 185, 650, 0);
                //room type
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, roomTypeLabel.getText().trim(), 185, 635, 0);
                //resident
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, roomNumberLabel.getText().trim(), 185, 620, 0);
                for (int j = 0; j < roomInventoryTable.getRowCount(); j++) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, roomInventoryTable.getValueAt(j, 0).toString().trim(), rowSize-60, columnSize + added, 0);
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, roomInventoryTable.getValueAt(j, 1).toString().trim(), rowSize+30, columnSize + added, 0);
                    added -= 15;
                }
                
                content.endText();
            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();
            pdfWriter.close();
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        } catch (DocumentException | IOException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_previewRoomInventoryButtonActionPerformed

    private void clearInventoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearInventoryButtonActionPerformed
        if (md.confirmationSave(this) == md.YES) {
            modelTop = (DefaultTableModel) roomInventoryTable.getModel();
            modelTop.setRowCount(0);
        }
    }//GEN-LAST:event_clearInventoryButtonActionPerformed

    private void previewGadgetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewGadgetButtonActionPerformed
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
            FileOutputStream fos = new FileOutputStream(path+"\\gadget\\"+nameLabel.getText().trim()+"GadgetForm.pdf");
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
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, roomNumberLabel.getText().trim(), 240, 630, 0);
                //room type
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, roomTypeLabel.getText().trim(), 240, 615, 0);
                //resident
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, nameLabel.getText().trim(), 240, 600, 0);
                for (int j = 0; j<gadgetInventoryTable.getRowCount(); j++) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, gadgetInventoryTable.getValueAt(j, 0).toString().trim(), rowSize-60, columnSize + added, 0);
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, gadgetInventoryTable.getValueAt(j, 1).toString().trim(), rowSize+65, columnSize + added, 0);
                    added -= 15;
                }
                
                content.endText();
            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();
            pdfWriter.close();
        } catch (DocumentException | FileNotFoundException | IllegalPdfSyntaxException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        } catch (IOException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_previewGadgetButtonActionPerformed

    private void canceGadgetlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_canceGadgetlButtonActionPerformed
        if (md.confirmationCancel(this) == md.YES) {
            this.dispose();
        }
    }//GEN-LAST:event_canceGadgetlButtonActionPerformed

    private boolean insertGadget(ArrayList<String> gadgetDetails){
        boolean status = false;
        try {
            int c = 0;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM gadget");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c++;
            }
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
            preparedStatement.execute();
            int c1 = 0;
            preparedStatement = connection.prepareStatement("SELECT * FROM gadget");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c1++;
            }
            if (c == (c1 - 1)) {
                status =  true;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
            status =  false;
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InventoryForm().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton canceGadgetlButton;
    private javax.swing.JButton clearInventoryButton;
    private javax.swing.JScrollPane gadgetInventoryScrollPane;
    private javax.swing.JTable gadgetInventoryTable;
    private javax.swing.JLabel gadgetLabel;
    private javax.swing.JSpinner gadgetSpinner;
    private javax.swing.JPanel inventoryFormPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel nameLabelOnly;
    private javax.swing.JLabel numberOfGadgetLabel;
    private javax.swing.JButton previewGadgetButton;
    private javax.swing.JButton previewRoomInventoryButton;
    private javax.swing.JButton roomInventoryButton;
    private javax.swing.JLabel roomInventoryLabel;
    private javax.swing.JScrollPane roomInventoryScrollPane;
    private javax.swing.JTable roomInventoryTable;
    private javax.swing.JLabel roomNumberLabel;
    private javax.swing.JLabel roomNumberLabelOnly;
    private javax.swing.JLabel roomTypeLabel;
    private javax.swing.JLabel roomTypeLabelOnly;
    private javax.swing.JButton saveGadgetButton;
    // End of variables declaration//GEN-END:variables
}
