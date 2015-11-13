
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.processing.DPFPSampleConversion;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.Color;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kenneth
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    private boolean iterator = true;
    private final Capture captureDialog;
    private final IDGetter idg;
    private String verificationId = "";
    private TimerThread timerThread;
    private Registry registry;
    private NPRInterface client;
    private DefaultTableModel model;
    private DefaultTableModel model2;
    private boolean isEntered = false;
    private boolean mouseClicked = false;
    private Image fingerprint = null;
    private DPFPSample sample = null;
    private DPFPCapture capture = DPFPGlobal.getCaptureFactory().createCapture();
    private boolean isRecognized = false;
    private int iteration = 0;
    private int r = -1;
    private int c = -1;
    private int ROW = -1;
    private String user;
    private boolean isScanned = false;
    private DPFPSample finger1 = null;
    private DPFPSample finger2 = null;
    private DPFPTemplate orgTemplate = null;
    private DPFPTemplate orgTemplate2 = null;
    private DPFPFeatureExtraction featureExtraction;
    private DPFPEnrollment enrollment;
    private String standBy = "";
    private boolean isPressed = false;
    private String finger1String;
    private String finger2String;
    private javax.swing.JTextField uname;
    private javax.swing.JPasswordField pword;
    private boolean isActivated = true;
    private final MessageDialog md = new MessageDialog();
    private boolean registerFingerprint = false;
    private int sampleFinger = 0;
    
    public MainFrame() {
        initComponents();
        uname = new javax.swing.JTextField();
        uname.setFont(new java.awt.Font("Rondalo", 0, 14));
        pword = new javax.swing.JPasswordField();
        pword.setFont(new java.awt.Font("Rondalo", 0, 14));
        getContentPane().setBackground(Color.BLACK);
        setExtendedState(MAXIMIZED_BOTH);
        String serverName = "1N8P1R";
        try {
            //"192.168.1.249"
            registry = LocateRegistry.getRegistry("localhost",1099);
            client = (NPRInterface) registry.lookup(serverName);
        } catch (RemoteException | NullPointerException | NotBoundException ex) {
            try {
                registry = LocateRegistry.getRegistry("192.168.0.249",1099);
                client = (NPRInterface) registry.lookup(serverName);
            } catch (RemoteException | NullPointerException | NotBoundException ex1) {
                try {
                    String ipAddress = JOptionPane.showInputDialog(this, "IP Address of the server:", "181 NPR", JOptionPane.INFORMATION_MESSAGE);
                    registry = LocateRegistry.getRegistry(ipAddress.trim(), 1099);
                    client = (NPRInterface) registry.lookup(serverName);
                } catch (RemoteException | NullPointerException | NotBoundException ex2) {
                    new MessageDialog().error(this, "System can't access the specified IP Address.");
                    System.exit(0);
//                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        pop.add(register);
        pop.add(change);
        setVisibility();
        timerThread = new TimerThread(time1, date1);
        timerThread.start();
        captureDialog = new Capture(this, true);
        idg = new IDGetter(this, true, idGetter);
        scan(new javax.swing.JLabel(), 0);
    }

    private void setVisibility(){
        home.setVisible(false);
        timeInAndOut.setVisible(true);
        visitorTimeInOut.setVisible(false);
        tenantSearch.setVisible(false);
        groupVisit.setVisible(false);
        terms.setVisible(false);
        listFingers.setVisible(false);
        login.setVisible(false);
        registerFinger.setVisible(false);
        viewGroupVisit.setVisible(false);
        
        table.getTableHeader().setReorderingAllowed(Boolean.FALSE);
        table1.getTableHeader().setReorderingAllowed(Boolean.FALSE);
        table2.getTableHeader().setReorderingAllowed(Boolean.FALSE);
        visitorTable.getTableHeader().setReorderingAllowed(Boolean.FALSE);
        jTable1.getTableHeader().setReorderingAllowed(Boolean.FALSE);
        jTable2.getTableHeader().setReorderingAllowed(Boolean.FALSE);
        
        this.getRootPane().setDefaultButton(jButton8);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        area = new javax.swing.ButtonGroup();
        pop = new javax.swing.JPopupMenu();
        change = new javax.swing.JMenuItem();
        register = new javax.swing.JMenuItem();
        idGetter = new javax.swing.JTextField();
        home = new javax.swing.JLayeredPane();
        time_in_out = new javax.swing.JLabel();
        visitor_login = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        tenant_search = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        day = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        visitorTimeInOut = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        visitorName = new javax.swing.JComboBox();
        residentName = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        inform = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        submit = new javax.swing.JButton();
        reason = new javax.swing.JComboBox();
        groupVisitButton = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jLabel50 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        timeInAndOut = new javax.swing.JPanel();
        clear = new javax.swing.JButton();
        date1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        time1 = new javax.swing.JLabel();
        picture = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        status = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        tenantSearch = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        search1 = new javax.swing.JTextField();
        groupVisit = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        mezzanine = new javax.swing.JRadioButton();
        fitness = new javax.swing.JRadioButton();
        others = new javax.swing.JRadioButton();
        otherArea = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jSpinner3 = new javax.swing.JSpinner();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jSpinner5 = new javax.swing.JSpinner();
        jSpinner6 = new javax.swing.JSpinner();
        dateChooserCombo2 = new datechooser.beans.DateChooserCombo();
        residentNames = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        visitorTable = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        terms = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        listFingers = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel61 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        registerFinger = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jLabel57 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jButton15 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        login = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        jLabel52 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        viewGroupVisit = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();

        change.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        change.setText("Change");
        change.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeActionPerformed(evt);
            }
        });
        pop.add(change);

        register.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        register.setText("Register");
        register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerActionPerformed(evt);
            }
        });
        pop.add(register);

        idGetter.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                idGetterCaretUpdate(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("icons/Lobby.png")).getImage());
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        time_in_out.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Time In_Out.png"))); // NOI18N
        time_in_out.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        time_in_out.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                time_in_outMouseClicked(evt);
            }
        });

        visitor_login.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Visitor.png"))); // NOI18N
        visitor_login.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        visitor_login.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                visitor_loginMouseClicked(evt);
            }
        });

        exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Exit.png"))); // NOI18N
        exit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitMouseClicked(evt);
            }
        });

        tenant_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Tenant Search.png"))); // NOI18N
        tenant_search.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tenant_search.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tenant_searchMouseClicked(evt);
            }
        });

        date.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        date.setForeground(new java.awt.Color(255, 255, 255));
        date.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        date.setText("OCT 27, 2014");

        time.setFont(new java.awt.Font("Rondalo", 1, 24)); // NOI18N
        time.setForeground(new java.awt.Color(255, 255, 255));
        time.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        time.setText("00:00 PM");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/2.png"))); // NOI18N

        day.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        day.setForeground(new java.awt.Color(255, 255, 255));
        day.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        day.setText("Monday");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/2.jpg"))); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout homeLayout = new javax.swing.GroupLayout(home);
        home.setLayout(homeLayout);
        homeLayout.setHorizontalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(410, 410, 410)
                .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(visitor_login))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel4))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(620, 620, 620)
                .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(617, 617, 617)
                .addComponent(day, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(650, 650, 650)
                .addComponent(exit))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(270, 270, 270)
                .addComponent(time_in_out))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(460, 460, 460)
                .addComponent(tenant_search))
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        homeLayout.setVerticalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(time)
                .addGap(171, 171, 171)
                .addComponent(visitor_login))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jLabel4))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(date))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(day))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(410, 410, 410)
                .addComponent(exit))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(210, 210, 210)
                .addComponent(time_in_out))
            .addGroup(homeLayout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(tenant_search))
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        home.setLayer(time_in_out, javax.swing.JLayeredPane.DEFAULT_LAYER);
        home.setLayer(visitor_login, javax.swing.JLayeredPane.DEFAULT_LAYER);
        home.setLayer(exit, javax.swing.JLayeredPane.DEFAULT_LAYER);
        home.setLayer(tenant_search, javax.swing.JLayeredPane.DEFAULT_LAYER);
        home.setLayer(date, javax.swing.JLayeredPane.DEFAULT_LAYER);
        home.setLayer(time, javax.swing.JLayeredPane.DEFAULT_LAYER);
        home.setLayer(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        home.setLayer(day, javax.swing.JLayeredPane.DEFAULT_LAYER);
        home.setLayer(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        getContentPane().add(home, gridBagConstraints);

        visitorTimeInOut.setBackground(new java.awt.Color(255, 255, 255));
        visitorTimeInOut.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        visitorTimeInOut.setMaximumSize(new java.awt.Dimension(532, 559));
        visitorTimeInOut.setMinimumSize(new java.awt.Dimension(532, 559));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back.png"))); // NOI18N
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Rondalo", 0, 18)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        jLabel7.setText("NAME");

        visitorName.setEditable(true);
        visitorName.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N

        residentName.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        residentName.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel10.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        jLabel10.setText("RESIDENT NAME");

        jLabel13.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        jLabel13.setText("REASON");

        inform.setBackground(new java.awt.Color(255, 255, 255));
        inform.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        inform.setText("Inform Resident");
        inform.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButton3.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jButton3.setText("CLEAR");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        submit.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        submit.setText("SUBMIT");
        submit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitActionPerformed(evt);
            }
        });

        reason.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        reason.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inquire", "Visit", "School works", "Personal Business", "Others" }));
        reason.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        groupVisitButton.setFont(new java.awt.Font("Rondalo", 2, 14)); // NOI18N
        groupVisitButton.setForeground(new java.awt.Color(0, 102, 255));
        groupVisitButton.setText("Permit to use Building Premises");
        groupVisitButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        groupVisitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                groupVisitButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(visitorName, 0, 400, Short.MAX_VALUE)
                            .addComponent(residentName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(reason, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(inform)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(groupVisitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submit, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(visitorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(residentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(reason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inform)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 305, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(submit)
                    .addComponent(groupVisitButton))
                .addContainerGap())
        );

        jTabbedPane1.addTab("VISITOR LOGIN", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        table.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Visitor's Name", "Resident's Name", "Login Time"
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
        jScrollPane3.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setResizable(false);
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(1).setResizable(false);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setResizable(false);
            table.getColumnModel().getColumn(2).setPreferredWidth(50);
        }

        jButton1.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        jButton1.setText("LOGOUT");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        jLabel5.setText("VISITOR NAME");

        search.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        search.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                searchCaretUpdate(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search.png"))); // NOI18N
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        table2.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Resident's Name", "# of Visitors", "Area", "Start", "End"
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
        table2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table2MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(table2);
        if (table2.getColumnModel().getColumnCount() > 0) {
            table2.getColumnModel().getColumn(0).setResizable(false);
            table2.getColumnModel().getColumn(0).setPreferredWidth(150);
            table2.getColumnModel().getColumn(1).setResizable(false);
            table2.getColumnModel().getColumn(1).setPreferredWidth(30);
            table2.getColumnModel().getColumn(2).setResizable(false);
            table2.getColumnModel().getColumn(2).setPreferredWidth(40);
            table2.getColumnModel().getColumn(3).setResizable(false);
            table2.getColumnModel().getColumn(3).setPreferredWidth(20);
            table2.getColumnModel().getColumn(4).setResizable(false);
            table2.getColumnModel().getColumn(4).setPreferredWidth(20);
        }

        jLabel50.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        jLabel50.setText("GROUP VISIT");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel50)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("VISITOR LOGOUT", jPanel3);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/181.jpg"))); // NOI18N

        javax.swing.GroupLayout visitorTimeInOutLayout = new javax.swing.GroupLayout(visitorTimeInOut);
        visitorTimeInOut.setLayout(visitorTimeInOutLayout);
        visitorTimeInOutLayout.setHorizontalGroup(
            visitorTimeInOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visitorTimeInOutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
            .addGroup(visitorTimeInOutLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        visitorTimeInOutLayout.setVerticalGroup(
            visitorTimeInOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visitorTimeInOutLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(visitorTimeInOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        getContentPane().add(visitorTimeInOut, gridBagConstraints);

        timeInAndOut.setBackground(new java.awt.Color(255, 255, 255));
        timeInAndOut.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        timeInAndOut.setMaximumSize(new java.awt.Dimension(520, 305));
        timeInAndOut.setMinimumSize(new java.awt.Dimension(520, 305));
        timeInAndOut.setPreferredSize(new java.awt.Dimension(520, 305));

        clear.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        clear.setText("CLEAR");
        clear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clear.setEnabled(false);
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });

        date1.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        date1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        date1.setText("OCT 27, 2014");

        jLabel8.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("RESIDENT TIME-IN/OUT");

        time1.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        time1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        time1.setText("10:29 PM");

        picture.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        jLabel9.setText("Name");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back.png"))); // NOI18N
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        name.setEditable(false);
        name.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        name.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel12.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        jLabel12.setText("Status");

        status.setEditable(false);
        status.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        status.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/181.jpg"))); // NOI18N

        jLabel62.setFont(new java.awt.Font("Rondalo", 2, 14)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(0, 102, 255));
        jLabel62.setText("Register Fingerprint");
        jLabel62.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel62.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel62MouseClicked(evt);
            }
        });

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jTextField3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTextField3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout timeInAndOutLayout = new javax.swing.GroupLayout(timeInAndOut);
        timeInAndOut.setLayout(timeInAndOutLayout);
        timeInAndOutLayout.setHorizontalGroup(
            timeInAndOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timeInAndOutLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(timeInAndOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timeInAndOutLayout.createSequentialGroup()
                        .addComponent(picture, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(timeInAndOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, timeInAndOutLayout.createSequentialGroup()
                                .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addGap(101, 101, 101)
                                .addComponent(clear))
                            .addComponent(date1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(time1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(timeInAndOutLayout.createSequentialGroup()
                                .addGroup(timeInAndOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel12))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(name, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(status, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, timeInAndOutLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)))
                .addContainerGap())
        );
        timeInAndOutLayout.setVerticalGroup(
            timeInAndOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timeInAndOutLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(timeInAndOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timeInAndOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timeInAndOutLayout.createSequentialGroup()
                        .addComponent(date1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(time1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(timeInAndOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clear)
                            .addComponent(jLabel62))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(timeInAndOutLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(picture, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.ipadx = 18;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        getContentPane().add(timeInAndOut, gridBagConstraints);

        tenantSearch.setBackground(new java.awt.Color(255, 255, 255));
        tenantSearch.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tenantSearch.setMaximumSize(new java.awt.Dimension(439, 428));
        tenantSearch.setMinimumSize(new java.awt.Dimension(439, 428));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back.png"))); // NOI18N
        jLabel16.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("RESIDENT SEARCH");

        jLabel18.setFont(new java.awt.Font("Rondalo", 1, 12)); // NOI18N
        jLabel18.setText("RESIDENT'S NAME");

        table1.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(table1);
        if (table1.getColumnModel().getColumnCount() > 0) {
            table1.getColumnModel().getColumn(0).setPreferredWidth(300);
        }

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/181.jpg"))); // NOI18N

        search1.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        search1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                search1CaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout tenantSearchLayout = new javax.swing.GroupLayout(tenantSearch);
        tenantSearch.setLayout(tenantSearchLayout);
        tenantSearchLayout.setHorizontalGroup(
            tenantSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tenantSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tenantSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tenantSearchLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tenantSearchLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(search1)))
                .addContainerGap())
        );
        tenantSearchLayout.setVerticalGroup(
            tenantSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tenantSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tenantSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tenantSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(search1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        getContentPane().add(tenantSearch, gridBagConstraints);

        groupVisit.setBackground(new java.awt.Color(255, 255, 255));
        groupVisit.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/181.jpg"))); // NOI18N

        jLabel23.setFont(new java.awt.Font("Rondalo", 1, 24)); // NOI18N
        jLabel23.setText("PERMIT TO USE BUILDING PREMISES");

        jLabel24.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Jan 01, 2001");

        jLabel25.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Monday");

        jLabel26.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("12:00 AM");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel27.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel27.setText("Name:");

        jLabel28.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel28.setText("Reserved Area");

        jLabel30.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel30.setText("No. Of Guest");

        jSpinner1.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 16, 1));
        jSpinner1.setEditor(new JSpinner.DefaultEditor(jSpinner1));
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        mezzanine.setBackground(new java.awt.Color(255, 255, 255));
        area.add(mezzanine);
        mezzanine.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        mezzanine.setText("MEZZANINE HALL");
        mezzanine.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mezzanineMouseClicked(evt);
            }
        });
        mezzanine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mezzanineActionPerformed(evt);
            }
        });

        fitness.setBackground(new java.awt.Color(255, 255, 255));
        area.add(fitness);
        fitness.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        fitness.setText("FITNESS CENTER");
        fitness.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fitnessMouseClicked(evt);
            }
        });

        others.setBackground(new java.awt.Color(255, 255, 255));
        area.add(others);
        others.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        others.setText("OTHERS:");
        others.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                othersMouseClicked(evt);
            }
        });

        otherArea.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

        jLabel31.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel31.setText("Date Of Use");

        jLabel32.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel32.setText("Time START");

        jLabel33.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jLabel33.setText("Time END");

        jSpinner2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(12, 1, 12, 1));
        jSpinner2.setToolTipText("hour");
        jSpinner2.setEditor(new JSpinner.DefaultEditor(jSpinner2));

        jSpinner3.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jSpinner3.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        jSpinner3.setToolTipText("minute");
        jSpinner3.setEditor(new JSpinner.DefaultEditor(jSpinner3));

        jComboBox2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AM", "PM" }));

        jComboBox3.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AM", "PM" }));

        jSpinner5.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jSpinner5.setModel(new javax.swing.SpinnerNumberModel(12, 1, 12, 1));
        jSpinner5.setToolTipText("hour");
        jSpinner5.setEditor(new JSpinner.DefaultEditor(jSpinner5));

        jSpinner6.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
        jSpinner6.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        jSpinner6.setToolTipText("minute");
        jSpinner6.setEditor(new JSpinner.DefaultEditor(jSpinner6));

        dateChooserCombo2.setCurrentView(new datechooser.view.appearance.AppearancesList("Light",
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
    dateChooserCombo2.setFormat(2);
    dateChooserCombo2.setFieldFont(new java.awt.Font("Rondalo", java.awt.Font.PLAIN, 14));

    residentNames.setEditable(false);
    residentNames.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    residentNames.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    residentNames.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            residentNamesMouseClicked(evt);
        }
    });

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addComponent(jLabel27)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(residentNames))
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addComponent(jLabel28)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(fitness)
                        .addComponent(mezzanine)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(others)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(otherArea, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGap(28, 28, 28)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSpinner2, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                        .addComponent(jSpinner5))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jSpinner6, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(73, 73, 73))))
                .addComponent(dateChooserCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(28, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel30))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(dateChooserCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel31))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinner2)
                        .addComponent(jLabel32)
                        .addComponent(jSpinner3)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSpinner6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel33))
                    .addContainerGap())
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27)
                        .addComponent(residentNames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28)
                        .addComponent(mezzanine))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(fitness)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(others)
                        .addComponent(otherArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE))))
    );

    visitorTable.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    visitorTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Name Of Guests", "Valid ID Number"
        }
    ) {
        Class[] types = new Class [] {
            java.lang.String.class, java.lang.String.class
        };

        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }
    });
    jScrollPane1.setViewportView(visitorTable);
    if (visitorTable.getColumnModel().getColumnCount() > 0) {
        visitorTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        visitorTable.getColumnModel().getColumn(1).setResizable(false);
    }

    jButton6.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton6.setText("<html>TERMS &<br>CONDITIONS</html>");
    jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton6.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton6ActionPerformed(evt);
        }
    });

    jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Prev.png"))); // NOI18N
    jLabel38.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jLabel38.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jLabel38MouseClicked(evt);
        }
    });

    jButton7.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton7.setText("SUBMIT");
    jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton7.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton7ActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout groupVisitLayout = new javax.swing.GroupLayout(groupVisit);
    groupVisit.setLayout(groupVisitLayout);
    groupVisitLayout.setHorizontalGroup(
        groupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(groupVisitLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(groupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(groupVisitLayout.createSequentialGroup()
                    .addComponent(jLabel15)
                    .addGap(57, 57, 57)
                    .addComponent(jLabel23)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(groupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(groupVisitLayout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(groupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupVisitLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jLabel38))
                        .addComponent(jButton6)
                        .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addContainerGap())
    );
    groupVisitLayout.setVerticalGroup(
        groupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(groupVisitLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(groupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(groupVisitLayout.createSequentialGroup()
                    .addComponent(jLabel24)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel25))
                .addGroup(groupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel26)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(groupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(groupVisitLayout.createSequentialGroup()
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton7)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel38)))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 7;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = 10;
    gridBagConstraints.gridheight = 22;
    gridBagConstraints.ipadx = 28;
    gridBagConstraints.ipady = -1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 10, 11, 0);
    getContentPane().add(groupVisit, gridBagConstraints);

    terms.setBackground(new java.awt.Color(255, 255, 255));
    terms.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel39.setFont(new java.awt.Font("Rondalo", 1, 24)); // NOI18N
    jLabel39.setText("TERMS & CONDITIONS");

    jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/181.jpg"))); // NOI18N

    jPanel5.setBackground(new java.awt.Color(255, 255, 255));
    jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

    jLabel41.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel41.setText("The party agrees to the terms, rules and regulations of 181 North Place Residences, as evidenced by the signature set forth below:");

    jPanel6.setBackground(new java.awt.Color(255, 255, 255));

    jLabel42.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel42.setText("<html>1. Drinking of alcoholic beverages, and bringing in of any dangerous<br> articles or controlled substances (e.g. firearms, prohibited drugs,<br> etc.) are strictly not allowed within the premises of 181 North Place<br> Residences. Smoking is not allowed inside the building. This facility is<br> equipped with a smoke alarm system.</html>");

    jLabel43.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel43.setText("<html>2. Only registered guests are allowed access to the space. Please<br>\nentertain your visitor at the Lobby area at the Ground Floor.</html>");

    jLabel44.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel44.setText("3. Keep the property and all furnishings in good order.");

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel44))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel6Layout.setVerticalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel44)
            .addGap(0, 0, Short.MAX_VALUE))
    );

    jPanel8.setBackground(new java.awt.Color(255, 255, 255));

    jLabel45.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel45.setText("<html>\n4. The management is not responsible for any accidents, injuries or<br>\nillness that occurs while on the premises or its facilities or for any<br>\nloss of personal belongings or valuables of the guest.\n</html>");

    jLabel46.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel46.setText("5. Pets are NOT allowed.");

    jLabel47.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel47.setText("<html>\n6. Parking is limited to one (1) vehicle only and is allowed to park in<br>\ndesignated parking areas only.\n</html>");

    jLabel48.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel48.setText("<html>\n7. The pary will be charged Php 100.00 per hour for the<br>\nconsumption of energy whenever applicable.\n</html>");

    javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
    jPanel8.setLayout(jPanel8Layout);
    jPanel8Layout.setHorizontalGroup(
        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel8Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel46)
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel8Layout.setVerticalGroup(
        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel8Layout.createSequentialGroup()
            .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel46)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel41)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );

    jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Prev.png"))); // NOI18N
    jLabel49.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jLabel49.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jLabel49MouseClicked(evt);
        }
    });

    javax.swing.GroupLayout termsLayout = new javax.swing.GroupLayout(terms);
    terms.setLayout(termsLayout);
    termsLayout.setHorizontalGroup(
        termsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(termsLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(termsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(termsLayout.createSequentialGroup()
                    .addComponent(jLabel40)
                    .addGap(186, 186, 186)
                    .addComponent(jLabel39)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel49)))
            .addContainerGap())
    );
    termsLayout.setVerticalGroup(
        termsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(termsLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(termsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel40)
                .addComponent(jLabel39)
                .addComponent(jLabel49))
            .addGap(18, 18, 18)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 18;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.ipady = -3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
    getContentPane().add(terms, gridBagConstraints);

    listFingers.setBackground(new java.awt.Color(255, 255, 255));
    listFingers.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    listFingers.setMaximumSize(new java.awt.Dimension(470, 430));
    listFingers.setMinimumSize(new java.awt.Dimension(470, 430));

    jLabel53.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
    jLabel53.setText("LIST OF RESIDENTS");

    jTextField2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jTextField2.addCaretListener(new javax.swing.event.CaretListener() {
        public void caretUpdate(javax.swing.event.CaretEvent evt) {
            jTextField2CaretUpdate(evt);
        }
    });

    jTable1.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jTable1.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "ID", "Name", "Finger 1", "Finger 2"
        }
    ) {
        Class[] types = new Class [] {
            java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
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
    jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jTable1MouseClicked(evt);
        }
        public void mousePressed(java.awt.event.MouseEvent evt) {
            jTable1MousePressed(evt);
        }
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            jTable1MouseReleased(evt);
        }
    });
    jScrollPane2.setViewportView(jTable1);
    if (jTable1.getColumnModel().getColumnCount() > 0) {
        jTable1.getColumnModel().getColumn(0).setResizable(false);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(1).setResizable(false);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(2).setResizable(false);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(3).setResizable(false);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(50);
    }

    jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Prev.png"))); // NOI18N
    jLabel61.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jLabel61.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jLabel61MouseClicked(evt);
        }
    });

    jLabel75.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
    jLabel75.setText("Search:");

    javax.swing.GroupLayout listFingersLayout = new javax.swing.GroupLayout(listFingers);
    listFingers.setLayout(listFingersLayout);
    listFingersLayout.setHorizontalGroup(
        listFingersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(listFingersLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(listFingersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addGroup(listFingersLayout.createSequentialGroup()
                    .addComponent(jLabel53)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel61))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, listFingersLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel75)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    listFingersLayout.setVerticalGroup(
        listFingersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(listFingersLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(listFingersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel53)
                .addComponent(jLabel61))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(listFingersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel75))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
            .addContainerGap())
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 16;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 11;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
    getContentPane().add(listFingers, gridBagConstraints);

    registerFinger.setBackground(new java.awt.Color(255, 255, 255));
    registerFinger.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    registerFinger.setMaximumSize(new java.awt.Dimension(490, 431));
    registerFinger.setMinimumSize(new java.awt.Dimension(490, 431));

    jLabel54.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
    jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel54.setText("FINGER 1");
    jLabel54.setToolTipText("Main Finger");
    jLabel54.setMaximumSize(new java.awt.Dimension(172, 200));
    jLabel54.setMinimumSize(new java.awt.Dimension(172, 200));
    jLabel54.setPreferredSize(new java.awt.Dimension(172, 200));

    jButton11.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton11.setText("CAPTURE");
    jButton11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton11.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton11ActionPerformed(evt);
        }
    });

    jButton12.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton12.setText("CLEAR");
    jButton12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton12.setEnabled(false);
    jButton12.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton12ActionPerformed(evt);
        }
    });

    jLabel57.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
    jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel57.setText("FINGER 2");
    jLabel57.setToolTipText("Backup Finger");
    jLabel57.setMaximumSize(new java.awt.Dimension(172, 200));
    jLabel57.setMinimumSize(new java.awt.Dimension(172, 200));
    jLabel57.setPreferredSize(new java.awt.Dimension(172, 200));

    jButton13.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton13.setText("CAPTURE");
    jButton13.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton13.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton13ActionPerformed(evt);
        }
    });

    jButton14.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton14.setText("CLEAR");
    jButton14.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton14.setEnabled(false);
    jButton14.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton14ActionPerformed(evt);
        }
    });

    jLabel58.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel58.setText("ID");

    jLabel59.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

    jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Prev.png"))); // NOI18N
    jLabel60.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jLabel60.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jLabel60MouseClicked(evt);
        }
    });

    jLabel55.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel55.setText("NAME");

    jLabel56.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

    jCheckBox1.setBackground(new java.awt.Color(255, 255, 255));
    jCheckBox1.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jCheckBox1.setText("Validate 1");
    jCheckBox1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jCheckBox1.setEnabled(false);
    jCheckBox1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jCheckBox1MouseClicked(evt);
        }
    });

    jCheckBox2.setBackground(new java.awt.Color(255, 255, 255));
    jCheckBox2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jCheckBox2.setText("Validate 2");
    jCheckBox2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jCheckBox2.setEnabled(false);
    jCheckBox2.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jCheckBox2MouseClicked(evt);
        }
    });

    jCheckBox5.setBackground(new java.awt.Color(255, 255, 255));
    jCheckBox5.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jCheckBox5.setText("Validate 2");
    jCheckBox5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jCheckBox5.setEnabled(false);
    jCheckBox5.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jCheckBox5MouseClicked(evt);
        }
    });

    jCheckBox6.setBackground(new java.awt.Color(255, 255, 255));
    jCheckBox6.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jCheckBox6.setText("Validate 1");
    jCheckBox6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jCheckBox6.setEnabled(false);
    jCheckBox6.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jCheckBox6MouseClicked(evt);
        }
    });

    jButton15.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton15.setText("SAVE");
    jButton15.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton15.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton15ActionPerformed(evt);
        }
    });

    jTextField1.setEditable(false);
    jTextField1.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

    javax.swing.GroupLayout registerFingerLayout = new javax.swing.GroupLayout(registerFinger);
    registerFinger.setLayout(registerFingerLayout);
    registerFingerLayout.setHorizontalGroup(
        registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerFingerLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(registerFingerLayout.createSequentialGroup()
                    .addComponent(jTextField1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton15))
                .addGroup(registerFingerLayout.createSequentialGroup()
                    .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(registerFingerLayout.createSequentialGroup()
                            .addComponent(jLabel58)
                            .addGap(41, 41, 41)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(registerFingerLayout.createSequentialGroup()
                            .addComponent(jLabel55)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(registerFingerLayout.createSequentialGroup()
                            .addGap(34, 34, 34)
                            .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jButton11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, registerFingerLayout.createSequentialGroup()
                                    .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(8, 8, 8)
                                    .addComponent(jCheckBox2)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                            .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerFingerLayout.createSequentialGroup()
                                    .addComponent(jCheckBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(10, 10, 10)
                                    .addComponent(jCheckBox5))
                                .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel60)))
            .addContainerGap())
    );
    registerFingerLayout.setVerticalGroup(
        registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerFingerLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel60)
                .addGroup(registerFingerLayout.createSequentialGroup()
                    .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel58)
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGap(18, 18, 18)
            .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(registerFingerLayout.createSequentialGroup()
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(7, 7, 7)
                    .addComponent(jButton11)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton12))
                .addGroup(registerFingerLayout.createSequentialGroup()
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton13)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton14)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2))
                .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox5)))
            .addGap(32, 32, 32)
            .addGroup(registerFingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton15)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 16;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 21;
    gridBagConstraints.gridheight = 4;
    gridBagConstraints.ipady = -1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 10, 0, 0);
    getContentPane().add(registerFinger, gridBagConstraints);

    login.setBackground(new java.awt.Color(255, 255, 255));
    login.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    login.setMaximumSize(new java.awt.Dimension(280, 148));
    login.setMinimumSize(new java.awt.Dimension(280, 148));

    jLabel29.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel29.setText("Username");

    jLabel51.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel51.setText("Password");

    username.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

    password.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N

    jLabel52.setFont(new java.awt.Font("Rondalo", 1, 18)); // NOI18N
    jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel52.setText("ADMINISTRATOR LOGIN");

    jButton8.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton8.setText("LOGIN");
    jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton8.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton8ActionPerformed(evt);
        }
    });

    jButton9.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jButton9.setText("CANCEL");
    jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jButton9.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton9ActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout loginLayout = new javax.swing.GroupLayout(login);
    login.setLayout(loginLayout);
    loginLayout.setHorizontalGroup(
        loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(loginLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addGroup(loginLayout.createSequentialGroup()
                    .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel29)
                        .addComponent(jLabel51))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(password)
                        .addComponent(username)))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jButton9)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton8)))
            .addContainerGap())
    );
    loginLayout.setVerticalGroup(
        loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(loginLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel52)
            .addGap(17, 17, 17)
            .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel29)
                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel51)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton8)
                .addComponent(jButton9))
            .addContainerGap(13, Short.MAX_VALUE))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 19;
    gridBagConstraints.gridheight = 13;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
    getContentPane().add(login, gridBagConstraints);

    viewGroupVisit.setBackground(new java.awt.Color(255, 255, 255));
    viewGroupVisit.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    viewGroupVisit.setMaximumSize(new java.awt.Dimension(490, 390));
    viewGroupVisit.setMinimumSize(new java.awt.Dimension(490, 390));

    jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Prev.png"))); // NOI18N
    jLabel63.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    jLabel63.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jLabel63MouseClicked(evt);
        }
    });

    jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/181.jpg"))); // NOI18N

    jTable2.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jTable2.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Name", "Valid Id"
        }
    ) {
        Class[] types = new Class [] {
            java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean [] {
            false, false
        };

        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    jScrollPane6.setViewportView(jTable2);
    if (jTable2.getColumnModel().getColumnCount() > 0) {
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(200);
        jTable2.getColumnModel().getColumn(1).setResizable(false);
    }

    jLabel65.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel65.setText("Resident Name:");

    jLabel66.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N

    jLabel67.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel67.setText("Start:");

    jLabel68.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N

    jLabel69.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel69.setText("End:");

    jLabel70.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N

    jLabel71.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel71.setText("Area:");

    jLabel72.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N

    jLabel73.setFont(new java.awt.Font("Rondalo", 0, 14)); // NOI18N
    jLabel73.setText("Date:");

    jLabel74.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N

    javax.swing.GroupLayout viewGroupVisitLayout = new javax.swing.GroupLayout(viewGroupVisit);
    viewGroupVisit.setLayout(viewGroupVisitLayout);
    viewGroupVisitLayout.setHorizontalGroup(
        viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(viewGroupVisitLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addGroup(viewGroupVisitLayout.createSequentialGroup()
                    .addComponent(jLabel64)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel63))
                .addGroup(viewGroupVisitLayout.createSequentialGroup()
                    .addComponent(jLabel65)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(viewGroupVisitLayout.createSequentialGroup()
                    .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(viewGroupVisitLayout.createSequentialGroup()
                            .addComponent(jLabel73)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel74, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(viewGroupVisitLayout.createSequentialGroup()
                            .addComponent(jLabel67)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel69))
                        .addGroup(viewGroupVisitLayout.createSequentialGroup()
                            .addComponent(jLabel71)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGap(18, 18, 18)
                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addContainerGap())
    );
    viewGroupVisitLayout.setVerticalGroup(
        viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(viewGroupVisitLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel64)
                .addComponent(jLabel63))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(viewGroupVisitLayout.createSequentialGroup()
                    .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel71)
                        .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(jLabel69))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(viewGroupVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel73)
                .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
            .addContainerGap())
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 26;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 22;
    gridBagConstraints.gridheight = 12;
    gridBagConstraints.ipady = 40;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
    getContentPane().add(viewGroupVisit, gridBagConstraints);

    pack();
    setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
//        HomePanel p = new HomePanel();
//        panel.add(p, java.awt.BorderLayout.CENTER);
//        panel.revalidate();
//        panel.repaint();
    }//GEN-LAST:event_formWindowOpened

    private void time_in_outMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_time_in_outMouseClicked
        // TODO add your handling code here:
        timeInAndOut.setVisible(true);
        home.setVisible(false);
        timerThread = new TimerThread(time1, date1);
        timerThread.start();
        if(name.getText().isEmpty()){
            clear.setEnabled(false);
        }
    }//GEN-LAST:event_time_in_outMouseClicked

    private void visitor_loginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_visitor_loginMouseClicked
        // TODO add your handling code here:
        visitorTimeInOut.setVisible(true);
        home.setVisible(false);
        model = (DefaultTableModel) table.getModel();
        setChoices();
        setTables();
        model2 = (DefaultTableModel) table2.getModel();
        model2.getDataVector().removeAllElements();
        model2.fireTableDataChanged();
        setTable2();
    }//GEN-LAST:event_visitor_loginMouseClicked

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void tenant_searchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tenant_searchMouseClicked
        // TODO add your handling code here:
        tenantSearch.setVisible(true);
        home.setVisible(false);
        model = (DefaultTableModel) table1.getModel();
        setTable();
    }//GEN-LAST:event_tenant_searchMouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        // TODO add your handling code here:
        home.setVisible(true);
        visitorTimeInOut.setVisible(false);
        timerThread = new TimerThread(time, date, day);
        timerThread.start();
        search.setText("");
        visitorName.setSelectedIndex(-1);
        if(residentName.getItemCount() > 0){
            residentName.setSelectedIndex(0);
        }
        reason.setSelectedIndex(0);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        visitorName.setSelectedIndex(-1);
        residentName.setSelectedIndex(0);
        reason.setSelectedIndex(0);
        inform.setSelected(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitActionPerformed
        try {
            // TODO add your handling code here:
            if(new MessageDialog().confirmationSubmit(this) == new MessageDialog().YES){
                if(residentName.getSelectedItem() == "Administrator"){
                    if(!client.insertVisitor(new VisitorImpl(visitorName.getSelectedItem().toString(), reason.getSelectedItem().toString(), "administrator"))){
                        new MessageDialog().successful(this);
                    } else {
                        new MessageDialog().unsuccessful(this);
                    }
                } else {
                    if(!client.insertVisitor(new VisitorImpl(visitorName.getSelectedItem().toString(), reason.getSelectedItem().toString(), residentName.getSelectedItem().toString()))){
                        if(inform.isSelected()){
                            String id = client.getResidentIdnum(residentName.getSelectedItem().toString());
                            String mobileNumber = client.getNumber(id).getResidentContact();
                            ArrayList<String> e = new ArrayList<>();
                            e.add(mobileNumber);
                            e.add("We would like to inform you that you have a visitor in the lobby whose name is "+visitorName.getSelectedItem()+".");
                            e.add("Visitor");
                            e.add(id);
                            e.add("resident");
                            if(client.sendMessage(e)){
                                new MessageDialog().message(this);
                            } else {
                                new MessageDialog().messageNotSent(this);
                            }
                        }
                        new MessageDialog().successful(this);
                    } else {
                        new MessageDialog().unsuccessful(this);
                    }
                }

            }
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
        visitorName.setSelectedIndex(-1);
        residentName.setSelectedIndex(0);
        reason.setSelectedIndex(0);
        inform.setSelected(false);
        setChoices();
        setTables();
    }//GEN-LAST:event_submitActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(!(table.getSelectedRowCount() == 0)){
            if(new MessageDialog().logout(this) == new MessageDialog().YES){
                try {
                    if(!client.logoutVisitor(new VisitorImpl(model.getValueAt(table.getSelectedRow(), 0).toString()))){
                        setTables();
                    } else {
                        new MessageDialog().unsuccessful(this, "Logout Unsuccessful");
                    }
                } catch (RemoteException ex) {
//                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(this, ex.getMessage());
                }
            }
        } else {
            new MessageDialog().error(this, "No row is selected.");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void searchCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_searchCaretUpdate
        // TODO add your handling code here:
        setTables();
    }//GEN-LAST:event_searchCaretUpdate

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        setTables();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        // TODO add your handling code here:
        name.setText("");
        status.setText("");
        picture.setIcon(null);
        clear.setEnabled(true);
    }//GEN-LAST:event_clearActionPerformed

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        // TODO add your handling code here:
        home.setVisible(true);
        timeInAndOut.setVisible(false);
        timerThread = new TimerThread(time, date, day);
        timerThread.start();
        name.setText("");
        status.setText("");
        picture.setIcon(null);
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
        // TODO add your handling code here:
        home.setVisible(true);
        tenantSearch.setVisible(false);
        timerThread = new TimerThread(time, date, day);
        timerThread.start();
        search1.setText("");
    }//GEN-LAST:event_jLabel16MouseClicked

    private void search1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_search1CaretUpdate
        // TODO add your handling code here:
        setTable();
    }//GEN-LAST:event_search1CaretUpdate

    private void groupVisitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_groupVisitButtonMouseClicked
        // TODO add your handling code here:
        timerThread = new TimerThread(jLabel26, jLabel24, jLabel25);
        timerThread.start();
        residentNames.setText("");
        visitorTimeInOut.setVisible(false);
        groupVisit.setVisible(true);
        mezzanine.setSelected(false);
        fitness.setSelected(false);
        others.setSelected(true);
        jSpinner1.setValue(1);
        jSpinner2.setValue(12);
        jSpinner3.setValue(0);
        jSpinner5.setValue(12);
        jSpinner6.setValue(0);
        jComboBox2.setSelectedIndex(0);
        model = (DefaultTableModel) visitorTable.getModel();
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        model.addRow(new Object[]{});
    }//GEN-LAST:event_groupVisitButtonMouseClicked

    private void jLabel38MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel38MouseClicked
        // TODO add your handling code here:
        if(new MessageDialog().confirmationExit(this) == new MessageDialog().YES){
            visitorTimeInOut.setVisible(true);
            groupVisit.setVisible(false);
            model = (DefaultTableModel) table.getModel();
            setTables();
            setChoices();
            setTable2();
        }
        residentName.setSelectedIndex(0);
        reason.setSelectedIndex(0);
    }//GEN-LAST:event_jLabel38MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        groupVisit.setVisible(false);
        terms.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jLabel49MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel49MouseClicked
        // TODO add your handling code here:
        groupVisit.setVisible(true);
        terms.setVisible(false);
    }//GEN-LAST:event_jLabel49MouseClicked

    private void othersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_othersMouseClicked
        // TODO add your handling code here:
        otherArea.setEditable(true);
    }//GEN-LAST:event_othersMouseClicked

    private void fitnessMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fitnessMouseClicked
        // TODO add your handling code here:
        otherArea.setEditable(false);
    }//GEN-LAST:event_fitnessMouseClicked

    private void mezzanineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mezzanineMouseClicked
        // TODO add your handling code here:
        otherArea.setEditable(false);
    }//GEN-LAST:event_mezzanineMouseClicked

    private void mezzanineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mezzanineActionPerformed
        // TODO add your handling code here:
        otherArea.setEditable(true);
    }//GEN-LAST:event_mezzanineActionPerformed

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        // TODO add your handling code here:
        if(model.getRowCount() < (int)jSpinner1.getValue()){
            model.addRow(new Object[]{});
        } else if(model.getRowCount() > (int)jSpinner1.getValue()){
            model.removeRow(model.getRowCount()-1);
        }
    }//GEN-LAST:event_jSpinner1StateChanged

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        boolean isValid = true;
        boolean isSuccessfulVisitor = false;
        boolean isOthersSpecified = true;
        int increment = 0;
        
        for(int i = 0; i < model.getRowCount(); i++){
            if(model.getValueAt(i, 0) == null || model.getValueAt(i,1) == null){
                isValid = false;
                break;
            } else if(model.getValueAt(i, 0).toString().trim().isEmpty() || model.getValueAt(i,1).toString().trim().isEmpty()){
                isValid = false;
                break;
            }
        }
        if(others.isSelected()){
            if(otherArea.getText().isEmpty()){
                isOthersSpecified = false;
            }
        }
        
        if(isValid && isOthersSpecified && !residentNames.getText().isEmpty()){
            for(int i = 0; i < model.getRowCount(); i++){
                try {
                    String vName = model.getValueAt(i, 0).toString();
                    String rName = residentNames.getText();
                    String timeIn = jSpinner2.getValue() + ":" + jSpinner3.getValue() + ":00 " + jComboBox2.getSelectedItem();
                    String timeOut = jSpinner5.getValue() + ":" + jSpinner6.getValue() + ":00 " + jComboBox3.getSelectedItem();
                    String d = jLabel24.getText();
                    String startD = new SimpleDateFormat("MMM dd, yyyy").format(dateChooserCombo2.getDateFormat().parse(dateChooserCombo2.getText()));
                    String vId = model.getValueAt(i, 1).toString();
                    String area = null;
                    if(mezzanine.isSelected()){
                        area = "mezzanine";
                    } else if(fitness.isSelected()){
                        area = "fitness";
                    } else {
                        area = otherArea.getText();
                    }
                    if(client.insertGroupVisit(new VisitorImpl(vName,rName,timeIn,timeOut,d,startD,vId,area))){
                        isSuccessfulVisitor = false;
                        increment++;
                    }
                    if(increment == 0){
                        isSuccessfulVisitor = true;
                    }
                } catch (RemoteException | ParseException ex) {
//                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(this, ex.getMessage());
                }
            }
        } else {
            if(!isValid){
                new MessageDialog().unsuccessful(this, "All Names and Valid IDs must be Specified in the Table.");
            }
            if(!isOthersSpecified){
                new MessageDialog().unsuccessful(this, "\"Others\" Field must be Specified.");
            }
            if(residentNames.getText().isEmpty()){
                new MessageDialog().unsuccessful(this, "Conforme of Resident is required");
            }
        }
        
        if(!isSuccessfulVisitor && increment > 0){
            new MessageDialog().warning(this, "Some of the Names are not Successfully inserted in the database.");
            groupVisit.setVisible(false);
            visitorTimeInOut.setVisible(true);
            setTable2();
            visitorName.setSelectedIndex(-1);
            residentName.setSelectedIndex(0);
        } else if(isSuccessfulVisitor){
            new MessageDialog().successful(this);
            groupVisit.setVisible(false);
            visitorTimeInOut.setVisible(true);
            setTable2();
            visitorName.setSelectedIndex(-1);
            residentName.setSelectedIndex(0);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jLabel62MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel62MouseClicked
        // TODO add your handling code here:
        timeInAndOut.setVisible(false);
        login.setVisible(true);
        username.setText("");
        password.setText("");
        isEntered = true;
    }//GEN-LAST:event_jLabel62MouseClicked

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        if(login.isVisible()){
            if(isEntered){
                try {
                    user = client.admin_login(new UserLogin(username.getText(),password.getText()));
                    if(user != null){
                        login.setVisible(false);
                        listFingers.setVisible(true);
                        jTextField2.setText("");
                        setResidentFinger();
                        isEntered = false;
                    } else {
                        new MessageDialog().error(this, "Please Try Again.");
                    }
                } catch (RemoteException ex) {
//                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(this, ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jLabel61MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel61MouseClicked
        // TODO add your handling code here:
        if(md.confirmationBack(this) == md.YES){
            listFingers.setVisible(false);
            timeInAndOut.setVisible(true);
        }
    }//GEN-LAST:event_jLabel61MouseClicked

    private void jLabel60MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel60MouseClicked
        // TODO add your handling code here:
        if(capture != null){
            if(capture.isStarted()){
                stopCapture();
            }
            scan(new javax.swing.JLabel(), 0);
        }
        registerFinger.setVisible(false);
        listFingers.setVisible(true);
        setResidentFinger();
        try {
            ArrayList<ResidentImpl> residentImpl = client.getAllResidentFingerprint(jTextField2.getText());
            model = (DefaultTableModel) jTable1.getModel();
            model.getDataVector().removeAllElements();
            model.fireTableDataChanged();
            for(ResidentImpl ri : residentImpl){
                boolean f1 = false;
                boolean f2 = false;
                if(ri.getFingerprint1() != null){
                    f1 = true;
                }
                if(ri.getFingerprint2() != null){
                    f2 = true;
                }
                model.addRow(new Object[]{
                    ri.getId(),
                    ri.getFullName(),
                    f1,
                    f2
                });
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }//GEN-LAST:event_jLabel60MouseClicked

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        login.setVisible(false);
        timeInAndOut.setVisible(true);
        isEntered = false;
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        mouseClicked = true;
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        // TODO add your handling code here:
        if(mouseClicked && jTable1.getSelectedRowCount() == 1){
            if(evt.isPopupTrigger()){
                javax.swing.JTable source = (javax.swing.JTable) evt.getSource();
                r = source.rowAtPoint(evt.getPoint());
                c = source.columnAtPoint(evt.getPoint());
                if(! source.isRowSelected(r)){
                    source.changeSelection(r, c, false, false);
                }
                if((boolean)jTable1.getValueAt(jTable1.getSelectedRow(),2) == Boolean.FALSE || (boolean)jTable1.getValueAt(jTable1.getSelectedRow(),3) == Boolean.FALSE){
                    register.setEnabled(true);
                    change.setEnabled(false);
                } else {
                    register.setEnabled(false);
                    change.setEnabled(true);
                }
                pop.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
        mouseClicked = false;
    }//GEN-LAST:event_jTable1MouseReleased

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1MousePressed

    private void registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerActionPerformed
        // TODO add your handling code here:
        registerFingerprint = true;
        
        jTextField1.setText("");
        listFingers.setVisible(false);
        registerFinger.setVisible(true);
        jLabel56.setText(model.getValueAt(r, 1).toString());
        jLabel59.setText(model.getValueAt(r, 0).toString());
        jLabel54.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setIcon(null);
        jLabel54.setText("FINGER 1");
        jButton11.setEnabled(true);
        jButton12.setEnabled(false);
        jLabel57.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel57.setIcon(null);
        jLabel57.setText("FINGER 2");
        jButton13.setEnabled(true);
        jButton14.setEnabled(false);
        
        jCheckBox1.setSelected(false);
        jCheckBox2.setSelected(false);
        jCheckBox5.setSelected(false);
        jCheckBox6.setSelected(false);
        
        jCheckBox1. setVisible(false);
        jCheckBox2. setVisible(false);
        jCheckBox5. setVisible(false);
        jCheckBox6. setVisible(false);
        jButton15.setEnabled(false);
    }//GEN-LAST:event_registerActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here
        if(capture != null){
            if(capture.isStarted()){
                stopCapture();
                jButton13.setEnabled(true);
                jButton14.setEnabled(false);
            }
        }
        scan(jLabel54, 1);
        jButton11.setEnabled(false);
        jButton12.setEnabled(true);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        if(capture != null){
            if(capture.isStarted()){
                stopCapture();
                jButton11.setEnabled(true);
                jButton12.setEnabled(false);
            }
        }
        scan(jLabel57, 2);
        jButton13.setEnabled(false);
        jButton14.setEnabled(true);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        capture.stopCapture();
        jLabel54.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setIcon(null);
        jLabel54.setText("FINGER 1");
        jButton11.setEnabled(true);
        jButton12.setEnabled(false);
        
        jCheckBox1.setSelected(false);
        jCheckBox2.setSelected(false);
        
        jCheckBox1. setVisible(false);
        jCheckBox2. setVisible(false);
        jTextField1.setText("");
        
        jButton15.setEnabled(false);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        capture.stopCapture();
        jLabel57.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel57.setIcon(null);
        jLabel57.setText("FINGER 2");
        jButton13.setEnabled(true);
        jButton14.setEnabled(false);
        
        jCheckBox5.setSelected(false);
        jCheckBox6.setSelected(false);
        
        jCheckBox5. setVisible(false);
        jCheckBox6. setVisible(false);
        jTextField1.setText("");
        
        jButton15.setEnabled(false);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jCheckBox1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox1MouseClicked
        // TODO add your handling code here:
        if(capture.isStarted()){
            stopCapture();
        }
        standBy = "val1";
        scan(jLabel54, 3);
    }//GEN-LAST:event_jCheckBox1MouseClicked

    private void jCheckBox2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox2MouseClicked
        // TODO add your handling code here:
        if(capture.isStarted()){
            stopCapture();
        }
        standBy = "val2";
        scan(jLabel54, 3);
    }//GEN-LAST:event_jCheckBox2MouseClicked

    private void jCheckBox5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox5MouseClicked
        // TODO add your handling code here:
        if(capture.isStarted()){
            stopCapture();
        }
        standBy = "val5";
        scan(jLabel54, 4);
    }//GEN-LAST:event_jCheckBox5MouseClicked

    private void jCheckBox6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox6MouseClicked
        // TODO add your handling code here:
        if(capture.isStarted()){
            stopCapture();
        }
        standBy = "val6";
        scan(jLabel54, 4);
    }//GEN-LAST:event_jCheckBox6MouseClicked

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        if(new MessageDialog().confirmationSave(this) == new MessageDialog().YES){
            try {
                if(registerFingerprint){
                    if(!client.registerResidentFingerprint(new ResidentImpl(0,jLabel59.getText(),jLabel56.getText(),finger1String,finger2String,""))){
                        new MessageDialog().successful(this);
                        registerFinger.setVisible(false);
                        listFingers.setVisible(true);
                        stopCapture();
                        scan(new javax.swing.JLabel(), 0);
                        registerFingerprint = false;
                        searchFinger();
                    } else {
                        new MessageDialog().unsuccessful(this);
                    }
                } else {
                    if(client.updateResidentFingerprint(new ResidentImpl(0,jLabel59.getText(),jLabel56.getText(),finger1String,finger2String,""))){
                        new MessageDialog().successful(this);
                        registerFinger.setVisible(false);
                        listFingers.setVisible(true);
                        stopCapture();
                        scan(new javax.swing.JLabel(), 0);
                        searchFinger();
                    } else {
                        new MessageDialog().unsuccessful(this);
                    }
                }
                
            } catch (RemoteException ex) {
//                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                md.error(this, ex.getMessage());
            }
        }
        if(capture != null){
            if(capture.isStarted()){
                stopCapture();
            }
            scan(new javax.swing.JLabel(), 0);
        }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void table2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table2MouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 2 && table2.getSelectedRowCount() == 1){
            ROW = table2.getSelectedRow();
            viewGroupVisit.setVisible(true);
            visitorTimeInOut.setVisible(false);
            jLabel66.setText(table2.getValueAt(ROW, 0).toString());
            jLabel68.setText(table2.getValueAt(ROW, 3).toString());
            jLabel70.setText(table2.getValueAt(ROW, 4).toString());
            jLabel72.setText(table2.getValueAt(ROW, 2).toString());
            Date d = Calendar.getInstance().getTime();
            String dd = new SimpleDateFormat("MMM dd, yyyy").format(d);
            jLabel74.setText(dd);
            try {
                ArrayList<VisitorImpl> visitorImpl = client.getVisitorPerGroup(new VisitorImpl(table2.getValueAt(ROW, 0).toString(),"","",table2.getValueAt(ROW, 2).toString(),0));
                model = (DefaultTableModel) jTable2.getModel();
                model.getDataVector().removeAllElements();
                model.fireTableDataChanged();
                for(VisitorImpl v: visitorImpl){
                    model.addRow(new Object[]{
                        v.getVisitorName(),
                        v.getValidId()
                    });
                }
            } catch (RemoteException ex) {
//                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                md.error(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_table2MouseClicked

    private void jLabel63MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel63MouseClicked
        // TODO add your handling code here:
        viewGroupVisit.setVisible(false);
        visitorTimeInOut.setVisible(true);
        model = (DefaultTableModel) table.getModel();
        setChoices();
        setTables();
        model2 = (DefaultTableModel) table2.getModel();
        model2.getDataVector().removeAllElements();
        model2.fireTableDataChanged();
        setTable2();
    }//GEN-LAST:event_jLabel63MouseClicked

    private void jTextField2CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField2CaretUpdate
        // TODO add your handling code here:
        searchFinger();
    }//GEN-LAST:event_jTextField2CaretUpdate

    private void changeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeActionPerformed
        // TODO add your handling code here:
        registerFingerprint = false;
        
        jTextField1.setText("");
        listFingers.setVisible(false);
        registerFinger.setVisible(true);
        jLabel56.setText(model.getValueAt(r, 1).toString());
        jLabel59.setText(model.getValueAt(r, 0).toString());
        jLabel54.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setIcon(null);
        jLabel54.setText("FINGER 1");
        jButton11.setEnabled(true);
        jButton12.setEnabled(false);
        jLabel57.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel57.setIcon(null);
        jLabel57.setText("FINGER 2");
        jButton13.setEnabled(true);
        jButton14.setEnabled(false);
        
        jCheckBox1.setSelected(false);
        jCheckBox2.setSelected(false);
        jCheckBox5.setSelected(false);
        jCheckBox6.setSelected(false);
        
        jCheckBox1. setVisible(false);
        jCheckBox2. setVisible(false);
        jCheckBox5. setVisible(false);
        jCheckBox6. setVisible(false);
        jButton15.setEnabled(false);
    }//GEN-LAST:event_changeActionPerformed

    private void idGetterCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_idGetterCaretUpdate
        // TODO add your handling code here:
        if (iterator) {
            try {
                // TODO add your handling code here:
                Integer.parseInt(idGetter.getText().trim());
                if (groupVisit.isVisible()) {
                    if (idg.isVisible()) {
                        idg.setVisible(false);
                    }
                    setInformation(idGetter.getText().trim());
                } else {
                    if(client.getResidentStatus(idGetter.getText().trim()).trim().equals("Active")) {
                        String response = client.time_In_Out(idGetter.getText().trim());
                        if (response != null) {
                            if (idg.isVisible()) {
                                idg.setVisible(false);
                            }
                            setInformation(response);
                            new MessageDialog().successful(this);
                        } else {
                            new MessageDialog().error(this, "ID doesn't exist");
                        }
                    } else if(client.getResidentStatus(idGetter.getText().trim()).trim().equals("Not Active")){
                        new MessageDialog().unsuccessful(this, "The was Account Deactivated by the Administrator.");
                    } else {
                        new MessageDialog().unsuccessful(this, "The ID specified doesn't exist.");
                    }
                }
            } catch (RemoteException ex) {
//                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                md.error(this, ex.getMessage());
            } catch (NumberFormatException ex) {
//                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                new MessageDialog().error(this, "Invalid ID");
            }
            iterator = false;
        } else {
            iterator = true;
        }
    }//GEN-LAST:event_idGetterCaretUpdate

    private void jTextField3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField3MouseClicked
        // TODO add your handling code here:
        try {
            Accounts acct = client.getAccounts();
            if (!acct.getBiometrics().equalsIgnoreCase("Yes") || jTextField3.getText().equals("SCANNER NOT CONNECTED - CLICK ME")) {
                if (md.login(this, client)) {
                    idg.showMe();
                }
            }
            uname.setText("");
            pword.setText("");
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }//GEN-LAST:event_jTextField3MouseClicked

    private void residentNamesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_residentNamesMouseClicked
        // TODO add your handling code here:
        try {
            Accounts acct = client.getAccounts();
            if(!acct.getBiometrics().equalsIgnoreCase("Yes") || jTextField3.getText().equals("SCANNER NOT CONNECTED - CLICK ME")){
                if(md.login(this, client)){
                    idg.showMe();
                }
            }
            uname.setText("");
            pword.setText("");
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }//GEN-LAST:event_residentNamesMouseClicked

    private void setTables(){
        try {
            ArrayList<VisitorImpl> visitorImpl = client.searchVisitor(search.getText(), true);
            model.getDataVector().removeAllElements();
            model.fireTableDataChanged();
            for(VisitorImpl v : visitorImpl){
                DateFormat oldFormat = new SimpleDateFormat("HH:mm:ss");
                DateFormat newFormat = new SimpleDateFormat("hh:mm a");
                Date date = oldFormat.parse(v.getTimeIn());
                model.addRow(new Object[]{
                    v.getVisitorName(),
                    v.getResidentFullName(),
                    newFormat.format(date)
                });
            }
        } catch (RemoteException | ParseException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }
    
    private void setChoices(){
        try {
            ArrayList<VisitorImpl> visitorImpl = client.searchVisitor("",false);
            visitorName.removeAllItems();
            for(VisitorImpl v : visitorImpl){
                visitorName.addItem(v.getVisitorName());
            }
            visitorName.setSelectedIndex(-1);
            
            ArrayList<ResidentImpl> residentImpl = client.searchTenant("");
            residentName.removeAllItems();
            residentName.addItem("Administrator");
            for(ResidentImpl r : residentImpl){
                residentName.addItem(r.getFullName());
            }
            residentName.setSelectedIndex(0);
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }
    
    public void setInformation(String id){
        try {
            ResidentImpl residentImpl = client.getResidentInformation(id);
            if(residentImpl != null){
                if(timeInAndOut.isVisible()){
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            captureDialog.visibilityOff();
                        }
                    });
                    name.setText(residentImpl.getFullName());
                    status.setText("LOGGED-"+residentImpl.getLastStatus());
                    if(residentImpl.getPicture() != null){
                        InputStream in = new ByteArrayInputStream(residentImpl.getPicture());
                        BufferedImage img = ImageIO.read(in);
                        if(img != null){
                            Image image = img.getScaledInstance(picture.getWidth(), picture.getHeight(), Image.SCALE_SMOOTH);
                            picture.setIcon(new ImageIcon(image));
                        }
                    }
                } else if(groupVisit.isVisible()){
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            captureDialog.visibilityOff();
                        }
                    });
                    residentNames.setText(residentImpl.getFullName());
                } else {
                    messager("RESIDENT: " + residentImpl.getFullName());
                    setDelaying(1000L);
                    messager("SUCCESSFULLY LOGGED-" + residentImpl.getLastStatus());
                    setDelaying(1000L);
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            captureDialog.visibilityOff();
                        }
                    });
                }
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        } catch (IOException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }
    
    private void setTable(){
        try {
            ArrayList<ResidentImpl> residentImpl = client.searchTenant(search1.getText());
            model.getDataVector().removeAllElements();
            model.fireTableDataChanged();
            for(ResidentImpl r : residentImpl){
                model.addRow(new Object[]{
                    r.getFullName(),
                    r.getLastStatus()
                });
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }
    
    private void setTable2(){
        try {
            ArrayList<VisitorImpl> visitorImpl = client.getGroupVisit();
            DateFormat sysTime = new SimpleDateFormat("hh:ss a");
            model2.getDataVector().removeAllElements();
            model2.fireTableDataChanged();
            for(VisitorImpl v : visitorImpl){
                model2.addRow(new Object[]{
                    v.getResidentFullName(),
                    v.getCount(),
                    v.getArea(),
                    sysTime.format(new SimpleDateFormat("HH:mm:ss").parse(v.getTimeIn())),
                    sysTime.format(new SimpleDateFormat("HH:mm:ss").parse(v.getTimeOut()))
                });
            }
        } catch (RemoteException | ParseException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }
    
    private void scan(javax.swing.JLabel label, int fingerSample){
        sampleFinger = fingerSample;
        capture.addDataListener(new DPFPDataAdapter(){
        
            @Override
            public void dataAcquired(DPFPDataEvent e){
                isRecognized = true;
                label.setText("");
                label.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
                DPFPSampleConversion sampleConversion = DPFPGlobal.getSampleConversionFactory();
                if(sampleFinger == 1){
                    finger1 = DPFPGlobal.getSampleFactory().createSample();
                    finger1 = e.getSample();
                    fingerprint = sampleConversion.createImage(finger1);
                    enrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
                    while(enrollment.getFeaturesNeeded() > 0){
                        try {
                            featureExtraction = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
                            DPFPFeatureSet featureSet = featureExtraction.createFeatureSet(finger1, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                            enrollment.addFeatures(featureSet);
                        } catch (DPFPImageQualityException ex) {
//                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            md.error(null, ex.getMessage());
                        }
                    }
                    orgTemplate = enrollment.getTemplate();
                    StringBuilder sb = new StringBuilder();
                    for(byte b : orgTemplate.serialize()){
                        sb.append(String.format("%02x", b));
                    }
                    finger1String = sb.toString();
                } else if(sampleFinger == 2){
                    finger2 = DPFPGlobal.getSampleFactory().createSample();
                    finger2 = e.getSample();
                    fingerprint = sampleConversion.createImage(finger2);
                    enrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
                    while(enrollment.getFeaturesNeeded() > 0){
                        try {
                            featureExtraction = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
                            DPFPFeatureSet featureSet = featureExtraction.createFeatureSet(finger2, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                            enrollment.addFeatures(featureSet);
                        } catch (DPFPImageQualityException ex) {
//                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            md.error(null, ex.getMessage());
                        }
                    }
                    orgTemplate2 = enrollment.getTemplate();
                    StringBuilder sb = new StringBuilder();
                    for(byte b : orgTemplate2.serialize()){
                        sb.append(String.format("%02x", b));
                    }
                    finger2String = sb.toString();
                } else {
                    sample = DPFPGlobal.getSampleFactory().createSample();
                    sample = e.getSample();
                    fingerprint = sampleConversion.createImage(sample);
                }
                
                switch(sampleFinger){
                    case 1:
                        fingerprint = fingerprint.getScaledInstance(172, jLabel54.getHeight()+20, Image.SCALE_SMOOTH);
                        jLabel54.setIcon(new ImageIcon(fingerprint));
                        break;
                    case 2:
                        fingerprint = fingerprint.getScaledInstance(172, jLabel57.getHeight()+20, Image.SCALE_SMOOTH);
                        jLabel57.setIcon(new ImageIcon(fingerprint));
                        break;
                }
//                if(sampleFinger != 3 && sampleFinger != 4){
//                    fingerprint = fingerprint.getScaledInstance(172, label.getHeight()+20, Image.SCALE_SMOOTH);
//                    label.setIcon(new ImageIcon(fingerprint));
//                }
            }
        });
        
        capture.addReaderStatusListener(new DPFPReaderStatusAdapter(){
        
            @Override
            public void readerConnected(DPFPReaderStatusEvent e){
                try {
                    Accounts acct = client.getAccounts();
                    if(acct.getBiometrics().equalsIgnoreCase("No")){
                        jTextField3.setText("SCANNER DISABLED BY THE ADMINISTRATOR - CLICK ME");
                        jTextField3.setForeground(new java.awt.Color(0,0,255));
                    } else {
                        jTextField3.setText("FINGER PRINT SCANNER CONNECTED");
                        jTextField3.setForeground(new java.awt.Color(0,0,255));
                    }
                } catch (RemoteException ex) {
//                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(null, ex.getMessage());
                }
                if(sampleFinger == 3 || sampleFinger == 4){
                    jTextField1.setText("READER READY FOR VALIDATION. PLACE REGISTERED FINGER");
                    jTextField1.setForeground(new java.awt.Color(0,0,255));
                } else if(sampleFinger == 1 || sampleFinger == 2){
                    jTextField1.setText("PLACE YOUR FINGER");
                    jTextField1.setForeground(new java.awt.Color(0,0,255));
                }
            }
            
            @Override
            public void readerDisconnected(DPFPReaderStatusEvent e){
                jTextField3.setText("SCANNER NOT CONNECTED - CLICK ME");
                jTextField3.setForeground(new java.awt.Color(255,0,0));
                jTextField1.setText("FINGER PRINT SCANNER NOT CONNECTED");
                jTextField1.setForeground(new java.awt.Color(255,0,0));
                
            }
        });
        
        capture.addSensorListener(new DPFPSensorAdapter(){
        
            @Override
            public void fingerTouched(DPFPSensorEvent e){
                if(!registerFinger.isVisible()){
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            captureDialog.visibilityOn();
                        }
                    });
                    colorPasser(new java.awt.Color(0,0,255));
                    messager("SCANNING . . .");
                } else {
                    jTextField1.setText("SCANNING . . .");
                    jTextField1.setForeground(new java.awt.Color(0,0,255));
                }
                isScanned = true;
                try {
                    Accounts acct = client.getAccounts();
                    if(!acct.getBiometrics().equalsIgnoreCase("Yes") || jTextField3.getText().equals("FINGER PRINT SCANNER NOT CONNECTED")){
                        if(acct.getBiometrics().equalsIgnoreCase("No")) {
                            jTextField3.setText("SCANNER DISABLED BY THE ADMINISTRATOR - CLICK ME");
                            jTextField3.setForeground(new java.awt.Color(0,0,255));
                        }
                        stopCapture();

                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                captureDialog.visibilityOff();
                            }
                        });
                    }
                } catch (RemoteException ex) {
//                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    md.error(null, ex.getMessage());
                }
            }
            
            @Override
            public void fingerGone(DPFPSensorEvent e){
                if(isScanned){
                    if(!isRecognized){
                        colorPasser(new java.awt.Color(255,0,0));
                        messager("FINGER PRINT UNRECOGNIZED. TRY AGAIN");
                        setDelaying(1000L);
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                captureDialog.visibilityOff();
                            }
                        });
                        jTextField1.setText("FAILED TO CAPTURE. PLACE YOUR FINGER INTO THE DEVICE AGAIN");
                        jTextField1.setForeground(new java.awt.Color(255,0,0));
                    } else {
                        if(sampleFinger == 0){
                            if(performVerification("monitor")){
                                if(!isActivated){
                                    colorPasser(new java.awt.Color(255,0,0));
                                    messager("DEACTIVATED ACCOUNT");
                                    setDelaying(1000L);
                                    SwingUtilities.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            captureDialog.visibilityOff();
                                        }
                                    });
                                }
                            } else {
                                colorPasser(new java.awt.Color(255,0,0));
                                messager("FINGER PRINT UNMATCHED. TRY AGAIN");
                                iteration++;
                                setDelaying(1000L);
                                SwingUtilities.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        captureDialog.visibilityOff();
                                    }
                                });
                                if (iteration == 3) {
                                    stopCapture();
                                    SwingUtilities.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            captureDialog.visibilityOff();
                                        }
                                    });
                                    try {
                                        if(md.login(null, client)){
                                            SwingUtilities.invokeLater(new Runnable() {

                                                @Override
                                                public void run() {
                                                    idg.showMe();
                                                }
                                            });
                                            scan(new javax.swing.JLabel(), 0);
                                            iteration = 0;
                                        }
                                        iteration = 0;
                                        stopCapture();
                                        scan(new javax.swing.JLabel(), 0);
                                    } catch (RemoteException ex) {
//                                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                        md.error(null, ex.getMessage());
                                    }
                                }
                            }
                        } else {
                            if(sampleFinger == 3 || sampleFinger == 4){
                                String finger;
                                if(sampleFinger == 3){
                                    finger = "main";
                                } else {
                                    finger = "backup";
                                }
                                if(performVerification(finger)){
                                    jTextField1.setText("FINGERPRINT MATCHED");
                                    jTextField1.setForeground(new java.awt.Color(0,204,0));
                                    setSelected();
                                } else {
                                    jTextField1.setText("FINGER PRINT UNMATCHED. REPEAT PROCESS");
                                    jTextField1.setForeground(new java.awt.Color(255,0,0));
                                }

                                if(jCheckBox1.isSelected() && jCheckBox2.isSelected() && jCheckBox5.isSelected() && jCheckBox6.isSelected()){
                                    jButton15.setEnabled(true);
                                } else {
                                    jButton15.setEnabled(false);
                                }
                            } else {
                                if(sampleFinger == 1){
                                    jCheckBox1. setVisible(true);
                                    jCheckBox2. setVisible(true);
                                } else {
                                    jCheckBox5. setVisible(true);
                                    jCheckBox6. setVisible(true);
                                }
                                jTextField1.setText("SUCCESSFUL");
                                jTextField1.setForeground(new java.awt.Color(0,204,0));
                                stopCapture();
                            }
                        }
                    }
                }
                isRecognized = false;
                isScanned = false;
            }
        });
        if(!capture.isStarted()){
            capture.startCapture();
        }
    }
    
    private void stopCapture(){
        capture.stopCapture();
    }
    
    private void setResidentFinger(){
        try {
            ArrayList<ResidentImpl> residentImpl = client.getAllResidentFingerprint();
            model = (DefaultTableModel) jTable1.getModel();
            model.getDataVector().removeAllElements();
            model.fireTableDataChanged();
            for(ResidentImpl ri : residentImpl){
                boolean f1 = false;
                boolean f2 = false;
                if(ri.getFingerprint1() != null){
                    f1 = true;
                }
                if(ri.getFingerprint2() != null){
                    f2 = true;
                }
                model.addRow(new Object[]{
                    ri.getId(),
                    ri.getFullName(),
                    f1,
                    f2
                });
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }
    
    public boolean performVerification(String finger) {
        try {
            
            DPFPFeatureExtraction featureExtractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            DPFPFeatureSet featureSet = featureExtractor.createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
            
            DPFPVerification matcher = DPFPGlobal.getVerificationFactory().createVerification();
            matcher.setFARRequested(DPFPVerification.MEDIUM_SECURITY_FAR);
            
            if(finger.equals("main")){
                if(orgTemplate != null){
                    DPFPVerificationResult res = matcher.verify(featureSet, orgTemplate);
                    return res.isVerified();
                }
            } else if(finger.equals("backup")){
                if(orgTemplate2 != null){
                    DPFPVerificationResult res = matcher.verify(featureSet, orgTemplate2);
                    return res.isVerified();
                }
            } else {
                ArrayList<ResidentImpl> residentImpl = client.getAllResidentFingerprint();
                messager("VERIFYING . . .");
                boolean verified = false;
                for(ResidentImpl res : residentImpl){
                    if(res.getFingerprint1() != null && res.getFingerprint2() != null){
                        DPFPTemplate temp1 = DPFPGlobal.getTemplateFactory().createTemplate();
                        temp1.deserialize(stringToByte(res.getFingerprint1()));
                        DPFPTemplate temp2 = DPFPGlobal.getTemplateFactory().createTemplate();
                        temp2.deserialize(stringToByte(res.getFingerprint2()));
                        if(matcher.verify(featureSet, temp1).isVerified() || matcher.verify(featureSet, temp2).isVerified()){
                            verificationId = res.getId();
//                            residentNames.setText(res.getFullName());
                            verified = true;
                            if(res.getStatus().equals("Active")){
                                isActivated = true;
                                colorPasser(new java.awt.Color(0,204,0));
                                messager("FINGER PRINT MATCHED");
                                setDelaying(1000L);
                                iteration = 0;
                                if(timeInAndOut.isVisible()){
                                    setInformation(client.time_In_Out(verificationId));
                                } else {
                                    setInformation(verificationId);
                                }
                                clear.setEnabled(true);
                            } else if(res.getStatus().equals("Leave")){
                                return false;
//                                new MessageDialog().error(this, "This account was deactivated. Consult the administrators\nfor this issue.");
                            } else {
                                isActivated = false;
                            }
                            break;
                        }
                    }
                }
                return verified;
            }
            
        } catch (DPFPImageQualityException | RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
        return false;
    }
    
    private void setSelected(){
        switch(standBy){
            case "val1":
                jCheckBox1.setSelected(true);
                break;
            case "val2":
                jCheckBox2.setSelected(true);
                break;
            case "val5":
                jCheckBox5.setSelected(true);
                break;
            case "val6":
                jCheckBox6.setSelected(true);
                break;
        }
        standBy = "";
        stopCapture();
    }
    
    private byte[] stringToByte(String hex){
        HexBinaryAdapter adapter = new HexBinaryAdapter();
        byte[] bytes = adapter.unmarshal(hex);
        return bytes;
    }
    
    private void searchFinger(){
        try {
            ArrayList<ResidentImpl> residentImpl = client.getAllResidentFingerprint(jTextField2.getText());
            model = (DefaultTableModel) jTable1.getModel();
            model.getDataVector().removeAllElements();
            model.fireTableDataChanged();
            for(ResidentImpl ri : residentImpl){
                boolean f1 = false;
                boolean f2 = false;
                if(ri.getFingerprint1() != null){
                    f1 = true;
                }
                if(ri.getFingerprint2() != null){
                    f2 = true;
                }
                model.addRow(new Object[]{
                    ri.getId(),
                    ri.getFullName(),
                    f1,
                    f2
                });
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
        }
    }
    
    private void messager(String message){
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                captureDialog.setMessage(message);
            }
        });
        captureDialog.setMessage(message);
    }
    
    private void colorPasser(Color c){
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                captureDialog.setLabelColor(c);
            }
        });
    }
    
    private void setDelaying(long l){
        try {
            Thread.sleep(l);
        } catch (InterruptedException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            md.error(this, ex.getMessage());
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
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            new MessageDialog().error(null, ex.getMessage());
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup area;
    private javax.swing.JMenuItem change;
    private javax.swing.JButton clear;
    private javax.swing.JLabel date;
    private javax.swing.JLabel date1;
    private datechooser.beans.DateChooserCombo dateChooserCombo2;
    private javax.swing.JLabel day;
    private javax.swing.JLabel exit;
    private javax.swing.JRadioButton fitness;
    private javax.swing.JPanel groupVisit;
    private javax.swing.JLabel groupVisitButton;
    private javax.swing.JLayeredPane home;
    private javax.swing.JTextField idGetter;
    private javax.swing.JCheckBox inform;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
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
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JSpinner jSpinner6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JPanel listFingers;
    private javax.swing.JPanel login;
    private javax.swing.JRadioButton mezzanine;
    private javax.swing.JTextField name;
    private javax.swing.JTextField otherArea;
    private javax.swing.JRadioButton others;
    private javax.swing.JPasswordField password;
    private javax.swing.JLabel picture;
    private javax.swing.JPopupMenu pop;
    private javax.swing.JComboBox reason;
    private javax.swing.JMenuItem register;
    private javax.swing.JPanel registerFinger;
    private javax.swing.JComboBox residentName;
    private javax.swing.JTextField residentNames;
    private javax.swing.JTextField search;
    private javax.swing.JTextField search1;
    private javax.swing.JTextField status;
    private javax.swing.JButton submit;
    private javax.swing.JTable table;
    private javax.swing.JTable table1;
    private javax.swing.JTable table2;
    private javax.swing.JPanel tenantSearch;
    private javax.swing.JLabel tenant_search;
    private javax.swing.JPanel terms;
    private javax.swing.JLabel time;
    private javax.swing.JLabel time1;
    private javax.swing.JPanel timeInAndOut;
    private javax.swing.JLabel time_in_out;
    private javax.swing.JTextField username;
    private javax.swing.JPanel viewGroupVisit;
    private javax.swing.JComboBox visitorName;
    private javax.swing.JTable visitorTable;
    private javax.swing.JPanel visitorTimeInOut;
    private javax.swing.JLabel visitor_login;
    // End of variables declaration//GEN-END:variables
}
