package com.clearsolutions.usermanager.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
class LoggerTest {

    static final String LOG_TITLE = "Test log title";
    static final String LOG_MSG = "Test log message";

    private static final String ERROR_MESSAGE = String.format(
            """
                    \u001B[1;91m================================================== %s ==================================================\u001B[1;91m
                    \u001B[0;95m ERROR Message: %s
                    \u001B[1;91m====================================================================================================================\u001B[0m""",
            LOG_TITLE, LOG_MSG);
    private static final String INFO_MSG = String.format(
            """
                    \u001B[1;96m================================================== %s ==================================================\u001B[1;96m
                    \u001B[0;95m INFO Message: %s
                    \u001B[1;96m====================================================================================================================\u001B[0m""",
            LOG_TITLE, LOG_MSG);
    private static final String WARN_MSG = String.format(
            """
                    \u001B[1;93m================================================== %s ==================================================\u001B[1;93m
                    \u001B[0;95m WARNING Message: %s
                    \u001B[1;93m====================================================================================================================\u001B[0m""",
            LOG_TITLE, LOG_MSG);

    @Test
    void testLogErrorMessage(CapturedOutput output) {
        Logger.error(LOG_TITLE, LOG_MSG);
        assertTrue(output.getOut().contains(ERROR_MESSAGE));
    }

    @Test
    void testLogInfoMessage(CapturedOutput output) {
        Logger.info(LOG_TITLE, LOG_MSG);
        assertTrue(output.getOut().contains(INFO_MSG));
    }

    @Test
    void testLogWarningMessage(CapturedOutput output) {
        Logger.warning(LOG_TITLE, LOG_MSG);
        assertTrue(output.getOut().contains(WARN_MSG));
    }
}
