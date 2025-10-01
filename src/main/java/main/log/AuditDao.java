package main.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditDao {

    public static void log(String username, String eventType, String ip) {
        String sql = "INSERT INTO t_auth_log (email,role,mot_de_passe) VALUES (?, ?, ?)";
        try (Connection conn = DbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, eventType);
            ps.setString(3, ip);
            ps.executeUpdate();
            System.out.println("Audit inserted: " + eventType + " for " + username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
