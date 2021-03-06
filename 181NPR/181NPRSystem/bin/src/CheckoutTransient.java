
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.SwingUtilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kenneth
 */
public class CheckoutTransient extends javax.swing.JDialog {

    private final String transientIdnum;
    private final NPRInterface client;
    private final String fname;
    private final DecimalFormat decF = new DecimalFormat("#,##0.00");
    private final MessageDialog md = new MessageDialog();
    private String balanceTransient = "0.00";
    private final String user;
    
    /**
     * Creates new form CheckoutTransient
     * @param parent
     * @param modal
     * @param client
     * @param transientIdnum
     * @param fname
     */
    public CheckoutTransient(java.awt.Frame parent, boolean modal, NPRInterface client, String transientIdnum, String fname, String user) {
        super(parent, modal);
        initComponents();
        this.client = client;
        this.transientIdnum = transientIdnum;
        this.fname = fname;
        this.user = user;
        setInformation();
        setTransientControl();
        if(balance.getText().equals("0.00") || (balance.getText().contains("(") && balance.getText().contains(")"))){
            checkout.setEnabled(true);
            amountTendered.setEditable(false);
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

        updater = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        arrDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        depDate = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        accBill = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lastAmountPaid = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        remBill = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        amountTendered = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        balance = new javax.swing.JTextField();
        cancel = new javax.swing.JButton();
        checkout = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        cash = new javax.swing.JRadioButton();
        cheque = new javax.swing.JRadioButton();
        bd = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        remarks = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        controlNumber = new javax.swing.JTextField();

        updater.setText("0.0");
        updater.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                updaterCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        jLabel1.setText("CHECKOUT");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel2.setText("Name:");

        name.setEditable(false);
        name.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel3.setText("Date of Arrival:");

        arrDate.setEditable(false);
        arrDate.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel4.setText("Date of Departure:");

        depDate.setEditable(false);
        depDate.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel8.setText("Accumulated Bill:");

        accBill.setEditable(false);
        accBill.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        accBill.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        accBill.setText("0.00");

        jLabel9.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel9.setText("Total Amount Paid:");

        lastAmountPaid.setEditable(false);
        lastAmountPaid.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        lastAmountPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        lastAmountPaid.setText("0.00");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel9))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastAmountPaid)
                    .addComponent(arrDate, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(depDate, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(accBill)
                    .addComponent(name))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(arrDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(depDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(accBill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lastAmountPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        jLabel6.setText("Remaining Bill:");

        remBill.setEditable(false);
        remBill.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        remBill.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        remBill.setText("0.00");

        jLabel7.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        jLabel7.setText("Payment:");

        amountTendered.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        amountTendered.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        amountTendered.setText("0.00");
        amountTendered.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                amountTenderedCaretUpdate(evt);
            }
        });
        amountTendered.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                amountTenderedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                amountTenderedFocusLost(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        jLabel5.setText("Balance:");

        balance.setEditable(false);
        balance.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        balance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        balance.setText("0.00");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remBill, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(amountTendered)
                    .addComponent(balance))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(remBill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(amountTendered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(balance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancel.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        cancel.setText("Cancel");
        cancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        checkout.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        checkout.setText("Checkout");
        checkout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        checkout.setEnabled(false);
        checkout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkoutActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mode of Payment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        cash.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(cash);
        cash.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cash.setSelected(true);
        cash.setText("Cash");
        cash.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cheque.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(cheque);
        cheque.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cheque.setText("Cheque");
        cheque.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        bd.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(bd);
        bd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bd.setText("Bank Deposit");
        bd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cash)
                .addGap(18, 18, 18)
                .addComponent(cheque)
                .addGap(18, 18, 18)
                .addComponent(bd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cash)
                    .addComponent(cheque)
                    .addComponent(bd))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        remarks.setColumns(20);
        remarks.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        remarks.setLineWrap(true);
        remarks.setRows(5);
        remarks.setWrapStyleWord(true);
        remarks.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Remarks", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        jScrollPane1.setViewportView(remarks);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Control Number"));

        controlNumber.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(controlNumber)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(controlNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkout)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(checkout)
                            .addComponent(cancel))))
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

    private void amountTenderedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_amountTenderedFocusGained
        // TODO add your handling code here:
        if(amountTendered.getText().trim().equals("0.00") && amountTendered.isEditable()){
            amountTendered.setText("");
        }
    }//GEN-LAST:event_amountTenderedFocusGained

    private void amountTenderedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_amountTenderedFocusLost
        // TODO add your handling code here:
        if(amountTendered.isEditable()){
            if(amountTendered.getText().trim().isEmpty()){
                amountTendered.setText("0.00");
            } else {
                amountTendered.setText(decF.format(Double.parseDouble(amountTendered.getText().replace(",", ""))));
            }
            String rem = remBill.getText().trim().replace(",", "");
            if(rem.contains("(") && rem.contains(")")){
                rem = "-" + rem.substring(1,rem.length()-1).trim();
            }
            double bal = Double.parseDouble(rem.trim()) - Double.parseDouble(amountTendered.getText().trim().replace(",", ""));

            if(bal < 0){
                checkout.setEnabled(true);
                balance.setText("( "+ (bal * -1) +" )");
            } else if(bal == 0){
                checkout.setEnabled(true);
                balance.setText(decF.format(bal));
            } else {
                checkout.setEnabled(false);
                balance.setText(decF.format(bal));
            }
        }
    }//GEN-LAST:event_amountTenderedFocusLost

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_cancelActionPerformed

    private void checkoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkoutActionPerformed
        // TODO add your handling code here:
        try {
            boolean tester1 = true;
            boolean tester2 = true;
            boolean tester3 = true;
            String mode = "";
            if(cash.isSelected()){
                mode = "cash";
            } else if(cheque.isSelected()){
                mode = "cheque";
            } else {
                mode = "bd";
            }
            if(md.confirmationCheckout(null) == md.YES){
                if(client.saveTransientBillingToPdf(transientIdnum, amountTendered.getText().trim().replace(",", ""), client.getAdminName(user), controlNumber.getText(), mode, remarks.getText())){
                    md.successful(null, "Done saving Transient Form to PDF");
                } else {
                    md.unsuccessful(null, "Failed to save file to PDF.");
                }
                if(client.updateTransientStatus("Checkout", transientIdnum)){
                    if(!client.updateTransientBill(transientIdnum, Double.parseDouble(accBill.getText().trim().replace(",", "")), user)){
                        md.error(null, "Error Updating Bill");
                    }
                    ArrayList<String> info = new ArrayList<>();
                    if(cash.isSelected()){
                        info.add("cash");
                    } else if(cheque.isSelected()){
                        info.add("cheque");
                    } else {
                        info.add("bd");
                    }
                    
                    info.add(controlNumber.getText().trim());
                    info.add(remarks.getText());
                    if(!client.updateTransientInfo(info, transientIdnum)){
                        md.error(null, "Error Updating transient Information");
                    }
                    
                    for(String rooms : client.getRoomsFromTransient(transientIdnum)){
                        if(!client.updateRoomStatusFromTransient(rooms, "unoccupied")){
                            md.error(null, "Error updating room status");
                            break;
                        }
                    }
                    if(!client.updateTransientFurnitureToNull(transientIdnum)){
                        md.error(null, "Error updating furnitures");
                    }
                    if(tester1 && tester2 && tester3){
                        md.successful(null, "Checkout successful");
                        dispose();
                    }
                } else {
                    md.unsuccessful(null, "Checkout failed!");
                }
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(CheckoutTransient.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(null, ex.getMessage());
        }
    }//GEN-LAST:event_checkoutActionPerformed

    private void amountTenderedCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_amountTenderedCaretUpdate
        // TODO add your handling code here:
        String rem = remBill.getText().trim().replace(",", "");
        if(rem.contains("(") && rem.contains(")")){
            rem = "-" + rem.substring(1,rem.length()-1).trim();
        }
        if(amountTendered.getText().trim().isEmpty()){
            double bal = Double.parseDouble(rem.trim()) - Double.parseDouble("0.0");

            if(bal < 0){
                checkout.setEnabled(true);
                balance.setText("( "+ (bal * -1) +" )");
            } else if(bal == 0){
                checkout.setEnabled(true);
                balance.setText(decF.format(bal));
            } else {
                checkout.setEnabled(false);
                balance.setText(decF.format(bal));
            }
        } else {
            double bal = 0.0;
            try {
                bal = Double.parseDouble(rem.trim()) - Double.parseDouble(amountTendered.getText().trim().replace(",", ""));
            } catch (NumberFormatException ex) {
                bal = Double.parseDouble(rem.trim()) - Double.parseDouble(balanceTransient);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updater.setText(balanceTransient);
                    }
                });
