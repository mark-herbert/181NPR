/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mark Herbert Cabuang
 */
import java.awt.Toolkit;
import java.rmi.RemoteException;
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

public class Billing_v2 extends javax.swing.JFrame {

    private final NPRInterface client;
    private String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    private DefaultTableModel tableModel = new DefaultTableModel();
    private ShuttleService ss;
    private String idnum = "";
    private String previousDate = "";
    private double balance = 0.0;

    private double totalAmount = 0.0;
    private double billingBalance = 0.0;
    private double rRate = 0.0;
    private double sRate = 0.0;
    private double gRate = 0.0;
    private double amt = 0.0;
    private double amtPd = 0.0;
    private double bal = 0.0;
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    private String user = "";

    private int c = 0;
    private MessageDialog md = new MessageDialog();

    /**
     * Creates new form Billing_v2
     *
     * @param client
     * @param idnum
     * @param prevDate
     * @param user
     */
    public Billing_v2(NPRInterface client, String idnum, String prevDate, String user) {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Billing.png")));
        initComponents();
        this.client = client;
        this.idnum = idnum;
        previousDate = prevDate;
        this.user = user;
        try {
            ss = new ShuttleService(this.client, this.idnum, ridesNo);
        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(Billing.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }

        try {
            gadgetRate.setText("0.00");
            ArrayList<String> billing = client.getResidentAndRoomInfo(idnum);
            nameLabel.setText(billing.get(0));
            addressLabel.setText(billing.get(1));
            roomNumberLabel.setText(billing.get(2));
            switch (billing.get(3)) {
                case "T":
                    roomTypeLabel.setText("Triple-Sharing Room");
                    break;
                case "S":
                    roomTypeLabel.setText("Single Room");
                    break;
                case "D":
                    roomTypeLabel.setText("Double-Sharing Room");
                    break;
            }
            switch (roomTypeLabel.getText().trim()) {
                case "Triple-Sharing Room":
                    roomRateLabel.setText(client.getRoomRate("Triple-Sharing Room")+"");
                    break;
                case "Double-Sharing Room":
                    roomRateLabel.setText(client.getRoomRate("Double-Sharing Room")+"");
                    break;
                case "Single Room":
                    roomRateLabel.setText(client.getRoomRate("Single Room")+"");
                    break;
            }
            if (client.residentHasPreviousStatement(idnum)) {
                ArrayList<String> info = client.getBillingInfo(idnum);
                if(info.get(7) != null){
                    shuttle.setSelected(true);
                    if(info.get(7).equals("Monthly")){
                        monthTypeRadioButton.setSelected(true);
                        datesButton.setEnabled(false);
                        ridesNo.setEnabled(false);
                        jLabel19.setEnabled(false);
                    } else {
                        rideTypeRadioButton.setSelected(true);
                        datesButton.setEnabled(true);
                        ridesNo.setEnabled(true);
                    }
                    if(info.get(8) != null){
                        shuttleRate.setText(df.format(Double.parseDouble(info.get(8))));
                    }
                }
                dateInLabel.setText(info.get(2));
                dateOutLabel.setText(info.get(3));
                roomRate.setText(info.get(6));
                noDays.setText(info.get(5));
                
                tableModel = (DefaultTableModel) gadgetTable.getModel();
                tableModel.getDataVector().removeAllElements();
                tableModel.fireTableDataChanged();
                gadgetTable.setModel(tableModel);
                ArrayList<BillingGadgetImpl> gadget = client.getBillingGadget(client.getBillingId(idnum));
                for (BillingGadgetImpl a : gadget) {
                    tableModel.addRow(new Object[]{
                        a.isIsSelected(),
                        a.getItemName(),
                        a.getGadgetRate()
                    });
                    if (a.isIsSelected()) {
                        double d = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
                        gadgetRate.setText((d + a.getGadgetRate()) + "");
                    }
                }
            } else {
                shuttle.setSelected(false);
                datesButton.setEnabled(false);
                monthTypeRadioButton.setEnabled(false);
                rideTypeRadioButton.setEnabled(false);
                shuttleRate.setEnabled(false);
                shuttleRate.setText("0.00");

                tableModel = (DefaultTableModel) gadgetTable.getModel();
                tableModel.getDataVector().removeAllElements();
                tableModel.fireTableDataChanged();
                gadgetTable.setModel(tableModel);
                ArrayList<String[]> gadget = client.getResidentAndGadgetInfo(idnum);
                for (String[] a : gadget) {
                    tableModel.addRow(new Object[]{
                        false,
                        a[0],
                        a[1]
                    });
                }
                ArrayList<String> billingInfo = client.getResidentAndBillingInfo(this.idnum);
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(billingInfo.get(1));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, 1);
                date = cal.getTime();
                String e = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                dateInLabel.setText(e);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                String d = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                dateOutLabel.setText(d);
                int numberOfDays = cal.getTime().getDate() - date.getDate();
                noDays.setText(numberOfDays + "");

                double monthlyRate = 0.0;
                switch (roomTypeLabel.getText().trim()) {
                    case "Triple-Sharing Room":
                        monthlyRate = client.getRoomRate("Triple-Sharing Room");
                        break;
                    case "Double-Sharing Room":
                        monthlyRate = client.getRoomRate("Double-Sharing Room");
                        break;
                    case "Single Room":
                        monthlyRate = client.getRoomRate("Single Room");
                        break;
                }
                roomRateLabel.setText(df.format(monthlyRate));
                double rateOnDays = 0.0;
                if (numberOfDays < cal.getTime().getDate()) {
                    double ratePerDay = monthlyRate / 30;
                    rateOnDays = ratePerDay * numberOfDays;
                } else {
                    rateOnDays = monthlyRate;
                }

                int dd = new SimpleDateFormat("yyyy-MM-dd").parse(dateInLabel.getText()).getDate();
                roomRateLabel.setText(df.format(monthlyRate));
                if (dd == 1) {
                    roomRate.setText(df.format(monthlyRate));
                    noDays.setText(cal.getTime().getDate() + "");
                } else {
                    double ratePerDay = monthlyRate / 30;
                    double rateOnDay = ratePerDay * numberOfDays;
                    roomRate.setText(df.format(rateOnDay));
                }
                rRate = Double.parseDouble(roomRate.getText().trim().replace(",", ""));
                sRate = Double.parseDouble(shuttleRate.getText().trim().replace(",", ""));
                gRate = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
                amt = rRate + sRate + gRate;

                totalAmountTextField.setText(df.format(amt));

            }
        } catch (RemoteException | ParseException ex) {
//            Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }

    }

    public Billing_v2(NPRInterface client) {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/Billing.png")));
        initComponents();
        this.client = client;
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
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        name = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        dueDate = new datechooser.beans.DateChooserCombo();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        roomNumberLabel = new javax.swing.JLabel();
        roomTypeLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        roomRateLabel = new javax.swing.JLabel();
        dateInLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        roomRate = new javax.swing.JFormattedTextField();
        noDays = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        dateOutLabel = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        monthTypeRadioButton = new javax.swing.JRadioButton();
        rideTypeRadioButton = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        datesButton = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        ridesNo = new javax.swing.JFormattedTextField();
        shuttleRate = new javax.swing.JFormattedTextField();
        shuttle = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        gadgetTable = new javax.swing.JTable();
        gadgetRate = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        remarks = new javax.swing.JTextArea();
        clearButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        totalAmountTextField = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        nameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        nameLabel.setText("Name:");
        jPanel2.add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 22, 470, -1));

        addressLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        addressLabel.setText("Address:");
        jPanel2.add(addressLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 48, 470, -1));

        dueDate.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
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
    dueDate.setNothingAllowed(false);
    dueDate.setFormat(2);
    dueDate.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
    jPanel2.add(dueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 20, -1, -1));

    jLabel3.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel3.setText("Due Date");
    jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 20, 50, 20));

    jPanel3.setBackground(new java.awt.Color(255, 255, 255));
    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Room Rate", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Rondalo", 0, 16))); // NOI18N
    jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    roomNumberLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    roomNumberLabel.setText("Room Number:");
    jPanel3.add(roomNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 170, -1));

    roomTypeLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    roomTypeLabel.setText("Room Type:");
    jPanel3.add(roomTypeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 180, -1));

    jLabel6.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel6.setText("Room Number:");
    jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 33, -1, -1));

    jLabel21.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel21.setText("Room Type:");
    jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 54, 69, -1));

    jLabel28.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel28.setText("Start Date:");
    jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 60, -1));

    jLabel27.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel27.setText("Room Rate:");
    jPanel3.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 80, 70, -1));

    roomRateLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    roomRateLabel.setText("Room Type:");
    jPanel3.add(roomRateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 80, 120, -1));

    dateInLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    dateInLabel.setText("Room Type:");
    jPanel3.add(dateInLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 100, -1));

    jLabel11.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel11.setText("Monthly Rate:");
    jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 69, -1));

    roomRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
    roomRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    roomRate.setText("0.00");
    roomRate.addCaretListener(new javax.swing.event.CaretListener() {
        public void caretUpdate(javax.swing.event.CaretEvent evt) {
            roomRateCaretUpdate(evt);
        }
    });
    roomRate.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
            roomRateFocusLost(evt);
        }
    });
    jPanel3.add(roomRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 120, -1));

    noDays.setEditable(false);
    noDays.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
    jPanel3.add(noDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, 50, -1));

    jLabel24.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel24.setText("No. of Days:");
    jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 180, 60, 20));

    jLabel22.setFont(new java.awt.Font("Rondalo", 2, 12)); // NOI18N
    jLabel22.setForeground(new java.awt.Color(255, 0, 0));
    jLabel22.setText("*Room rate / 30 days");
    jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 150, 120, 20));

    dateOutLabel.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    dateOutLabel.setText("Room Type:");
    jPanel3.add(dateOutLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 100, -1));

    jLabel10.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel10.setText("End Date:");
    jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 50, -1));

    jPanel5.setBackground(new java.awt.Color(255, 255, 255));
    jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Other Rate", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Rondalo", 0, 16))); // NOI18N
    jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    jPanel6.setBackground(new java.awt.Color(255, 255, 255));
    jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel18.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel18.setText("Rate Type");

    monthTypeRadioButton.setBackground(new java.awt.Color(255, 255, 255));
    buttonGroup1.add(monthTypeRadioButton);
    monthTypeRadioButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    monthTypeRadioButton.setText("Monthly ");
    monthTypeRadioButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    monthTypeRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            monthTypeRadioButtonActionPerformed(evt);
        }
    });

    rideTypeRadioButton.setBackground(new java.awt.Color(255, 255, 255));
    buttonGroup1.add(rideTypeRadioButton);
    rideTypeRadioButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    rideTypeRadioButton.setText("Rides");
    rideTypeRadioButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    rideTypeRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            rideTypeRadioButtonActionPerformed(evt);
        }
    });

    jLabel19.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel19.setText("No. of Rides:");

    datesButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    datesButton.setText("Date/s Availed");
    datesButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    datesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            datesButtonActionPerformed(evt);
        }
    });

    jLabel20.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel20.setText("Shuttle Rate:");

    ridesNo.setEditable(false);
    ridesNo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
    ridesNo.setText("0");
    ridesNo.addCaretListener(new javax.swing.event.CaretListener() {
        public void caretUpdate(javax.swing.event.CaretEvent evt) {
            ridesNoCaretUpdate(evt);
        }
    });

    shuttleRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
    shuttleRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    shuttleRate.setText("0.00");
    shuttleRate.addCaretListener(new javax.swing.event.CaretListener() {
        public void caretUpdate(javax.swing.event.CaretEvent evt) {
            shuttleRateCaretUpdate(evt);
        }
    });

    shuttle.setBackground(new java.awt.Color(255, 255, 255));
    shuttle.setText("Shuttle Rate");
    shuttle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    shuttle.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            shuttleActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addComponent(shuttle)
                    .addGap(74, 74, 74)
                    .addComponent(jLabel19)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18)
                    .addGap(18, 18, 18)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(rideTypeRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(monthTypeRadioButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel20)
                    .addGap(12, 12, 12)))
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(shuttleRate)
                .addComponent(datesButton, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addComponent(ridesNo))
            .addContainerGap())
    );
    jPanel6Layout.setVerticalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGap(26, 26, 26)
                            .addComponent(datesButton, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel19)
                                .addComponent(ridesNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(0, 33, Short.MAX_VALUE)))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(shuttleRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20)))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                    .addComponent(shuttle)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(monthTypeRadioButton))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGap(26, 26, 26)
                            .addComponent(rideTypeRadioButton)))))
            .addContainerGap(26, Short.MAX_VALUE))
    );

    jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 22, 370, 120));

    jPanel7.setBackground(new java.awt.Color(255, 255, 255));
    jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
            true, false, false
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
    }

    gadgetRate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
    gadgetRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    gadgetRate.setText("0.00");
    gadgetRate.addCaretListener(new javax.swing.event.CaretListener() {
        public void caretUpdate(javax.swing.event.CaretEvent evt) {
            gadgetRateCaretUpdate(evt);
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
                    .addGap(0, 141, Short.MAX_VALUE)
                    .addComponent(jLabel23)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(gadgetRate, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    jPanel7Layout.setVerticalGroup(
        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 23, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel23)
                .addComponent(gadgetRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    jPanel5.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 153, 370, -1));

    jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
    jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Remarks", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Rondalo", 0, 16))); // NOI18N
    jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    remarks.setColumns(20);
    remarks.setLineWrap(true);
    remarks.setRows(5);
    jScrollPane1.setViewportView(remarks);

    clearButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    clearButton.setText("Clear");
    clearButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    clearButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            clearButtonActionPerformed(evt);
        }
    });

    saveButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    saveButton.setText("Save");
    saveButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    saveButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveButtonActionPerformed(evt);
        }
    });

    cancelButton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    cancelButton.setText("Back");
    cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelButtonActionPerformed(evt);
        }
    });

    jLabel25.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
    jLabel25.setText("Total Amount: ");

    totalAmountTextField.setEditable(false);
    totalAmountTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
    totalAmountTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    totalAmountTextField.setText("0.00");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalAmountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(totalAmountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton)
                    .addComponent(clearButton)))
            .addContainerGap(19, Short.MAX_VALUE))
    );

    getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 540));

    pack();
    setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void datesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datesButtonActionPerformed
        if (!ss.isVisible()) {
            ss.setVisible(true);
            ss.setAlwaysOnTop(true);
        }
    }//GEN-LAST:event_datesButtonActionPerformed

    private void ridesNoCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_ridesNoCaretUpdate
        try {
            if(rideTypeRadioButton.isSelected()) {
                double[] shuttleRates = client.getShuttleRate();
                double rideRate = 0.0;
                rideRate = Double.parseDouble(shuttleRates[0] + "");
                double noOfRides = 0.0;
                double prev = 0.0;
                if (!ridesNo.getText().trim().isEmpty()) {
//                if (Double.parseDouble(shuttleRate.getText()) != 0.0) {
//                    prev = Double.parseDouble(shuttleRate.getText());
//                }
                    noOfRides = Double.parseDouble(ridesNo.getText().trim().replace(",", ""));
                }
                rideRate *= noOfRides;
                shuttleRate.setText(df.format(rideRate + prev));
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_ridesNoCaretUpdate

    private void gadgetTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gadgetTableMouseClicked
        try {
            tableModel = (DefaultTableModel) gadgetTable.getModel();
            double c = 0.0;
            for (int count = 0; count < tableModel.getRowCount(); count++) {
                if (((Boolean) tableModel.getValueAt(count, 0)) && tableModel.getValueAt(count, 2) != null) {
                    c += Double.parseDouble(tableModel.getValueAt(count, 2).toString().replace(",", ""));
                }
            }
            gadgetRate.setText(df.format(c));
        } catch (NullPointerException npe) {

        }
    }//GEN-LAST:event_gadgetTableMouseClicked

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (md.confirmationSave(this) == md.YES) {
            try {
                saveToPDF();
                ArrayList<String> billing = new ArrayList<>();
                billing.add(timeStamp);//1
                if (Double.parseDouble(roomRateLabel.getText().trim().replace(",", "")) == Double.parseDouble(roomRate.getText().trim().replace(",", ""))) {
                    billing.add(new SimpleDateFormat("yyyy-MM-dd").format(getFirstDateOfCurrentMonth()));//2
                    billing.add(new SimpleDateFormat("yyyy-MM-dd").format(getLastDateOfCurrentMonth()));//3
                    billing.add("Monthly");//4
                    billing.add(noDays.getText().trim());//5
                    billing.add(roomRate.getText().trim().replace(",", ""));//6
                    rRate = Double.parseDouble(roomRate.getText().trim().replace(",", ""));//7
                } else {
                    billing.add(new SimpleDateFormat("yyyy-MM-dd").format(getFirstDateOfCurrentMonth()));//2
                    billing.add(new SimpleDateFormat("yyyy-MM-dd").format(getLastDateOfCurrentMonth()));//3
                    billing.add("Daily");//4
                    billing.add(noDays.getText().trim());//5
                    billing.add(roomRate.getText().trim().replace(",", ""));//6
                    rRate = Double.parseDouble(roomRate.getText().trim().replace(",", ""));//7
                }

                //shuttle
                if (shuttle.isSelected()) {
                    if (monthTypeRadioButton.isSelected()) {
                        billing.add("Monthly");//8
                        billing.add(client.getShuttleRate()[1] + "");//9
                        sRate = Double.parseDouble(shuttleRate.getText().trim().replace(",", ""));
                    } else {
                        billing.add("Daily");//8
                        billing.add(client.getShuttleRate()[0] * Integer.parseInt(ridesNo.getText().trim()) + "");//9
                        sRate = Double.parseDouble(shuttleRate.getText().trim().replace(",", ""));
                    }
                } else {
                    billing.add(null);//8
                    billing.add(null);//9
                }

                //gadget
                if (!gadgetRate.getText().isEmpty()) {
                    billing.add(gadgetRate.getText().trim().replace(",", ""));//10
                    gRate = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
                } else {
                    billing.add(null);//10
                    gRate = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
                }

                if (!remarks.getText().isEmpty()) {
                    billing.add(remarks.getText().trim());//11
                } else {
                    billing.add(null);//11
                }

                billing.add(this.idnum);//12
                billing.add(client.getRoomId(roomNumberLabel.getText().trim()));//13
                amt = rRate + sRate + gRate;
                billing.add(amt + "");//14
                amtPd = client.getPreviousBalance(idnum, previousDate);
                amt += amtPd;
                billing.add(amt + "");//15
                billing.add(null);//16
                billing.add(null);//17
                billing.add(null);//18
                billing.add(null);//19
                billing.add("Unpaid");//20
                //insertInto billingGadget 

                if (client.insertBilling(billing)) {
                    tableModel = (DefaultTableModel) gadgetTable.getModel();
                    for (int c = 0; c < tableModel.getRowCount(); c++) {
                        if (!client.insertBillingGadget(client.getBillingId(idnum),
                                client.getGadgetId(tableModel.getValueAt(c, 1).toString(), idnum),
                                (Boolean) tableModel.getValueAt(c, 0))) {
                            JOptionPane.showMessageDialog(this, "Failed!", "Failed", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new Billing_Resident(client, user).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed!", "Failed", JOptionPane.WARNING_MESSAGE);
                }

            } catch (RemoteException | ParseException ex) {
//                Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, ex.getMessage());
            }
        }

    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (md.confirmationCancel(this) == md.YES) {
            boolean status = true;
            try {
                if (!client.deleteShuttle(idnum)) {
                    status = false;
                }
            } catch (RemoteException ex) {
//                Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, ex.getMessage());
            }
            if (status) {
                new Billing_Resident(client, user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed!", "Failed", JOptionPane.WARNING_MESSAGE);
            }

        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clearButtonActionPerformed

    private void gadgetRateCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_gadgetRateCaretUpdate
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                double shuttleR = Double.parseDouble(shuttleRate.getText().trim().replace(",", ""));
                double gadgetR = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
                double roomR = Double.parseDouble(roomRate.getText().trim().replace(",", ""));
                double total = shuttleR + gadgetR + roomR;
                totalAmountTextField.setText(df.format(total));
            }
        });
    }//GEN-LAST:event_gadgetRateCaretUpdate

    private void roomRateCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_roomRateCaretUpdate
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                double shuttleR = Double.parseDouble(shuttleRate.getText().trim().replace(",", ""));
                double gadgetR = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
                double roomR = Double.parseDouble(roomRate.getText().trim().replace(",", ""));
                double total = shuttleR + gadgetR + roomR;
                totalAmountTextField.setText(df.format(total));
            }
        });
    }//GEN-LAST:event_roomRateCaretUpdate

    private void shuttleRateCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_shuttleRateCaretUpdate
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                double shuttleR = Double.parseDouble(shuttleRate.getText().trim().replace(",", ""));
                double gadgetR = Double.parseDouble(gadgetRate.getText().trim().replace(",", ""));
                double roomR = Double.parseDouble(roomRate.getText().trim().replace(",", ""));
                double total = shuttleR + gadgetR + roomR;
                totalAmountTextField.setText(df.format(total));
            }
        });
    }//GEN-LAST:event_shuttleRateCaretUpdate

    private void shuttleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shuttleActionPerformed
        if (shuttle.isSelected()) {
            try {
                ridesNo.setEnabled(false);
                ridesNo.setEditable(false);
                datesButton.setEnabled(false);
                monthTypeRadioButton.setEnabled(true);
                rideTypeRadioButton.setEnabled(true);
                shuttleRate.setEnabled(true);
                double[] shuttleRates = client.getShuttleRate();
                if (monthTypeRadioButton.isSelected() || rideTypeRadioButton.isSelected()) {
                    if (monthTypeRadioButton.isSelected()) {
                        shuttleRate.setText(df.format(shuttleRates[1]));
                        ridesNo.setEnabled(false);
                        ridesNo.setEditable(false);
                        datesButton.setEnabled(false);
                    } else {
                        shuttleRate.setText(df.format(shuttleRates[0]));
                    }
                } else {
                    monthTypeRadioButton.setSelected(true);
                    shuttleRate.setText(df.format(shuttleRates[1]));
                    ridesNo.setEnabled(false);
                    ridesNo.setEditable(false);
                    datesButton.setEnabled(false);
                }
            } catch (RemoteException ex) {
//                Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, ex.getMessage());
            }
        } else {
            datesButton.setEnabled(false);
            monthTypeRadioButton.setSelected(false);
            rideTypeRadioButton.setSelected(false);
            monthTypeRadioButton.setEnabled(false);
            rideTypeRadioButton.setEnabled(false);
            shuttleRate.setEnabled(false);
            shuttleRate.setText("0.00");
            datesButton.setEnabled(false);
            ridesNo.setEnabled(false);

        }
    }//GEN-LAST:event_shuttleActionPerformed

    private void monthTypeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthTypeRadioButtonActionPerformed
        try {
            // TODO add your handling code here:
            ridesNo.setEnabled(false);
            datesButton.setEnabled(false);
            double[] shuttleRates = client.getShuttleRate();
            if (monthTypeRadioButton.isSelected()) {
                shuttleRate.setText(df.format(shuttleRates[1]));
                ridesNo.setEnabled(false);
                datesButton.setEnabled(false);
            } else {
                shuttleRate.setText(df.format(shuttleRates[0]));
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_monthTypeRadioButtonActionPerformed

    private void rideTypeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rideTypeRadioButtonActionPerformed
        ridesNo.setEnabled(true);
        datesButton.setEnabled(true);
        shuttleRate.setText("0.00");
        try {
            shuttleRate.setText(df.format(client.getShuttleRate()[0]*Double.parseDouble(ridesNo.getText())));
        } catch (RemoteException ex) {
            Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_rideTypeRadioButtonActionPerformed

    private void roomRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roomRateFocusLost
        // TODO add your handling code here:
        if (roomRate.getText().isEmpty()) {
            roomRate.setText("0.00");
        }
    }//GEN-LAST:event_roomRateFocusLost

    private Date getFirstDateOfCurrentMonth() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateInLabel.getText().trim());
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
//        return cal.getTime();
    }

    private Date getLastDateOfCurrentMonth() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateOutLabel.getText().trim());
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
//        return cal.getTime();
    }

    private void saveToPDF() {
        ArrayList<String> info = new ArrayList<>();
        info.add(nameLabel.getText().trim()); //0
        info.add(roomNumberLabel.getText().trim()); //1
        info.add(roomRate.getText().trim().replace(",", "")); // 2
        info.add(shuttleRate.getText().trim().replace(",", "")); //3
        info.add(gadgetRate.getText().trim().replace(",", "")); //4
        info.add("0.00"); //5
        info.add(dateInLabel.getText().trim()); //6
        info.add(dueDate.getText().trim()); //7

        try {
            if (client.saveStatementOfAccountToPDF(info)) {
                md.successful(this, "Done saving Statement of Account Form to PDF");
            } else {
                md.unsuccessful(this, "Failed to save file to PDF");
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(Billing_v2.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Billing_v2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Billing_v2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Billing_v2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Billing_v2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Billing_v2(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addressLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel dateInLabel;
    private javax.swing.JLabel dateOutLabel;
    private javax.swing.JButton datesButton;
    private datechooser.beans.DateChooserCombo dueDate;
    private javax.swing.JFormattedTextField gadgetRate;
    private javax.swing.JTable gadgetTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton monthTypeRadioButton;
    private javax.swing.JLabel name;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JFormattedTextField noDays;
    private javax.swing.ButtonGroup rateType;
    private javax.swing.JTextArea remarks;
    private javax.swing.JRadioButton rideTypeRadioButton;
    private javax.swing.JFormattedTextField ridesNo;
    private javax.swing.JLabel roomNumberLabel;
    private javax.swing.JFormattedTextField roomRate;
    private javax.swing.JLabel roomRateLabel;
    private javax.swing.JLabel roomTypeLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JCheckBox shuttle;
    private javax.swing.JFormattedTextField shuttleRate;
    private javax.swing.JFormattedTextField totalAmountTextField;
    // End of variables declaration//GEN-END:variables
}
