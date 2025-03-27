package Database;

import java.sql.*;

public class UsersDB {
    private int userId;
    private String userName;
    private String password;
    private String userType;

    // Constructor
    public UsersDB(int userId, String userName, String password, String userType) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.userType = userType;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }

    // Essential methods only
    public static void createUser(UsersDB user) throws SQLException {
        String sql = "INSERT INTO Users (user_id, user_name, password, user_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getUserId());
            stmt.setString(2, user.getUserName());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getUserType());
            stmt.executeUpdate();
        }
    }

    public static UsersDB getUserByCredentials(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_name = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UsersDB(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getString("user_type")
                );
            }
        }
        return null;
    }

    public static boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE user_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}