//                amountTendered.setText(balanceTransient);
            }

            if(bal < 0){
                checkout.setEnabled(true);
                balance.setText("( "+ (bal * -1) +" )");
            } else if(bal == 0){
                checkout.setEnabled(true);
                balance.setText(decF.format(bal));
            } else {
                checkout.setEnabled(false);
                balance.setText(decF.format(bal));
            }
        }
        balanceTransient = updater.getText().trim();
    }//GEN-LAST:event_amountTenderedCaretUpdate

    private void updaterCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_updaterCaretUpdate
        // TODO add your handling code here:
        if(balanceTransient.trim().equals("0.00")){
            amountTendered.setText("");
        } else {
            if(updater.getText().trim().isEmpty()){
                amountTendered.setText("0.00");
            } else {
                amountTendered.setText(decF.format(Double.parseDouble(updater.getText().trim().replace(",", ""))));
            }
        }
    }//GEN-LAST:event_updaterCaretUpdate

    private void setInformation(){
        try {
            DateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
            TransientImpl transientImpl = client.getTransient(transientIdnum);
            name.setText(fname);
            arrDate.setText(df.format(new SimpleDateFormat("yyyy-MM-dd").parse(transientImpl.getDateEntered())));
            depDate.setText(df.format(new SimpleDateFormat("yyyy-MM-dd").parse(transientImpl.getDeparture())));
            accBill.setText(decF.format(Double.parseDouble(transientImpl.getTotalAmount().trim()) - Double.parseDouble(transientImpl.getDiscount().trim())));
            lastAmountPaid.setText(decF.format(Double.parseDouble(transientImpl.getAmountPaid())));
            String b = decF.format(Double.parseDouble(transientImpl.getBalance().trim()));
            if(Double.parseDouble(transientImpl.getBalance().trim()) < 0){
                b = "( " + decF.format(Double.parseDouble(transientImpl.getBalance().trim()) * -1) + " )";
            }
            remBill.setText(b);
            double bal = Double.parseDouble(transientImpl.getBalance().trim()) - Double.parseDouble(amountTendered.getText().trim().replace(",", ""));
            b = decF.format(bal);
            if(bal < 0){
                b = "( " + decF.format(bal * -1) + " )";
            }
            balance.setText(b);
        } catch (RemoteException | ParseException ex) {
//            Logger.getLogger(CheckoutTransient.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(null, ex.getMessage());
        }
    }
    
    private void setTransientControl(){
        try {
            String last = client.getLastControllNumber();
            String year = new SimpleDateFormat("yy").format(Calendar.getInstance().getTime());
            if(year.equals(last.substring(0,2))){
                String number = last.substring(2, 6);
                DecimalFormat df = new DecimalFormat("#0000");
                controlNumber.setText(year + df.format(Integer.parseInt(number)+1));
            } else {
                controlNumber.setText(year + "0000");
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(CheckoutTransient.class.getName()).log(Level.SEVERE, null, ex);
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
            java.util.logging.Logger.getLogger(CheckoutTransient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CheckoutTransient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CheckoutTransient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CheckoutTransient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CheckoutTransient dialog = new CheckoutTransient(new javax.swing.JFrame(), true, null, null, null,null);
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
    private javax.swing.JTextField accBill;
    private javax.swing.JTextField amountTendered;
    private javax.swing.JTextField arrDate;
    private javax.swing.JTextField balance;
    private javax.swing.JRadioButton bd;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancel;
    private javax.swing.JRadioButton cash;
    private javax.swing.JButton checkout;
    private javax.swing.JRadioButton cheque;
    private javax.swing.JTextField controlNumber;
    private javax.swing.JTextField depDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lastAmountPaid;
    private javax.swing.JTextField name;
    private javax.swing.JTextField remBill;
    private javax.swing.JTextArea remarks;
    private javax.swing.JTextField updater;
    // End of variables declaration//GEN-END:variables
}
