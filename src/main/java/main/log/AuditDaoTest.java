package main.log;

public class AuditDaoTest {
    public static void main(String[] args) {
        AuditDao.log("alice", "LOGIN_FAIL", "127.0.0.1");
        AuditDao.log("alice", "LOGIN_FAIL", "127.0.0.1");
        AuditDao.log("bob", "LOGIN_SUCCESS", "127.0.0.1");
        System.out.println("AuditDaoTest done - check t_auth_log in DBeaver");
    }
}
