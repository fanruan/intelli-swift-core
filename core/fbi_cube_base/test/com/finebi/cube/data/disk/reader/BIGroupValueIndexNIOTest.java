package com.finebi.cube.data.disk.reader;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.disk.writer.BIGroupValueIndexNIOWriter;
import com.finebi.cube.provider.BIGroupValueIndexNIOProvider;
import com.finebi.cube.provider.BIProjectPathProvider;
import com.finebi.cube.tools.GroupValueIndexTestTool;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;
import junit.framework.TestCase;

import java.io.File;

/**
 * This class created on 2016/4/1.
 *
 * @author Connery
 * @since 4.0
 */
public class BIGroupValueIndexNIOTest extends TestCase {
    BIGroupValueIndexNIOWriter writer;
    BIGroupValueIndexNIOReader reader;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String start = BIProjectPathProvider.projectPath + File.separator + "start";
        String length = BIProjectPathProvider.projectPath + File.separator + "length";
        String content = BIProjectPathProvider.projectPath + File.separator + "content";
        writer = BIGroupValueIndexNIOProvider.BIGroupValueIndexNIOWriterProvider(start, length, content);
        reader = BIGroupValueIndexNIOProvider.BIGroupValueIndexNIOReaderProvider(start, length, content);
    }

    public void testSpeed() {
    }

    public void testBasic() {
        try {
            GroupValueIndex one = GroupValueIndexTestTool.generateRandomIndex();
            GroupValueIndex two = GroupValueIndexTestTool.generateRandomIndex();
            GroupValueIndex three = GroupValueIndexTestTool.generateRandomIndex();
            writer.recordSpecificValue(0, one);
            writer.recordSpecificValue(2, two);
            writer.recordSpecificValue(3, three);
            assertEquals(reader.getSpecificValue(1).getRowsCountWithData(), 0);
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
            writer.recordSpecificValue(-1, GroupValueIndexTestTool.generateRandomIndex());
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