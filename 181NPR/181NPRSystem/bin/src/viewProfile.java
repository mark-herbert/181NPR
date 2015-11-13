
import datechooser.model.exeptions.IncompatibleDataExeption;
import datechooser.model.multiple.Period;
import datechooser.model.multiple.PeriodSet;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jefren
 */
public class viewProfile extends javax.swing.JFrame {
    private String residentId = "";
    private final NPRInterface client;
    private DefaultTableModel modelRight;
    private DefaultTableModel modelLeft;
    private DefaultTableModel tableModel = new DefaultTableModel();
    private int index = 0;
    private String id = "";
    private boolean test = false;
    private String file;
    private ArrayList<GadgetImpl> gadgetInfo;
    private ArrayList<FurnitureImpl> furnitureInfo;
    ResidentImpl resident;
    private byte[] currentProf = null;
    private final MessageDialog md = new MessageDialog();
    private Date lastDate = null;
    ArrayList<String> repository = new ArrayList<>();
    /**
     * Creates new form viewProfile
     * @param client
     */
    public viewProfile(NPRInterface client) {
        this.client = client;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Residents.png")));
        initComponents();
        numberOfGadgetLabel.setVisible(false);
        gadgetSpinner.setVisible(false);
    }
    public viewProfile() {
        this.client = null;
        initComponents();
    }
    
