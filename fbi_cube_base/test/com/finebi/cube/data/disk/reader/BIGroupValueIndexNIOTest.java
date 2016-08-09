package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.disk.BIDiskWriterReaderTest;
import com.finebi.cube.data.disk.reader.primitive.BIByteNIOReader;
import com.finebi.cube.data.disk.reader.primitive.BIIntegerNIOReader;
import com.finebi.cube.data.disk.reader.primitive.BILongNIOReader;
import com.finebi.cube.data.disk.writer.BIByteArrayNIOWriter;
import com.finebi.cube.data.disk.writer.BIGroupValueIndexNIOWriter;
import com.finebi.cube.data.disk.writer.primitive.BIByteNIOWriter;
import com.finebi.cube.data.disk.writer.primitive.BIIntegerNIOWriter;
import com.finebi.cube.data.disk.writer.primitive.BILongNIOWriter;
import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.data.output.primitive.ICubeByteWriter;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.data.output.primitive.ICubeLongWriter;
import com.finebi.cube.tools.GroupValueIndexTestTool;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.code.BILogger;
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
        String start = BIDiskWriterReaderTest.projectPath + File.separator + "start";
        String length = BIDiskWriterReaderTest.projectPath + File.separator + "length";
        String content = BIDiskWriterReaderTest.projectPath + File.separator + "content";
        ICubeLongWriter startPositionRecorder = new BILongNIOWriter(start);
        ICubeIntegerWriter lengthRecorder = new BIIntegerNIOWriter(length);
        ICubeByteWriter contentRecorder = new BIByteNIOWriter(content);
        writer = new BIGroupValueIndexNIOWriter(new BIByteArrayNIOWriter(startPositionRecorder, lengthRecorder, contentRecorder));
        ICubeLongReader startPositionRecorderReader = new BILongNIOReader(start);
        ICubeIntegerReader lengthRecorderReader = new BIIntegerNIOReader(length);
        ICubeByteReader contentRecorderReader = new BIByteNIOReader(content);
        reader = new BIGroupValueIndexNIOReader(new BIByteArrayNIOReader(startPositionRecorderReader, lengthRecorderReader, contentRecorderReader));
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
            BILogger.getLogger().error(e.getMessage(), e);
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
            assertTrue(false);
        }
    }
}