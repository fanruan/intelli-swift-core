package com.finebi.cube.common.log;

import com.finebi.common.log.BIUserLogPool;
import com.finebi.common.log.BIUserLogTest;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;

import java.io.ByteArrayInputStream;

/**
 * This class created on 2016/10/9.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BILoggerTest extends TestCase {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BIUserLogTest.class);

    static {
        String str =
                "log4j.rootLogger=INFO, rootA\n" +
                        "log4j.appender.rootA=org.apache.log4j.varia.NullAppender\n" +
                        "log4j.logger.LogVisible=INFO, ServerDailyRollingFile\n" +
                        "log4j.appender.ServerDailyRollingFile=com.finebi.common.log.BIUserLogAppender\n" +
                        "log4j.appender.ServerDailyRollingFile.layout=org.apache.log4j.PatternLayout\n" +
                        "log4j.appender.ServerDailyRollingFile.layout.ConversionPattern=%d - %m%n\n";

        PropertyConfigurator.configure(new ByteArrayInputStream(str.getBytes()));

    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/10/9
     */
    public void testBILogger() {
        try {
            BILogger logger_1 = BILoggerFactory.getLogger(BILogger.class);
            BILogger logger_2 = BILoggerFactory.getLogger();
            assertTrue(logger_1 == logger_2);

            BILogger logger_3 = BILoggerFactory.getLogger(BILoggerTest.class);
            BILogger logger_4 = BILoggerFactory.getLogger(BILoggerTest.class);
            assertTrue(logger_3 == logger_4);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2017/4/13
     */
    public void testBILoggerError() {
        try {
            BIUserLogPool logPool = BIUserLogPool.getInstance();
            logPool.clear();
            LOGGER.errorSticky("ABC");
            assertEquals(logPool.getLogEventPool().size(), 1);
            LoggingEvent event = logPool.getLogEventPool().get(0).getEvent();
            assertEquals(event.getMessage(), "ABC");
            assertEquals(event.getLevel(), Level.INFO);


            Exception e = new Exception("Exc");
            LOGGER.errorSticky("ABC2", e);
            assertEquals(logPool.getLogEventPool().size(), 2);
            LoggingEvent event2 = logPool.getLogEventPool().get(1).getEvent();
            assertEquals(event2.getMessage(), "ABC2");
            assertEquals(event2.getLevel(), Level.INFO);
            assertEquals(event2.getThrowableInformation().getThrowable().getMessage(), "Exc");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2017/4/13
     */
    public void testBILoggerInfo() {
        try {
            BIUserLogPool logPool = BIUserLogPool.getInstance();
            logPool.clear();
            LOGGER.infoSticky("ABC");
            assertEquals(logPool.getLogEventPool().size(), 1);
            LoggingEvent event = logPool.getLogEventPool().get(0).getEvent();
            assertEquals(event.getMessage(), "ABC");
            assertEquals(event.getLevel(), Level.INFO);
            Object[] list = new Object[2];
            list[0] = BIUserLogPool.getInstance();
            list[1] = BIUserLogPool.getInstance();

            Exception e = new Exception("Exc");
            LOGGER.infoSticky("ABC2", e);
            assertEquals(logPool.getLogEventPool().size(), 2);
            LoggingEvent event2 = logPool.getLogEventPool().get(1).getEvent();
            assertEquals(event2.getMessage(), "ABC2");
            assertEquals(event2.getLevel(), Level.INFO);
            assertEquals(event2.getThrowableInformation().getThrowable().getMessage(), "Exc");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2017/4/13
     */
    public void testBILoggerWarn() {
        try {
            BIUserLogPool logPool = BIUserLogPool.getInstance();
            logPool.clear();
            mainTask();
            assertEquals(logPool.getLogEventPool().size(), 1);
            LoggingEvent event = logPool.getLogEventPool().get(0).getEvent();
            assertEquals(event.getMessage(), "ABC");
            assertEquals(event.getLevel(), Level.INFO);


            Exception e = new Exception("Exc");
            LOGGER.warnSticky("ABC2", e);
            assertEquals(logPool.getLogEventPool().size(), 2);
            LoggingEvent event2 = logPool.getLogEventPool().get(1).getEvent();
            assertEquals(event2.getMessage(), "ABC2");
            assertEquals(event2.getLevel(), Level.INFO);
            assertEquals(event2.getThrowableInformation().getThrowable().getMessage(), "Exc");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }

    private void mainTask() {
        LOGGER.warnSticky("ABC");
    }
}
