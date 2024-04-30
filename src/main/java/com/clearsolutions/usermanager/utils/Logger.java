package com.clearsolutions.usermanager.utils;

import com.clearsolutions.usermanager.utils.enums.LogLevel;
import lombok.extern.slf4j.Slf4j;

import static com.clearsolutions.usermanager.utils.enums.LogLevel.*;

/**
 * Utility class for logging messages with different log levels.
 */
@Slf4j
public class Logger {

    private static final int LENGTH_OF_HALF_LINE = 50;

    /**
     * Logs an error message with the specified title and message.
     *
     * @param title   The title of the log entry.
     * @param message The message to be logged.
     */
    public static void error(final String title, final String message) {
        logMessage(ERROR, title, message);
    }

    /**
     * Logs a warning message with the specified title and message.
     *
     * @param title   The title of the log entry.
     * @param message The message to be logged.
     */
    public static void warning(final String title, final String message) {
        logMessage(WARNING, title, message);
    }

    /**
     * Logs an info message with the specified title and message.
     *
     * @param title   The title of the log entry.
     * @param message The message to be logged.
     */
    public static void info(final String title, final String message) {
        logMessage(INFO, title, message);
    }

    /**
     * Creates half of the top line for log formatting.
     *
     * @return A string representing half of the top line.
     */
    private static String createHalfOfTopLine() {
        return "=".repeat(LENGTH_OF_HALF_LINE);
    }

    /**
     * Creates the bottom line for log formatting based on the title length.
     *
     * @param title The title of the log entry.
     * @return A string representing the bottom line.
     */
    private static String createBottomLine(String title) {
        return "=".repeat(title.length() + 2) + createHalfOfTopLine().repeat(2);
    }

    /**
     * Formats and logs the message with appropriate formatting based on log level.
     *
     * @param logLevel The log level identifier.
     * @param title    The title of the log entry.
     * @param message  The message to be logged.
     */
    private static void logMessage(LogLevel logLevel, final String title, final String message) {
        String equalsLine = createHalfOfTopLine();
        String bottomLine = createBottomLine(title);
        String colorCode = logLevel.getColorCode();

        String formattedMessage = String.format(
                "\n%s%s %s %s%s\n\033[0;95m %s Message: %s\n%s%s\033[0m",
                colorCode, equalsLine, title, equalsLine, colorCode, logLevel, message, colorCode, bottomLine);

        switch (logLevel) {
            case ERROR -> log.error(formattedMessage);
            case WARNING -> log.warn(formattedMessage);
            case INFO -> log.info(formattedMessage);
            default -> log.debug(formattedMessage);
        }
    }
}
