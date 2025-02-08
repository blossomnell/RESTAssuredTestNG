package Utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerLoad {
    private static final Logger logger = LogManager.getLogger(LoggerLoad.class);

    static {
        try {
            // Configure Log4j 2 with log4j2.xml (not .properties)
            Configurator.initialize(null, "src/test/resources/log4j2.xml");
            logger.info("Log4j2 Initialized Successfully from: src/test/resources/log4j2.xml");
        } catch (Exception e) {
            System.err.println("Error initializing Log4j2: " + e.getMessage());
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }
}
