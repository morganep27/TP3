package main.log;

import java.io.Console;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BruteforceTest - mode interactif
 *
 * - A chaque tentative, l'utilisateur entre un mot de passe au prompt.
 * - Mot de passe correct = "dion" => LOGIN_SUCCESS (compteurs remis à zéro).
 * - Mot de passe incorrect => LOGIN_FAIL ; blocage après MAX_ATTEMPTS.
 *
 * Exécution recommandée depuis un terminal (Console disponible) :
 *   mvn -q compile exec:java -Dexec.mainClass="main.log.BruteforceTest"
 *
 * Dans un IDE, System.console() est souvent null ; le code utilisera Scanner.
 */

public class BruteforceTest {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_MS = 2 * 60 * 1000; // 2 minutes

    private static final Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
    private static final Map<String, Long> lockAt = new ConcurrentHashMap<>();

    private static boolean isBlocked(String user) {
        Long t = lockAt.get(user);
        if (t == null) return false;
        long elapsed = System.currentTimeMillis() - t;
        if (elapsed > BLOCK_MS) {
            lockAt.remove(user);
            attempts.remove(user);
            return false;
        }
        return true;
    }

    private static void fail(String user, String ip) {
        AtomicInteger c = attempts.computeIfAbsent(user, k -> new AtomicInteger(0));
        int newCount = c.incrementAndGet();
        System.out.println("Échec #" + newCount + " pour " + user);
        AuditDao.log(user, "LOGIN_FAIL", ip);

        if (newCount >= MAX_ATTEMPTS) {
            lockAt.put(user, System.currentTimeMillis());
            System.out.println(user + " est maintenant BLOQUÉ pour " + (BLOCK_MS / 1000) + "s");
            AuditDao.log(user, "BRUTEFORCE_BLOCK", ip);
        }
    }

    private static boolean attemptLogin(String user, String ip, String password,char pwd) {
        if (isBlocked(user)) {
            System.out.println(user + " est actuellement BLOQUÉ (tentative rejetée)");
            AuditDao.log(user, "LOGIN_FAIL_WHILE_BLOCKED", ip);
            return false;
        }
        if (Character.toString(pwd).equals(password)) {
            attempts.remove(user);
            lockAt.remove(user);
            System.out.println("SUCCESS : " + user + " s'est connecté avec le bon mot de passe.");
            AuditDao.log(user, "LOGIN_SUCCESS", ip);
            return true;
        } else {
            fail(user, ip);
            return false;
        }
    }

    public static void main(String[] args) {
        String user = "superman";
        String ip = "127.0.0.1";

        Console console = System.console();
        Scanner scanner = null;
        if (console == null) {
            // Console absent (probablement dans un IDE). On utilisera Scanner.
            scanner = new Scanner(System.in);
            System.out.println("Note : Console non disponible — saisie visible (IDE).");
        } else {
            System.out.println("Mode terminal détecté — saisie du mot de passe masquée.");
        }

        System.out.println("BruteforceTest interactif démarré pour user='" + user + "' ip='" + ip + "'.");
        System.out.println("Tape 'exit' pour quitter. Mot de passe correct = 'dion'.");

        int attemptNumber = 0;
        while (true) {
            char[] pwd = console.readPassword("Entrez votre mot de passe : ");
            String pwdString =Character.toString(pwd)
            attemptNumber++;
            System.out.println("\n--- Tentative n°" + attemptNumber + " ---");

            // Lire le mot de passe de l'utilisateur (masqué si possible)
            String password;
            if (console != null) {
                char[] pwdChars = console.readPassword("Entrez le mot de passe : ");
                System.out.println(pwdChars);
                if (pwdChars == null) {
                    System.out.println("Aucune saisie reçue (CTRL+D ?) — arrêt.");
                    break;
                }
                password = new String(pwdChars).trim();
            } else {
                System.out.print("Entrez le mot de passe : ");
                password = scanner.nextLine().trim();
            }

            if ("exit".equalsIgnoreCase(password)) {
                System.out.println("Sortie demandée — arrêt du simulateur.");
                break;
            }

            boolean ok = attemptLogin(user, ip, password,pwd);
            if (ok) {
                System.out.println(user + " : connexion réussie -> compteur réinitialisé.");
                // Si tu préfères arrêter après un succès, décommente la ligne suivante :
                // break;
            } else {
                if (isBlocked(user)) {
                    System.out.println(user + " est BLOQUÉ (vérifier BRUTEFORCE_BLOCK en DB).");
                } else {
                    System.out.println(user + " n'est pas bloqué (continuer les essais).");
                }
            }
        }

        if (scanner != null) {
            scanner.close();
        }
        System.out.println("BruteforceTest terminé - vérifier logs et table d'audit.");
    }
}
