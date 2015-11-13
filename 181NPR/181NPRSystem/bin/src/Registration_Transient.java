import datechooser.model.exeptions.IncompatibleDataExeption;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
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
public class Registration_Transient extends javax.swing.JFrame {
    
    private final NPRInterface client;
    private DefaultTableModel modelTop;
    private String id = "";
    private String tName = "";
    private String[] guestNames;
    private String lastPage = "";
    private TransientImpl transientDetail;
    private String separator = ":";
    private String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    private final String user;
    private final IFloorPlanPreview ifpp;
    private double perHead = 0.0;
    private double bedCharge = 0.0;
    private double rPerHead = 0.0;
    private double rBedCharge = 0.0;
    private double fPerHead = 0.0;
    private double fBedCharge = 0.0;
    private double sPerHead = 0.0;
    private double sBedCharge = 0.0;
    private int iterate = 0;
    private final MessageDialog md = new MessageDialog();
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private String arrDate;
    private String depDate;
    private boolean isFromTransient = false;

    /**
     * Creates new form TransientTemp
     *
     * @param client
     * @param lastPage
     * @param user
     */
    public Registration_Transient(NPRInterface client, String lastPage, String user) {
        this.lastPage = lastPage;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Registration.png")));
        initComponents();
        this.client = client;
        this.user = user;
        ifpp = new IFloorPlanPreview(reservedRoomTextField, roomReservedTextField, this.client);
        saveButton.setEnabled(false);
        clearButton.setEnabled(false);
//        PersonTextField.setEditable(false);
//        bedChargeTextField.setEditable(false);
        
        Calendar nextDay = Calendar.getInstance();
        nextDay.add(Calendar.DATE, 1);
        dateDepartureChooserCombo.setSelectedDate(nextDay);
        
        arrDate = dateArrivalChooserCombo.getDateFormat().format(Calendar.getInstance().getTime());
        depDate = dateDepartureChooserCombo.getDateFormat().format(nextDay.getTime());
        lastAmountPaid.setVisible(false);
        jLabel31.setVisible(false);
        clearButton.setEnabled(false);
        
        
    }

    public Registration_Transient(NPRInterface client, DefaultTableModel tableModel, String user) {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Registration.png")));
        initComponents();
        this.client = client;
        this.user = user;
        ifpp = new IFloorPlanPreview(reservedRoomTextField, roomReservedTextField, this.client);
        modelTop = tableModel;
        saveButton.setEnabled(false);
        clearButton.setEnabled(false);
        arrDate = dateArrivalChooserCombo.getDateFormat().format(Calendar.getInstance().getTime());
        depDate = dateArrivalChooserCombo.getDateFormat().format(Calendar.getInstance().getTime());
        lastAmountPaid.setVisible(false);
        jLabel31.setVisible(false);
        clearButton.setEnabled(false);
    }

    public Registration_Transient(NPRInterface client, String transientIdnum, String lastPage, String user) throws NullPointerException {
        this.lastPage = lastPage;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Registration.png")));
        initComponents();
//        jLabel36.setVisible(false);
//        discount.setVisible(false);
        arrDate = dateArrivalChooserCombo.getDateFormat().format(Calendar.getInstance().getTime());
        depDate = dateArrivalChooserCombo.getDateFormat().format(Calendar.getInstance().getTime());
        id = transientIdnum;
        this.client = client;
        this.user = user;
        this.isFromTransient = true;
        ifpp = new IFloorPlanPreview(reservedRoomTextField, roomReservedTextField, this.client);
        saveButton.setEnabled(false);
        try {
            if (client.isTransientThere(transientIdnum)) {
                TransientImpl transientInfo = client.getTransient(transientIdnum);
                lastNameTextField.setEditable(false);
                lastNameTextField.setEnabled(false);
                lastNameTextField.setText(transientInfo.getLast_name());
                firstNameTextField.setEditable(false);
                firstNameTextField.setEnabled(false);
                firstNameTextField.setText(transientInfo.getFirst_name());
                mobileNoTextField.setEditable(false);
                mobileNoTextField.setEnabled(false);
                mobileNoTextField.setText(transientInfo.getMobile_number());
                addressTextField.setEditable(false);
                addressTextField.setEnabled(false);
                addressTextField.setText(transientInfo.getAddress());
                emailTextField.setEditable(false);
                emailTextField.setEnabled(false);
                emailTextField.setText(transientInfo.getEmail());
                residentNameCheckBox.setSelected(false);
                residentNameCheckBox.setEnabled(false);
                parentCheckBox.setSelected(false);
                parentCheckBox.setEnabled(false);
                jLabel7.setEnabled(false);
                PersonTextField.setText(transientInfo.getChargePerson());
                bedChargeTextField.setText(transientInfo.getBedCharge());
                totalAmountTextField.setText(transientInfo.getTotalAmount());
                discount.setText(df.format(Double.parseDouble(transientInfo.getDiscount())));
                if(transientInfo.getAmountPaid().isEmpty()){
                    lastAmountPaid.setText("0.00");
                } else {
                    lastAmountPaid.setText(df.format(Double.parseDouble(transientInfo.getAmountPaid())));
                }
                double bal = Double.parseDouble(transientInfo.getBalance());
                if(bal < 0){
                    totalAmountTextField.setText("( "+df.format(bal * -1 ) + " )");
                } else {
                    totalAmountTextField.setText(df.format(bal));
                }
                reservedRoomTextField.setText(transientInfo.getNumberOfRooms());
                roomReservedTextField.setText(transientInfo.getReservedRooms());
                daysTextField.setText(transientInfo.getTotalDays());

                if(!transientInfo.getExtraBeds().isEmpty()){
                    bedSpinner.setValue(Integer.parseInt(transientInfo.getExtraBeds()));
                } else {
                    bedSpinner.setValue(0);
                }

                DateFormat dateo = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date dateout = dateo.parse(transientInfo.getArrival());
                Calendar calout = Calendar.getInstance();
                calout.setTime(dateout);
                dateArrivalChooserCombo.setSelectedDate(calout);
                dateArrivalChooserCombo.setEnabled(false);
                arrDate = dateArrivalChooserCombo.getText();

                DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date dateout1 = date1.parse(transientInfo.getDeparture());
                Calendar calout1 = Calendar.getInstance();
                calout1.setTime(dateout1);
                dateDepartureChooserCombo.setSelectedDate(calout1);
                depDate = dateDepartureChooserCombo.getText();

                try {
                    residentNameCheckBox.setSelected(false);
                    residentComboBox.addItem(client.getResidentName(transientIdnum));

                    relationComboBox.setSelectedItem(client.getRelationToResident(transientIdnum));
                    relationComboBox.setEnabled(false);
                } catch (NullPointerException e) {
                    residentNameCheckBox.setSelected(false);
                    residentComboBox.setEnabled(false);
                    relationComboBox.setEnabled(false);
                }
                guestSpinner.setValue(Integer.parseInt(transientInfo.getNoAdditionalGuest()));
                DefaultTableModel tableModel = (DefaultTableModel) guestTable.getModel();
                tableModel.setRowCount(0);
                guestNames = transientInfo.getAdditionalGuest().split(separator);
                for (String a : guestNames) {
                    if(!a.isEmpty()){
                        tableModel.addRow(new Object[]{a.trim()});
                    }
                }
                guestTable.setModel(tableModel);
            }
            clearButton.setVisible(false);
        } catch (ParseException | NumberFormatException ex) {
            md.error(this, ex.getMessage());
            bedSpinner.setValue(0);
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
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

        areaPanel = new javax.swing.JPanel();
        amountPanel = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        totalAmountTextField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        balanceTextField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        amountPaidTextField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel31 = new javax.swing.JLabel();
        lastAmountPaid = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        discount = new javax.swing.JTextField();
        infoPanel = new javax.swing.JPanel();
        lastNameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        firstNameTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        mobileNoTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();
        residentNameCheckBox = new javax.swing.JCheckBox();
        relationComboBox = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        residentComboBox = new javax.swing.JComboBox();
        parentCheckBox = new javax.swing.JCheckBox();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        stayPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        daysTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        reservedRoomTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        roomReservedTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        PersonTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        bedChargeTextField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        guestSpinner = new javax.swing.JSpinner();
        bedSpinner = new javax.swing.JSpinner();
        dateArrivalChooserCombo = new datechooser.beans.DateChooserCombo();
        dateDepartureChooserCombo = new datechooser.beans.DateChooserCombo();
        jScrollPane2 = new javax.swing.JScrollPane();
        guestTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        add = new javax.swing.JLabel();
        reg = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        areaPanel.setBackground(new java.awt.Color(255, 255, 255));
        areaPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        areaPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        amountPanel.setBackground(new java.awt.Color(255, 255, 255));
        amountPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total Amount", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N
        amountPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel18.setText("Total Amount");
        amountPanel.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, -1, -1));

