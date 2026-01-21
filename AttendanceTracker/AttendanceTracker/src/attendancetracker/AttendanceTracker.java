// AttendanceTracker.java
// A simple Java Swing application for attendance tracking.
// Requirements: JFrame window, labeled fields, system time, auto-generated E-Signature.

import javax.swing.*;          // Import Swing components
import java.awt.*;             // Import layout managers
import java.time.LocalDateTime; // For system date/time
import java.time.format.DateTimeFormatter; // For formatting date/time
import java.util.UUID;          // For generating unique E-Signature

public class AttendanceTracker {

    public static void main(String[] args) {
        // Create the main JFrame window
        JFrame frame = new JFrame("Attendance Tracker");
        frame.setSize(500, 350); // Increased size for better visibility
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use a JPanel with GridLayout for neat alignment (5 rows for submit button)
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create labels
        JLabel nameLabel = new JLabel("Attendance Name:");
        JLabel courseLabel = new JLabel("Course/Year:");
        JLabel timeInLabel = new JLabel("Time In:");
        JLabel signatureLabel = new JLabel("E-Signature:");

        // Create text fields with increased column width for visibility
        JTextField nameField = new JTextField(20);
        JTextField courseField = new JTextField(20);
        JTextField timeInField = new JTextField(20);
        JTextField signatureField = new JTextField(20);

        // Set Time In field with formatted current system date/time
        // Format: "January 06, 2026 - 10:15:34 PM"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm:ss a");
        String timeIn = LocalDateTime.now().format(formatter);
        timeInField.setText(timeIn);
        timeInField.setEditable(false); // Prevent editing

        // Generate E-Signature using UUID
        String eSignature = UUID.randomUUID().toString();
        signatureField.setText(eSignature);
        signatureField.setEditable(false); // Prevent editing

        // Create Submit button
        JButton submitButton = new JButton("Submit Attendance");
        
        // Add action listener for Submit button
        submitButton.addActionListener(e -> {
            // Get the entered values
            String name = nameField.getText().trim();
            String course = courseField.getText().trim();
            
            // Validate that name and course are not empty
            if (name.isEmpty() || course.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "Please fill in all fields!", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                // Show confirmation message
                JOptionPane.showMessageDialog(frame, 
                    "Attendance Submitted Successfully!\n\n" +
                    "Name: " + name + "\n" +
                    "Course/Year: " + course + "\n" +
                    "Time In: " + timeIn + "\n" +
                    "E-Signature: " + eSignature,
                    "Submission Confirmed", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Add components to panel
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(courseLabel);
        panel.add(courseField);
        panel.add(timeInLabel);
        panel.add(timeInField);
        panel.add(signatureLabel);
        panel.add(signatureField);
        panel.add(new JLabel("")); // Empty label for spacing
        panel.add(submitButton);   // Add submit button

        // Add panel to frame
        frame.add(panel);

        // Make the window visible
        frame.setVisible(true);
    }
}