package com.investbank.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingManager {
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    // Additional context-aware logging helpers can be added here
}
