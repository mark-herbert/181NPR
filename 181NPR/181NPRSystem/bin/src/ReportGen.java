/**
 * IMPORTS*
 */
//PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
//JAVA
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
//JFREE
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Joseph Madrid
 */
public class ReportGen extends javax.swing.JFrame {

    /**
     * Creates new form ReportGen
     */
//    private final XYSeries s1;
    /**
     * ASSIGNING PER ROWS
     */
    private DefaultTableModel tableModel = new DefaultTableModel();
    private JFreeChart chart;
    private DefaultCategoryDataset dataset;
    private CategoryPlot plot;
    private final NPRInterface client;
    private String user;
    private boolean setContinue = false;
    private String path = "";

    public ReportGen(NPRInterface client, String user) throws IOException {
        initComponents();
        this.client = client;
        this.user = user;
        tablelabel1.setEnabled(false);
        residents.setEnabled(false);
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
            residents.removeAllItems();
            ArrayList<ResidentImpl> list = client.getAllResident();
            for (ResidentImpl l : list) {
                residents.addItem(l.getFullName());
            }
        } catch (RemoteException | FileNotFoundException ex) {
//            Logger.getLogger(ReportGen.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        try {
            residents.removeAllItems();
            ArrayList<ResidentImpl> list = client.getAllResident();
            for (ResidentImpl l : list) {
                residents.addItem(l.getFullName());
            }
            setContinue = true;
        } catch (RemoteException ex) {
//            Logger.getLogger(ReportGen.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
        defaultPreview();
    }

    public ReportGen(NPRInterface client) {
        initComponents();
        this.client = client;
    }

