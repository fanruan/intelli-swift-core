package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.disk.writer.primitive.BIDoubleNIOWriter;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.io.File;

/**
 * This class created on 2016/4/1.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDoubleNIOReaderTest extends TestCase {
    public void testSpeed() {
        try {
            BIDoubleNIOWriter writer = new BIDoubleNIOWriter(BIByteNIOReaderTest.NIO_PATH_TEST);
            BIDoubleNIOReader reader = new BIDoubleNIOReader(new File(BIByteNIOReaderTest.NIO_PATH_TEST));

            writer.recordSpecificPositionValue(0, Double.valueOf(1));
            long start = System.currentTimeMillis();
            int sum = 0;
            int count = 1000000;
            for (int i = 0; i < count; i++) {
                sum += reader.getSpecificValue(0);
            }
            System.out.println("time:" + (System.currentTimeMillis() - start));
            assertEquals(sum, count);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testBasic() {
        try {
            BIDoubleNIOWriter writer = new BIDoubleNIOWriter(BIByteNIOReaderTest.NIO_PATH_TEST);
            BIDoubleNIOReader reader = new BIDoubleNIOReader(new File(BIByteNIOReaderTest.NIO_PATH_TEST));

            writer.recordSpecificPositionValue(0, Double.valueOf(1));
            writer.recordSpecificPositionValue(2, Double.valueOf(1));
            writer.recordSpecificPositionValue(3, Double.valueOf(1));
            assertEquals(reader.getSpecificValue(0l), Double.valueOf(1));
            assertEquals(reader.getSpecificValue(2l), Double.valueOf(1));
            assertEquals(reader.getSpecificValue(3l), Double.valueOf(1));

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testWriteNegativeValue() {
        try {
            BIDoubleNIOWriter writer = new BIDoubleNIOWriter(BIByteNIOReaderTest.NIO_PATH_TEST);
            writer.recordSpecificPositionValue(-1, Double.valueOf(1));
        } catch (Exception e) {
            return;
        }
        assertTrue(false);
    }

    public void testReadNegativeValue() {
        try {
            BIDoubleNIOReader reader = new BIDoubleNIOReader(new File(BIByteNIOReaderTest.NIO_PATH_TEST));
            reader.getSpecificValue(-1l);
        } catch (Exception e) {
            return;
        }
        assertTrue(false);
    }
}
