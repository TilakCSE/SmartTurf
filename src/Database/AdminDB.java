package Database;

import java.sql.*;

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

    // Getters (keep these as they might be used for admin session)
    public int getAdminId() { return adminId; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getContactInfo() { return contactInfo; }

    // Only keep essential authentication method
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
}