        totalAmountTextField.setEditable(false);
        totalAmountTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalAmountTextField.setText("0.00");
        totalAmountTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                totalAmountTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                totalAmountTextFieldFocusLost(evt);
            }
        });
        amountPanel.add(totalAmountTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, 110, -1));

        jLabel19.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel19.setText("Amount Paid");
        amountPanel.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 130, -1, -1));

        balanceTextField.setEditable(false);
        balanceTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        balanceTextField.setText("0.00");
        amountPanel.add(balanceTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 170, 110, -1));

        jLabel20.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel20.setText("Balance");
        amountPanel.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, -1, -1));

        amountPaidTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        amountPaidTextField.setText("0.00");
        amountPaidTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                amountPaidTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                amountPaidTextFieldFocusLost(evt);
            }
        });
        amountPanel.add(amountPaidTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, 110, -1));
        amountPanel.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 210, 10));

        jLabel31.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel31.setText("Last Amount Paid");
        amountPanel.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        lastAmountPaid.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lastAmountPaid.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lastAmountPaid.setText("0.00");
        amountPanel.add(lastAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 110, 20));

        jLabel36.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel36.setText("Discount");
        amountPanel.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 100, -1, -1));

        discount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        discount.setText("0.00");
        discount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                discountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                discountFocusLost(evt);
            }
        });
        amountPanel.add(discount, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 110, -1));

        areaPanel.add(amountPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 400, 200));

        infoPanel.setBackground(new java.awt.Color(255, 255, 255));
        infoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transient's Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N
        infoPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lastNameTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                lastNameTextFieldCaretUpdate(evt);
            }
        });
        infoPanel.add(lastNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 286, -1));

        jLabel1.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel1.setText("Last Name");
        infoPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jLabel3.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel3.setText("First Name");
        infoPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        firstNameTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                firstNameTextFieldCaretUpdate(evt);
            }
        });
        infoPanel.add(firstNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 286, -1));

        jLabel4.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel4.setText("Mobile Number");
        infoPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        mobileNoTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                mobileNoTextFieldCaretUpdate(evt);
            }
        });
        infoPanel.add(mobileNoTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 286, -1));

        jLabel5.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel5.setText("Home Address");
        infoPanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, 20));

        addressTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                addressTextFieldCaretUpdate(evt);
            }
        });
        addressTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                addressTextFieldKeyTyped(evt);
            }
        });
        infoPanel.add(addressTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, 286, -1));

        jLabel6.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel6.setText("E-mail ");
        infoPanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 70, 20));

        emailTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                emailTextFieldCaretUpdate(evt);
            }
        });
        infoPanel.add(emailTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 286, -1));

        residentNameCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        residentNameCheckBox.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        residentNameCheckBox.setText("Resident's Name");
        residentNameCheckBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        residentNameCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                residentNameCheckBoxActionPerformed(evt);
            }
        });
        infoPanel.add(residentNameCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 125, -1));

        relationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Other", "Mother", "Father", "Brother", "Sister", "Grandmother", "Grandfather", "Aunt", "Uncle" }));
        relationComboBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        relationComboBox.setEnabled(false);
        infoPanel.add(relationComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 240, 237, -1));

        jLabel7.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel7.setText("Relation to Resident");
        jLabel7.setEnabled(false);
        infoPanel.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, -1, -1));

        residentComboBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        residentComboBox.setEnabled(false);
        residentComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                residentComboBoxItemStateChanged(evt);
            }
        });
        infoPanel.add(residentComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 200, 241, -1));

        parentCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        parentCheckBox.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        parentCheckBox.setText("Same room with resident");
        parentCheckBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        parentCheckBox.setEnabled(false);
        parentCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentCheckBoxActionPerformed(evt);
            }
        });
        infoPanel.add(parentCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 150, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 0, 0));
        jLabel22.setText("*");
        infoPanel.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 0, 0));
        jLabel25.setText("*");
        infoPanel.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 0, 0));
        jLabel26.setText("*");
        infoPanel.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        areaPanel.add(infoPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 400, 320));

        stayPanel.setBackground(new java.awt.Color(255, 255, 255));
        stayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stay Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N
        stayPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel8.setText("Date of Arrival");
        stayPanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, 120, 20));

        jLabel9.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel9.setText("Date of Checkout");
        stayPanel.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 130, 20));

        jLabel10.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel10.setText("Total Number of Days");
        stayPanel.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 179, -1, -1));

        daysTextField.setEditable(false);
        daysTextField.setBackground(new java.awt.Color(255, 255, 255));
        daysTextField.setText("1");
        daysTextField.setEnabled(false);
        daysTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                daysTextFieldCaretUpdate(evt);
            }
        });
        stayPanel.add(daysTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 176, 64, -1));

        jLabel11.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel11.setText("Number of Reserved Rooms");
        stayPanel.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, 140, 20));

        reservedRoomTextField.setEditable(false);
        reservedRoomTextField.setBackground(new java.awt.Color(255, 255, 255));
        reservedRoomTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                reservedRoomTextFieldCaretUpdate(evt);
            }
        });
        stayPanel.add(reservedRoomTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, 248, -1));

        jLabel12.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel12.setText("Reserved Room Number/s");
        stayPanel.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, -1, -1));

        roomReservedTextField.setEditable(false);
        roomReservedTextField.setBackground(new java.awt.Color(255, 255, 255));
        roomReservedTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                roomReservedTextFieldCaretUpdate(evt);
            }
        });
        stayPanel.add(roomReservedTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, 210, -1));

        jLabel13.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel13.setText("No. of Additional Guest/s");
        stayPanel.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        jLabel14.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel14.setText("Names of Additional Guest/s :  ");
        stayPanel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, -1));

        jLabel15.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel15.setText("No. of Extra Bed/s");
        stayPanel.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, -1, -1));

        jLabel16.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel16.setText("Guest(s) Charge");
        stayPanel.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 410, -1, -1));

        PersonTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        PersonTextField.setText("0.00");
        PersonTextField.setEnabled(false);
        PersonTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                PersonTextFieldCaretUpdate(evt);
            }
        });
        PersonTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                PersonTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                PersonTextFieldFocusLost(evt);
            }
        });
        stayPanel.add(PersonTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 410, 80, -1));

        jLabel17.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel17.setText("Bed Charge");
        stayPanel.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, -1, -1));

        bedChargeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        bedChargeTextField.setText("0.00");
        bedChargeTextField.setEnabled(false);
        bedChargeTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                bedChargeTextFieldCaretUpdate(evt);
            }
        });
        bedChargeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                bedChargeTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                bedChargeTextFieldFocusLost(evt);
            }
        });
        stayPanel.add(bedChargeTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 480, 80, -1));

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Floor Plan.png"))); // NOI18N
        jLabel21.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
        });
        stayPanel.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 120, 140));

        jLabel23.setFont(new java.awt.Font("Rondalo", 0, 11)); // NOI18N
        jLabel23.setText("day/s");
        stayPanel.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 180, -1, -1));

        jLabel24.setFont(new java.awt.Font("Rondalo", 0, 11)); // NOI18N
        jLabel24.setText("guest/s");
        stayPanel.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 209, -1, -1));
        stayPanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, 201, 10));

        guestSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        guestSpinner.setEditor(new JSpinner.DefaultEditor(guestSpinner));
        guestSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                guestSpinnerStateChanged(evt);
            }
        });
        stayPanel.add(guestSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 209, 64, -1));

        bedSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        bedSpinner.setEditor(new JSpinner.DefaultEditor(bedSpinner));
        bedSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bedSpinnerStateChanged(evt);
            }
        });
        stayPanel.add(bedSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 450, 80, -1));

        dateArrivalChooserCombo.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
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
    dateArrivalChooserCombo.setNothingAllowed(false);
    dateArrivalChooserCombo.setFieldFont(new java.awt.Font("Rondalo", java.awt.Font.PLAIN, 11));
    dateArrivalChooserCombo.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
    dateArrivalChooserCombo.addCommitListener(new datechooser.events.CommitListener() {
        public void onCommit(datechooser.events.CommitEvent evt) {
            dateArrivalChooserComboOnCommit(evt);
        }
    });
    stayPanel.add(dateArrivalChooserCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 100, 248, -1));

    dateDepartureChooserCombo.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
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
dateDepartureChooserCombo.setNothingAllowed(false);
dateDepartureChooserCombo.setFieldFont(new java.awt.Font("Rondalo", java.awt.Font.PLAIN, 11));
dateDepartureChooserCombo.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
dateDepartureChooserCombo.addCommitListener(new datechooser.events.CommitListener() {
    public void onCommit(datechooser.events.CommitEvent evt) {
        dateDepartureChooserComboOnCommit(evt);
    }
    });
    stayPanel.add(dateDepartureChooserCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 130, 248, -1));

    guestTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Guest Name"
        }
    ));
    jScrollPane2.setViewportView(guestTable);

    stayPanel.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 530, 140));

    jButton1.setFont(new java.awt.Font("Rondalo", 1, 10)); // NOI18N
    jButton1.setText("-");
    jButton1.setEnabled(false);
    jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
        }
    });
    stayPanel.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(517, 70, 30, -1));

    jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
    jLabel27.setForeground(new java.awt.Color(255, 0, 0));
    jLabel27.setText("*");
    stayPanel.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, -1, -1));

    jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
    jLabel29.setForeground(new java.awt.Color(255, 0, 0));
    jLabel29.setText("*");
    stayPanel.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, -1, -1));

    jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
    jLabel30.setForeground(new java.awt.Color(255, 0, 0));
    jLabel30.setText("*");
    stayPanel.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, -1, -1));

    jLabel32.setFont(new java.awt.Font("Rondalo", 2, 10)); // NOI18N
    jLabel32.setText("NOTE:");
    stayPanel.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 410, 40, -1));

    jLabel33.setFont(new java.awt.Font("Rondalo", 0, 11)); // NOI18N
    jLabel33.setText("Private Suite Rate");
    stayPanel.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 410, -1, -1));

    jLabel34.setFont(new java.awt.Font("Rondalo", 0, 11)); // NOI18N
    jLabel34.setText("Family Rate:");
    stayPanel.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 450, 90, -1));

    jLabel35.setFont(new java.awt.Font("Rondalo", 0, 11)); // NOI18N
    jLabel35.setText("Regular Rate (2 pax):");
    stayPanel.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 430, -1, -1));

    add.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    add.setText("0.00");
    stayPanel.add(add, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 450, 100, -1));

    reg.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    reg.setText("0.00");
    stayPanel.add(reg, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 430, 100, -1));

    areaPanel.add(stayPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 30, 560, 520));

    saveButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    saveButton.setText("Save");
    saveButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    saveButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveButtonActionPerformed(evt);
        }
    });
    areaPanel.add(saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 560, 70, -1));

    cancelButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    cancelButton.setText("Back");
    cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelButtonActionPerformed(evt);
        }
    });
    areaPanel.add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 560, 70, -1));

    clearButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    clearButton.setText("Clear");
    clearButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    clearButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            clearButtonActionPerformed(evt);
        }
    });
    areaPanel.add(clearButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 560, 70, -1));

    jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/help.png"))); // NOI18N
    jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jLabel2MouseClicked(evt);
        }
    });
    areaPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 10, -1, -1));

    jLabel28.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
    jLabel28.setForeground(new java.awt.Color(255, 0, 0));
    jLabel28.setText("* Required Fields");
    areaPanel.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 560, -1, -1));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(areaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1024, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(areaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 615, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    pack();
    setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
        try {
            if (evt.getClickCount() == 2) {
                if (lastNameTextField.getText().trim().equals("") & firstNameTextField.getText().trim().equals("")) {
                    md.error(this, "Please Fill-up Necessary Informations");
                } else {
                    if(!ifpp.isVisible()){
                        ifpp.setVisible(true);
                    } else {
                        ifpp.setAlwaysOnTop(true);
                        ifpp.setAlwaysOnTop(false);
                    }

                }
            }
        } catch (NullPointerException ex) {
            if (!lastNameTextField.getText().equals("")
                    | !firstNameTextField.getText().equals("")) {
                new IFloorPlanPreview(reservedRoomTextField, roomReservedTextField, client).setVisible(true);
            } else {
                md.error(this, ex.getMessage());
            }
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jLabel21MouseClicked

    private boolean insertIntoDatabase() {
        boolean tester = true;
        try {
            if (client.isSaved(firstNameTextField.getText().trim(), lastNameTextField.getText().trim())) {
                ArrayList<String> transientInfo = new ArrayList<>();
                tName = lastNameTextField.getText() +", "+firstNameTextField.getText();
                transientInfo.add(lastNameTextField.getText());
                transientInfo.add(firstNameTextField.getText());
                transientInfo.add(mobileNoTextField.getText());
                transientInfo.add(addressTextField.getText());
                transientInfo.add(emailTextField.getText());
                
                transientInfo.add(timeStamp);

                transientInfo.add(relationComboBox.getSelectedItem().toString());
                transientInfo.add(totalAmountTextField.getText().replace(",", ""));
                transientInfo.add(amountPaidTextField.getText().replace(",", ""));
                String bal = balanceTextField.getText().replace(",", "");
                if(balanceTextField.getText().contains("(") && balanceTextField.getText().contains(")")){
                    bal ="-" + balanceTextField.getText().substring(1, balanceTextField.getText().length()-1).trim().replace(",", "");
                }
                transientInfo.add(bal);
                transientInfo.add(reservedRoomTextField.getText());
                transientInfo.add(roomReservedTextField.getText());

                DateFormat originalFormat1 = dateArrivalChooserCombo.getDateFormat();
                DateFormat targetFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate1 = originalFormat1.parse(dateArrivalChooserCombo.getText());
                String formattedDateArrival = targetFormat1.format(utilDate1);
                transientInfo.add(formattedDateArrival);

                DateFormat originalFormat2 = dateDepartureChooserCombo.getDateFormat();
                DateFormat targetFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate2 = originalFormat2.parse(dateDepartureChooserCombo.getText());
                String formattedDateDeparture = targetFormat2.format(utilDate2);
                transientInfo.add(formattedDateDeparture);

                transientInfo.add(daysTextField.getText());
                transientInfo.add(guestSpinner.getValue().toString());
                String names = "";
                modelTop = (DefaultTableModel) guestTable.getModel();
                for(int count = 0; count < modelTop.getRowCount(); count++){
                    if(names.isEmpty()){
                        names = modelTop.getValueAt(count, 0).toString().trim();
                    } else {
                        names += (":"+modelTop.getValueAt(count, 0).toString().trim());
                    }
                }
                transientInfo.add(names);
                transientInfo.add(bedSpinner.getValue().toString());
                transientInfo.add(bedChargeTextField.getText().replace(",", ""));
                transientInfo.add(PersonTextField.getText().replace(",", ""));
                if (residentComboBox.isEnabled()) {
                    transientInfo.add(client.getResidentId(residentComboBox.getSelectedItem().toString()));
                } else {
                    transientInfo.add("");
                }
                transientInfo.add(discount.getText().replace(",", ""));
                if (client.insertTransient(transientInfo)) {
                    String[] rooms = roomReservedTextField.getText().split(",");
                    client.updateFurnitureFromTransient(client.getTransientId(tName));
                    for (String room : rooms) {
                        if (!client.updateRoomStatusFromTransient(room, "fully occupied")) {
                            md.unsuccessful(this, "Room(s) update failed!");
                            tester = false;
                            break;
                        }
                        for (int c = 0; c < client.getFurnitureRowCount(room); c++) {
                            if (!client.updateFurnitureFromTransient(client.getTransientId(tName))) {
                                md.unsuccessful(this, "Furniture(s) update failed!");
                                tester = false;
                                break;
                            }
                        }
                    }
                    clearAll();
                    dispose();
                } else {
                    md.unsuccessful(this);
                    tester = false;
                }
            } else {
                ArrayList<String> transientInfo = new ArrayList<>();
                transientInfo.add(totalAmountTextField.getText().replace(",", ""));
                transientInfo.add(amountPaidTextField.getText().replace(",", ""));
                String bal = balanceTextField.getText().replace(",", "");
                if(balanceTextField.getText().contains("(") && balanceTextField.getText().contains(")")){
                    bal ="-" + balanceTextField.getText().substring(1, balanceTextField.getText().length()-1).trim().replace(",", "");
                }
                transientInfo.add(bal);
                transientInfo.add(reservedRoomTextField.getText());
                transientInfo.add(roomReservedTextField.getText());

                transientInfo.add(timeStamp);

                DateFormat originalFormat1 = dateArrivalChooserCombo.getDateFormat();
                DateFormat targetFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate1 = originalFormat1.parse(dateArrivalChooserCombo.getText());
                String formattedDateArrival = targetFormat1.format(utilDate1);
                transientInfo.add(formattedDateArrival);

                DateFormat originalFormat2 = dateDepartureChooserCombo.getDateFormat();
                DateFormat targetFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate2 = originalFormat2.parse(dateDepartureChooserCombo.getText());
                String formattedDateDeparture = targetFormat2.format(utilDate2);
                transientInfo.add(formattedDateDeparture);

                transientInfo.add(daysTextField.getText());
                transientInfo.add(guestSpinner.getValue().toString());
                transientInfo.add(bedSpinner.getValue().toString());
                modelTop = (DefaultTableModel) guestTable.getModel();
                String names = "";
                for(int count = 0; count < modelTop.getRowCount(); count++){
                    if(names.isEmpty()){
                        names = modelTop.getValueAt(modelTop.getRowCount() - 1, 0).toString().trim();
                    } else {
                        names += (":"+modelTop.getValueAt(modelTop.getRowCount() - 1, 0).toString().trim());
                    }
                }
                transientInfo.add(names);
                transientInfo.add(PersonTextField.getText());
                transientInfo.add(bedChargeTextField.getText());
                transientInfo.add("Extend");
                transientInfo.add(discount.getText().replace(",", ""));
                if (client.updateTransient(transientInfo, id)) {
                    String[] rooms = roomReservedTextField.getText().split(",");
                    for (String room : rooms) {
                        if (!client.updateRoomStatusFromTransient(room, "fully occupied")) {
                            md.unsuccessful(this, "Room(s) update failed!");
                            tester = false;
                            break;
                        }
                        for (int c = 0; c < client.getFurnitureRowCount(room); c++) {
                            if (!client.updateFurnitureFromTransient(id)) {
                                md.unsuccessful(this, "Furniture(s) update failed!");
                                tester = false;
                                break;
                            }
                        }
                    }
                    clearAll();
                    dispose();
                } else {
                    tester = false;
                }
            }
        } catch (NumberFormatException | ParseException | RemoteException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tester;
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
        if (checker()) {
            if (md.confirmationSave(this) == md.YES) {
                saveToPdf();
                if (insertIntoDatabase()) {
                    md.successful(this);
                    ifpp.setIFPPVisible();
                    new Transient_Form(client, user).setVisible(true);
                    saveButton.setEnabled(false);
                    cancelButton.setText("Back");
                } else {
                    md.unsuccessful(this);
                }
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed
    
    private boolean checker() {
        boolean tester = false;
        //
        if (nameChecker() & checkGuestTable() & mobileNumberChecker() & checkInDateChecker() & checkArrivalDate() & (emailAddressChecker() | totalAmountNumberChecker()
                & AmountPaidNumberChecker() & chargePerGuestChecker() & bedChecker())) {
            tester = true;
        }
        return tester;
    }
    
    private boolean nameChecker(){
        if(firstNameTextField.getText().contains(",") || lastNameTextField.getText().contains(",")){
            md.error(this, "Check Comma (,) in the fields Last Name and First Name");
            return false;
        } else if(firstNameTextField.getText().trim().isEmpty() || lastNameTextField.getText().trim().isEmpty()){
            md.error(this, "First Name and Last Name are Required to fill-in.");
        }
        return true;
    }
    
    private boolean guestsChecker(){
        for(int i = 0; i< guestTable.getRowCount(); i++){
            if(guestTable.getValueAt(i, 0) == null || guestTable.getValueAt(i,0).toString().isEmpty()){
                md.error(this, "Please specify all the guest's names.");
                return false;
            }
        }
        return true;
    }

    private boolean checkInDateChecker() {
        boolean tester = true;
        try {
            Date arrivalDate = dateArrivalChooserCombo.getDateFormat().parse(dateArrivalChooserCombo.getText());
            Date departureDate = dateDepartureChooserCombo.getDateFormat().parse(dateDepartureChooserCombo.getText());
            if (arrivalDate.after(departureDate) || departureDate.before(arrivalDate)) {
                md.error(this, "Invalid check in date.");
                tester = false;
            }
        } catch (ParseException ex) {
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return tester;
    }

    private boolean checkArrivalDate() {
        boolean tester = true;
        Date arrivalDate = new Date();
        Date dateToday = null;
        try {
            arrivalDate = dateArrivalChooserCombo.getDateFormat().parse(dateArrivalChooserCombo.getText());
            dateToday = new SimpleDateFormat("yyyy-MM-dd").parse(timeStamp);
        } catch (ParseException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (arrivalDate.before(dateToday) && !lastPage.equals("transient_form")) {
            md.error(this, "Date of arrival must be greater than date today.");
            tester = false;
        }
        return tester;
    }

    private boolean checkGuestTable(){
        boolean tester = true;
        for(int i = 0; i < guestTable.getRowCount(); i ++){
            if(guestTable.getValueAt(i, 0) == null || guestTable.getValueAt(i, 0).toString().isEmpty()){
                md.error(this, "Names of the Guest(s) must be specified.");
                tester = false;
                break;
            }
        }
        return tester;
    }
    
    private boolean chargePerGuestChecker() {
        boolean tester = true;
        try {
            Double.parseDouble(PersonTextField.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            tester = false;
            md.error(this, "You've entered an alphanumeric, please try again.");
            totalAmountComputation();
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
//        String str = PersonTextField.getText().trim();
//        char[] chars = str.toCharArray();
//        boolean tester = true;
//        //if it is null                
//        if (!str.equals("")) {
//            //checks if the number is alphanumeric
//            for (int i = 0; i < chars.length; i++) {
//                try {
//                    Integer.parseInt(String.valueOf(chars[i]));
//                } catch (NumberFormatException nfe) {
//                    tester = false;
//                    JOptionPane.showMessageDialog(null, "You've entered an alphanumeric, please try again.", "Error", JOptionPane.ERROR_MESSAGE);
//                    PersonTextField.setText("");
//                }
//            }
//        }
        return tester;
    }

    private boolean bedChecker() {
        boolean tester = true;
        try {
            Double.parseDouble(bedChargeTextField.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            tester = false;
            md.error(this, "You've entered an alphanumeric, please try again.");
            totalAmountComputation();
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
//        String str = bedChargeTextField.getText().trim();
//        char[] chars = str.toCharArray();
//        boolean tester = true;
//        //if it is null                
//        if (!str.equals("")) {
//            //checks if the number is alphanumeric
//            for (int i = 0; i < chars.length; i++) {
//                try {
//                    Integer.parseInt(String.valueOf(chars[i]));
//                } catch (NumberFormatException nfe) {
//                    tester = false;
//                    JOptionPane.showMessageDialog(null, "You've entered an alphanumeric, please try again.", "Error", JOptionPane.ERROR_MESSAGE);
//                    bedChargeTextField.setText("");
//                }
//            }
//        }
        return tester;
    }

    private boolean mobileNumberChecker() {
        String str = mobileNoTextField.getText().trim();
        char[] chars = str.toCharArray();
        boolean tester = false;
        //if it is null                
        if (!str.equals("")) {
            //checks if the number is alphanumeric
            for (int i = 0; i < chars.length; i++) {
                try {
                    Integer.parseInt(String.valueOf(chars[i]));
                } catch (NumberFormatException nfe) {
                    md.error(this, "You've entered an alphanumeric, please try again.");
                    mobileNoTextField.setText("");
                    break;
                }
            }
            //checks if the number is 11 digits.
            if (str.length() == 11) {
                tester = true;
            } else {
                md.error(this, "The number you've enter is more than or less than 11 digits or\n an abroad number..");
                mobileNoTextField.setText("");
            }
        }
        return tester;
    }

    private boolean totalAmountNumberChecker() {
        boolean tester = true;
        try {
            Double.parseDouble(totalAmountTextField.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            tester = false;
            md.error(this, "You've entered an alphanumeric, please try again.");
            totalAmountComputation();
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
//        String str = totalAmountTextField.getText().trim();
//        char[] chars = str.toCharArray();
//        boolean tester = true;
//        //if it is null                
//        if (!str.equals("")) {
//            //checks if the number is alphanumeric
//            for (int i = 0; i < chars.length; i++) {
//                try {
//                    Integer.parseInt(String.valueOf(chars[i]));
//                } catch (NumberFormatException nfe) {
//                    tester = false;
//                    JOptionPane.showMessageDialog(null, "You've entered an alphanumeric, please try again.", "Error", JOptionPane.ERROR_MESSAGE);
//                    totalAmountTextField.setText("");
//                }
//            }
//        }
        return tester;
    }

    private boolean AmountPaidNumberChecker() {
        boolean tester = true;
        try {
            Double.parseDouble(amountPaidTextField.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex){
            tester = false;
            md.error(this, "You've entered an alphanumeric, please try again.");
            amountPaidTextField.setText("0");
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
//        String str = amountPaidTextField.getText().trim();
//        char[] chars = str.toCharArray();
//        boolean tester = true;
//        //if it is null                
//        if (!str.equals("")) {
//            //checks if the number is alphanumeric
//            for (int i = 0; i < chars.length; i++) {
//                try {
//                    Integer.parseInt(String.valueOf(chars[i]));
//                } catch (NumberFormatException nfe) {
//                    tester = false;
//                    JOptionPane.showMessageDialog(null, "You've entered an alphanumeric, please try again.", "Error", JOptionPane.ERROR_MESSAGE);
//                    amountPaidTextField.setText("");
//                }
//            }
//        }
        return tester;
    }

    private boolean emailAddressChecker() {
        char atSymbol = '@';
        String dotCom = ".com";
        String emailAddress = emailTextField.getText().trim();
        boolean tester = true;
        //find the @ symbol
        int atpos = emailAddress.indexOf(atSymbol);
        //find the .com
        int emadd = emailAddress.indexOf(dotCom, atpos);
        //if it is null                
        if (!emailAddress.equals("")) {
            if (emadd == -1) {
                tester = false;
                md.error(this, "Invalid email address.");
                emailTextField.setText("");
            }
        }
        return tester;
    }

    private void residentNameCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_residentNameCheckBoxActionPerformed
        residentComboBoxPreview();
        if(residentNameCheckBox.isSelected()){
            perHead = fPerHead;
            bedCharge = fBedCharge;
        } else {
            perHead = rPerHead;
            bedCharge = rBedCharge;
        }
        totalAmountComputation();
    }//GEN-LAST:event_residentNameCheckBoxActionPerformed

    private void residentComboBoxPreview() throws HeadlessException {
        residentComboBox.setEditable(false);
        try {
            ArrayList<String> names = client.getResidentNames();
            for (String name : names) {
                residentComboBox.addItem(name);
            }
            if (residentNameCheckBox.isSelected()) {
                residentComboBox.setEnabled(true);
                relationComboBox.setEnabled(true);
                parentCheckBox.setEnabled(true);

            } else {
                residentComboBox.setEnabled(false);
                residentComboBox.removeAllItems();
                relationComboBox.setEditable(false);
                relationComboBox.setEnabled(false);
                parentCheckBox.setEnabled(false);
            }
        } catch (RemoteException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void parentCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parentCheckBoxActionPerformed
        try {
            if (parentCheckBox.isSelected()) {
                
                if(roomReservedTextField.getText().isEmpty()){
                    roomReservedTextField.setText(client.getRoomNumber(residentComboBox.getSelectedItem().toString()));
                } else {
                    roomReservedTextField.setText(roomReservedTextField.getText() + ", "+client.getRoomNumber(residentComboBox.getSelectedItem().toString()));
                }

                if (reservedRoomTextField.getText().isEmpty()) {
                    reservedRoomTextField.setText("1");
                } else {
                    reservedRoomTextField.setText((Integer.parseInt(reservedRoomTextField.getText()) + 1) + "");
                }
                
                String idNum = client.getResidentId(residentComboBox.getSelectedItem().toString().trim());
                ArrayList<String> roomDetails = client.getResidentRoomDetail(idNum);
                if(roomDetails.get(2).equals("Single Room")){
                    perHead = 0.0;
                    bedCharge = 0.0;
                }
            } else {
                reservedRoomTextField.setText("");
                roomReservedTextField.setText("");
                
                perHead = fPerHead;
                bedCharge = fBedCharge;
            }
            totalAmountComputation();
        } catch (RemoteException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_parentCheckBoxActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        // TODO add your handling code here:
        clearAll();
        saveButton.setEnabled(false);
        clearButton.setEnabled(false);
    }//GEN-LAST:event_clearButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (md.confirmationCancel(this) == md.YES) {
            if (lastPage.contains("registration")) {
                ifpp.setIFPPVisible();
                new Registration(client, user).setVisible(true);
                this.dispose();
            } else {
                ifpp.setIFPPVisible();
                new Transient_Form(client, user).setVisible(true);
                this.dispose();
            }
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void furnitureChecker() throws NullPointerException {
        try {
            if (!client.removeFurniture()) {
                md.error(this, "Furniture(s) update failed!");
            }
        } catch (RemoteException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addressTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addressTextFieldKeyTyped
        // TODO add your handling code here:
        enableClearButton();
        enableButton();
    }//GEN-LAST:event_addressTextFieldKeyTyped

    private void amountPaidTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_amountPaidTextFieldFocusLost
        // TODO add your handling code here:
        try {
            if(amountPaidTextField.getText().isEmpty()){
                amountPaidTextField.setText("0.00");
            } else {
                amountPaidTextField.setText(df.format(Double.parseDouble(amountPaidTextField.getText().trim())));
                double totalAmount = Double.parseDouble(totalAmountTextField.getText().replace(",","").trim());
                double amountPaid = Double.parseDouble(amountPaidTextField.getText().replace(",","").trim());
                double lastAmount = Double.parseDouble(lastAmountPaid.getText().replace(",","").trim());
                double dscnt = Double.parseDouble(discount.getText().replace(",", "").trim());
                double balance = totalAmount - (amountPaid + lastAmount + dscnt);
                if (balance < 0) {
                    balanceTextField.setText("( "+ (balance * -1) +" )");
//                    md.error(this, "Negative number detected.");
//                    balanceTextField.setText("");
                } else {
                    balanceTextField.setText(df.format(balance) + "");
                }
            }
        } catch (NumberFormatException e) {
            md.error(this, e.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, e);
            //JOptionPane.showMessageDialog(null, "Not a number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_amountPaidTextFieldFocusLost

    private void guestSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_guestSpinnerStateChanged
        int row = Integer.parseInt("" + guestSpinner.getValue());
        modelTop = (DefaultTableModel) guestTable.getModel();
        modelTop.setNumRows(row);
        guestTable.setModel(modelTop);
        if(row == 0){
            PersonTextField.setEnabled(false);
            PersonTextField.setText("0");
        } else {
            PersonTextField.setEnabled(true);
        }
        totalAmountComputation();
    }//GEN-LAST:event_guestSpinnerStateChanged

    private void residentComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_residentComboBoxItemStateChanged
        // TODO add your handling code here:
        if(!lastPage.equals("transient_form")){
            if (parentCheckBox.isSelected()) {
                parentCheckBox.setSelected(false);
                reservedRoomTextField.setText("");
                roomReservedTextField.setText("");
            } else {
                parentCheckBox.setEnabled(true);
            }
        }
    }//GEN-LAST:event_residentComboBoxItemStateChanged

    private void totalAmountTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalAmountTextFieldFocusLost
        // TODO add your handling code here:
        if(totalAmountTextField.getText().isEmpty()){
            totalAmountTextField.setText("0.00");
        } else {
            totalAmountTextField.setText(df.format(Double.parseDouble(totalAmountTextField.getText().trim())));
        }
        balanceTextField.setText(df.format(Double.parseDouble(totalAmountTextField.getText().trim()) - Double.parseDouble(amountPaidTextField.getText().trim())) + "");
    }//GEN-LAST:event_totalAmountTextFieldFocusLost

    private void totalAmountTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalAmountTextFieldFocusGained
        // TODO add your handling code here:
        if(totalAmountTextField.getText().equals("0.00")){
            totalAmountTextField.setText("");
        }
    }//GEN-LAST:event_totalAmountTextFieldFocusGained

    private void amountPaidTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_amountPaidTextFieldFocusGained
        // TODO add your handling code here:
        if(amountPaidTextField.getText().equals("0.00")){
            amountPaidTextField.setText("");
        }
    }//GEN-LAST:event_amountPaidTextFieldFocusGained

    private void bedSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bedSpinnerStateChanged
        // TODO add your handling code here:
        int value = Integer.parseInt(bedSpinner.getValue().toString());
//        if (value > 0) {
//            bedChargeTextField.setEditable(true);
//            bedChargeTextField.setEnabled(true);
//        } else {
//            bedChargeTextField.setEditable(false);
//            bedChargeTextField.setEnabled(false);
//            bedChargeTextField.setText("0");
//        }
        if(value == 0){
            bedChargeTextField.setEnabled(false);
            bedChargeTextField.setText("0");
        } else {
            bedChargeTextField.setEnabled(true);
        }
        totalAmountComputation();
    }//GEN-LAST:event_bedSpinnerStateChanged

    private void dateArrivalChooserComboOnCommit(datechooser.events.CommitEvent evt) {//GEN-FIRST:event_dateArrivalChooserComboOnCommit
        days();
    }//GEN-LAST:event_dateArrivalChooserComboOnCommit

    private void dateDepartureChooserComboOnCommit(datechooser.events.CommitEvent evt) {//GEN-FIRST:event_dateDepartureChooserComboOnCommit
        days();
    }//GEN-LAST:event_dateDepartureChooserComboOnCommit

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        try {
            String path = "";
            BufferedReader br = new BufferedReader(new FileReader("dir\\docs.txt"));
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
            Desktop.getDesktop().browse(new File(path).toURI());
        } catch (IOException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jLabel2MouseClicked

    private void lastNameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_lastNameTextFieldCaretUpdate
        // TODO add your handling code here:
        enableClearButton();
        enableButton();
    }//GEN-LAST:event_lastNameTextFieldCaretUpdate

    private void firstNameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_firstNameTextFieldCaretUpdate
        // TODO add your handling code here:
        enableClearButton();
        enableButton();
    }//GEN-LAST:event_firstNameTextFieldCaretUpdate

    private void mobileNoTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_mobileNoTextFieldCaretUpdate
        // TODO add your handling code here:
        enableClearButton();
        enableButton();
    }//GEN-LAST:event_mobileNoTextFieldCaretUpdate

    private void roomReservedTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_roomReservedTextFieldCaretUpdate
        // TODO add your handling code here:
        try {
            if(roomReservedTextField.getText().isEmpty()){
                jButton1.setEnabled(false);
                parentCheckBox.setSelected(false);
            } else {
                jButton1.setEnabled(true);
                boolean check = false;
                if(!roomReservedTextField.getText().isEmpty()){
                    for(String rooms :roomReservedTextField.getText().split(",")){
                        if(residentComboBox.getItemCount() > 0){
                            if(rooms.trim().equals(client.getRoomNumber(residentComboBox.getSelectedItem().toString()).trim())){
                                check = true;
                                break;
                            }
                        }
                    }
                }
                parentCheckBox.setSelected(check);
            }

            if(roomReservedTextField.getText().contains("200") || roomReservedTextField.getText().contains("300") || roomReservedTextField.getText().contains("400")){

            }
            } catch (RemoteException ex) {
//                Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, ex.getMessage());
            }
        enableClearButton();
        enableButton();
    }//GEN-LAST:event_roomReservedTextFieldCaretUpdate

    private void addressTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_addressTextFieldCaretUpdate
        // TODO add your handling code here:
        enableClearButton();
    }//GEN-LAST:event_addressTextFieldCaretUpdate

    private void emailTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_emailTextFieldCaretUpdate
        // TODO add your handling code here:
        enableClearButton();
    }//GEN-LAST:event_emailTextFieldCaretUpdate

    private void PersonTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_PersonTextFieldCaretUpdate
        // TODO add your handling code here:
        enableClearButton();
    }//GEN-LAST:event_PersonTextFieldCaretUpdate

    private void bedChargeTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_bedChargeTextFieldCaretUpdate
        // TODO add your handling code here:
        enableClearButton();
    }//GEN-LAST:event_bedChargeTextFieldCaretUpdate

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        rates();
//        totalAmountTextField.setText((Double.parseDouble(totalAmountTextField.getText()) + securityCharge) + "");
        totalAmountComputation();
    }//GEN-LAST:event_formWindowOpened

    private void daysTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_daysTextFieldCaretUpdate
        // TODO add your handling code here:
        if(iterate != 0){
            totalAmountComputation();
            iterate = 0;
        } else {
            iterate = 1;
        }
    }//GEN-LAST:event_daysTextFieldCaretUpdate

    private void PersonTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PersonTextFieldFocusLost
        // TODO add your handling code here:
        try{
            if(PersonTextField.getText().trim().isEmpty()){
                PersonTextField.setText("0.00");
            } else {
                PersonTextField.setText(df.format(Double.parseDouble(PersonTextField.getText())) + "");
                totalAmountComputationV2();
            }
        } catch ( NumberFormatException ex) {
            totalAmountComputation();
        }
    }//GEN-LAST:event_PersonTextFieldFocusLost

    private void bedChargeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bedChargeTextFieldFocusLost
        // TODO add your handling code here:
        try{
            if(bedChargeTextField.getText().trim().isEmpty()){
                bedChargeTextField.setText("0.00");
            } else {
                bedChargeTextField.setText(df.format(Double.parseDouble(bedChargeTextField.getText())) + "");
                totalAmountComputationV2();
            }
        } catch ( NumberFormatException ex) {
            totalAmountComputation();
        }
    }//GEN-LAST:event_bedChargeTextFieldFocusLost

    private void PersonTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PersonTextFieldFocusGained
        // TODO add your handling code here:
        if(PersonTextField.getText().equals("0.00")){
            PersonTextField.setText("");
        }
    }//GEN-LAST:event_PersonTextFieldFocusGained

    private void bedChargeTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bedChargeTextFieldFocusGained
        // TODO add your handling code here:
        if(bedChargeTextField.getText().equals("0.00")){
            bedChargeTextField.setText("");
        }
    }//GEN-LAST:event_bedChargeTextFieldFocusGained

    private void reservedRoomTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_reservedRoomTextFieldCaretUpdate
        // TODO add your handling code here:
        enableClearButton();
        enableButton();
    }//GEN-LAST:event_reservedRoomTextFieldCaretUpdate

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String[] rooms = roomReservedTextField.getText().split(",");
        String r = "";
        for(int c = 0; c < rooms.length-1; c++){
            
            if(r.trim().isEmpty()){
                r = rooms[c];
            } else {
                r += ", "+ rooms[c];
            }
        }
        roomReservedTextField.setText(r);
        if((rooms.length - 1) == 0){
            reservedRoomTextField.setText("");
        } else {
            reservedRoomTextField.setText((rooms.length - 1) + "");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void discountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_discountFocusGained
        // TODO add your handling code here:
        if(discount.getText().trim().equals("0.00")){
            discount.setText("");
        }
    }//GEN-LAST:event_discountFocusGained

    private void discountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_discountFocusLost
        // TODO add your handling code here:
        if(discount.getText().trim().isEmpty()){
            discount.setText("0.00");
        } else {
            try {
                double d = Double.parseDouble(discount.getText().trim());
                discount.setText(df.format(d));
            } catch (NumberFormatException ex){
                discount.setText("0.00");
            }
            
        }
        totalAmountComputation();
    }//GEN-LAST:event_discountFocusLost

    private void days() {
        try {
            Calendar cal1 = new GregorianCalendar();
            Calendar cal2 = new GregorianCalendar();

            DateFormat originalFormat1 = dateArrivalChooserCombo.getDateFormat();
            java.util.Date utilDate1 = originalFormat1.parse(dateArrivalChooserCombo.getText());
            cal1.setTime(utilDate1);

            originalFormat1 = dateDepartureChooserCombo.getDateFormat();
            utilDate1 = originalFormat1.parse(dateDepartureChooserCombo.getText());
            cal2.setTime(utilDate1);

            int dayCount = 1;
            if(!isFromTransient){
                dayCount = daysBetween(cal1.getTime(),cal2.getTime());
            } else {
                isFromTransient = false;
            }
            
            if(dayCount <= 0){
                md.warning(this, "Please check the dates.");
                daysTextField.setText("1");
                cal1.setTime(dateArrivalChooserCombo.getDateFormat().parse(arrDate));
                dateArrivalChooserCombo.setSelectedDate(cal1);
                cal2.setTime(dateDepartureChooserCombo.getDateFormat().parse(depDate));
                dateDepartureChooserCombo.setSelectedDate(cal2);
            } else {
                daysTextField.setText(dayCount + "");
            }
        } catch (ParseException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private void clearAll() {
        rates();
        try {
            dateArrivalChooserCombo.setDefaultPeriods(new datechooser.model.multiple.PeriodSet(
                    new datechooser.model.multiple.Period(Calendar.getInstance(),Calendar.getInstance())));
            dateDepartureChooserCombo.setDefaultPeriods(new datechooser.model.multiple.PeriodSet(
                    new datechooser.model.multiple.Period(Calendar.getInstance(),Calendar.getInstance())));
        } catch (IncompatibleDataExeption ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
        daysTextField.setText("1");
        lastNameTextField.setText("");
        firstNameTextField.setText("");
        mobileNoTextField.setText("");
        addressTextField.setText("");
        emailTextField.setText("");
        amountPaidTextField.setText("");
        modelTop = (DefaultTableModel) guestTable.getModel();
        modelTop.setRowCount(0);
        reservedRoomTextField.setText("");
        roomReservedTextField.setText("");
        totalAmountTextField.setText(df.format(perHead));
        amountPaidTextField.setText("0.00");
        guestSpinner.setValue(0);
        bedSpinner.setValue(0);
        residentNameCheckBox.setSelected(false);
        parentCheckBox.setSelected(false);
        residentComboBox.removeAllItems();
        relationComboBox.setEditable(false);
        relationComboBox.setEnabled(false);
        parentCheckBox.setEnabled(false);
        relationComboBox.setSelectedItem("Other");

    }

    private void enableClearButton() {
        // TODO add your handling code here:
        if (!lastNameTextField.getText().isEmpty()
                || !firstNameTextField.getText().isEmpty()
                || !mobileNoTextField.getText().isEmpty()
                || !addressTextField.getText().isEmpty()
                || !emailTextField.getText().isEmpty()
                //|| !additionalGuestTextArea.getText().isEmpty()
                || !PersonTextField.getText().equals("0.00")
                || !bedChargeTextField.getText().equals("0.00")) {
            clearButton.setEnabled(true);
        } else {
            clearButton.setEnabled(false);
        }
    }

    private void enableButton() {
        // TODO add your handling code here:
        if (!lastNameTextField.getText().isEmpty()
                & !firstNameTextField.getText().isEmpty()
                & !mobileNoTextField.getText().isEmpty()
                & !reservedRoomTextField.getText().isEmpty()) {
            saveButton.setEnabled(true);
        } else {
            saveButton.setEnabled(false);
        }
    }

    private void totalAmountComputation(){
        double perHeadTotal = Integer.parseInt(daysTextField.getText().trim().replace(",", "")) * perHead;
        double perGuestTotal = ((int) guestSpinner.getValue() * perHead) * Integer.parseInt(daysTextField.getText().trim().replace(",", ""));
        PersonTextField.setText(df.format((int) guestSpinner.getValue() * perHead * Integer.parseInt(daysTextField.getText().trim().replace(",", ""))));
        double perBedTotal = ((int) bedSpinner.getValue() * bedCharge) * Integer.parseInt(daysTextField.getText().trim().replace(",", ""));
        bedChargeTextField.setText(df.format((int) bedSpinner.getValue() * bedCharge * Integer.parseInt(daysTextField.getText().trim().replace(",", ""))));
        
//        if(!lastPage.contains("transient_form")){
            totalAmountTextField.setText(df.format((perHeadTotal + perGuestTotal + perBedTotal)));
            balanceTextField.setText(df.format(Double.parseDouble(totalAmountTextField.getText().trim().replace(",", "")) - Double.parseDouble(discount.getText().trim().replace(",", "")) - (Double.parseDouble(amountPaidTextField.getText().trim().replace(",", "")) + Double.parseDouble(lastAmountPaid.getText().trim().replace(",", "")))));
//        }
    }
    
    private void totalAmountComputationV2(){
        double perHeadTotal = Integer.parseInt(daysTextField.getText().trim().replace(",", "")) * perHead;
        double perGuestTotal = Double.parseDouble(PersonTextField.getText().trim().replace(",", ""));
        double perBedTotal = Double.parseDouble(bedChargeTextField.getText().trim().replace(",", ""));
        
//        if(!lastPage.contains("transient_form")){
            totalAmountTextField.setText(df.format((perHeadTotal + perGuestTotal + perBedTotal)));
            balanceTextField.setText(df.format(Double.parseDouble(totalAmountTextField.getText().trim().replace(",", "")) - Double.parseDouble(discount.getText().trim().replace(",", "")) - (Double.parseDouble(amountPaidTextField.getText().trim().replace(",", "")) + Double.parseDouble(lastAmountPaid.getText().trim().replace(",", "")))));
//        }
//        double perHeadTotal = Double.parseDouble(PersonTextField.getText().trim());
//        double perBedTotal = Double.parseDouble(bedChargeTextField.getText().trim());
//        totalAmountTextField.setText(df.format(perHeadTotal + perBedTotal) + "");
//        balanceTextField.setText(df.format(Double.parseDouble(totalAmountTextField.getText().trim()) - Double.parseDouble(amountPaidTextField.getText().trim())) + "");
    }
    
    private void rates(){
        try {
            RatesImpl ri = client.getRates();
            perHead = ri.getTransientRatePerPerson();
            bedCharge = ri.getTransientRateBedCharge();
            
            rPerHead = ri.getTransientRatePerPerson();
            rBedCharge = ri.getTransientRateBedCharge();
            
            fPerHead = ri.getTransientFamilyPerPerson();
            fBedCharge = ri.getTransientFamilyBedCharge();
            
            sPerHead = ri.getTransientSuitePerPerson();
            sBedCharge = ri.getTransientSuiteBedCharge();
            reg.setText(df.format(sPerHead));
            add.setText(df.format(sBedCharge));
        } catch (RemoteException ex) {
            md.error(this, ex.getMessage());
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveToPdf(){
        try{
            ArrayList<String> names = new ArrayList<>();
            TransientImpl trans = new TransientImpl();
            
            trans.setLast_name(lastNameTextField.getText());
            trans.setFirst_name(firstNameTextField.getText());
            trans.setMobile_number(mobileNoTextField.getText());
            trans.setAddress(addressTextField.getText());
            trans.setEmail(emailTextField.getText());
            if(residentNameCheckBox.isSelected()){
                trans.setFull_name(residentComboBox.getSelectedItem().toString());
                trans.setRelation(residentComboBox.getSelectedItem().toString());
            }
            trans.setReservedRooms(roomReservedTextField.getText());
            trans.setArrival(dateArrivalChooserCombo.getText());
            trans.setDeparture(dateDepartureChooserCombo.getText());
            trans.setNoAdditionalGuest(guestSpinner.getValue().toString());
            
            for(int i = 0; i < guestTable.getRowCount(); i++){
                names.add(guestTable.getValueAt(i, 0).toString());
            }
            
            if(client.saveTransientToPdf(trans, names)){
                md.successful(this, "Done saving Transient Form to PDF");
            } else {
                md.unsuccessful(this, "Failed to save file to PDF.");
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(Registration_Transient.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Error!", "Error", JOptionPane.ERROR_MESSAGE);
            java.util.logging.Logger.getLogger(Registration_Transient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Registration_Transient(null, "", null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField PersonTextField;
    private javax.swing.JLabel add;
    private javax.swing.JTextField addressTextField;
    private javax.swing.JTextField amountPaidTextField;
    private javax.swing.JPanel amountPanel;
    private javax.swing.JPanel areaPanel;
    private javax.swing.JTextField balanceTextField;
    private javax.swing.JTextField bedChargeTextField;
    private javax.swing.JSpinner bedSpinner;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private datechooser.beans.DateChooserCombo dateArrivalChooserCombo;
    private datechooser.beans.DateChooserCombo dateDepartureChooserCombo;
    private javax.swing.JTextField daysTextField;
    private javax.swing.JTextField discount;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JTextField firstNameTextField;
    private javax.swing.JSpinner guestSpinner;
    private javax.swing.JTable guestTable;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lastAmountPaid;
    private javax.swing.JTextField lastNameTextField;
    private javax.swing.JTextField mobileNoTextField;
    private javax.swing.JCheckBox parentCheckBox;
    private javax.swing.JLabel reg;
    private javax.swing.JComboBox relationComboBox;
    private javax.swing.JTextField reservedRoomTextField;
    private javax.swing.JComboBox residentComboBox;
    private javax.swing.JCheckBox residentNameCheckBox;
    private javax.swing.JTextField roomReservedTextField;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel stayPanel;
    private javax.swing.JTextField totalAmountTextField;
    // End of variables declaration//GEN-END:variables
}