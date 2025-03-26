package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDB {
    private int clientId;
    private String userName;
    private String password;
    private String clientEmail;
    private String contactInfo;

    public ClientDB(int clientId, String userName, String password, String clientEmail, String contactInfo) {
        this.clientId = clientId;
        this.userName = userName;
        this.password = password;
        this.clientEmail = clientEmail;
        this.contactInfo = contactInfo;
    }

    // Getters
    public int getClientId() { return clientId; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getClientEmail() { return clientEmail; }
    public String getContactInfo() { return contactInfo; }

    public static ClientDB getClientByCredentials(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Client WHERE user_name = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ClientDB(
                        rs.getInt("client_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getString("client_email"),
                        rs.getString("contact_info")
                );
            }
        }
        return null;
    }

    // CRUD Operations
    public static void createClient(ClientDB client) throws SQLException {
        String sql = "INSERT INTO Client (client_id, user_name, password, client_email, contact_info) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, client.getClientId());
            stmt.setString(2, client.getUserName());
            stmt.setString(3, client.getPassword());
            stmt.setString(4, client.getClientEmail());
            stmt.setString(5, client.getContactInfo());
            stmt.executeUpdate();
        }
    }

    public static ClientDB getClientById(int clientId) throws SQLException {
        String sql = "SELECT * FROM Client WHERE client_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ClientDB(
                        rs.getInt("client_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getString("client_email"),
                        rs.getString("contact_info")
                );
            }
        }
        return null;
    }

    public static List<ClientDB> getAllClients() throws SQLException {
        List<ClientDB> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(new ClientDB(
                        rs.getInt("client_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getString("client_email"),
                        rs.getString("contact_info")
                ));
            }
        }
        return clients;
    }

    public static void updateClient(ClientDB client) throws SQLException {
        String sql = "UPDATE Client SET user_name = ?, password = ?, client_email = ?, contact_info = ? WHERE client_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getUserName());
            stmt.setString(2, client.getPassword());
            stmt.setString(3, client.getClientEmail());
            stmt.setString(4, client.getContactInfo());
            stmt.setInt(5, client.getClientId());
            stmt.executeUpdate();
        }
    }

    public static void deleteClient(int clientId) throws SQLException {
        String sql = "DELETE FROM Client WHERE client_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            stmt.executeUpdate();
        }
    }
}