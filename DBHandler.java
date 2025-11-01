import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {
    
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/doctor_app_db";
    private static final String USER = "root"; 
    private static final String PASS = "2007"; // ⚠️ Ensure this is your correct password!

    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Check lib folder and classpath.");
            e.printStackTrace();
        }
        return conn;
    }

    // INSERT (7 parameters: pName, age, bloodGroup, dName, dobStr, apptDateStr, timeStr)
    public static boolean insertAppointment(String pName, int age, String bloodGroup, String dName, String dateOfBirthStr, String apptDateStr, String timeStr) {
        // SQL: Includes appointment_date and appointment_date_of_birth
        String sql = "INSERT INTO appointments (patient_name, age, blood_group, doctor_name, appointment_date_of_birth, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Date sqlDob = Date.valueOf(dateOfBirthStr);
            Date sqlApptDate = Date.valueOf(apptDateStr);
            Time sqlTime = Time.valueOf(timeStr);

            pstmt.setString(1, pName);
            pstmt.setInt(2, age); 
            pstmt.setString(3, bloodGroup); 
            pstmt.setString(4, dName);
            pstmt.setDate(5, sqlDob); 
            pstmt.setDate(6, sqlApptDate);
            pstmt.setTime(7, sqlTime);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("SQL Error during insertion: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
             System.err.println("Date/Time format error. Dates must be YYYY-MM-DD and Time HH:MM:SS.");
             e.printStackTrace();
             return false;
        }
    }
    
    // Helper method to process a ResultSet row into a String array for the JTable
    private static String[] processAppointmentRow(ResultSet rs) throws SQLException {
        String[] appointment = new String[6];
        appointment[0] = rs.getString("patient_name");
        appointment[1] = String.valueOf(rs.getInt("age"));
        appointment[2] = rs.getString("blood_group");
        appointment[3] = rs.getString("doctor_name");
        appointment[4] = rs.getString("formatted_dob");
        // Combining Appointment Date and Time into the last column (index 5)
        appointment[5] = rs.getDate("appointment_date").toString() + " " + rs.getTime("appointment_time").toString(); 
        return appointment;
    }
    
    // GET ALL APPOINTMENTS
    public static List<String[]> getAllAppointments() {
        List<String[]> appointments = new ArrayList<>();
        String sql = "SELECT patient_name, age, blood_group, doctor_name, " +
                     "CONCAT(LPAD(DAY(appointment_date_of_birth), 2, '0'), '-', " +
                     "UPPER(LEFT(MONTHNAME(appointment_date_of_birth), 3)), '-', " +
                     "YEAR(appointment_date_of_birth)) as formatted_dob, " +
                     "appointment_date, appointment_time FROM appointments " +
                     "ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                appointments.add(processAppointmentRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // NEW: SEARCH APPOINTMENTS BY DATE
    public static List<String[]> searchAppointmentsByDate(String searchDateStr) {
        List<String[]> appointments = new ArrayList<>();
        String sql = "SELECT patient_name, age, blood_group, doctor_name, " +
                     "CONCAT(LPAD(DAY(appointment_date_of_birth), 2, '0'), '-', " +
                     "UPPER(LEFT(MONTHNAME(appointment_date_of_birth), 3)), '-', " +
                     "YEAR(appointment_date_of_birth)) as formatted_dob, " +
                     "appointment_date, appointment_time FROM appointments " +
                     "WHERE appointment_date = ? " + // Filter by appointment date
                     "ORDER BY appointment_time ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Convert String search date to SQL Date
            Date sqlSearchDate = Date.valueOf(searchDateStr);
            pstmt.setDate(1, sqlSearchDate);

            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(processAppointmentRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching appointments: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Search date format error. Date must be YYYY-MM-DD.");
            e.printStackTrace();
        }
        return appointments;
    }
}