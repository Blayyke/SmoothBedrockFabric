package me.xa5.smoothbedrock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SBLogger {
    private Logger logger = LogManager.getLogger("SmoothBedrock");

    public void warn(String message) {
        logger.warn(prefix(message));
    }

    public void info(String message) {
        logger.info(prefix(message));
    }

    private String prefix(String message) {
        return "[" + logger.getName() + "] " + message;
    }
}