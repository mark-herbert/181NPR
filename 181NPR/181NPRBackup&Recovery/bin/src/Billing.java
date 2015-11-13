import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kathreen Silen
 */
public class Billing extends javax.swing.JFrame {

    /**
     * Creates new form Billing
     */
    private String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    private String idnum = "";
    private DefaultTableModel tableModel = new DefaultTableModel();
    private ShuttleService ss;
    private double totalAmount = 0.0;
    private double billingBalance = 0.0;
    private double tRoomRate = 0.0;
    private double tShuttleRate = 0.0;
    private double tGadgetRate = 0.0;
    private double taddition = 0.0;
    private double tAmount = 0.0;
    private double amtPd = 0.0;
    private double bal = 0.0;
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    private MessageDialog md = new MessageDialog();
    private Connection connection;

    public Billing(String idnum, String prevDatePaid) {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Backup and Recovery.png")));
        initComponents();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(InventoryForm.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }

        this.idnum = idnum;
        setResidentControl();
        try {
            ss = new ShuttleService(this.idnum, ridesNo);
        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        additional.setEnabled(false);

        previousBalance.setText(df.format(getPreviousBalance(idnum, getDatein(idnum, prevDatePaid))));//not these

        balance.setText(df.format(this.billingBalance));
        try {
            ArrayList<String> billing = getResidentAndRoomInfo(idnum);
            nameLabel.setText(billing.get(0));
            addressLabel.setText(billing.get(1));
            roomNumberLabel.setText(billing.get(2));
            switch (billing.get(3)) {
                case "T":
                    roomTypeLabel.setText("Triple-Sharing Room");
                    break;
                case "S":
                    roomTypeLabel.setText("Single-Sharing Room");
                    break;
                case "D":
                    roomTypeLabel.setText("Double-Sharing Room");
                    break;
            }
            double[] shuttleRates = getShuttleRate();
            monthTypeRadioButton.setSelected(true);
            if (monthTypeRadioButton.isSelected()) {
                shuttleRate.setText(df.format(shuttleRates[1]));
                ridesNo.setEnabled(false);
                ridesNo.setEditable(false);
                datesButton.setEnabled(false);
            } else {
                shuttleRate.setText(df.format(shuttleRates[0]));
            }

            tableModel = (DefaultTableModel) gadgetTable.getModel();
            tableModel.getDataVector().removeAllElements();
            tableModel.fireTableDataChanged();
            gadgetTable.setModel(tableModel);
            ArrayList<BillingGadgetImpl> gadget = getBillingGadget(getBillingId(idnum));
            for (BillingGadgetImpl a : gadget) {
                tableModel.addRow(new Object[]{
                    a.isIsSelected(),
                    a.getItemName(),
                    a.getGadgetRate()
                });
                if (a.isIsSelected()) {
                    double d = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
                    gadgetRate.setText(df.format(d + a.getGadgetRate()));
                }
            }

            ArrayList<String> billingInfo = getBillingInfo(idnum);
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(billingInfo.get(2));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String e = new SimpleDateFormat("MM/dd/yyyy").format(cal.getTime());
            dateInLabel.setText(e);
            date = new SimpleDateFormat("yyyy-MM-dd").parse(billingInfo.get(3));
            String d = new SimpleDateFormat("MM/dd/yyyy").format(date.getTime());
            dateOutLabel.setText(d);
            noDays.setText(billingInfo.get(5));
            roomRate.setText(billingInfo.get(6));

            double monthlyRate = 0.0;
            switch (roomTypeLabel.getText().trim()) {
                case "Triple-Sharing Room":
                    monthlyRate = getRoomRate("Triple-Sharing Room");
                    break;
                case "Double-Sharing Room":
                    monthlyRate = getRoomRate("Double-Sharing Room");
                    break;
                case "Single-Sharing Room":
                    monthlyRate = getRoomRate("Single Room");
                    break;
            }
            String type = getBillingShuttleType(this.idnum);
            if (type != null) {
                ArrayList<String> billingInformation = getBillingInfo(idnum.trim());
                if (type.contains("Monthly")) {
                    shuttle.setSelected(true);
                    monthTypeRadioButton.setSelected(true);
                    ridesNo.setEditable(false);
                    datesButton.setEnabled(false);
                    ridesNo.setEnabled(false);
                    ridesNo.setEnabled(false);
                    ridesNo.setEditable(false);
                    datesButton.setEnabled(false);
                    monthTypeRadioButton.setSelected(true);
                    shuttleRates = getShuttleRate();

                    if (monthTypeRadioButton.isSelected()) {
                        shuttleRate.setText(df.format(Double.parseDouble(billingInformation.get(8))));
                        ridesNo.setEnabled(false);
                        ridesNo.setEditable(false);
                        datesButton.setEnabled(false);
                    } else {
                        shuttleRate.setText(df.format(Double.parseDouble(billingInformation.get(8))));
                    }
                } else {
                    shuttleRates = getShuttleRate();
                    shuttle.setSelected(true);
                    rideTypeRadioButton.setSelected(true);
                    datesButton.setEnabled(false);
                    ridesNo.setText(getTotalRides(this.idnum) + "");
//                    shuttleRate.setText("" + (client.getTotalRides(this.idnum) * shuttleRates[0]));
                    shuttleRate.setText(df.format(Double.parseDouble(billingInformation.get(8))));
                }
            } else {
                shuttle.setSelected(false);
                jLabel18.setEnabled(false);
                monthTypeRadioButton.setEnabled(false);
                rideTypeRadioButton.setEnabled(false);
                jLabel19.setEnabled(false);
                ridesNo.setEnabled(false);
                datesButton.setEnabled(false);
                shuttleRate.setEnabled(false);
                jLabel20.setEnabled(false);
                shuttleRate.setText("0.00");
            }
//            int dd = new SimpleDateFormat("yyyy-MM-dd").parse(billingInfo.get(0)).getDate();
            roomRateLabel.setText(df.format(monthlyRate));
            tRoomRate = Double.parseDouble(roomRate.getText().trim().replace(",", ""));
            tShuttleRate = Double.parseDouble(shuttleRate.getText().trim().replace(",", ""));
            tGadgetRate = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
            tAmount = tRoomRate + tShuttleRate + tGadgetRate;

            totalRoomRate.setText(df.format(tRoomRate));
            totalShuttleRate.setText(df.format(tShuttleRate));
            totalGadgetRate.setText(df.format(tGadgetRate));
            totalAmountField.setText(df.format(tAmount));

            dateLabel.setText(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
        } catch (ParseException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }

    }

    public Billing() {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Reservation.png")));
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rateType = new javax.swing.ButtonGroup();
        shuttleType = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        name = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        controlNumber = new javax.swing.JTextField();
        dateLabel = new datechooser.beans.DateChooserCombo();
        jPanel3 = new javax.swing.JPanel();
        roomNumberLabel = new javax.swing.JLabel();
        roomTypeLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        noDays = new javax.swing.JFormattedTextField();
        roomRate = new javax.swing.JFormattedTextField();
        roomRateCheckBox = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        roomRateLabel = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        dateOutLabel = new javax.swing.JLabel();
        dateInLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        addCheckbox = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        balance = new javax.swing.JFormattedTextField();
        totalAmountField = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        previousBalance = new javax.swing.JLabel();
        additional = new javax.swing.JFormattedTextField();
        totalRoomRate = new javax.swing.JFormattedTextField();
        totalShuttleRate = new javax.swing.JFormattedTextField();
        totalGadgetRate = new javax.swing.JFormattedTextField();
        amountPaid = new javax.swing.JFormattedTextField();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        shuttle = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        monthTypeRadioButton = new javax.swing.JRadioButton();
        rideTypeRadioButton = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        datesButton = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        ridesNo = new javax.swing.JFormattedTextField();
        shuttleRate = new javax.swing.JFormattedTextField();
        jPanel7 = new javax.swing.JPanel();
        gadgets = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        gadgetTable = new javax.swing.JTable();
        gadgetRate = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        remarks = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        adminName = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        cash = new javax.swing.JRadioButton();
        cheque = new javax.swing.JRadioButton();
        bd = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Resident Information", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Rondalo", 0, 16))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel1.setText("Name:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 22, 38, -1));

        jLabel2.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel2.setText("Address:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 48, -1, -1));

        name.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jPanel2.add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 22, 129, 14));

        jLabel5.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Date");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 20, 50, 20));

        nameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        nameLabel.setText("Name:");
        jPanel2.add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 22, 490, -1));

        addressLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        addressLabel.setText("Address:");
        jPanel2.add(addressLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 48, 490, -1));

        jLabel3.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel3.setText("Control Number");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(552, 50, 80, -1));

        controlNumber.setEditable(false);
        controlNumber.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jPanel2.add(controlNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 50, 140, -1));

        dateLabel.setFormat(2);
        jPanel2.add(dateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, 160, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 780, 80));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        roomNumberLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        roomNumberLabel.setText("Room Number:");
        jPanel3.add(roomNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 110, -1));

        roomTypeLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        roomTypeLabel.setText("Room Type:");
        jPanel3.add(roomTypeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 230, 20));

        jLabel6.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel6.setText("Room Number:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel21.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel21.setText("Room Type:");
        jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 69, -1));

        jLabel11.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel11.setText("Monthly Rate:");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 69, -1));

        jLabel22.setFont(new java.awt.Font("Rondalo", 2, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 0, 0));
        jLabel22.setText("*Room rate / 30 days");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 120, 20));

        jLabel24.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel24.setText("No. of Days:");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 170, 60, 20));

        noDays.setEditable(false);
        noDays.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        noDays.setEnabled(false);
        jPanel3.add(noDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 170, 50, -1));

        roomRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        roomRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        roomRate.setText("0.00");
        roomRate.setEnabled(false);
        jPanel3.add(roomRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, 120, -1));

        roomRateCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        roomRateCheckBox.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        roomRateCheckBox.setSelected(true);
        roomRateCheckBox.setText("Room Rate");
        roomRateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomRateCheckBoxActionPerformed(evt);
            }
        });
        jPanel3.add(roomRateCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 20));

        jLabel10.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel10.setText("End Date:");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 50, -1));

        roomRateLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        roomRateLabel.setText("Room Type:");
        jPanel3.add(roomRateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 120, -1));

        jLabel27.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel27.setText("Room Rate:");
        jPanel3.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 85, 69, -1));

        jLabel28.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel28.setText("Start Date:");
        jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 60, -1));

        dateOutLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dateOutLabel.setText("Room Type:");
        jPanel3.add(dateOutLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 100, -1));

        dateInLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dateInLabel.setText("Room Type:");
        jPanel3.add(dateInLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 100, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 104, 370, 210));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Total Amount", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Rondalo", 0, 16))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel12.setText("Room Rate:");
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 97, -1));

        jLabel13.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel13.setText("Shuttle Rate:");
        jPanel4.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 77, 97, -1));

        jLabel14.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel14.setText("Gadget Rate:");
        jPanel4.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 106, 97, -1));

        addCheckbox.setBackground(new java.awt.Color(255, 255, 255));
        addCheckbox.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        addCheckbox.setText("Additional 10%:");
        addCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                addCheckboxItemStateChanged(evt);
            }
        });
        addCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCheckboxActionPerformed(evt);
            }
        });
        jPanel4.add(addCheckbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jLabel15.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel15.setText("Total amount:");
        jPanel4.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 97, -1));

        jLabel16.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel16.setText("Amount paid:");
        jPanel4.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, -1));

        jLabel17.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel17.setText("Balance:");
        jPanel4.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 97, -1));
        jPanel4.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 338, 10));

        balance.setEditable(false);
        balance.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        balance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        balance.setText("0.00");
        jPanel4.add(balance, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 270, 203, -1));

        totalAmountField.setEditable(false);
        totalAmountField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        totalAmountField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalAmountField.setText("0.00");
        totalAmountField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                totalAmountFieldCaretUpdate(evt);
            }
        });
        jPanel4.add(totalAmountField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 204, 24));

        jLabel25.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel25.setText("Last month balance:");
        jPanel4.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 97, -1));

        previousBalance.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        previousBalance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        previousBalance.setText("0.0");
        previousBalance.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jPanel4.add(previousBalance, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 180, 200, 20));

        additional.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        additional.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        additional.setText("0.00");
        additional.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                additionalCaretUpdate(evt);
            }
        });
        additional.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                additionalFocusLost(evt);
            }
        });
        jPanel4.add(additional, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 204, 24));

        totalRoomRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        totalRoomRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalRoomRate.setText("0.00");
        totalRoomRate.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                totalRoomRateCaretUpdate(evt);
            }
        });
        totalRoomRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                totalRoomRateFocusLost(evt);
            }
        });
        jPanel4.add(totalRoomRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 204, 24));

        totalShuttleRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        totalShuttleRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalShuttleRate.setText("0.00");
        totalShuttleRate.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                totalShuttleRateCaretUpdate(evt);
            }
        });
        totalShuttleRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                totalShuttleRateFocusLost(evt);
            }
        });
        jPanel4.add(totalShuttleRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 204, 24));

        totalGadgetRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        totalGadgetRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalGadgetRate.setText("0.00");
        totalGadgetRate.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                totalGadgetRateCaretUpdate(evt);
            }
        });
        totalGadgetRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                totalGadgetRateFocusLost(evt);
            }
        });
        jPanel4.add(totalGadgetRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 204, 24));

        amountPaid.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        amountPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        amountPaid.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                amountPaidCaretUpdate(evt);
            }
        });
        amountPaid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                amountPaidFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                amountPaidFocusLost(evt);
            }
        });
        jPanel4.add(amountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 230, 204, 24));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 370, 310));

        saveButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        saveButton.setText("Confirm");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 640, 70, -1));

        cancelButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 640, 70, -1));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Other Rate", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Rondalo", 0, 16))); // NOI18N
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        shuttle.setBackground(new java.awt.Color(255, 255, 255));
        shuttle.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        shuttle.setText("Shuttle Service");
        shuttle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shuttleActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel18.setText("Rate Type");

        monthTypeRadioButton.setBackground(new java.awt.Color(255, 255, 255));
        shuttleType.add(monthTypeRadioButton);
        monthTypeRadioButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        monthTypeRadioButton.setText("Monthly ");
        monthTypeRadioButton.setEnabled(false);
        monthTypeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthTypeRadioButtonActionPerformed(evt);
            }
        });

        rideTypeRadioButton.setBackground(new java.awt.Color(255, 255, 255));
        shuttleType.add(rideTypeRadioButton);
        rideTypeRadioButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        rideTypeRadioButton.setText("Rides");
        rideTypeRadioButton.setEnabled(false);
        rideTypeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rideTypeRadioButtonActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel19.setText("No. of Rides:");
        jLabel19.setEnabled(false);

        datesButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        datesButton.setText("Date/s Availed");
        datesButton.setEnabled(false);
        datesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datesButtonActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel20.setText("Shuttle Rate:");

        ridesNo.setEditable(false);
        ridesNo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        ridesNo.setEnabled(false);
        ridesNo.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ridesNoCaretUpdate(evt);
            }
        });

        shuttleRate.setEditable(false);
        shuttleRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        shuttleRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        shuttleRate.setText("0.00");
        shuttleRate.setToolTipText("");
        shuttleRate.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                shuttleRateCaretUpdate(evt);
            }
        });
        shuttleRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                shuttleRateFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(shuttle)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rideTypeRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(monthTypeRadioButton))
                        .addGap(10, 10, 10)
                        .addComponent(jLabel19))
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shuttleRate)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(ridesNo, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(datesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(shuttleRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(shuttle)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(monthTypeRadioButton)
                                .addComponent(jLabel19)
                                .addComponent(ridesNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rideTypeRadioButton)
                                    .addComponent(datesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(33, 33, 33)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 360, 120));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        gadgets.setBackground(new java.awt.Color(255, 255, 255));
        gadgets.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        gadgets.setSelected(true);
        gadgets.setText("Additional Appliance/Gadget");
        gadgets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gadgetsActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jLabel23.setText("Gadget Rate:");

        gadgetTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "Laptop", "500"},
                {null, "Tablet", "250"},
                { new Boolean(false), "iPhone", "300"},
                {null, null, null}
            },
            new String [] {
                " ", "Item", "Rate"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
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
        gadgetTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gadgetTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(gadgetTable);
        if (gadgetTable.getColumnModel().getColumnCount() > 0) {
            gadgetTable.getColumnModel().getColumn(0).setMinWidth(20);
            gadgetTable.getColumnModel().getColumn(0).setPreferredWidth(20);
            gadgetTable.getColumnModel().getColumn(0).setMaxWidth(20);
            gadgetTable.getColumnModel().getColumn(1).setResizable(false);
            gadgetTable.getColumnModel().getColumn(2).setResizable(false);
        }

        gadgetRate.setEditable(false);
        gadgetRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        gadgetRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        gadgetRate.setText("0.00");
        gadgetRate.setEnabled(false);
        gadgetRate.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                gadgetRateCaretUpdate(evt);
            }
        });
        gadgetRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                gadgetRateFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gadgetRate, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(gadgets)
                .addGap(0, 197, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(gadgets)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(gadgetRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 364, 190));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Remarks", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Rondalo", 0, 16))); // NOI18N
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        remarks.setColumns(20);
        remarks.setLineWrap(true);
        remarks.setRows(4);
        remarks.setWrapStyleWord(true);
        jScrollPane1.setViewportView(remarks);

        jPanel5.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 370, 80));

        jLabel4.setText("Administrator Name:");
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, -1, -1));
        jPanel5.add(adminName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 430, 260, -1));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 170, 400, 460));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mode of Payment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 12))); // NOI18N

        cash.setBackground(new java.awt.Color(255, 255, 255));
        cash.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        cash.setSelected(true);
        cash.setText("Cash");

        cheque.setBackground(new java.awt.Color(255, 255, 255));
        cheque.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        cheque.setText("Cheque");

        bd.setBackground(new java.awt.Color(255, 255, 255));
        bd.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        bd.setText("Back Deposit");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cash)
                .addGap(18, 18, 18)
                .addComponent(cheque)
                .addGap(18, 18, 18)
                .addComponent(bd)
                .addContainerGap(151, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cash)
                    .addComponent(cheque)
                    .addComponent(bd))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 100, 400, 70));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 680));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void datesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datesButtonActionPerformed
        if (!ss.isVisible()) {
            ss.setVisible(true);
            ss.setAlwaysOnTop(true);
        }
    }//GEN-LAST:event_datesButtonActionPerformed

    private void monthTypeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthTypeRadioButtonActionPerformed
        // TODO add your handling code here:
        ridesNo.setEnabled(false);
        ridesNo.setEditable(false);
        datesButton.setEnabled(false);
        double[] shuttleRates = getShuttleRate();
        if (monthTypeRadioButton.isSelected()) {
            ridesNo.setEnabled(false);
            ridesNo.setEditable(false);
            datesButton.setEnabled(false);
        }
    }//GEN-LAST:event_monthTypeRadioButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        if (md.confirmationCancel(this) == md.YES) {
            new Billing_Resident().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to save?", "Confirmation", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            try {
                saveToPDF();
                ArrayList<String> billing = new ArrayList<>();
                billing.add(new SimpleDateFormat("yyyy-MM-dd").format(dateLabel.getDateFormat().parse(dateLabel.getText()))); //0
                billing.add(remarks.getText()); //2
                if (!totalRoomRate.getText().isEmpty()) {
                    billing.add(totalRoomRate.getText().replace(",", "")); //3
                } else {
                    billing.add("0.00"); //3
                }
                if (!totalShuttleRate.getText().isEmpty()) {
                    billing.add(totalShuttleRate.getText().replace(",", "")); //4
                } else {
                    billing.add("0.00"); //4
                }
                if (!totalGadgetRate.getText().isEmpty()) {
                    billing.add(totalGadgetRate.getText().replace(",", "")); //5
                } else {
                    billing.add("0.00"); //5
                }
                if (addCheckbox.isSelected() & !additional.getText().isEmpty()) {
                    billing.add(additional.getText().replace(",", "")); //6
                } else {
                    billing.add("0.00"); //6
                }
                if (!totalAmountField.getText().isEmpty()) {
                    billing.add(totalAmountField.getText().replace(",", "")); //7
                } else {
                    billing.add("0.00"); //7
                }
