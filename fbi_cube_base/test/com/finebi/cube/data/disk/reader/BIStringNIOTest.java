package com.finebi.cube.data.disk.reader;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.disk.writer.BIStringNIOWriter;
import com.finebi.cube.provider.BIStringNIOProvider;
import com.finebi.cube.provider.BIProjectPathProvider;
import com.fr.bi.stable.utils.algorithem.BIRandomUitils;
import com.fr.general.ComparatorUtils;
import junit.framework.TestCase;

import java.io.File;

/**
 * This class created on 2016/4/1.
 *
 * @author Connery
 * @since 4.0
 */
public class BIStringNIOTest extends TestCase {
    BIStringNIOWriter writer;
    BIStringNIOReader reader;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String start = BIProjectPathProvider.projectPath + File.separator + "start";
        String length = BIProjectPathProvider.projectPath + File.separator + "length";
        String content = BIProjectPathProvider.projectPath + File.separator + "content";
        writer = BIStringNIOProvider.BIStringNIOWriterProvider(start, length, content);
        reader = BIStringNIOProvider.BIStringNIOReaderProvider(start, length, content);
    }

    public void testSpeed() {
    }

    public void testBasic() {
        try {
            String one = BIRandomUitils.getRandomCharacterString(1000);
            String two = BIRandomUitils.getRandomCharacterString(1000);
            String three = BIRandomUitils.getRandomCharacterString(1000);
            writer.recordSpecificValue(0, one);
            writer.recordSpecificValue(2, two);
            writer.recordSpecificValue(3, three);
            assertEquals(reader.getSpecificValue(1).length(), 0);
            assertTrue(ComparatorUtils.equals(reader.getSpecificValue(0), one));
            assertTrue(ComparatorUtils.equals(reader.getSpecificValue(2), two));
            assertTrue(ComparatorUtils.equals(reader.getSpecificValue(3), three));

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testWriteNegativeValue() {
        try {
            writer.recordSpecificValue(-1, BIRandomUitils.getRandomCharacterString(1000));
        } catch (Exception e) {
            /**
             * Expect Exception
             */
            return;
        }
        assertTrue(false);
    }

    public void testReadNegativeValue() {
        try {
            reader.getSpecificValue(-1);
        } catch (Exception e) {
            /**
             * Expect Exception
             */
            return;
        }
        assertTrue(false);
    }
}
