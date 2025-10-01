package main.log;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuditDaoTest {
    // Version sans MySQL : on écrit dans logs/audit.log + console
    public static void log(String user, String action, String ip) {
        String line = LocalDateTime.now() + " " + user + " " + action + " from " + ip;
        System.out.println("[AUDIT] " + line);

        // On peut aussi écrire dans un fichier pour preuve
        try (FileWriter fw = new FileWriter("logs/audit.log", true)) {
            fw.write(line + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