//                if(!totalAmountField.getText().isEmpty()){
//                    billing.add(totalAmountField.getText()); //8
//                } else {
//                    billing.add("0.00"); //8
//                }
                if (!amountPaid.getText().isEmpty()) {
                    billing.add(amountPaid.getText().replace(",", "")); //9
                } else {
                    billing.add("0.00"); //9
                }
                if (!balance.getText().isEmpty()) {
                    billing.add(balance.getText().replace(",", "")); //10
                } else {
                    billing.add("0.00"); //10
                }
                billing.add(this.idnum); //11
                billing.add(controlNumber.getText().trim()); //12
                billing.add("resident"); //13
                if (cash.isSelected()) {
                    billing.add("cash"); //14
                } else if (cheque.isSelected()) {
                    billing.add("cheque"); //14
                } else {
                    billing.add("bd"); //15
                }
                billing.add("00000001"); //16
                if (!updateBilling(billing)) {
                    JOptionPane.showMessageDialog(this, "Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new Billing_Resident().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed!", "Failed", JOptionPane.WARNING_MESSAGE);
                }

            } catch (NumberFormatException ex) {
//                Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, ex.getMessage());
            } catch (ParseException ex) {
//                Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void addCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_addCheckboxItemStateChanged

    }//GEN-LAST:event_addCheckboxItemStateChanged

