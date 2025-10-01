package main.log;


import java.util.HashMap;
import java.util.Map;

public class BruteforceTest {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_MS = 2 * 60 * 1000; // 2 minutes
    private static final Map<String, Integer> attempts = new HashMap<>();
    private static final Map<String, Long> lockAt = new HashMap<>();

    private static boolean isBlocked(String user) {
        Long t = lockAt.get(user);
        if (t == null) return false;
        if (System.currentTimeMillis() - t > BLOCK_MS) {
            lockAt.remove(user);
            attempts.put(user, 0);
            return false;
        }
        return true;
    }

    private static void fail(String user) {
        if (isBlocked(user)) {
            System.out.println(user + " is BLOCKED");
            AuditDao.log(user, "BRUTEFORCE_BLOCK", "127.0.0.1");
            return;
        }
        int c = attempts.getOrDefault(user, 0) + 1;
        attempts.put(user, c);
        System.out.println("fail #" + c + " for " + user);
        AuditDao.log(user, "LOGIN_FAIL", "127.0.0.1");
        if (c >= MAX_ATTEMPTS) {
            lockAt.put(user, System.currentTimeMillis());
            System.out.println(user + " now blocked");
            AuditDao.log(user, "BRUTEFORCE_BLOCK", "127.0.0.1");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String u = "charlie";
        for (int i = 1; i <= 6; i++) {
            fail(u);
            Thread.sleep(200); // petit délai entre tentatives
        }
        System.out.println("BruteforceTest finished - check logs and DB for BRUTEFORCE_BLOCK");
    }
}
