package main.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {

    private static final Logger logger = LoggerFactory.getLogger("com.example.authapp");

    public static void main(String[] args) {
        logger.info("login.attempt username=alice ip=127.0.0.1");
        logger.info("login.fail username=alice ip=127.0.0.1");
        logger.info("login.attempt username=bob ip=127.0.0.1");
        logger.info("login.success username=bob ip=127.0.0.1");
        System.out.println("LogTest done - check logs/app.log");
    }
}
