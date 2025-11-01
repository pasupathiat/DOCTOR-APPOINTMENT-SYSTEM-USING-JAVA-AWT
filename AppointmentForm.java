import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.sql.Date; 
import java.text.SimpleDateFormat;
import java.util.regex.Pattern; 

public class AppointmentForm extends JFrame implements ActionListener {
    // Declared variables
    JTextField pNameField, ageField, searchDateField; 
    JButton bookButton, searchButton, viewAllButton; 
    JComboBox<String> doctorComboBox, timeComboBox, bloodGroupComboBox;
    
    // DOB ComboBoxes
    JComboBox<Integer> dayComboBox, yearComboBox;
    JComboBox<String> monthComboBox;

    // APPOINTMENT DATE ComboBoxes
    JComboBox<Integer> apptDayComboBox, apptYearComboBox;
    JComboBox<String> apptMonthComboBox;

    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    
    // Constants
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 18); 
    private static final Dimension FIELD_SIZE = new Dimension(200, 30); 
    private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private static final String[] DOCTORS = {"Dr.PASUPATHI A T (Cardiology)", "Dr. Jones (Dermatology)", "Dr. Khan (Pediatrics)", "Dr. Lee (General)", "Dr. Patel (Orthopedics)"};
    private static final String[] DAY_TIMES = {"09:00:00", "10:30:00", "13:00:00", "14:30:00", "16:00:00"};
    private static final String[] BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public AppointmentForm() {
        super("Royal Hospital - Appointment Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setLayout(new BorderLayout(10, 10)); 
        
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        
        // --- Title and Info Panel ---
        JLabel titleLabel = new JLabel("Royal Hospital", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(30, 144, 255)); 
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0)); 
        mainContentPanel.add(titlePanel);
    
        JTextArea infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setFont(new Font("SansSerif", Font.PLAIN, 25));
        infoText.setBackground(getBackground());
        String info = "Royal Hospital is a leading multi-specialty healthcare provider dedicated to quality patient care,Since 1979";
        infoText.setText(info);
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 20, 100));
        infoPanel.add(infoText, BorderLayout.CENTER);
        mainContentPanel.add(infoPanel);
        mainContentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
      
        // --- Input Panel Setup ---
        JPanel inputContainer = new JPanel(new GridBagLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Initialize components
        pNameField = createTextField();
        ageField = createTextField(); 
        bloodGroupComboBox = createComboBox(BLOOD_GROUPS);
        doctorComboBox = createComboBox(DOCTORS);
        timeComboBox = createComboBox(DAY_TIMES);
        
        // DOB ComboBoxes initialization
        dayComboBox = createDateComboBox(IntStream.rangeClosed(1, 31).boxed().toArray(Integer[]::new));
        monthComboBox = createMonthComboBox(MONTHS); // *** CORRECTED METHOD ***
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = IntStream.rangeClosed(currentYear - 100, currentYear).boxed().toArray(Integer[]::new);
        yearComboBox = createDateComboBox(years);
        yearComboBox.setSelectedItem(currentYear - 25); 

        // APPOINTMENT DATE ComboBoxes initialization 
        Integer[] apptYears = IntStream.rangeClosed(currentYear, currentYear + 1).boxed().toArray(Integer[]::new);
        apptDayComboBox = createDateComboBox(IntStream.rangeClosed(1, 31).boxed().toArray(Integer[]::new));
        apptMonthComboBox = createMonthComboBox(MONTHS); // *** CORRECTED METHOD ***
        apptYearComboBox = createDateComboBox(apptYears);
        apptYearComboBox.setSelectedItem(currentYear); 
        
        int row = 0;
        row = addFieldRow(inputPanel, gbc, row, "Patient Name:", pNameField);
        row = addFieldRow(inputPanel, gbc, row, "Age:", ageField);
        row = addFieldRow(inputPanel, gbc, row, "Blood Group:", bloodGroupComboBox);

        // Date of Birth Row (Existing)
        row = addDateRow(inputPanel, gbc, row, "Date of Birth:", dayComboBox, monthComboBox, yearComboBox);
        
        // Appointment Date Row (NEW)
        row = addDateRow(inputPanel, gbc, row, "Appointment Date:", apptDayComboBox, apptMonthComboBox, apptYearComboBox);
        
        row = addFieldRow(inputPanel, gbc, row, "Doctor Name:", doctorComboBox);
        row = addFieldRow(inputPanel, gbc, row, "Time Slot:", timeComboBox);
        
        inputContainer.add(inputPanel); 
        mainContentPanel.add(inputContainer);

        // --- Booking Button Panel ---
        bookButton = new JButton("Book Appointment");
        bookButton.setFont(new Font("SansSerif", Font.BOLD, 18)); 
        bookButton.setBackground(new Color(30, 144, 255)); 
        bookButton.setForeground(Color.GREEN); 
        bookButton.addActionListener(this); 
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(bookButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainContentPanel.add(buttonPanel); 
        
        // --- Table Setup ---
        String[] columnNames = {"Patient Name", "Age", "Blood Group", "Doctor Name", "Date of Birth", "Appointment Date & Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        
        appointmentsTable = new JTable(tableModel);
        appointmentsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        appointmentsTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setPreferredSize(new Dimension(900, 200));
        
        // --- Table Title ---
        JLabel tableTitle = new JLabel("Appointments", JLabel.CENTER);
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainContentPanel.add(tableTitle);

        // --- Search Panel (Relocated and Fixed) ---
        searchDateField = new JTextField(10);
        searchDateField.setFont(FIELD_FONT);
        searchDateField.setText(DATE_FORMAT.format(new java.util.Date())); // Default to today
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchButton.addActionListener(this); 
        
        viewAllButton = new JButton("View All Appointments"); 
        viewAllButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        viewAllButton.addActionListener(this); 
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        // Fix for the JLabel constructor error
        JLabel searchLabel = new JLabel("Date (YYYY-MM-DD):", JLabel.RIGHT); 
        searchLabel.setFont(LABEL_FONT); 
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchDateField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton); // Add the new button
        
        mainContentPanel.add(searchPanel);
        
        // --- Table Panel ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(tablePanel);
        
        add(mainContentPanel, BorderLayout.CENTER);
        loadAppointments(); 
        setLocationRelativeTo(null); 
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(FIELD_SIZE);
        field.setFont(FIELD_FONT);
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setPreferredSize(FIELD_SIZE);
        box.setFont(FIELD_FONT);
        return box;
    }

    // Helper method for integer date components (Day/Year)
    private JComboBox<Integer> createDateComboBox(Integer[] items) {
        JComboBox<Integer> comboBox = new JComboBox<>(items);
        comboBox.setFont(FIELD_FONT);
        // Set smaller preferred size for Day/Year
        comboBox.setPreferredSize(new Dimension(70, 30)); 
        return comboBox;
    }
    
    // NEW Helper method for month components (String)
    private JComboBox<String> createMonthComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(FIELD_FONT);
        // Set a reasonable width for the month name
        comboBox.setPreferredSize(new Dimension(80, 30)); 
        return comboBox;
    }
    
    // Helper method for adding the date row
    private int addDateRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComboBox<Integer> day, JComboBox<String> month, JComboBox<Integer> year) {
        JLabel label = new JLabel(labelText, JLabel.RIGHT);
        label.setFont(LABEL_FONT);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(label, gbc);
        
        // Use FlowLayout to put date components next to each other
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(day);
        datePanel.add(month);
        datePanel.add(year);
        datePanel.setMaximumSize(FIELD_SIZE); 
        
        gbc.gridx = 1; 
        panel.add(datePanel, gbc);
        
        return row + 1;
    }
    
    private int addFieldRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel label = new JLabel(labelText, JLabel.RIGHT);
        label.setFont(LABEL_FONT);
        panel.add(label, gbc);
        
        gbc.gridx = 1; 
        panel.add(component, gbc);
        
        return row + 1;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bookButton) {
            handleBookingAction();
        } else if (e.getSource() == searchButton) {
            handleSearchAction();
        } else if (e.getActionCommand().equals("View All Appointments")) {
            loadAppointments(null); // Passing null reloads all appointments
            searchDateField.setText(DATE_FORMAT.format(new java.util.Date())); // Reset search field to today
        }
    }
    
    private void handleBookingAction() {
        String pName = pNameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String bloodGroup = (String) bloodGroupComboBox.getSelectedItem();
        String dName = (String) doctorComboBox.getSelectedItem();
        String timeStr = (String) timeComboBox.getSelectedItem();
        
        // DOB Processing
        int dobDay = (Integer) dayComboBox.getSelectedItem();
        int dobMonth = monthComboBox.getSelectedIndex() + 1; 
        int dobYear = (Integer) yearComboBox.getSelectedItem();
        String dobStr = String.format("%04d-%02d-%02d", dobYear, dobMonth, dobDay);

        // APPOINTMENT DATE Processing
        int apptDay = (Integer) apptDayComboBox.getSelectedItem();
        int apptMonth = apptMonthComboBox.getSelectedIndex() + 1; 
        int apptYear = (Integer) apptYearComboBox.getSelectedItem();
        String apptDateStr = String.format("%04d-%02d-%02d", apptYear, apptMonth, apptDay);
        
        if (pName.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Patient Name and Age.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Date.valueOf(apptDateStr); 
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Appointment Date selected.", "Date Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = DBHandler.insertAppointment(pName, age, bloodGroup, dName, dobStr, apptDateStr, timeStr);

        if (success) {
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            pNameField.setText("");
            ageField.setText("");
            loadAppointments(null); 
        } else {
            JOptionPane.showMessageDialog(this, "Failed to book appointment. Check the VS Code TERMINAL for errors.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleSearchAction() {
        String searchDate = searchDateField.getText().trim();
        
        if (searchDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date in YYYY-MM-DD format.", "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Simple regex to check YYYY-MM-DD format
        if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", searchDate)) {
             JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Search Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        loadAppointments(searchDate);
    }


    private void loadAppointments() {
        loadAppointments(null);
    }

    private void loadAppointments(String searchDate) {
        try {
            tableModel.setRowCount(0);
            List<String[]> appointments;
            
            if (searchDate == null || searchDate.isEmpty()) {
                appointments = DBHandler.getAllAppointments();
            } else {
                appointments = DBHandler.searchAppointmentsByDate(searchDate);
            }
            
            if (appointments.isEmpty()) {
                String msg = (searchDate == null || searchDate.isEmpty()) ? "No appointments found." : "No appointments found for date: " + searchDate;
                String[] noData = {msg, "", "", "", "", ""};
                tableModel.addRow(noData);
            } else {
                for (String[] appointment : appointments) {
                    tableModel.addRow(appointment);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading appointments: " + e.getMessage());
            e.printStackTrace();
            String[] error = {"Error loading appointments", "", "", "", "", ""};
            tableModel.addRow(error);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AppointmentForm frame = new AppointmentForm();
                frame.setVisible(true); 
            }
        });
    }
}