// dateInChooserCombo.setEnabled(false);
//        dateOutChooserCombo.setEnabled(false);
//        noDays.setText("");
//        try {
//            if (monthlyRadioButton.isSelected()) {
//                monthlyRadioButton.setEnabled(true);
//                switch (roomTypeLabel.getText().trim()) {
//                    case "Triple-Sharing Room":
//                        roomRate.setText(client.getRoomRate("Triple-Sharing Room") + "");
//                        break;
//                    case "Double-Sharing Room":
//                        roomRate.setText(client.getRoomRate("Double-Sharing Room") + "");
//                        break;
//                    case "Single-Sharing Room":
//                        roomRate.setText(client.getRoomRate("Single Room") + "");
//                        break;
//                }
//                noDays.setText("");
//            } else {
//                roomRate.setEnabled(true);
//                roomRate.setText("0");
//            }
//        } catch (RemoteException ex) {
//            Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
//        }
    private void rideTypeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rideTypeRadioButtonActionPerformed
        ridesNo.setEnabled(false);
        datesButton.setEnabled(false);
    }//GEN-LAST:event_rideTypeRadioButtonActionPerformed

    private void gadgetTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gadgetTableMouseClicked
        try {
            tableModel = (DefaultTableModel) gadgetTable.getModel();
            double c = 0.0;
            for (int count = 0; count < tableModel.getRowCount(); count++) {
                if (((Boolean) tableModel.getValueAt(count, 0)) && tableModel.getValueAt(count, 2) != null) {
                    c += Double.parseDouble(tableModel.getValueAt(count, 2).toString());
                }
            }
            gadgetRate.setText(df.format(c));
        } catch (NullPointerException ex) {
        }

    }//GEN-LAST:event_gadgetTableMouseClicked

    private void addCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCheckboxActionPerformed
        try {
            if (addCheckbox.isSelected()) {
                additional.setEditable(true);
                additional.setEnabled(true);
//                double roomR = Double.parseDouble(roomRate.getText().trim());
//                double tenPercent = 0.1 * roomR;
//                Date today = Calendar.getInstance().getTime();
//                int days = today.getDate() - 5;
//                additional.setText(df.format(tenPercent*days));
//                if (!additional.getText().isEmpty()) {
//                    totalAmount = Double.parseDouble(totalRoomRate.getText()) + Double.parseDouble(totalShuttleRate.getText())
//                            + Double.parseDouble(totalGadgetRate.getText()) + Double.parseDouble(amountPaid.getText());
//                additional.setText(df.format(tenPercent*days));
//                } else {
//                    totalAmount = Double.parseDouble(totalRoomRate.getText()) + Double.parseDouble(totalShuttleRate.getText())
//                            + Double.parseDouble(totalGadgetRate.getText());
//                    additional.setText("0.00");
//                }
                additional.setText(df.format(Double.parseDouble(totalRoomRate.getText().replace(",", "")) * 0.1));
            } else {
//                totalAmount = Double.parseDouble(totalRoomRate.getText()) + Double.parseDouble(totalShuttleRate.getText())
//                        + Double.parseDouble(totalGadgetRate.getText());
//                additional.setEditable(false);
//                additional.setEnabled(false);
                additional.setText("0.00");
            }
            totalAmountField.setText(df.format(totalAmount));
        } catch (NumberFormatException ex) {
        }

    }//GEN-LAST:event_addCheckboxActionPerformed

    private void ridesNoCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_ridesNoCaretUpdate
        double[] shuttleRates = getShuttleRate();
        double rideRate = 0.0;
        rideRate = Double.parseDouble(df.format(shuttleRates[0]));
        double noOfRides = 0.0;
        if (!ridesNo.getText().trim().isEmpty()) {
            noOfRides = Double.parseDouble(ridesNo.getText().trim());
        }
        rideRate *= noOfRides;
        shuttleRate.setText(df.format(rideRate));
    }//GEN-LAST:event_ridesNoCaretUpdate

    private void roomRateCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomRateCheckBoxActionPerformed

        if (roomRateCheckBox.isSelected()) {
            try {
                ArrayList<String> billingInfo = getResidentAndBillingInfo(this.idnum);
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(billingInfo.get(0));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                int numberOfDays = cal.getTime().getDate() - date.getDate();
//                noDays.setText(numberOfDays+"");

                double monthlyRate = 0.0;
                switch (roomTypeLabel.getText().trim()) {
                    case "Triple-Sharing Room":
                        monthlyRate = getRoomRate("Triple-Sharing Room");
                        break;
                    case "Double-Sharing Room":
                        monthlyRate = getRoomRate("Double-Sharing Room");
                        break;
                    case "Single-Sharing Room":
                        monthlyRate = getRoomRate("Single Room");
                        break;
                }
//                roomRateLabel.setText(df.format(monthlyRate));
//                double ratePerDay = monthlyRate / 30;
//                double rateOnDays = ratePerDay * numberOfDays;
//                roomRate.setText(df.format(rateOnDays));
                roomRate.setEditable(false);
                roomRate.setEnabled(false);
                noDays.setEditable(false);
                noDays.setEnabled(false);
            } catch (ParseException ex) {
//                Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, ex.getMessage());
            }
            totalRoomRate.setText(df.format(Double.parseDouble(roomRate.getText().trim().replace(",", ""))));
        } else {
            roomRate.setEditable(false);
            roomRate.setEnabled(false);
            noDays.setEditable(false);
            noDays.setEnabled(false);
            totalRoomRate.setText("0.00");
        }
    }//GEN-LAST:event_roomRateCheckBoxActionPerformed

    private void amountPaidCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_amountPaidCaretUpdate
        try {
            Double.parseDouble(amountPaid.getText().trim());
            if (amountPaid.getText().isEmpty()) {
                amtPd = 0.0;
            } else {
                amtPd = Double.parseDouble(amountPaid.getText().trim().replace(",", ""));
            }
        } catch (NumberFormatException ex) {
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                totalAmount = Double.parseDouble(totalAmountField.getText().trim().replace(",", ""));
                double prev = Double.parseDouble(previousBalance.getText().trim().replace(",", ""));
                bal = prev + totalAmount - amtPd;
                balance.setText(df.format(bal));
            }
        });
    }//GEN-LAST:event_amountPaidCaretUpdate

    private void totalRoomRateCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_totalRoomRateCaretUpdate
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (totalRoomRate.getText().isEmpty()) {
                    tRoomRate = 0.0;
                } else {
                    tRoomRate = Double.parseDouble(totalRoomRate.getText().trim().replace(",", ""));
                }
                tShuttleRate = Double.parseDouble(totalShuttleRate.getText().trim().replace(",", ""));
                tGadgetRate = Double.parseDouble(totalGadgetRate.getText().trim().replace(",", ""));
                taddition = Double.parseDouble(additional.getText().trim().replace(",", ""));
                tAmount = tRoomRate + tShuttleRate + tGadgetRate + taddition;
                totalAmountField.setText(df.format(tAmount));
            }
        });
    }//GEN-LAST:event_totalRoomRateCaretUpdate

    private void totalShuttleRateCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_totalShuttleRateCaretUpdate
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                tRoomRate = Double.parseDouble(totalRoomRate.getText().trim().replace(",", ""));
                if (totalShuttleRate.getText().isEmpty()) {
                    tShuttleRate = 0.0;
                } else {
                    tShuttleRate = Double.parseDouble(totalShuttleRate.getText().trim().replace(",", ""));
                }
                tGadgetRate = Double.parseDouble(totalGadgetRate.getText().trim().replace(",", ""));
                taddition = Double.parseDouble(additional.getText().trim().replace(",", ""));
                tAmount = tRoomRate + tShuttleRate + tGadgetRate + taddition;
                totalAmountField.setText(df.format(tAmount));
            }
        });
    }//GEN-LAST:event_totalShuttleRateCaretUpdate

    private void totalGadgetRateCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_totalGadgetRateCaretUpdate
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                tRoomRate = Double.parseDouble(totalRoomRate.getText().trim().replace(",", ""));
                tShuttleRate = Double.parseDouble(totalShuttleRate.getText().trim().replace(",", ""));
                if (totalGadgetRate.getText().isEmpty()) {
                    tGadgetRate = 0.0;
                } else {
                    tGadgetRate = Double.parseDouble(totalGadgetRate.getText().trim().replace(",", ""));
                }
                taddition = Double.parseDouble(additional.getText().trim().replace(",", ""));
                tAmount = tRoomRate + tShuttleRate + tGadgetRate + taddition;
                totalAmountField.setText(df.format(tAmount));
            }
        });
    }//GEN-LAST:event_totalGadgetRateCaretUpdate

    private void additionalCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_additionalCaretUpdate
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                tRoomRate = Double.parseDouble(totalRoomRate.getText().trim().replace(",", ""));
                tShuttleRate = Double.parseDouble(totalShuttleRate.getText().trim().replace(",", ""));
                tGadgetRate = Double.parseDouble(totalGadgetRate.getText().trim().replace(",", ""));
                if (additional.getText().isEmpty()) {
                    taddition = 0.0;
                } else {
                    taddition = Double.parseDouble(additional.getText().trim().replace(",", ""));
                }
                tAmount = tRoomRate + tShuttleRate + tGadgetRate + taddition;
                totalAmountField.setText(df.format(tAmount));
            }
        });
    }//GEN-LAST:event_additionalCaretUpdate

    private void amountPaidFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_amountPaidFocusGained
        // TODO add your handling code here:
        if (amountPaid.getText().equals("0.00")) {
            amountPaid.setText("");
        }
    }//GEN-LAST:event_amountPaidFocusGained

    private void amountPaidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_amountPaidFocusLost
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (amountPaid.getText().trim().isEmpty()) {
                    amountPaid.setText("0.00");
                } else {
                    amountPaid.setText(df.format(Double.parseDouble(amountPaid.getText().trim().replace(",", ""))));
                }
            }
        });
    }//GEN-LAST:event_amountPaidFocusLost

    private void totalAmountFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_totalAmountFieldCaretUpdate
        // TODO add your handling code here:
        try {
            Double.parseDouble(amountPaid.getText().trim().replace(",", ""));
            amtPd = Double.parseDouble(amountPaid.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                totalAmount = Double.parseDouble(totalAmountField.getText().trim().replace(",", "")) + Double.parseDouble(previousBalance.getText().trim().replace(",", "")) - amtPd;
                balance.setText(df.format(totalAmount));
            }
        });
    }//GEN-LAST:event_totalAmountFieldCaretUpdate

    private void gadgetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gadgetsActionPerformed
        if (gadgets.isSelected()) {
//            try {
//                tableModel = (DefaultTableModel) gadgetTable.getModel();
//                tableModel.getDataVector().removeAllElements();
//                tableModel.fireTableDataChanged();
//                gadgetTable.setModel(tableModel);
//                ArrayList<String[]> gadget = client.getResidentAndGadgetInfo(idnum);
//                for (String[] a : gadget) {
//                    tableModel.addRow(new Object[]{
//                        false,
//                        a[0],
//                        a[1]
//                    });
//                }
//            } catch (RemoteException ex) {
//                Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
//            }
            gadgetTable.setEnabled(true);
            gadgetRate.setEnabled(true);
            totalGadgetRate.setText(df.format(Double.parseDouble(gadgetRate.getText().trim().replace(",", ""))));
        } else {
//            tableModel = (DefaultTableModel) gadgetTable.getModel();
//            tableModel.setRowCount(0);
            gadgetTable.setEnabled(false);
//            gadgetRate.setText("");
            totalGadgetRate.setText("0.00");
        }
    }//GEN-LAST:event_gadgetsActionPerformed

    private void shuttleRateCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_shuttleRateCaretUpdate
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (shuttleRate.getText().trim().isEmpty()) {
                    shuttleRate.setText("0.00");
                } else {
                    totalShuttleRate.setText(df.format(Double.parseDouble(shuttleRate.getText().replace(",", ""))));
                }

            }
        });
    }//GEN-LAST:event_shuttleRateCaretUpdate

    private void gadgetRateCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_gadgetRateCaretUpdate
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (gadgetRate.getText().trim().isEmpty()) {
                    gadgetRate.setText("0.00");
                } else {
                    totalGadgetRate.setText(df.format(Double.parseDouble(gadgetRate.getText().replace(",", ""))));
                }

            }
        });
    }//GEN-LAST:event_gadgetRateCaretUpdate

    private void totalRoomRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalRoomRateFocusLost
        // TODO add your handling code here:
        if (totalRoomRate.getText().isEmpty()) {
            totalRoomRate.setText("0.00");
        }
    }//GEN-LAST:event_totalRoomRateFocusLost

    private void totalShuttleRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalShuttleRateFocusLost
        // TODO add your handling code here:
        if (totalShuttleRate.getText().isEmpty()) {
            totalShuttleRate.setText("0.00");
        }
    }//GEN-LAST:event_totalShuttleRateFocusLost

    private void totalGadgetRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalGadgetRateFocusLost
        // TODO add your handling code here:
        if (totalGadgetRate.getText().isEmpty()) {
            totalGadgetRate.setText("0.00");
        }
    }//GEN-LAST:event_totalGadgetRateFocusLost

    private void additionalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_additionalFocusLost
        // TODO add your handling code here:
        if (additional.getText().isEmpty()) {
            additional.setText("0.00");
        }
    }//GEN-LAST:event_additionalFocusLost

    private void shuttleRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_shuttleRateFocusLost
        // TODO add your handling code here:
        if (shuttleRate.getText().isEmpty()) {
            shuttleRate.setText("0.00");
        }
    }//GEN-LAST:event_shuttleRateFocusLost

    private void gadgetRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gadgetRateFocusLost
        // TODO add your handling code here:
        if (gadgetRate.getText().isEmpty()) {
            gadgetRate.setText("0.00");
        }
    }//GEN-LAST:event_gadgetRateFocusLost

    private void shuttleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shuttleActionPerformed
        if (shuttle.isSelected()) {
//                monthTypeRadioButton.setEnabled(true);
//                rideTypeRadioButton.setEnabled(true);
//                datesButton.setEnabled(true);
            totalShuttleRate.setText(df.format(Double.parseDouble(shuttleRate.getText().trim().replace(",", ""))));
            double[] shuttleRates = getShuttleRate();
            if (monthTypeRadioButton.isSelected()) {
//                    ridesNo.setEnabled(false);
//                    ridesNo.setEditable(false);
//                    datesButton.setEnabled(false);
            }
        } else {
//            shuttleRate.setEnabled(false);
//            monthTypeRadioButton.setEnabled(false);
//            rideTypeRadioButton.setEnabled(false);
//            datesButton.setEnabled(false);
            totalShuttleRate.setText("0.00");
        }
    }//GEN-LAST:event_shuttleActionPerformed

    private Date getFirstDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private Date getLastDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private void saveToPDF() {
        ArrayList<String> info = new ArrayList<>();
        info.add(nameLabel.getText().trim()); //0
        info.add(roomNumberLabel.getText().trim() + ": " + roomTypeLabel.getText().trim()); //1
        info.add(amountPaid.getText().trim()); //2
        info.add(adminName.getText()); //3
        info.add(controlNumber.getText().trim()); //4
        if (cash.isSelected()) {
            info.add("cash"); //5
        } else if (cheque.isSelected()) {
            info.add("cheque"); //5
        } else {
            info.add("bd"); //5
        }
        info.add(remarks.getText().trim()); //6
        info.add(idnum); //7

        if (savePaymentRemittanceToPDF(info)) {
            md.successful(this, "Done saving Statement of Account Form to PDF");
        } else {
            md.unsuccessful(this, "Failed to save file to PDF");
        }
    }

    private void setResidentControl() {
        String last = getLastControllNumber();
        String year = new SimpleDateFormat("yy").format(Calendar.getInstance().getTime());
        if (year.equals(last.substring(0, 2))) {
            String number = last.substring(2, 6);
            DecimalFormat df = new DecimalFormat("#0000");
            controlNumber.setText(year + df.format(Integer.parseInt(number) + 1));
        } else {
            controlNumber.setText(year + "0000");
        }
    }

    public String getDatein(String id, String date) {
        String d = "";
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT billingDatein FROM billing WHERE residentIdnum = ? AND billingDatePaid = ?");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, date);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                d = resultSet.getString("billingDatein");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return d;
    }

    public double getPreviousBalance(String id, String prev) {
        double balance = 0.0;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT billingBalance FROM billing WHERE residentIdnum = ? AND billingStatus = 'Paid' ORDER BY billingDatePaid DESC LIMIT 1");
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                balance = resultSet.getDouble("billingBalance");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return balance;
    }

    public ArrayList<String> getResidentAndRoomInfo(String residentId) {
        ArrayList<String> billing = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT CONCAT(residentFname, \" \" ,residentLname) AS residentName, "
                    + "residentHomeAddress, roomNumber, roomType  "
                    + "FROM resident NATURAL JOIN room WHERE residentIdnum = ?");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                billing.add(resultSet.getString("residentName"));
                billing.add(resultSet.getString("residentHomeAddress"));
                billing.add(resultSet.getString("roomNumber"));
                billing.add(resultSet.getString("roomType"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return billing;
    }

    public double[] getShuttleRate() {
        double[] rates = new double[2];
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rate");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rates[0] = resultSet.getDouble("shuttleRateDaily");
                rates[1] = resultSet.getDouble("shuttleRateMonthly");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return rates;
    }

    public String getBillingId(String residentId) {
        String id = "";
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT billingIdnum FROM billing WHERE residentIdnum = ?");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getString("billingIdnum");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return id;
    }

    public ArrayList<BillingGadgetImpl> getBillingGadget(String billingId) {
        ArrayList<BillingGadgetImpl> billingGadget = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT gadgetItemName, gadgetRate, isChecked FROM billing NATURAL JOIN billinggadget NATURAL JOIN gadget WHERE billinggadget.billingIdnum = ?");
            preparedStatement.setString(1, billingId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BillingGadgetImpl bill = new BillingGadgetImpl();
                bill.setItemName(resultSet.getString("gadgetItemName"));
                bill.setGadgetRate(resultSet.getDouble("gadgetRate"));
                bill.setIsSelected(resultSet.getBoolean("isChecked"));
                billingGadget.add(bill);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return billingGadget;
    }

    public ArrayList<String> getResidentAndBillingInfo(String residentId, String dateIn) {
        ArrayList<String> billing = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT * FROM billing NATURAL JOIN resident WHERE residentIdnum = ? AND billingDatePaid = ? ORDER BY billingDatePaid DESC, billingDatePaid DESC");
            preparedStatement.setString(1, residentId);
            preparedStatement.setDate(2, new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateIn).getTime()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                billing.add(resultSet.getString("billingDatein"));
                billing.add(resultSet.getString("billingDateout"));
                billing.add(resultSet.getString("billingRateType"));
                billing.add(resultSet.getString("billingNoofDays"));
                billing.add(resultSet.getString("billingTotalRoomrate"));
                billing.add(resultSet.getString("billingTotalShuttleRate"));
                billing.add(resultSet.getString("billingTotalGadgetRate"));
                billing.add(resultSet.getString("billingAdditionalFee"));
                billing.add(resultSet.getString("billingTotalAmount"));
                billing.add(resultSet.getString("billingAmountPaid"));
                billing.add(resultSet.getString("billingBalance"));
                billing.add(resultSet.getString("billingRemarks"));
                billing.add(resultSet.getString("billingShuttleRate"));
            }
        } catch (SQLException | ParseException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return billing;
    }

    public double getRoomRate(String roomType) {
        double rate = 0.0;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            switch (roomType) {
                case "Single Room":
                    preparedStatement = connection.prepareStatement("SELECT `rentRoomSingleSharing`  FROM rate");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rentRoomSingleSharing");
                    }
                    break;

                case "Double-Sharing Room":
                    preparedStatement = connection.prepareStatement("SELECT `rentRoomDoubleSharing`  FROM rate");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rentRoomDoubleSharing");
                    }
                    break;

                case "Triple-Sharing Room":
                    preparedStatement = connection.prepareStatement("SELECT `rentRoomTripleSharing`  FROM rate");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rentRoomTripleSharing");
                    }
                    break;

                default:
                    preparedStatement = connection.prepareStatement("SELECT `rentRoomMasterSuite`  FROM rate");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        rate = resultSet.getDouble("rentRoomMasterSuite");
                    }
                    break;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return rate;
    }

    public String getBillingShuttleType(String residentId) {
        String shuttleType = "";
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT * FROM billing NATURAL JOIN resident WHERE residentIdnum = ? AND billingStatus LIKE 'Unpaid' ORDER BY billingDatePaid DESC, billingDatePaid DESC LIMIT 1");
            preparedStatement.setInt(1, Integer.parseInt(residentId));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                shuttleType = resultSet.getString("billingShuttleRatetype");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return shuttleType;
    }

    public ArrayList<String> getBillingInfo(String residentIdnum) {
        ArrayList<String> info = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM billing WHERE residentIdnum = ? AND billingStatus LIKE 'Unpaid'");
            preparedStatement.setInt(1, Integer.parseInt(residentIdnum));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                info.add(rs.getString("billingIdnum")); //0
                info.add(rs.getString("billingDatePaid")); //1
                info.add(rs.getString("billingDatein")); //2
                info.add(rs.getString("billingDateout")); //3
                info.add(rs.getString("billingRateType")); //4
                info.add(rs.getString("billingNoofDays")); //5
                info.add(rs.getString("billingRoomRate")); //6
                info.add(rs.getString("billingShuttleRateType")); //7
                info.add(rs.getString("billingShuttleRate")); //8
                info.add(rs.getString("billingGadgetRate")); //9
                info.add(rs.getString("billingRemarks")); //10
                info.add(rs.getString("billingBalance")); //11
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
            return null;
        }
        return info;
    }

    public int getTotalRides(String residentIdnum) {
        int c = 0;
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT shuttleNoRides FROM shuttle WHERE residentIdnum = ? AND shuttleStatus = 'unpaid'");
            preparedStatement.setString(1, residentIdnum);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c += resultSet.getDouble("shuttleNoRides");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return c;
    }

    public boolean updateBilling(ArrayList<String> billingInfo) {
        boolean status = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `billing` SET "
                    + "`billingDatePaid` = ?, " //1
                    + "`billingRemarks` = ?, " //2
                    + "`billingTotalRoomrate` = ?, " //3
                    + "`billingTotalShuttlerate` = ?, " //4
                    + "`billingTotalGadgetrate` = ?, " //
                    + "`billingAdditionalfee` = ?, " //6
                    + "`billingTotalAmount` = ?, " //7
                    + "`billingAmountPaid` = ?, " //8
                    + "`billingStatus` = ?, " //9
                    + "`billingBalance` = ?, " //10
                    + "`adminIdnum` = ? " //11
                    + "WHERE residentIdnum = ? AND billingStatus LIKE 'Unpaid'"); //12
            if (billingInfo.get(0) != null) {
                preparedStatement.setString(1, billingInfo.get(0));
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (billingInfo.get(1) != null) {
                preparedStatement.setString(2, billingInfo.get(1));
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }
            if (billingInfo.get(2) != null) {
                preparedStatement.setDouble(3, Double.parseDouble(billingInfo.get(2)));
            } else {
                preparedStatement.setNull(3, Types.DOUBLE);
            }
            if (billingInfo.get(3) != null) {
                preparedStatement.setDouble(4, Double.parseDouble(billingInfo.get(3)));
            } else {
                preparedStatement.setNull(4, Types.DOUBLE);
            }
            if (billingInfo.get(4) != null) {
                preparedStatement.setDouble(5, Double.parseDouble(billingInfo.get(4)));
            } else {
                preparedStatement.setNull(5, Types.DOUBLE);
            }
            if (billingInfo.get(5) != null) {
                preparedStatement.setDouble(6, Double.parseDouble(billingInfo.get(5)));
            } else {
                preparedStatement.setNull(6, Types.DOUBLE);
            }
            if (billingInfo.get(6) != null) {
                preparedStatement.setDouble(7, Double.parseDouble(billingInfo.get(6)));
            } else {
                preparedStatement.setNull(7, Types.DOUBLE);
            }
            if (billingInfo.get(7) != null) {
                preparedStatement.setDouble(8, Double.parseDouble(billingInfo.get(7)));
            } else {
                preparedStatement.setNull(8, Types.DOUBLE);
            }
            preparedStatement.setString(9, "paid");
            if (billingInfo.get(8) != null) {
                preparedStatement.setDouble(10, Double.parseDouble(billingInfo.get(9)));
            } else {
                preparedStatement.setNull(10, Types.DOUBLE);
            }
            preparedStatement.setInt(11, Integer.parseInt(billingInfo.get(13).trim()));
            preparedStatement.setInt(12, Integer.parseInt(billingInfo.get(9)));
            status = preparedStatement.executeUpdate() == 1;

            if (status) {
                preparedStatement = connection.prepareStatement("SELECT billingIdnum FROM billing WHERE residentIdnum = ? ORDER BY BillingDatePaid DESC LIMIT 1");
                preparedStatement.setInt(1, Integer.parseInt(billingInfo.get(10)));
                ResultSet rs = preparedStatement.executeQuery();
                rs.next();
                preparedStatement = connection.prepareStatement("INSERT INTO controlNumber (controlNumber, type, id) VALUES (?,?,?)");
                preparedStatement.setInt(1, Integer.parseInt(billingInfo.get(11)));
                preparedStatement.setString(2, billingInfo.get(12).trim());
                preparedStatement.setInt(3, rs.getInt("billingIdnum"));
                preparedStatement.execute();
            }
        } catch (SQLException ex) {
            status = false;
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return status;
    }

    public ArrayList<String> getResidentAndBillingInfo(String residentId) {
        ArrayList<String> billing = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = connection.prepareStatement("SELECT * FROM (SELECT * FROM billing NATURAL JOIN resident ORDER BY billingDatein DESC, billingDatePaid DESC) as t1 WHERE residentIdnum = ? GROUP BY residentIdnum ORDER BY billingDatein DESC, billingDatePaid DESC");
            preparedStatement.setString(1, residentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                billing.add(resultSet.getString("billingDatein"));
                billing.add(resultSet.getString("billingDateout"));
                billing.add(resultSet.getString("billingRateType"));
                billing.add(resultSet.getString("billingNoofDays"));
                billing.add(resultSet.getString("billingTotalRoomrate"));
                billing.add(resultSet.getString("billingTotalShuttleRate"));
                billing.add(resultSet.getString("billingTotalGadgetRate"));
                billing.add(resultSet.getString("billingAdditionalFee"));
                billing.add(resultSet.getString("billingTotalAmount"));
                billing.add(resultSet.getString("billingAmountPaid"));
                billing.add(resultSet.getString("billingBalance"));
                billing.add(resultSet.getString("billingRemarks"));
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        return billing;
    }

    public String getAdminName(String id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT CONCAT(adminFname, ' ', adminLname) as name FROM admin WHERE adminIdnum = ?");
            preparedStatement.setInt(1, Integer.parseInt(id));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
            return null;
        }
        return null;
    }

    public boolean savePaymentRemittanceToPDF(ArrayList<String> info) {
        try {
            // TODO add your handling code here:
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

            Document doc = new Document();
            FileOutputStream fos = new FileOutputStream(path + "\\residentBilling\\" + info.get(0) + "Payment Remittance.pdf");
            PdfWriter.getInstance(doc, fos);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM billing WHERE residentIdnum = ? AND billingStatus LIKE 'unpaid'");
            preparedStatement.setInt(1, Integer.parseInt(info.get(7)));
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();

            PdfReader pdfReader = new PdfReader("Payment Remittance.pdf");

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                PdfContentByte content = pdfStamper.getOverContent(i);
                //Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 11);

                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Control Number: " + info.get(4), 300, 260, 0);
                if (info.get(5).equals("cash")) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 346, 218, 0);
                } else if (info.get(5).equals("cheque")) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 347, 198, 0);
                } else {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 347, 178, 0);
                }

                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(6), 330, 140, 0);
                //Date
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, new SimpleDateFormat("MM/dd/yyyy").format(dateLabel.getDateFormat().parse(dateLabel.getText())), 460, 260, 0);
                //Resident name
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(0), 90, 250, 0);
                //Purpose of payment
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Resident Monthly Billing", 90, 223, 0);
                //Room Details
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(1), 125, 183, 0);
                //Amount
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(2), 125, 158, 0);
                //Received By admin  name!
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, info.get(3), 125, 105, 0);
                content.endText();

            }
            pdfStamper.close();
            pdfReader.close();
            fos.close();

            return true;
        } catch (DocumentException | SQLException | ParseException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
            return false;
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
            return false;
        } catch (IOException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
            return false;
        }
    }

    public String getLastControllNumber() {
        String last = new SimpleDateFormat("yy").format(Calendar.getInstance().getTime()) + "0000";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT controlNumber FROM controlNumber ORDER BY controlNumber DESC LIMIT 1");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (rs.getString("controlNumber") != null) {
                    return rs.getString("controlNumber");
                } else {
                    return last;
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }

        return last;
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
            java.util.logging.Logger.getLogger(Billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Billing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Billing(null, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addCheckbox;
    private javax.swing.JFormattedTextField additional;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JTextField adminName;
    private javax.swing.JFormattedTextField amountPaid;
    private javax.swing.JFormattedTextField balance;
    private javax.swing.JRadioButton bd;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton cash;
    private javax.swing.JRadioButton cheque;
    private javax.swing.JTextField controlNumber;
    private javax.swing.JLabel dateInLabel;
    private datechooser.beans.DateChooserCombo dateLabel;
    private javax.swing.JLabel dateOutLabel;
    private javax.swing.JButton datesButton;
    private javax.swing.JFormattedTextField gadgetRate;
    private javax.swing.JTable gadgetTable;
    private javax.swing.JCheckBox gadgets;
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton monthTypeRadioButton;
    private javax.swing.JLabel name;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JFormattedTextField noDays;
    private javax.swing.JLabel previousBalance;
    private javax.swing.ButtonGroup rateType;
    private javax.swing.JTextArea remarks;
    private javax.swing.JRadioButton rideTypeRadioButton;
    private javax.swing.JFormattedTextField ridesNo;
    private javax.swing.JLabel roomNumberLabel;
    private javax.swing.JFormattedTextField roomRate;
    private javax.swing.JCheckBox roomRateCheckBox;
    private javax.swing.JLabel roomRateLabel;
    private javax.swing.JLabel roomTypeLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JCheckBox shuttle;
    private javax.swing.JFormattedTextField shuttleRate;
    private javax.swing.ButtonGroup shuttleType;
    private javax.swing.JFormattedTextField totalAmountField;
    private javax.swing.JFormattedTextField totalGadgetRate;
    private javax.swing.JFormattedTextField totalRoomRate;
    private javax.swing.JFormattedTextField totalShuttleRate;
    // End of variables declaration//GEN-END:variables
}