    public viewProfile(NPRInterface client, String residentId) {
        this.client = client;
        this.residentId = residentId;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Residents.png")));
        initComponents();
        numberOfGadgetLabel.setVisible(false);
        gadgetSpinner.setVisible(false);
        cancel.setEnabled(false);
        try {
            resident = client.getResidentInfo(residentId);
            lName.setText(resident.getLast_name());
            fName.setText(resident.getFirst_name());
            mName.setText(resident.getMiddle_name());
            gender.setSelectedItem(resident.getGender());

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(resident.getBirthdate());
            Date d = birthdate.getDateFormat().parse(birthdate.getDateFormat().format(date));
            Calendar cal  = Calendar.getInstance();
            cal.setTime(d);
            
            birthdate.setSelectedDate(cal);
            birthdate.setDefaultPeriods(new PeriodSet(new Period(cal,cal)));

            lastDate = birthdate.getDateFormat().parse(birthdate.getText());
            
            email.setText(resident.getEmail());
            mobileNo.setText(resident.getMobile_number());
            address.setText(resident.getAddress());
            zipCode.setText(resident.getZipcode());

            displayGadgetTable(residentId);
            displayFurnitureTable(residentId);
            nameLabel.setText(resident.getFirst_name() + " " + resident.getLast_name());
            
            currentProf = resident.getPicture();
            if(currentProf != null){
                InputStream in = new ByteArrayInputStream(currentProf);
                BufferedImage img = ImageIO.read(in);
                if(img != null){
                    Image image = img.getScaledInstance(218, 204, Image.SCALE_SMOOTH);
                    profilePic.setIcon(new ImageIcon(image));
                }
            }
            fatherName.setText(resident.getFatherName());
            fatherContact.setText(resident.getFatherContact());
            fatherEmail.setText(resident.getFatherEmail());
            fatherLandline.setText(resident.getFatherLandLine());
            motherName.setText(resident.getMotherName());
            motherContact.setText(resident.getMotherContact());
            motherEmail.setText(resident.getMotherEmail());
            motherLandline.setText(resident.getMotherLandLine());
            guardianName.setText(resident.getGuardianName());
            guardianAddress.setText(resident.getGuardianAddress());
            guardianContact.setText(resident.getGuardianContact());
            guardianRelation.setText(resident.getGuardianRelation());
        } catch (SQLException | ParseException | RemoteException ex) {
//            Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        } catch (IOException ex) {
//            Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        } catch (IncompatibleDataExeption ex) {
//            Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
        
    }

    private void displayFurnitureTable(String residentId1) throws SQLException, RemoteException {
        tableModel = (DefaultTableModel) dormFurnitureTable.getModel();
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
        furnitureInfo = client.getFurnitureFromResident(residentId1);
                for (FurnitureImpl info : furnitureInfo) {
                    tableModel.addRow(new Object[]{
                        info.getItem_name(),
                        info.getControl_number(),
                        info.getFurniture_id()
                    });
                }
        dormFurnitureTable.setModel(tableModel);
        
        tableModel = (DefaultTableModel) furnitureItemTable.getModel();
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
        furnitureInfo = client.getFurniture(client.getRoomIdFromResidentId(residentId1));
                for (FurnitureImpl info : furnitureInfo) {
                    tableModel.addRow(new Object[]{
                        info.getItem_name(),
                        info.getControl_number(),
                        info.getFurniture_id()
                    });
                }
        furnitureItemTable.setModel(tableModel);
    }
    
    private void displayGadgetTable(String residentId) throws RemoteException {
        tableModel = (DefaultTableModel) gadgetInventoryTable.getModel();
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
        gadgetInfo = client.getGadget(residentId);
        repository = new ArrayList<>();
        for (GadgetImpl info : gadgetInfo) {
            repository.add(info.getId());
            tableModel.addRow(new Object[]{
                false,
                info.getItem_name(),
                info.getDescription(),
                info.getSerial_number(),
                info.getVoltage(),
                info.getWattage(),
                info.getRate()
            });
        }
        gadgetInventoryTable.setModel(tableModel);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooser = new javax.swing.JFileChooser();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        profilePic = new javax.swing.JLabel();
        change = new javax.swing.JButton();
        Save = new javax.swing.JButton();
        lblPanel = new javax.swing.JPanel();
        Edit = new javax.swing.JButton();
        editLbl = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lName = new javax.swing.JTextField();
        fName = new javax.swing.JTextField();
        mName = new javax.swing.JTextField();
        gender = new javax.swing.JComboBox();
        email = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        mobileNo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        address = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        zipCode = new javax.swing.JTextField();
        birthdate = new datechooser.beans.DateChooserCombo();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        fatherContact = new javax.swing.JTextField();
        fatherEmail = new javax.swing.JTextField();
        fatherLandline = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        motherContact = new javax.swing.JTextField();
        motherEmail = new javax.swing.JTextField();
        motherLandline = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        guardianContact = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        guardianRelation = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        guardianName = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        guardianAddress = new javax.swing.JTextArea();
        fatherName = new javax.swing.JLabel();
        motherName = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        gadgetInventoryScrollPane = new javax.swing.JScrollPane();
        gadgetInventoryTable = new javax.swing.JTable();
        nameLabel = new javax.swing.JLabel();
        delete = new javax.swing.JButton();
        add = new javax.swing.JButton();
        edit = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        removeButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        furnitureItemTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        dormFurnitureTable = new javax.swing.JTable();
        numberOfGadgetLabel = new javax.swing.JLabel();
        gadgetSpinner = new javax.swing.JSpinner();
        cancel = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();

        chooser.setCurrentDirectory(new java.io.File("C:\\Users\\Kenneth\\Downloads\\pics of residents\\pics of residents"));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(831, 688));
        setUndecorated(true);
        setResizable(false);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setMaximumSize(new java.awt.Dimension(831, 688));
        jPanel4.setMinimumSize(new java.awt.Dimension(831, 688));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Profile Picture", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 14))); // NOI18N

        profilePic.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        change.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        change.setText("Change");
        change.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        change.setEnabled(false);
        change.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(change, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                    .addComponent(profilePic, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(profilePic, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(change, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Save.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        Save.setText("Save");
        Save.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Save.setEnabled(false);
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });

        lblPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout lblPanelLayout = new javax.swing.GroupLayout(lblPanel);
        lblPanel.setLayout(lblPanelLayout);
        lblPanelLayout.setHorizontalGroup(
            lblPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        lblPanelLayout.setVerticalGroup(
            lblPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Edit.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        Edit.setText("Edit");
        Edit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });

        editLbl.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        editLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editLbl.setText("Profile");

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Personal Information"));
        jPanel2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel2.setText("Last Name");

        jLabel3.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel3.setText("First Name");

        jLabel4.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel4.setText("Middle Name");

        jLabel5.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel5.setText("Gender");

        jLabel6.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel6.setText("Birthdate");

        jLabel7.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel7.setText("Email");

        lName.setEditable(false);
        lName.setBackground(new java.awt.Color(255, 255, 255));
        lName.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N

        fName.setEditable(false);
        fName.setBackground(new java.awt.Color(255, 255, 255));
        fName.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N

        mName.setEditable(false);
        mName.setBackground(new java.awt.Color(255, 255, 255));
        mName.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N

        gender.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        gender.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Male", "Female" }));
        gender.setEnabled(false);

        email.setEditable(false);
        email.setBackground(new java.awt.Color(255, 255, 255));
        email.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel9.setText("Mobile No.");

        mobileNo.setEditable(false);
        mobileNo.setBackground(new java.awt.Color(255, 255, 255));
        mobileNo.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel10.setText("Address");

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        address.setEditable(false);
        address.setColumns(20);
        address.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        address.setRows(5);
        jScrollPane2.setViewportView(address);

        jLabel11.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel11.setText("Zip-code");

        zipCode.setEditable(false);
        zipCode.setBackground(new java.awt.Color(255, 255, 255));
        zipCode.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N

        birthdate.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
            new datechooser.view.appearance.ViewAppearance("custom",
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    true,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 255),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(128, 128, 128),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(255, 0, 0),
                    false,
                    false,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                (datechooser.view.BackRenderer)null,
                false,
                true)));
    birthdate.setNothingAllowed(false);
    birthdate.setFormat(1);
    birthdate.setEnabled(false);
    birthdate.setFieldFont(new java.awt.Font("Rondalo", java.awt.Font.PLAIN, 14));
    birthdate.setCurrentNavigateIndex(0);
    birthdate.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
    birthdate.addCommitListener(new datechooser.events.CommitListener() {
        public void onCommit(datechooser.events.CommitEvent evt) {
            birthdateOnCommit(evt);
        }
    });

    jPanel7.setBackground(new java.awt.Color(255, 255, 255));
    jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Parents and Guardiang Contact Info", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 12))); // NOI18N

    jLabel12.setText("Father Contact Info:");

    jLabel13.setText("Mobile Number:");

    jLabel14.setText("Email:");

    jLabel15.setText("Landline:");

    fatherContact.setEditable(false);
    fatherContact.setBackground(new java.awt.Color(255, 255, 255));

    fatherEmail.setEditable(false);
    fatherEmail.setBackground(new java.awt.Color(255, 255, 255));

    fatherLandline.setEditable(false);
    fatherLandline.setBackground(new java.awt.Color(255, 255, 255));

    jLabel16.setText("Mother Contact Info:");

    jLabel17.setText("Mobile Number:");

    jLabel18.setText("Email:");

    jLabel19.setText("Landline:");

    motherContact.setEditable(false);
    motherContact.setBackground(new java.awt.Color(255, 255, 255));