    private void yearlyPreview() throws NumberFormatException {
        exportcounttableexcel.setEnabled(true);
        exportcounttablepdf.setEnabled(true);

        tableModel = (DefaultTableModel) dataTable.getModel();
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
        String str[] = {"Years", "Values"};
        tableModel.setColumnIdentifiers(str);
        ChartPanel chartPanel;
        displaypane.removeAll();
        displaypane.revalidate();
        displaypane.repaint();
        displaypane.setLayout(new BorderLayout());
        //row
        String series1 = "Results";
        //column
        String year[] = {"2014", "2015", "2016", "2017", "2018", "2019", "2020"};

        dataset = new DefaultCategoryDataset();
        dataset.addValue(0, series1, year[0]);
        dataset.addValue(1, series1, year[1]);
        dataset.addValue(2, series1, year[2]);
        dataset.addValue(3, series1, year[3]);
        dataset.addValue(4, series1, year[4]);
        dataset.addValue(5, series1, year[5]);
        dataset.addValue(6, series1, year[6]);

        chart = ChartFactory.createBarChart(
                "181 North Place Residences Graph", // chart title
                "Years", // domain axis label
                "Value", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
                0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp1 = new GradientPaint(
                0.0f, 0.0f, Color.green,
                0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp2 = new GradientPaint(
                0.0f, 0.0f, Color.red,
                0.0f, 0.0f, Color.lightGray
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        final org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        chartPanel = new ChartPanel(chart);
        displaypane.add(chartPanel, BorderLayout.CENTER);
    }

    private void defaultPreview() throws NumberFormatException {

        tableModel = (DefaultTableModel) dataTable.getModel();
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
        String str[] = {"X", "Y"};
        tableModel.setColumnIdentifiers(str);

        exportcounttableexcel.setEnabled(false);
        exportcounttablepdf.setEnabled(false);
//        exportgraphtoimage.setEnabled(false);

        ChartPanel chartPanel;
        displaypane.removeAll();
        displaypane.revalidate();
        displaypane.repaint();
        displaypane.setLayout(new BorderLayout());
        //row
        String series1 = "Results";
        //column
        String values[] = {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-"};
        int value[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < values.length; i++) {
            tableModel.addRow(new Object[]{
                values[i],
                value[i]
            });
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int c = 0; c < value.length; c++) {
            dataset.addValue(value[c], series1, values[c]);
        }

        chart = ChartFactory.createBarChart(
                "181 North Place Residences Graph", // chart title
                "Default", // domain axis label
                "Value", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
                0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp1 = new GradientPaint(
                0.0f, 0.0f, Color.green,
                0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp2 = new GradientPaint(
                0.0f, 0.0f, Color.red,
                0.0f, 0.0f, Color.lightGray
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        final org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        chartPanel = new ChartPanel(chart);
        displaypane.add(chartPanel, BorderLayout.CENTER);
    }

    private void monthlyPreview2014Reservation() throws NumberFormatException {
        try {
            exportcounttableexcel.setEnabled(true);
            exportcounttablepdf.setEnabled(true);
//            exportgraphtoimage.setEnabled(true);

            tableModel = (DefaultTableModel) dataTable.getModel();
            tableModel.getDataVector().removeAllElements();
            tableModel.fireTableDataChanged();
            String str[] = {"Months", "Values"};
            tableModel.setColumnIdentifiers(str);

            ChartPanel chartPanel;
            displaypane.removeAll();
            displaypane.revalidate();
            displaypane.repaint();
            displaypane.setLayout(new BorderLayout());
            //row
            String series1 = "Results";
            //column,
            String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String yr = datecombobox.getSelectedItem().toString();
            int value[] = new int[12];
            int c = 0;
            for (String month : months) {
                value[c] = client.getCountMonthlyReportReservation("0" + (c + 1), yr);
                c++;
            }

            for (int i = 0; i < months.length; i++) {
                tableModel.addRow(new Object[]{
                    months[i],
                    value[i]
                });
            }
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(value[0], series1, months[0]);
            dataset.addValue(value[1], series1, months[1]);
            dataset.addValue(value[2], series1, months[2]);
            dataset.addValue(value[3], series1, months[3]);
            dataset.addValue(value[4], series1, months[4]);
            dataset.addValue(value[5], series1, months[5]);
            dataset.addValue(value[6], series1, months[6]);
            dataset.addValue(value[7], series1, months[7]);
            dataset.addValue(value[8], series1, months[8]);
            dataset.addValue(value[9], series1, months[9]);
            dataset.addValue(value[10], series1, months[10]);
            dataset.addValue(value[11], series1, months[11]);

            chart = ChartFactory.createBarChart(
                    "181 North Place Residences Graph", // chart title
                    "Months of the Year 2014", // domain axis label
                    "Value", // range axis label
                    dataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
            );

            // set the background color for the chart...
            chart.setBackgroundPaint(Color.white);

            // get a reference to the plot for further customisation...
            final CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            // set the range axis to display integers only...
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // disable bar outlines...
            final BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setDrawBarOutline(false);

            // set up gradient paints for series...
            final GradientPaint gp0 = new GradientPaint(
                    0.0f, 0.0f, Color.blue,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp1 = new GradientPaint(
                    0.0f, 0.0f, Color.green,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp2 = new GradientPaint(
                    0.0f, 0.0f, Color.red,
                    0.0f, 0.0f, Color.lightGray
            );
            renderer.setSeriesPaint(0, gp0);
            renderer.setSeriesPaint(1, gp1);
            renderer.setSeriesPaint(2, gp2);

            final org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
            );
            chartPanel = new ChartPanel(chart);
            displaypane.add(chartPanel, BorderLayout.CENTER);

        } catch (RemoteException ex) {
//            Logger.getLogger(ReportGen.class.getName()).log(Level.SEVERE, null, ex);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tablelabel = new javax.swing.JLabel();
        combo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        yearradiobutton = new javax.swing.JRadioButton();
        monthlyradiobutton = new javax.swing.JRadioButton();
        panel1 = new javax.swing.JPanel();
        displaypane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        datecombobox = new javax.swing.JComboBox();
        selectyearlabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        exportcounttableexcel = new javax.swing.JButton();
        exportcounttablepdf = new javax.swing.JButton();
        residents = new javax.swing.JComboBox();
        tablelabel1 = new javax.swing.JLabel();
        weeklyradiobutton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        home = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);
        setFocusable(false);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("icons/Reports.png")).getImage());
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Report Generation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 24))); // NOI18N

        tablelabel.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        tablelabel.setText("Table:");
        tablelabel.setEnabled(false);

        combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Please choose", "Number of Reservation", "Number of Registration", "Number of Late Residents", "Number of Late Payers" }));
        combo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboItemStateChanged(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        jLabel3.setText("Select Time Period:");

        yearradiobutton.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(yearradiobutton);
        yearradiobutton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        yearradiobutton.setText("Yearly");
        yearradiobutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearradiobuttonActionPerformed(evt);
            }
        });

        monthlyradiobutton.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(monthlyradiobutton);
        monthlyradiobutton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        monthlyradiobutton.setText("Monthly");
        monthlyradiobutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthlyradiobuttonActionPerformed(evt);
            }
        });

        panel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout displaypaneLayout = new javax.swing.GroupLayout(displaypane);
        displaypane.setLayout(displaypaneLayout);
        displaypaneLayout.setHorizontalGroup(
            displaypaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 658, Short.MAX_VALUE)
        );
        displaypaneLayout.setVerticalGroup(
            displaypaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 403, Short.MAX_VALUE)
        );

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"5", "6"},
                {"6", "7"},
                {"5", "6"}
            },
            new String [] {
                "X", "Y"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        dataTable.setEnabled(false);
        jScrollPane2.setViewportView(dataTable);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(displaypane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displaypane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        datecombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2014", "2015", "2016", "2017", "2018", "2019", "2020" }));
        datecombobox.setEnabled(false);
        datecombobox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                datecomboboxItemStateChanged(evt);
            }
        });

        selectyearlabel.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        selectyearlabel.setText("Year:");
        selectyearlabel.setEnabled(false);

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/archivesicon.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Export Reports", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rondalo", 0, 18))); // NOI18N

        exportcounttableexcel.setText("Export Table to Excel");
        exportcounttableexcel.setEnabled(false);
        exportcounttableexcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportcounttableexcelActionPerformed(evt);
            }
        });

        exportcounttablepdf.setText("Export Table to PDF");
        exportcounttablepdf.setEnabled(false);
        exportcounttablepdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportcounttablepdfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(exportcounttableexcel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addComponent(exportcounttablepdf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(exportcounttableexcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(exportcounttablepdf, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        residents.setEnabled(false);
        residents.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                residentsItemStateChanged(evt);
            }
        });
        residents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                residentsActionPerformed(evt);
            }
        });

        tablelabel1.setFont(new java.awt.Font("Rondalo", 1, 14)); // NOI18N
        tablelabel1.setText("Resident Name:");
        tablelabel1.setEnabled(false);

        weeklyradiobutton.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(weeklyradiobutton);
        weeklyradiobutton.setFont(new java.awt.Font("Rondalo", 0, 12)); // NOI18N
        weeklyradiobutton.setText("Weekly");
        weeklyradiobutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weeklyradiobuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(weeklyradiobutton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(monthlyradiobutton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(yearradiobutton))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(30, 30, 30)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(101, 101, 101)
                                    .addComponent(residents, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(tablelabel1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(40, 40, 40)
                                .addComponent(selectyearlabel)
                                .addGap(4, 4, 4)
                                .addComponent(datecombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tablelabel)
                                .addGap(4, 4, 4)
                                .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(datecombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(selectyearlabel)
                            .addComponent(tablelabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(weeklyradiobutton)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(yearradiobutton)
                                        .addComponent(monthlyradiobutton))))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tablelabel1)
                                .addComponent(residents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back.png"))); // NOI18N
        home.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(440, 440, 440)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(home, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(home))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
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
        new ArchivesMenu(client, user).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void exportcounttablepdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportcounttablepdfActionPerformed
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path+"\\counttablePDF.pdf"));
            document.open();

            PdfPTable tab = new PdfPTable(dataTable.getColumnCount());
            for (int i = 0; i < dataTable.getColumnCount(); i++) {
                tab.addCell(dataTable.getColumnName(i));
            }
            for (int i = 0; i < dataTable.getRowCount(); i++) {
                for (int j = 0; j < dataTable.getColumnCount(); j++) {
                    tab.addCell(dataTable.getValueAt(i, j).toString());
                }
            }
            document.add(tab);

            JOptionPane.showMessageDialog(null, "Data saved at " + path
                    + "'\\counttablePDF.pdf' successfully", "Message",
                    JOptionPane.INFORMATION_MESSAGE);
            document.close();

        } catch (FileNotFoundException | DocumentException ex) {
//            Logger.getLogger(ResidentArchiveFrame.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_exportcounttablepdfActionPerformed

    private void exportcounttableexcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportcounttableexcelActionPerformed
        // TODO add your handling code here:
        try {
            ExcelExporter exp = new ExcelExporter();
            String file = path+"\\countTable.xls";
            int count = 0;
            if (file.equalsIgnoreCase(file)) {
                exp.fillData(dataTable, new File(file));
                count += 1;
            }

            JOptionPane.showMessageDialog(null, "Data saved at " + path
                    + "'\\countTable.xls' successfully", "Message",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
//            ex.printStackTrace();
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_exportcounttableexcelActionPerformed

    private void comboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboItemStateChanged
        String table = combo.getSelectedItem().toString().trim();
        residents.setEnabled(false);
        tablelabel1.setEnabled(false);
        datecombobox.setEnabled(false);
        weeklyradiobutton.setEnabled(false);
        yearradiobutton.setEnabled(false);
        monthlyradiobutton.setSelected(true);
        monthlyradiobutton.setEnabled(false);

        if (yearradiobutton.isSelected()) {
            switch (table) {
                case "Number of Reservation":
                    tablelabel1.setEnabled(false);
                    residents.setEnabled(false);
                    datecombobox.setEnabled(true);
                    weeklyradiobutton.setEnabled(false);
                    yearradiobutton.setEnabled(true);
                    monthlyradiobutton.setEnabled(true);
                    yearlyPreview(getTable());
                    break;

                case "Number of Registration":
                    tablelabel1.setEnabled(false);
                    residents.setEnabled(false);
                    datecombobox.setEnabled(true);
                    weeklyradiobutton.setEnabled(false);
                    yearradiobutton.setEnabled(true);
                    monthlyradiobutton.setEnabled(true);
                    yearlyPreview(getTable());
                    break;
                case "Number of Late Residents":
                    tablelabel1.setEnabled(false);
                    residents.setEnabled(false);
                    datecombobox.setEnabled(true);
                    weeklyradiobutton.setEnabled(false);
                    yearradiobutton.setEnabled(true);
                    monthlyradiobutton.setEnabled(true);
                    yearlyPreview(getTable());
                    break;
                case "Number of Late Payers":
                    residents.setEnabled(true);
                    datecombobox.setEnabled(true);
                    weeklyradiobutton.setEnabled(false);
                    yearradiobutton.setEnabled(false);
                    monthlyradiobutton.setEnabled(true);
                    yearlyPreview(getTable());
                    break;
                default:
                    residents.setEnabled(false);
                    datecombobox.setEnabled(false);
                    weeklyradiobutton.setEnabled(false);
                    yearradiobutton.setEnabled(false);
                    monthlyradiobutton.setEnabled(false);
                    defaultPreview();
            }
        } else {
            String year = datecombobox.getSelectedItem().toString().trim();
            switch (year) {
                case "2014":
                    monthly(year);
                    break;
                case "2015":
                    monthly(year);
                    break;
                case "2016":
                    monthly(year);
                    break;
                case "2017":
                    monthly(year);
                    break;
                case "2018":
                    monthly(year);
                    break;
                case "2019":
                    monthly(year);
                    break;
                case "2020":
                    monthly(year);
                    break;
            }
        }
    }//GEN-LAST:event_comboItemStateChanged

    private void monthly(String year) throws NumberFormatException {
        switch (combo.getSelectedItem().toString()) {
            case "Number of Reservation":
                residents.setEnabled(false);
                datecombobox.setEnabled(true);
                weeklyradiobutton.setEnabled(false);
                yearradiobutton.setEnabled(true);
                monthlyradiobutton.setEnabled(true);
                monthlyPreview(year, getTable());
                break;
            case "Number of Registration":
                residents.setEnabled(false);
                datecombobox.setEnabled(true);
                weeklyradiobutton.setEnabled(false);
                yearradiobutton.setEnabled(true);
                monthlyradiobutton.setEnabled(true);
                monthlyPreview(year, getTable());
                break;
            case "Number of Late Residents":
                residents.setEnabled(true);
                datecombobox.setEnabled(true);
                weeklyradiobutton.setEnabled(false);
                yearradiobutton.setEnabled(false);
                monthlyradiobutton.setEnabled(true);
                monthlyPreview(year, getTable());
                break;
            case "Number of Late Payers":
                residents.setEnabled(true);
                datecombobox.setEnabled(true);
                weeklyradiobutton.setEnabled(false);
                yearradiobutton.setEnabled(false);
                monthlyradiobutton.setEnabled(true);
                monthlyPreview(year, getTable());
                break;
            default:
                residents.setEnabled(false);
                datecombobox.setEnabled(false);
                weeklyradiobutton.setEnabled(false);
                yearradiobutton.setEnabled(false);
                monthlyradiobutton.setEnabled(false);
                defaultPreview();
        }
    }

    private void monthlyradiobuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthlyradiobuttonActionPerformed
        setPreview();
    }//GEN-LAST:event_monthlyradiobuttonActionPerformed

    private void setPreview() throws NumberFormatException {
        if (monthlyradiobutton.isSelected()) {
            datecombobox.setEnabled(true);
            selectyearlabel.setEnabled(true);

            if (datecombobox.isVisible()) {
                tablelabel.setEnabled(true);
                combo.setEnabled(true);
            }
            monthlyPreview(datecombobox.getSelectedItem().toString(), getTable());
        } else if (yearradiobutton.isSelected()) {
            datecombobox.setEnabled(false);
            selectyearlabel.setEnabled(false);
            tablelabel.setEnabled(true);
            combo.setEnabled(true);
            yearlyPreview(getTable());
        }
    }

    private void yearradiobuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearradiobuttonActionPerformed
        setPreview();
    }//GEN-LAST:event_yearradiobuttonActionPerformed

    private void datecomboboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_datecomboboxItemStateChanged
        // TODO add your handling code here:
        String year = datecombobox.getSelectedItem().toString().trim();
        switch (year) {
            case "2014":
                if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Reservation")) {
                    monthlyPreview(year, "Reservation");
                } else if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Registration")) {
                    monthlyPreview(year, "Registration");
                }
                break;
            case "2015":
                if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Reservation")) {
                    monthlyPreview(year, "Reservation");
                } else if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Registration")) {
                    monthlyPreview(year, "Registration");
                }
                break;
            case "2016":
                if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Reservation")) {
                    monthlyPreview(year, "Reservation");
                } else if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Registration")) {
                    monthlyPreview(year, "Registration");
                }
                break;
            case "2017":
                if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Reservation")) {
                    monthlyPreview(year, "Reservation");
                } else if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Registration")) {
                    monthlyPreview(year, "Registration");
                }
                break;
            case "2018":
                if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Reservation")) {
                    monthlyPreview(year, "Reservation");
                } else if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Registration")) {
                    monthlyPreview(year, "Registration");
                }
                break;
            case "2019":
                if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Reservation")) {
                    monthlyPreview(year, "Reservation");
                } else if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Registration")) {
                    monthlyPreview(year, "Registration");
                }
                break;
            case "2020":
                if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Reservation")) {
                    monthlyPreview(year, "Reservation");
                } else if (combo.getSelectedItem().toString().trim().equalsIgnoreCase("Number of Registration")) {
                    monthlyPreview(year, "Registration");
                }
                break;
        }
    }//GEN-LAST:event_datecomboboxItemStateChanged

    private void homeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeMouseClicked
        this.dispose();
        new Main(client, user).setVisible(true);
    }//GEN-LAST:event_homeMouseClicked

    private void residentsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_residentsItemStateChanged
        try {
            if (residents.getItemCount() > 0) {
                String resident = residents.getSelectedItem().toString();
                weeklyPreviewOfLate(client.getResidentId(resident));
            }
        } catch (RemoteException ex) {
//            Logger.getLogger(ReportGen.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }//GEN-LAST:event_residentsItemStateChanged

    private void residentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_residentsActionPerformed
//        try {
//            String resident = residents.getSelectedItem().toString();
//            weeklyPreviewOfLate(client.getResidentId(resident));
//        } catch (RemoteException ex) {
//            Logger.getLogger(ReportGen.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_residentsActionPerformed

    private void weeklyradiobuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weeklyradiobuttonActionPerformed
        setPreview();
    }//GEN-LAST:event_weeklyradiobuttonActionPerformed

    private void weeklyPreviewOfLate(String residentId) {
        try {
            exportcounttableexcel.setEnabled(true);
            exportcounttablepdf.setEnabled(true);
//            exportgraphtoimage.setEnabled(true);

            tableModel = (DefaultTableModel) dataTable.getModel();
            tableModel.getDataVector().removeAllElements();
            tableModel.fireTableDataChanged();
            String str[] = {"Months", "Values"};
            tableModel.setColumnIdentifiers(str);

            ChartPanel chartPanel;
            displaypane.removeAll();
            displaypane.revalidate();
            displaypane.repaint();
            displaypane.setLayout(new BorderLayout());
            //row
            String series1 = "Results";
            //column,
            String days[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            String resident = residents.getSelectedItem().toString();

            int value[] = new int[12];
            int c = 0;
            for (String day : days) {
                value[c] = client.getDailyReport("0" + (c + 1), client.getResidentId(resident));
                c++;
            }

            for (int i = 0; i < days.length; i++) {
                tableModel.addRow(new Object[]{
                    days[i],
                    value[i]
                });
            }
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(value[0], series1, days[0]);
            dataset.addValue(value[1], series1, days[1]);
            dataset.addValue(value[2], series1, days[2]);
            dataset.addValue(value[3], series1, days[3]);
            dataset.addValue(value[4], series1, days[4]);
            dataset.addValue(value[5], series1, days[5]);
            dataset.addValue(value[6], series1, days[6]);

            chart = ChartFactory.createBarChart(
                    "181 North Place Residences Graph", // chart title
                    "Months of the Year 2014", // domain axis label
                    "Value", // range axis label
                    dataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
            );

            // set the background color for the chart...
            chart.setBackgroundPaint(Color.white);

            // get a reference to the plot for further customisation...
            final CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            // set the range axis to display integers only...
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // disable bar outlines...
            final BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setDrawBarOutline(false);

            // set up gradient paints for series...
            final GradientPaint gp0 = new GradientPaint(
                    0.0f, 0.0f, Color.blue,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp1 = new GradientPaint(
                    0.0f, 0.0f, Color.green,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp2 = new GradientPaint(
                    0.0f, 0.0f, Color.red,
                    0.0f, 0.0f, Color.lightGray
            );
            renderer.setSeriesPaint(0, gp0);
            renderer.setSeriesPaint(1, gp1);
            renderer.setSeriesPaint(2, gp2);

            final org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
            );
            chartPanel = new ChartPanel(chart);
            displaypane.add(chartPanel, BorderLayout.CENTER);
        } catch (RemoteException ex) {
//            Logger.getLogger(ReportGen.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }

    private void monthlyPreview(String year, String table) throws NumberFormatException {
        try {
            exportcounttableexcel.setEnabled(true);
            exportcounttablepdf.setEnabled(true);
//            exportgraphtoimage.setEnabled(true);

            tableModel = (DefaultTableModel) dataTable.getModel();
            tableModel.getDataVector().removeAllElements();
            tableModel.fireTableDataChanged();
            String str[] = {"Months", "Values"};
            tableModel.setColumnIdentifiers(str);

            ChartPanel chartPanel;
            displaypane.removeAll();
            displaypane.revalidate();
            displaypane.repaint();
            displaypane.setLayout(new BorderLayout());
            //row
            String series1 = "Results";
            //column,
            String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String yr = year;
            int value[] = new int[12];
            int c = 0;
            switch (table) {
                case "Reservation":
                    for (String month : months) {
                        value[c] = client.getCountMonthlyReportReservation("0" + (c + 1), yr);
                        c++;
                    }
                    break;

                case "Registration":
                    for (String month : months) {
                        value[c] = client.getCountMonthlyReportRegistration("0" + (c + 1), yr);
                        c++;
                    }
                    break;

                case "Bill":
                    for (String month : months) {
                        String result = client.getMonthlyBillingReport("0" + (c + 1), yr);
                        if (result != null) {
                            String[] dates = result.split("-");
                            if (Integer.parseInt(dates[2]) > 5) {
                                value[c] += 1;
                            }
                        }
                        c++;
                    }
                    break;
            }

            for (int i = 0; i < months.length; i++) {
                tableModel.addRow(new Object[]{
                    months[i],
                    value[i]
                });
            }
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(value[0], series1, months[0]);
            dataset.addValue(value[1], series1, months[1]);
            dataset.addValue(value[2], series1, months[2]);
            dataset.addValue(value[3], series1, months[3]);
            dataset.addValue(value[4], series1, months[4]);
            dataset.addValue(value[5], series1, months[5]);
            dataset.addValue(value[6], series1, months[6]);
            dataset.addValue(value[7], series1, months[7]);
            dataset.addValue(value[8], series1, months[8]);
            dataset.addValue(value[9], series1, months[9]);
            dataset.addValue(value[10], series1, months[10]);
            dataset.addValue(value[11], series1, months[11]);

            chart = ChartFactory.createBarChart(
                    "181 North Place Residences Graph", // chart title
                    "Months of the Year 2014", // domain axis label
                    "Value", // range axis label
                    dataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
            );

            // set the background color for the chart...
            chart.setBackgroundPaint(Color.white);

            // get a reference to the plot for further customisation...
            final CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            // set the range axis to display integers only...
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // disable bar outlines...
            final BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setDrawBarOutline(false);

            // set up gradient paints for series...
            final GradientPaint gp0 = new GradientPaint(
                    0.0f, 0.0f, Color.blue,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp1 = new GradientPaint(
                    0.0f, 0.0f, Color.green,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp2 = new GradientPaint(
                    0.0f, 0.0f, Color.red,
                    0.0f, 0.0f, Color.lightGray
            );
            renderer.setSeriesPaint(0, gp0);
            renderer.setSeriesPaint(1, gp1);
            renderer.setSeriesPaint(2, gp2);

            final org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
            );
            chartPanel = new ChartPanel(chart);
            displaypane.add(chartPanel, BorderLayout.CENTER);
        } catch (RemoteException ex) {
//            Logger.getLogger(ReportGen.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }

    private void yearlyPreview(String table) throws NumberFormatException {
        try {
            exportcounttableexcel.setEnabled(true);
            exportcounttablepdf.setEnabled(true);
//            exportgraphtoimage.setEnabled(true);

            tableModel = (DefaultTableModel) dataTable.getModel();
            tableModel.getDataVector().removeAllElements();
            tableModel.fireTableDataChanged();
            String str[] = {"Months", "Values"};
            tableModel.setColumnIdentifiers(str);

            ChartPanel chartPanel;
            displaypane.removeAll();
            displaypane.revalidate();
            displaypane.repaint();
            displaypane.setLayout(new BorderLayout());
            //row
            String series1 = "Results";
            //column,
            String years[] = {"2014", "2015", "2016", "2017", "2018", "2019", "2020"};
            int value[] = new int[7];
            int c = 0;
            switch (table) {
                case "Reservation":
                    for (String year : years) {
                        value[c] = client.getCountYearlyReportReservation(year);
                        c++;
                    }
                    break;

                case "Registration":
                    for (String year : years) {
                        value[c] = client.getCountYearlyReportRegistration(year);
                        c++;
                    }
                    break;
            }

            for (int i = 0; i < years.length; i++) {
                tableModel.addRow(new Object[]{
                    years[i],
                    value[i]
                });
            }
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(value[0], series1, years[0]);
            dataset.addValue(value[1], series1, years[1]);
            dataset.addValue(value[2], series1, years[2]);
            dataset.addValue(value[3], series1, years[3]);
            dataset.addValue(value[4], series1, years[4]);
            dataset.addValue(value[5], series1, years[5]);
            dataset.addValue(value[6], series1, years[6]);

            chart = ChartFactory.createBarChart(
                    "181 North Place Residences Graph", // chart title
                    "Months of the Year 2014", // domain axis label
                    "Value", // range axis label
                    dataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
            );

            // set the background color for the chart...
            chart.setBackgroundPaint(Color.white);

            // get a reference to the plot for further customisation...
            final CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            // set the range axis to display integers only...
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // disable bar outlines...
            final BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setDrawBarOutline(false);

            // set up gradient paints for series...
            final GradientPaint gp0 = new GradientPaint(
                    0.0f, 0.0f, Color.blue,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp1 = new GradientPaint(
                    0.0f, 0.0f, Color.green,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp2 = new GradientPaint(
                    0.0f, 0.0f, Color.red,
                    0.0f, 0.0f, Color.lightGray
            );
            renderer.setSeriesPaint(0, gp0);
            renderer.setSeriesPaint(1, gp1);
            renderer.setSeriesPaint(2, gp2);

            final org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
            );
            chartPanel = new ChartPanel(chart);
            displaypane.add(chartPanel, BorderLayout.CENTER);
        } catch (RemoteException ex) {
//            Logger.getLogger(ReportGen.class.getName()).log(Level.SEVERE, null, ex);
            new MessageDialog().error(this, ex.getMessage());
        }
    }

    private String getTable() {
        String table = "";
        switch (combo.getSelectedItem().toString()) {
            case "Number of Reservation":
                table = "Reservation";
                break;

            case "Number of Registration":
                table = "Registration";
                break;

            case "Number of Late Residents":
                table = "Logs";
                break;

            case "Number of Late Payers":
                table = "Bill";
                break;
        }
        return table;
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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ReportGen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReportGen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReportGen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReportGen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReportGen(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox combo;
    private javax.swing.JTable dataTable;
    private javax.swing.JComboBox datecombobox;
    private javax.swing.JPanel displaypane;
    private javax.swing.JButton exportcounttableexcel;
    private javax.swing.JButton exportcounttablepdf;
    private javax.swing.JLabel home;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton monthlyradiobutton;
    private javax.swing.JPanel panel1;
    private javax.swing.JComboBox residents;
    private javax.swing.JLabel selectyearlabel;
    private javax.swing.JLabel tablelabel;
    private javax.swing.JLabel tablelabel1;
    private javax.swing.JRadioButton weeklyradiobutton;
    private javax.swing.JRadioButton yearradiobutton;
    // End of variables declaration//GEN-END:variables
}
