package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDB {
    private int adminId;
    private String userName;
    private String password;
    private String contactInfo;

    public AdminDB(int adminId, String userName, String password, String contactInfo) {
        this.adminId = adminId;
        this.userName = userName;
        this.password = password;
        this.contactInfo = contactInfo;
    }

    // Getters
    public int getAdminId() { return adminId; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getContactInfo() { return contactInfo; }

    public static AdminDB getAdminByCredentials(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Admin WHERE user_name = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AdminDB(
                        rs.getInt("admin_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getString("contact_info")
                );
            }
        }
        return null;
    }

    // CRUD Operations
    public static void createAdmin(AdminDB admin) throws SQLException {
        String sql = "INSERT INTO Admin (admin_id, user_name, password, contact_info) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, admin.getAdminId());
            stmt.setString(2, admin.getUserName());
            stmt.setString(3, admin.getPassword());
            stmt.setString(4, admin.getContactInfo());
            stmt.executeUpdate();
        }
    }

    public static AdminDB getAdminById(int adminId) throws SQLException {
        String sql = "SELECT * FROM Admin WHERE admin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AdminDB(
                        rs.getInt("admin_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getString("contact_info")
                );
            }
        }
        return null;
    }

    public static List<AdminDB> getAllAdmins() throws SQLException {
        List<AdminDB> admins = new ArrayList<>();
        String sql = "SELECT * FROM Admin";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                admins.add(new AdminDB(
                        rs.getInt("admin_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getString("contact_info")
                ));
            }
        }
        return admins;
    }

    public static void updateAdmin(AdminDB admin) throws SQLException {
        String sql = "UPDATE Admin SET user_name = ?, password = ?, contact_info = ? WHERE admin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admin.getUserName());
            stmt.setString(2, admin.getPassword());
            stmt.setString(3, admin.getContactInfo());
            stmt.setInt(4, admin.getAdminId());
            stmt.executeUpdate();
        }
    }

    public static void deleteAdmin(int adminId) throws SQLException {
        String sql = "DELETE FROM Admin WHERE admin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            stmt.executeUpdate();
        }
    }
}