    motherEmail.setEditable(false);
    motherEmail.setBackground(new java.awt.Color(255, 255, 255));

    motherLandline.setEditable(false);
    motherLandline.setBackground(new java.awt.Color(255, 255, 255));

    jLabel20.setText("Guardian Info:");

    jLabel21.setText("Mobile Number:");

    guardianContact.setEditable(false);
    guardianContact.setBackground(new java.awt.Color(255, 255, 255));

    jLabel22.setText("Relation:");

    guardianRelation.setEditable(false);
    guardianRelation.setBackground(new java.awt.Color(255, 255, 255));

    jLabel24.setText("Name:");

    guardianName.setEditable(false);
    guardianName.setBackground(new java.awt.Color(255, 255, 255));

    jLabel25.setText("Address");

    jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    guardianAddress.setEditable(false);
    guardianAddress.setColumns(20);
    guardianAddress.setLineWrap(true);
    guardianAddress.setRows(5);
    jScrollPane6.setViewportView(guardianAddress);

    fatherName.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    fatherName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    fatherName.setText("<name>");

    motherName.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    motherName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    motherName.setText("<name>");

    javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
    jPanel7.setLayout(jPanel7Layout);
    jPanel7Layout.setHorizontalGroup(
        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(fatherContact)
                        .addComponent(fatherEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(fatherLandline, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))))
                .addComponent(jSeparator2)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(motherContact)
                        .addComponent(motherEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(motherLandline, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))))
                .addComponent(jSeparator3)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(guardianName)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                        .addComponent(guardianContact)
                        .addComponent(guardianRelation, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jLabel20)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jLabel12)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(fatherName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jLabel16)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(motherName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addContainerGap())
    );
    jPanel7Layout.setVerticalGroup(
        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel12)
                .addComponent(fatherName))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel13)
                .addComponent(fatherContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel14)
                .addComponent(fatherEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel15)
                .addComponent(fatherLandline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel16)
                .addComponent(motherName))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel17)
                .addComponent(motherContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel18)
                .addComponent(motherEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel19)
                .addComponent(motherLandline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel20)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel24)
                .addComponent(guardianName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel25)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel21)
                .addComponent(guardianContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel22)
                .addComponent(guardianRelation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                        .addComponent(email, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mobileNo, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(gender, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mName, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(fName, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(zipCode)
                        .addComponent(birthdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lName, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(lName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(fName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel4)
                .addComponent(mName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5)
                .addComponent(gender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel6)
                .addComponent(birthdate, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel9)
                .addComponent(mobileNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel7)
                .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(jLabel10))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel11)
                .addComponent(zipCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jScrollPane5.setViewportView(jPanel2);

    jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/monitoringsystem/181.jpg"))); // NOI18N

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(Edit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Save, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addComponent(jSeparator4))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(67, 67, 67)
            .addComponent(lblPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(291, 291, 291))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(editLbl)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(Save, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(lblPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addContainerGap())
    );

    jTabbedPane1.addTab("  Profile  ", new javax.swing.ImageIcon(getClass().getResource("/tab/Resident.png")), jPanel1); // NOI18N

    jPanel6.setBackground(new java.awt.Color(255, 255, 255));

    gadgetInventoryScrollPane.setBackground(new java.awt.Color(255, 255, 255));
    gadgetInventoryScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Gadget List"));

    gadgetInventoryTable.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    gadgetInventoryTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "", "Item Name", "Description", "Serial Number", "Voltage", "Wattage", "Monthly Charge"
        }
    ) {
        Class[] types = new Class [] {
            java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
        };
        boolean[] canEdit = new boolean [] {
            true, false, false, false, false, false, false
        };

        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    gadgetInventoryTable.getTableHeader().setReorderingAllowed(false);
    gadgetInventoryScrollPane.setViewportView(gadgetInventoryTable);
    if (gadgetInventoryTable.getColumnModel().getColumnCount() > 0) {
        gadgetInventoryTable.getColumnModel().getColumn(0).setMinWidth(20);
        gadgetInventoryTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        gadgetInventoryTable.getColumnModel().getColumn(0).setMaxWidth(20);
    }

    nameLabel.setText("Name");

    delete.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    delete.setText("Delete");
    delete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    delete.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteActionPerformed(evt);
        }
    });

    add.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    add.setText("Add");
    add.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    add.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            addActionPerformed(evt);
        }
    });

    edit.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    edit.setText("Edit");
    edit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    edit.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            editActionPerformed(evt);
        }
    });

    jPanel5.setBackground(new java.awt.Color(255, 255, 255));
    jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Furniture List"));

    removeButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    removeButton.setText("<");
    removeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    removeButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            removeButtonMouseClicked(evt);
        }
    });

    addButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    addButton.setText(">");
    addButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    addButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            addButtonMouseClicked(evt);
        }
    });

    jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));
    jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Room Furniture List"));

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
    jScrollPane4.setViewportView(furnitureItemTable);

    jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));
    jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Resident Furniture List"));

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
    jScrollPane3.setViewportView(dormFurnitureTable);

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(5, 5, 5)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(7, 7, 7)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .addContainerGap())
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(132, 132, 132)
            .addComponent(addButton)
            .addGap(11, 11, 11)
            .addComponent(removeButton)
            .addContainerGap(142, Short.MAX_VALUE))
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addContainerGap())
    );

    numberOfGadgetLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    numberOfGadgetLabel.setText("Number of Gadgets/Appliance:");

    gadgetSpinner.setFont(new java.awt.Font("Rondalo", 0, 11)); // NOI18N
    gadgetSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 15, 1));
    gadgetSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            gadgetSpinnerStateChanged(evt);
        }
    });

    cancel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    cancel.setText("Cancel");
    cancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    cancel.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(numberOfGadgetLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(gadgetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cancel)
                            .addGap(18, 18, 18)
                            .addComponent(edit)
                            .addGap(18, 18, 18)
                            .addComponent(add)
                            .addGap(18, 18, 18)
                            .addComponent(delete))
                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(gadgetInventoryScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE)))
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    jPanel6Layout.setVerticalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(nameLabel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(gadgetInventoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(delete)
                .addComponent(add)
                .addComponent(edit)
                .addComponent(numberOfGadgetLabel)
                .addComponent(gadgetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cancel))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(18, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("Furniture and Gadgets", new javax.swing.ImageIcon(getClass().getResource("/tab/Floor Plan.png")), jPanel6); // NOI18N

    jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/close.png"))); // NOI18N
    jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jLabel1MouseClicked(evt);
        }
    });

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jSeparator1)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1)
            .addContainerGap())
        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 827, Short.MAX_VALUE)
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
    setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        if(Edit.getText().contains("Edit")){
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to edit the information?", "Confirmation",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                editLbl.setText("Edit Profile");
                Edit.setText("Cancel");
                Save.setEnabled(true);
                change.setEnabled(true);
                editable(true);
            }
        } else {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel your editting?", "Confirmation",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                editLbl.setText("Profile");
                Edit.setText("Edit");
                Save.setEnabled(false);
                change.setEnabled(false);
                editable(false);
                try {
                    resident = client.getResidentInfo(residentId);
                    lName.setText(resident.getLast_name());
                    fName.setText(resident.getFirst_name());
                    mName.setText(resident.getMiddle_name());
                    gender.setSelectedItem(resident.getGender());

                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(resident.getBirthdate());
                    Date d = birthdate.getDateFormat().parse(birthdate.getDateFormat().format(date));
                    Calendar cal  = new GregorianCalendar();
                    cal.setTime(d);

                    birthdate.setDefaultPeriods(new datechooser.model.multiple.PeriodSet(
                            new datechooser.model.multiple.Period(cal,cal)));

                    email.setText(resident.getEmail());
                    mobileNo.setText(resident.getMobile_number());
                    address.setText(resident.getAddress());
                    zipCode.setText(resident.getZipcode());

                    displayGadgetTable(residentId);
                    displayFurnitureTable(residentId);
                    nameLabel.setText(resident.getFirst_name() + " " + resident.getLast_name());

                    currentProf = resident.getPicture();
                    if(currentProf != null){
                        InputStream in = new ByteArrayInputStream(currentProf);
                        BufferedImage img = ImageIO.read(in);
                        if(img != null){
                            Image image = img.getScaledInstance(218, 204, Image.SCALE_SMOOTH);
                            profilePic.setIcon(new ImageIcon(image));
                        }
                    }
                    fatherName.setText(resident.getFatherName());
                    fatherContact.setText(resident.getFatherContact());
                    fatherEmail.setText(resident.getFatherEmail());
                    fatherLandline.setText(resident.getFatherLandLine());
                    motherName.setText(resident.getMotherName());
                    motherContact.setText(resident.getMotherContact());
                    motherEmail.setText(resident.getEmail());
                    motherLandline.setText(resident.getMotherLandLine());
                    guardianName.setText(resident.getGuardianName());
                    guardianAddress.setText(resident.getGuardianAddress());
                    guardianContact.setText(resident.getGuardianContact());
                    guardianRelation.setText(resident.getGuardianRelation());
                } catch (SQLException | ParseException | RemoteException ex) {
//                    Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(this, ex.getMessage());
                } catch (IOException ex) {
//                    Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(this, ex.getMessage());
                } catch (IncompatibleDataExeption ex) {
//                    Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(this, ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_EditActionPerformed

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        if (md.confirmationSave(this) == md.YES) {
            try {
                ArrayList<String> residentDetails = new ArrayList<>();
                residentDetails.add(birthdate.getText());//0
                residentDetails.add(residentId.trim());//1
                residentDetails.add(lName.getText());//2
                residentDetails.add(fName.getText());//3
                residentDetails.add(mName.getText());//4
                residentDetails.add(address.getText());//5
                residentDetails.add(mobileNo.getText());//6
                residentDetails.add(email.getText());//7
                residentDetails.add(gender.getSelectedItem().toString());//8
                residentDetails.add(zipCode.getText());//9
                residentDetails.add(file);//10
                residentDetails.add(fatherContact.getText());//11
                residentDetails.add(fatherEmail.getText());//12
                residentDetails.add(fatherLandline.getText());//13
                residentDetails.add(motherContact.getText());//14
                residentDetails.add(motherEmail.getText());//15
                residentDetails.add(motherLandline.getText());//16
                residentDetails.add(guardianName.getText());//17
                residentDetails.add(guardianAddress.getText());//18
                residentDetails.add(guardianContact.getText());//19
                residentDetails.add(guardianRelation.getText());//20
                
                if(file != null){
                    currentProf = Files.readAllBytes(new File(file).toPath());
                }
                ResidentImpl res = new ResidentImpl();
                res.setPicture(currentProf);
                res.setId(residentId.trim());

                if(checker3() && checker() && checker2()){
                    if (client.updateResident(residentDetails, birthdate.getDateFormat()) && client.updateResidentPhoto(res)) {
                        md.successful(this);
                        editLbl.setText("Profile");
                        Edit.setText("Edit");
                        Edit.setEnabled(true);
                        Save.setEnabled(false);
                        change.setEnabled(false);
                        editable(false);
                    } else {
                        md.unsuccessful(this);
                    }
                } else {
                    md.unsuccessful(this);
                }
            } catch (RemoteException ex) {
                md.error(this, ex.getMessage());
//                Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                md.error(this, ex.getMessage());
//                Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_SaveActionPerformed

    private void changeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeActionPerformed
        FileNameExtensionFilter filter = new FileNameExtensionFilter("jpeg and png files", "jpg", "png");
        chooser.setFileFilter(filter);
        chooser.showOpenDialog(null);
        if (chooser.getSelectedFile() != null) {
            try {
                file = chooser.getSelectedFile().toString();
                BufferedImage img = ImageIO.read(new File(file));
                Image image = img.getScaledInstance(profilePic.getWidth(), profilePic.getHeight(), Image.SCALE_SMOOTH);
                profilePic.setIcon(new ImageIcon(image));
            } catch (IOException ex) {
                md.error(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_changeActionPerformed

    private void addButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addButtonMouseClicked
        modelRight =(DefaultTableModel) dormFurnitureTable.getModel();
        if(furnitureItemTable.getSelectedRowCount() > 0){
            for(int index : furnitureItemTable.getSelectedRows()){
                Object[] row = new Object[3];
                row[0] = furnitureItemTable.getValueAt(index, 0);
                row[1] = furnitureItemTable.getValueAt(index, 1);
                row[2] = furnitureItemTable.getValueAt(index, 2);
                try {
                    if(!client.updateFurniture(row[2].toString(), residentId)){
                        md.error(this, "Update Error");
                    }
                } catch (RemoteException ex) {
//                    Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(this, ex.getMessage());
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
    }//GEN-LAST:event_addButtonMouseClicked

    private void removeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeButtonMouseClicked
        // TODO add your handling code here:
        modelLeft =(DefaultTableModel) furnitureItemTable.getModel();
        if(dormFurnitureTable.getSelectedRowCount() > 0){
            for(int index : dormFurnitureTable.getSelectedRows()){
                Object[] row = new Object[3];
                row[0] = dormFurnitureTable.getValueAt(index, 0);
                row[1] = dormFurnitureTable.getValueAt(index, 1);
                row[2] = dormFurnitureTable.getValueAt(index, 2);
                try {
                    if(!client.updateFurnitureToNull(row[2].toString())){
                        md.error(this, "Update Error");
                    }
                } catch (RemoteException ex) {
//                    Logger.getLogger(FurnitureList.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(this, ex.getMessage());
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
    }//GEN-LAST:event_removeButtonMouseClicked

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        this.dispose();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void gadgetSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_gadgetSpinnerStateChanged
        add.setText("Save");
        int row = Integer.parseInt("" + gadgetSpinner.getValue());
        tableModel =(DefaultTableModel) gadgetInventoryTable.getModel();
        if(gadgetInventoryTable.getRowCount() > row){
            tableModel.removeRow(gadgetInventoryTable.getRowCount()-1);
        } else {
            tableModel.addRow(new Object[]{
                false, "", "", "", "", "", ""
            });
        }
        
        tableModel.setNumRows(row);
        gadgetInventoryTable.setModel(tableModel);
    }//GEN-LAST:event_gadgetSpinnerStateChanged

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want add a gadget?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (add.getText().equalsIgnoreCase("Add")) {
                    numberOfGadgetLabel.setVisible(true);
                    gadgetSpinner.setVisible(true);
                    gadgetInventoryTable.setModel(new javax.swing.table.DefaultTableModel(
                            new Object[0][0],
                            new String[]{
                                "", "Item Name", "Description", "Serial Number", "Voltage", "Wattage", "Monthly Charge"
                            }
                    ) {
                        Class[] types = new Class[]{
                            java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
                        };
                        boolean[] canEdit = new boolean[]{
                            false, true, true, true, true, true, true
                        };

                        public Class getColumnClass(int columnIndex) {
                            return types[columnIndex];
                        }

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return canEdit[columnIndex];
                        }
                    });
                    gadgetInventoryScrollPane.setViewportView(gadgetInventoryTable);
                    if (gadgetInventoryTable.getColumnModel().getColumnCount() > 0) {
                        gadgetInventoryTable.getColumnModel().getColumn(0).setMinWidth(20);
                        gadgetInventoryTable.getColumnModel().getColumn(0).setPreferredWidth(20);
                        gadgetInventoryTable.getColumnModel().getColumn(0).setMaxWidth(20);
                    }
                    index = 1;
                    cancel.setEnabled(true);
                    tableModel = (DefaultTableModel) gadgetInventoryTable.getModel();
                    tableModel.addRow(new Object[]{
                        false, "", "", "", "", "", ""
                    });
                    add.setText("Save");
                    edit.setEnabled(false);
                    delete.setEnabled(false);
                    gadgetSpinner.setValue(1);
                } else {
                    test = true;
                    tableModel = (DefaultTableModel) gadgetInventoryTable.getModel();

                    for (int count = 0; count < tableModel.getRowCount(); count++) {
                        ArrayList<String> gadgetInfo = new ArrayList<>();
                        gadgetInfo.add(tableModel.getValueAt(count, 1).toString());
                        gadgetInfo.add(tableModel.getValueAt(count, 2).toString());
                        gadgetInfo.add(tableModel.getValueAt(count, 3).toString());
                        gadgetInfo.add(tableModel.getValueAt(count, 4).toString());
                        gadgetInfo.add(tableModel.getValueAt(count, 5).toString());
                        
                        if(!tableModel.getValueAt(count, 6).toString().isEmpty()){
                            gadgetInfo.add(tableModel.getValueAt(count, 6).toString());
                        } else {
                            gadgetInfo.add(null);
                        }
                        if (!client.insertGadget(gadgetInfo, residentId)) {
                            md.unsuccessful(this);
                            test = false;
                            break;
                        }
                    }
                    if(test){
                        md.successful(this);
                        displayGadgetTable(residentId);
                        add.setText("Add");
                        edit.setEnabled(false);
                        add.setEnabled(false);
                        delete.setEnabled(false);
                        numberOfGadgetLabel.setVisible(false);
                        gadgetSpinner.setVisible(false);
                    }
                }
            } catch (RemoteException ex) {
                md.error(this, ex.getMessage());
//                Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_addActionPerformed

    private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want change information?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (edit.getText().equalsIgnoreCase("Edit")) {
                    tableModel = (DefaultTableModel) gadgetInventoryTable.getModel();
                    Object[][] table = new Object[tableModel.getRowCount()][7];
                    for (int count = 0; count < tableModel.getRowCount(); count++) {
                        table[count][0] = null;
                        table[count][1] = tableModel.getValueAt(count, 1);
                        table[count][2] = tableModel.getValueAt(count, 2);
                        table[count][3] = tableModel.getValueAt(count, 3);
                        table[count][4] = tableModel.getValueAt(count, 4);
                        table[count][5] = tableModel.getValueAt(count, 5);
                        table[count][6] = tableModel.getValueAt(count, 6);
                    }
                    gadgetInventoryTable.setModel(new javax.swing.table.DefaultTableModel(
                            table,
                            new String[]{
                                "", "Item Name", "Description", "Serial Number", "Voltage", "Wattage", "Monthly Charge"
                            }
                    ) {
                        Class[] types = new Class[]{
                            java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
                        };
                        boolean[] canEdit = new boolean[]{
                            false, true, true, true, true, true, true
                        };

                        public Class getColumnClass(int columnIndex) {
                            return types[columnIndex];
                        }

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return canEdit[columnIndex];
                        }
                    });
                    gadgetInventoryScrollPane.setViewportView(gadgetInventoryTable);
                    if (gadgetInventoryTable.getColumnModel().getColumnCount() > 0) {
                        gadgetInventoryTable.getColumnModel().getColumn(0).setMinWidth(20);
                        gadgetInventoryTable.getColumnModel().getColumn(0).setPreferredWidth(20);
                        gadgetInventoryTable.getColumnModel().getColumn(0).setMaxWidth(20);
                    }
                    edit.setText("Save");
                    add.setEnabled(false);
                    delete.setEnabled(false);
                    index = 2;
                    cancel.setEnabled(true);
                } else {
                    test = true;
                    int res = 0;
                    tableModel = (DefaultTableModel) gadgetInventoryTable.getModel();
                    ArrayList<String> gadgetDetail = null;
                    for (int count = 0; count < tableModel.getRowCount(); count++) {
                        gadgetDetail = new ArrayList<>();
                        gadgetDetail.add(residentId);
                        gadgetDetail.add(tableModel.getValueAt(count, 1).toString());
                        gadgetDetail.add(tableModel.getValueAt(count, 2).toString());
                        gadgetDetail.add(tableModel.getValueAt(count, 3).toString());
                        gadgetDetail.add(tableModel.getValueAt(count, 4).toString());
                        gadgetDetail.add(tableModel.getValueAt(count, 5).toString());
                        gadgetDetail.add(tableModel.getValueAt(count, 6).toString());
                        gadgetDetail.add(repository.get(count));
                        if (!client.updateGadget(gadgetDetail)) {
                            md.unsuccessful(this);
                            test = false;
                            break;
                        }
                    }
                    if (test) {
                        md.successful(this);
                        edit.setText("Edit");
                        add.setEnabled(true);
                        edit.setEnabled(false);
                        add.setEnabled(false);
                        delete.setEnabled(false);
                    }
                }
            } catch (RemoteException ex) {
                md.error(this, ex.getMessage());
//                Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_editActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        if (md.confirmationCancel(this) == md.YES) {
            try {
                if (index == 1) {
                    add.setText("Add");
                } else {
                    edit.setText("Edit");
                }
                
                numberOfGadgetLabel.setVisible(false);
                gadgetSpinner.setVisible(false);
                displayGadgetTable(residentId);
                tableModel = (DefaultTableModel) gadgetInventoryTable.getModel();
                Object[][] table = new Object[tableModel.getRowCount()][7];
                for (int count = 0; count < tableModel.getRowCount(); count++) {
                    table[count][0] = null;
                    table[count][1] = tableModel.getValueAt(count, 1);
                    table[count][2] = tableModel.getValueAt(count, 2);
                    table[count][3] = tableModel.getValueAt(count, 3);
                    table[count][4] = tableModel.getValueAt(count, 4);
                    table[count][5] = tableModel.getValueAt(count, 5);
                    table[count][6] = tableModel.getValueAt(count, 6);
                }
                gadgetInventoryTable.setModel(new javax.swing.table.DefaultTableModel(
                        table,
                        new String[]{
                            "", "Item Name", "Description", "Serial Number", "Voltage", "Wattage", "Monthly Charge"
                        }
                ) {
                    Class[] types = new Class[]{
                        java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
                    };
                    boolean[] canEdit = new boolean[]{
                        true, false, false, false, false, false, false
                    };

                    public Class getColumnClass(int columnIndex) {
                        return types[columnIndex];
                    }

                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return canEdit[columnIndex];
                    }
                });
                gadgetInventoryScrollPane.setViewportView(gadgetInventoryTable);
                if (gadgetInventoryTable.getColumnModel().getColumnCount() > 0) {
                    gadgetInventoryTable.getColumnModel().getColumn(0).setMinWidth(20);
                    gadgetInventoryTable.getColumnModel().getColumn(0).setPreferredWidth(20);
                    gadgetInventoryTable.getColumnModel().getColumn(0).setMaxWidth(20);
                }
                cancel.setEnabled(false);
            } catch ( RemoteException ex) {
                md.error(this, ex.getMessage());
//                Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            add.setEnabled(true);
            edit.setEnabled(true);
            delete.setEnabled(true);
        }
    }//GEN-LAST:event_cancelActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        if (md.confirmationDelete(this) == md.YES) {
            try {
                test = true;
                tableModel = (DefaultTableModel) gadgetInventoryTable.getModel();
                for (int count = tableModel.getRowCount(); count > 0; count--) {
                    if(tableModel.getValueAt(count-1, 0) != null){
                        if ((Boolean) tableModel.getValueAt(count-1, 0)) {
                            GadgetImpl gad = new GadgetImpl();
                            gad.setItem_name(tableModel.getValueAt(count-1, 1).toString());
                            gad.setDescription(tableModel.getValueAt(count-1, 2).toString());
                            gad.setSerial_number(tableModel.getValueAt(count-1, 3).toString());
                            if (!client.deleteGadget(residentId, gad)) {
                                md.unsuccessful(this);
                                test = false;
                                break;
                            }
                        }
                    }
                }
                if (test) {
                    displayGadgetTable(residentId);
                    md.successful(this);
                }
            } catch (RemoteException ex) {
//                Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
                md.error(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_deleteActionPerformed

    private void birthdateOnCommit(datechooser.events.CommitEvent evt) {//GEN-FIRST:event_birthdateOnCommit
        try {
            // TODO add your handling code here:
            Date bd = birthdate.getDateFormat().parse(birthdate.getText());
            if(bd.after(Calendar.getInstance().getTime())){
                md.error(this, "Birthdate can't be later than the date today.");
                Calendar cal = Calendar.getInstance();
                cal.setTime(lastDate);
                birthdate.setSelectedDate(cal);
                birthdate.setDefaultPeriods(new PeriodSet(new Period(cal,cal)));
            } else {
                lastDate = birthdate.getDateFormat().parse(birthdate.getText());
            }
        } catch (ParseException ex) {
//            Logger.getLogger(Registration_Tenant.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        } catch (IncompatibleDataExeption ex) {
//            Logger.getLogger(viewProfile.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }//GEN-LAST:event_birthdateOnCommit

    private void editable(boolean set) {
        lName.setEditable(set);
        fName.setEditable(set);
        mName.setEditable(set);
        gender.setEnabled(set);
        email.setEditable(set);
        birthdate.setEnabled(set);
        mobileNo.setEditable(set);
        address.setEditable(set);
        zipCode.setEditable(set);
        fatherContact.setEditable(set);
        fatherEmail.setEditable(set);
        fatherLandline.setEditable(set);
        motherContact.setEditable(set);
        motherEmail.setEditable(set);
        motherLandline.setEditable(set);
        guardianName.setEditable(set);
        guardianAddress.setEditable(set);
        guardianContact.setEditable(set);
        guardianRelation.setEditable(set);
    }
    
    private boolean checker() {
        boolean tester = false;
        if (mobileNumberChecker() 
                & mobileNumberChecker1()
                & mobileNumberChecker2()
                & mobileNumberChecker3()
                & emailAddressChecker1()
                & emailAddressChecker2()) {
            tester = true;
        } else {
            tester = false;
        }
        return tester;
    }
    
    private boolean mobileNumberChecker() {
        String str = mobileNo.getText().trim();
        char[] chars = str.toCharArray();
        boolean tester = true;

        if (!str.equals("")) {//checks if the number is alphanumeric
            for (int i = 0; i < chars.length; i++) {
                try {
                    Integer.parseInt(String.valueOf(chars[i]));
                } catch (NumberFormatException nfe) {
                    md.warning(this, "Not a Number");
                    mobileNo.setText("");
                    return false;
                }
            }
            //checks if the number is 11 digits.
            //if it is null
            if (str.length() != 11) {

                md.warning(this, "The mobile number must be 11 digits.");
                mobileNo.setText("");
                return false;
            }
        } else {
            md.warning(this, "Mobile number must not be empty.");
            return false;
        }
        return tester;
    }
    
    private boolean mobileNumberChecker1() {
        String str = fatherContact.getText().trim();
        char[] chars = str.toCharArray();
        boolean tester = true;

        if (!str.equals("")) {//checks if the number is alphanumeric
            for (int i = 0; i < chars.length; i++) {
                try {
                    Integer.parseInt(String.valueOf(chars[i]));
                } catch (NumberFormatException nfe) {
                    md.warning(this, "Not a Number");
                    fatherContact.setText("");
                    return false;
                }
            }
            //checks if the number is 11 digits.
            //if it is null
            if (str.length() != 11) {

                md.warning(this, "The mobile number must be 11 digits.");
                fatherContact.setText("");
                return false;
            }
        }
        return tester;
    }
    
    private boolean mobileNumberChecker2() {
        String str = motherContact.getText().trim();
        char[] chars = str.toCharArray();
        boolean tester = true;

        if (!str.equals("")) {//checks if the number is alphanumeric
            for (int i = 0; i < chars.length; i++) {
                try {
                    Integer.parseInt(String.valueOf(chars[i]));
                } catch (NumberFormatException nfe) {
                    md.warning(this, "Not a Number");
                    motherContact.setText("");
                    return false;
                }
            }
            //checks if the number is 11 digits.
            //if it is null      
            if (str.length() != 11) {

                md.warning(this, "The mobile number must be 11 digits.");
                motherContact.setText("");
                return false;
            }
        }
        return tester;
    }
    
    private boolean mobileNumberChecker3() {
        String str = guardianContact.getText().trim();
        char[] chars = str.toCharArray();
        boolean tester = true;

        if (!str.equals("")) {//checks if the number is alphanumeric
            for (int i = 0; i < chars.length; i++) {
                try {
                    Integer.parseInt(String.valueOf(chars[i]));
                } catch (NumberFormatException nfe) {
                    md.warning(this, "Not a Number");
                    guardianContact.setText("");
                    return false;
                }
            }
            //checks if the number is 11 digits.
            //if it is null      
            if (str.length() != 11) {
                md.warning(this, "The mobile number must be 11 digits.");
                guardianContact.setText("");
                return false;
            }
        }
        return tester;
    }

    private boolean emailAddressChecker1() {
        char atSymbol = '@';
        String dotCom = ".com";
        String emailAddress = fatherEmail.getText().trim();
        boolean tester = true;
        //find the @ symbol
        int atpos = emailAddress.indexOf(atSymbol);
        //find the .com
        int emadd = emailAddress.indexOf(dotCom, atpos);             
        if (!emailAddress.equals("")) {
            if (emadd == -1) {
                tester = false;
                fatherEmail.setText("");
                md.warning(this, "Invalid father email address.");
            }
        }
        return tester;
    }
    
    private boolean emailAddressChecker2() {
        char atSymbol = '@';
        String dotCom = ".com";
        String emailAddress = motherEmail.getText().trim();
        boolean tester = true;
        //find the @ symbol
        int atpos = emailAddress.indexOf(atSymbol);
        //find the .com
        int emadd = emailAddress.indexOf(dotCom, atpos);             
        if (!emailAddress.equals("")) {
            if (emadd == -1) {
                tester = false;
                motherEmail.setText("");
                md.warning(this, "Invalid mother email address.");
            }
        }
        return tester;
    }

    private boolean checker2() {
        if (!guardianAddress.getText().trim().isEmpty()
                || !guardianContact.getText().trim().isEmpty()
                || !guardianRelation.getText().trim().isEmpty()) {
            if (guardianName.getText().trim().isEmpty()) {
                md.warning(this, "Guardian name must not empty.");
                guardianName.setText("");
                return false;
            }
        }
        return true;
    }
    
    private boolean checker3() {
        if((lName.getText().trim().isEmpty() || fName.getText().trim().isEmpty()) && nameChecker()){
            md.warning(this, "First and last name must not empty.");
            return false;
        }
        return true;
    }
    
    
    
    private boolean nameChecker(){
        if(fName.getText().contains(",") || lName.getText().contains(",") || mName.getText().contains(",")){
            md.error(this, "Check Comma (,) in the fields Last Name, First Name, and Middle Name");
            return false;
        }
        return true;
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
            java.util.logging.Logger.getLogger(viewProfile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(viewProfile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(viewProfile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(viewProfile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new viewProfile(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Edit;
    private javax.swing.JButton Save;
    private javax.swing.JButton add;
    private javax.swing.JButton addButton;
    private javax.swing.JTextArea address;
    private datechooser.beans.DateChooserCombo birthdate;
    private javax.swing.JButton cancel;
    private javax.swing.JButton change;
    private javax.swing.JFileChooser chooser;
    private javax.swing.JButton delete;
    private javax.swing.JTable dormFurnitureTable;
    private javax.swing.JButton edit;
    private javax.swing.JLabel editLbl;
    private javax.swing.JTextField email;
    private javax.swing.JTextField fName;
    private javax.swing.JTextField fatherContact;
    private javax.swing.JTextField fatherEmail;
    private javax.swing.JTextField fatherLandline;
    private javax.swing.JLabel fatherName;
    private javax.swing.JTable furnitureItemTable;
    private javax.swing.JScrollPane gadgetInventoryScrollPane;
    private javax.swing.JTable gadgetInventoryTable;
    private javax.swing.JSpinner gadgetSpinner;
    private javax.swing.JComboBox gender;
    private javax.swing.JTextArea guardianAddress;
    private javax.swing.JTextField guardianContact;
    private javax.swing.JTextField guardianName;
    private javax.swing.JTextField guardianRelation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField lName;
    private javax.swing.JPanel lblPanel;
    private javax.swing.JTextField mName;
    private javax.swing.JTextField mobileNo;
    private javax.swing.JTextField motherContact;
    private javax.swing.JTextField motherEmail;
    private javax.swing.JTextField motherLandline;
    private javax.swing.JLabel motherName;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel numberOfGadgetLabel;
    private javax.swing.JLabel profilePic;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextField zipCode;
    // End of variables declaration//GEN-END:variables
}
