import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Labwork3 extends JFrame implements ActionListener, KeyListener {
    
    // Colors
    private Color yellow = new Color(255, 193, 7);
    private Color darkYellow = new Color(255, 160, 0);
    private Color lightYellow = new Color(255, 248, 225);
    private Color green = new Color(40, 167, 69);
    private Color red = new Color(220, 53, 69);
    
    // Input fields
    private JTextField txtAttendance, txtLab1, txtLab2, txtLab3;
    
    // Output labels
    private JLabel lblLabAvg, lblClassStanding, lblPassScore, lblExcellentScore;
    
    // Remarks
    private JTextArea txtRemarks;
    
    // Buttons
    private JButton btnCalculate, btnClear;
    
    public Labwork3() {
        setTitle("Prelim Exam Score Calculator");
        setSize(400, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        
        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(yellow);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("PRELIM EXAM SCORE CALCULATOR");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Input fields
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Attendance (0-100):"), gbc);
        gbc.gridx = 1;
        txtAttendance = new JTextField(10);
        txtAttendance.addKeyListener(this);
        mainPanel.add(txtAttendance, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Lab Work 1:"), gbc);
        gbc.gridx = 1;
        txtLab1 = new JTextField(10);
        txtLab1.addKeyListener(this);
        mainPanel.add(txtLab1, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Lab Work 2:"), gbc);
        gbc.gridx = 1;
        txtLab2 = new JTextField(10);
        txtLab2.addKeyListener(this);
        mainPanel.add(txtLab2, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Lab Work 3:"), gbc);
        gbc.gridx = 1;
        txtLab3 = new JTextField(10);
        txtLab3.addKeyListener(this);
        mainPanel.add(txtLab3, gbc);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnCalculate = new JButton("Calculate");
        btnCalculate.setBackground(yellow);
        btnCalculate.addActionListener(this);
        btnClear = new JButton("Clear");
        btnClear.addActionListener(this);
        btnPanel.add(btnCalculate);
        btnPanel.add(btnClear);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        mainPanel.add(btnPanel, gbc);
        
        // Results section
        JPanel resultsPanel = new JPanel(new GridBagLayout());
        resultsPanel.setBackground(lightYellow);
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.insets = new Insets(3, 5, 3, 5);
        
        gbc2.gridx = 0; gbc2.gridy = 0;
        resultsPanel.add(new JLabel("Lab Work Average:"), gbc2);
        gbc2.gridx = 1;
        lblLabAvg = new JLabel("---");
        lblLabAvg.setFont(new Font("Arial", Font.BOLD, 12));
        resultsPanel.add(lblLabAvg, gbc2);
        
        gbc2.gridx = 0; gbc2.gridy = 1;
        resultsPanel.add(new JLabel("Class Standing:"), gbc2);
        gbc2.gridx = 1;
        lblClassStanding = new JLabel("---");
        lblClassStanding.setFont(new Font("Arial", Font.BOLD, 12));
        resultsPanel.add(lblClassStanding, gbc2);
        
        gbc2.gridx = 0; gbc2.gridy = 2;
        resultsPanel.add(new JLabel("Required to PASS (75):"), gbc2);
        gbc2.gridx = 1;
        lblPassScore = new JLabel("---");
        lblPassScore.setFont(new Font("Arial", Font.BOLD, 12));
        resultsPanel.add(lblPassScore, gbc2);
        
        gbc2.gridx = 0; gbc2.gridy = 3;
        resultsPanel.add(new JLabel("Required for EXCELLENT (100):"), gbc2);
        gbc2.gridx = 1;
        lblExcellentScore = new JLabel("---");
        lblExcellentScore.setFont(new Font("Arial", Font.BOLD, 12));
        resultsPanel.add(lblExcellentScore, gbc2);
        
        gbc.gridy = 5; gbc.gridwidth = 2;
        mainPanel.add(resultsPanel, gbc);
        
        // Remarks
        JPanel remarksPanel = new JPanel(new BorderLayout());
        remarksPanel.setBorder(BorderFactory.createTitledBorder("Remarks"));
        txtRemarks = new JTextArea(3, 20);
        txtRemarks.setEditable(false);
        txtRemarks.setLineWrap(true);
        txtRemarks.setWrapStyleWord(true);
        txtRemarks.setText("Enter grades and press Calculate.");
        remarksPanel.add(txtRemarks, BorderLayout.CENTER);
        gbc.gridy = 6;
        mainPanel.add(remarksPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCalculate) {
            calculate();
        } else if (e.getSource() == btnClear) {
            clear();
        }
    }
    
    private void calculate() {
        try {
            double attendance = Double.parseDouble(txtAttendance.getText().trim());
            double lab1 = Double.parseDouble(txtLab1.getText().trim());
            double lab2 = Double.parseDouble(txtLab2.getText().trim());
            double lab3 = Double.parseDouble(txtLab3.getText().trim());
            
            // Validate
            if (attendance < 0 || attendance > 100 || lab1 < 0 || lab1 > 100 || 
                lab2 < 0 || lab2 > 100 || lab3 < 0 || lab3 > 100) {
                JOptionPane.showMessageDialog(this, "All values must be between 0 and 100.");
                return;
            }
            
            // Calculate
            double labAvg = (lab1 + lab2 + lab3) / 3;
            double classStanding = (attendance * 0.40) + (labAvg * 0.60);
            double requiredPass = (75 - (classStanding * 0.70)) / 0.30;
            double requiredExcellent = (100 - (classStanding * 0.70)) / 0.30;
            
            // Display results
            lblLabAvg.setText(String.format("%.2f", labAvg));
            lblClassStanding.setText(String.format("%.2f", classStanding));
            
            StringBuilder remarks = new StringBuilder();
            
            // Pass score
            if (requiredPass <= 0) {
                lblPassScore.setText("0 (Passed!)");
                lblPassScore.setForeground(green);
                remarks.append("You will pass even with 0 on the exam!\n");
            } else if (requiredPass > 100) {
                lblPassScore.setText(String.format("%.2f (Impossible)", requiredPass));
                lblPassScore.setForeground(red);
                remarks.append("Passing is not possible.\n");
            } else {
                lblPassScore.setText(String.format("%.2f", requiredPass));
                lblPassScore.setForeground(Color.BLACK);
                remarks.append(String.format("You need %.2f to pass.\n", requiredPass));
            }
            
            // Excellent score
            if (requiredExcellent <= 0) {
                lblExcellentScore.setText("0 (Guaranteed!)");
                lblExcellentScore.setForeground(green);
                remarks.append("Excellent grade guaranteed!");
            } else if (requiredExcellent > 100) {
                lblExcellentScore.setText(String.format("%.2f (Impossible)", requiredExcellent));
                lblExcellentScore.setForeground(red);
                remarks.append("Excellent grade not achievable.");
            } else {
                lblExcellentScore.setText(String.format("%.2f", requiredExcellent));
                lblExcellentScore.setForeground(Color.BLACK);
                remarks.append(String.format("You need %.2f for excellent.", requiredExcellent));
            }
            
            txtRemarks.setText(remarks.toString());
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        }
    }
    
    private void clear() {
        txtAttendance.setText("");
        txtLab1.setText("");
        txtLab2.setText("");
        txtLab3.setText("");
        lblLabAvg.setText("---");
        lblClassStanding.setText("---");
        lblPassScore.setText("---");
        lblPassScore.setForeground(Color.BLACK);
        lblExcellentScore.setText("---");
        lblExcellentScore.setForeground(Color.BLACK);
        txtRemarks.setText("Enter grades and press Calculate.");
        txtAttendance.requestFocus();
    }
    
    // Enter key navigation
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (e.getSource() == txtAttendance) {
                txtLab1.requestFocus();
            } else if (e.getSource() == txtLab1) {
                txtLab2.requestFocus();
            } else if (e.getSource() == txtLab2) {
                txtLab3.requestFocus();
            } else if (e.getSource() == txtLab3) {
                calculate();
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        new Labwork3();
    }
}
