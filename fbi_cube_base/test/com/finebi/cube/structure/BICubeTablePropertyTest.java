package com.finebi.cube.structure;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.location.BICubeConfigurationTest;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.DBFieldTestTool;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.utils.file.BIFileUtils;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/5/1.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTablePropertyTest extends TestCase {
    private BICubeTableProperty property;
    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    ICubeResourceLocation location;

    static {
    }

    public BICubeTablePropertyTest() {
        try {
            cubeConfiguration = new BICubeConfigurationTest();
            retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
            location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
            property = new BICubeTableProperty(location);
        } catch (BICubeResourceAbsentException e) {
            assertFalse(true);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ICubeResourceLocation location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
        File file = new File(location.getAbsolutePath());
        if (file.exists()) {
            BIFileUtils.delete(file);
        }

    }

    public void testRowCountWrite() {
        synchronized (this.getClass()) {
            try {
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                property.recordRowCount(10L);
                assertFalse(property.isRowCountReaderAvailable());
                assertTrue(property.isRowCountWriterAvailable());
            } catch (Exception e) {
                assertTrue(false);
            } finally {
                property.clear();

                File file = new File(location.getAbsolutePath());
                if (file.exists()) {
                    BIFileUtils.delete(file);
                }
            }
        }

    }

    public void testRowCountRead() {
        synchronized (this.getClass()) {
            try {
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                int count = 10;
                property.recordRowCount(count);
                assertFalse(property.isRowCountReaderAvailable());
                assertTrue(property.isRowCountWriterAvailable());
                assertEquals(property.getRowCount(), count);
                assertTrue(property.isRowCountReaderAvailable());
                assertTrue(property.isRowCountWriterAvailable());
            } catch (Exception e) {
                assertFalse(true);
            } finally {
                property.clear();

                File file = new File(location.getAbsolutePath());
                if (file.exists()) {
                    BIFileUtils.delete(file);
                }
            }
        }

    }

    public void testVersionWriteRead() {
        synchronized (this.getClass()) {

            try {
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                int version = 10;
                property.recordTableGenerateVersion(version);
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertTrue(property.isVersionWriterAvailable());
                assertEquals(property.getTableVersion(), version);
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertTrue(property.isVersionReaderAvailable());
                assertTrue(property.isVersionWriterAvailable());
            } catch (Exception e) {
                assertFalse(true);
            } finally {
                property.clear();

                File file = new File(location.getAbsolutePath());
                if (file.exists()) {
                    BIFileUtils.delete(file);
                }
            }
        }
    }

    public void testLastTimeWriteRead() {
        try {
            synchronized (this.getClass()) {
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());

                long time = System.currentTimeMillis();
                property.recordLastTime(time);

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertTrue(property.isTimeStampWriterAvailable());

                assertEquals(property.getCubeLastTime().getTime(), time);

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                assertTrue(property.isTimeStampReaderAvailable());
                assertTrue(property.isTimeStampWriterAvailable());
            }
        } catch (Exception e) {
            assertTrue(false);
        } finally {
            property.clear();

            File file = new File(location.getAbsolutePath());
            if (file.exists()) {
                BIFileUtils.delete(file);
            }
        }
    }

    public void testFieldInfoWriteRead() {
        try {
            synchronized (this.getClass()) {

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());
                assertFalse(property.isFieldWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());

                List<DBField> fields = new ArrayList<DBField>();
                fields.add(DBFieldTestTool.generateSTRING());
                property.recordTableStructure(fields);


                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());
                assertTrue(property.isFieldWriterAvailable());
                List<DBField> fields1 = property.getFieldInfo();

                assertEquals(fields1.size(), 1);
                assertEquals(fields1.get(0), fields.get(0));

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertTrue(property.isFieldReaderAvailable());
                assertTrue(property.isFieldWriterAvailable());
            }
        } catch (Exception e) {
            assertTrue(false);
        } finally {
            property.clear();

            File file = new File(location.getAbsolutePath());
            if (file.exists()) {
                BIFileUtils.delete(file);
            }
        }
    }


    public void testPropertyWriteRead() {
        try {
            synchronized (this.getClass()) {
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertFalse(property.isFieldWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());

                assertFalse(property.isPropertyExist());
                assertTrue(property.isFieldReaderAvailable());
                List<DBField> fields = new ArrayList<DBField>();
                fields.add(DBFieldTestTool.generateSTRING());
                property.recordTableStructure(fields);

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isVersionReaderAvailable());
                assertFalse(property.isVersionWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertTrue(property.isFieldReaderAvailable());
                assertTrue(property.isFieldWriterAvailable());
                assertTrue(property.isPropertyExist());
            }

        } catch (Exception e) {
            assertTrue(false);
        } finally {
            property.clear();
            File file = new File(location.getAbsolutePath());
            if (file.exists()) {
                BIFileUtils.delete(file);
            }
        }
    }
}
