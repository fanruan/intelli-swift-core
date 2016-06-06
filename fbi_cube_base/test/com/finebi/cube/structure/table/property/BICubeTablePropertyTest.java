package com.finebi.cube.structure.table.property;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.location.BICubeConfigurationTest;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.DBFieldTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.utils.code.BILogger;
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
    private ICubeResourceLocation location;


    public BICubeTablePropertyTest() {
        try {
            cubeConfiguration = new BICubeConfigurationTest();
            retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
            location = retrievalService.retrieveResource(new BITableKey(BITableSourceTestTool.getDBTableSourceD()));
            property = new BICubeTableProperty(location, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
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

    public void testRowCountWriteAvailable() {
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
                property.forceRelease();

                File file = new File(location.getAbsolutePath());
                if (file.exists()) {
                    BIFileUtils.delete(file);
                }
            }
        }

    }

    public void rowCountReadAvailable() {
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
                property.forceRelease();

                File file = new File(location.getAbsolutePath());
                if (file.exists()) {
                    BIFileUtils.delete(file);
                }
            }
        }

    }

    public void testVersionAvailable() {
        synchronized (this.getClass()) {

            try {

                long version = 10;
                property.addVersion(version);
                assertEquals(version,property.getVersion());

                version = 100;
                property.addVersion(version);
                assertEquals(version,property.getVersion());

                version = 1;
                property.addVersion(version);
                assertEquals(version,property.getVersion());
            } catch (Exception e) {
                assertFalse(true);
            } finally {
                property.forceRelease();

                File file = new File(location.getAbsolutePath());
                if (file.exists()) {
                    BIFileUtils.delete(file);
                }
            }
        }
    }

    public void testPropertyAvailable() {
        try {
            propertyAvailable();
            setUp();
            fieldInfoAvailable();
            setUp();

            lastTimeAvailable();
            setUp();


            rowCountReadAvailable();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void lastTimeAvailable() {
        try {
            synchronized (this.getClass()) {
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());

                long time = System.currentTimeMillis();
                property.recordLastTime(time);

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertTrue(property.isTimeStampWriterAvailable());

                assertEquals(property.getCubeLastTime().getTime(), time);

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertTrue(property.isTimeStampReaderAvailable());
                assertTrue(property.isTimeStampWriterAvailable());
            }
        } catch (Exception e) {
            assertTrue(false);
        } finally {
            property.forceRelease();

            File file = new File(location.getAbsolutePath());
            if (file.exists()) {
                BIFileUtils.delete(file);
            }
        }
    }

    public void fieldInfoAvailable() {
        try {
            synchronized (this.getClass()) {

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());
                assertFalse(property.isFieldWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());

                List<ICubeFieldSource> fields = new ArrayList<ICubeFieldSource>();
                fields.add(DBFieldTestTool.generateSTRING());
                property.recordTableStructure(fields);


                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());
                assertTrue(property.isFieldWriterAvailable());
                List<ICubeFieldSource> fields1 = property.getFieldInfo();

                assertEquals(fields1.size(), 1);
                assertEquals(fields1.get(0), fields.get(0));

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertTrue(property.isFieldReaderAvailable());
                assertTrue(property.isFieldWriterAvailable());
            }
        } catch (Exception e) {
            assertTrue(false);
        } finally {
            property.forceRelease();

            File file = new File(location.getAbsolutePath());
            if (file.exists()) {
                BIFileUtils.delete(file);
            }
        }
        propertyAvailable();
    }

    public void testAddNullFieldsPropertyAvailable() {
        try {
            synchronized (this.getClass()) {
                setUp();
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertFalse(property.isFieldWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());

                assertFalse(property.isPropertyExist());
                assertTrue(property.isFieldReaderAvailable());

                property.recordTableStructure(null);

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertTrue(property.isFieldReaderAvailable());
                assertTrue(property.isFieldWriterAvailable());
                property.recordLastTime();
                assertTrue(property.isPropertyExist());
            }

        } catch (Exception e) {
            assertTrue(false);
        } finally {
            property.forceRelease();
            File file = new File(location.getAbsolutePath());
            if (file.exists()) {
                BIFileUtils.delete(file);
            }
        }
    }

    public void propertyAvailable() {
        try {
            synchronized (this.getClass()) {
                setUp();
                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertFalse(property.isFieldWriterAvailable());
                assertFalse(property.isFieldReaderAvailable());

                assertFalse(property.isPropertyExist());
                assertTrue(property.isFieldReaderAvailable());
                List<ICubeFieldSource> fields = new ArrayList<ICubeFieldSource>();
                fields.add(DBFieldTestTool.generateSTRING());
                property.recordTableStructure(fields);

                assertFalse(property.isRowCountReaderAvailable());
                assertFalse(property.isRowCountWriterAvailable());
                assertFalse(property.isTimeStampReaderAvailable());
                assertFalse(property.isTimeStampWriterAvailable());
                assertTrue(property.isFieldReaderAvailable());
                assertTrue(property.isFieldWriterAvailable());
                property.recordLastTime();
                assertTrue(property.isPropertyExist());
            }

        } catch (Exception e) {
            assertTrue(false);
        } finally {
            property.forceRelease();
            File file = new File(location.getAbsolutePath());
            if (file.exists()) {
                BIFileUtils.delete(file);
            }
        }
    }

    public void testPropertyParentTables() {
        try {
            synchronized (this.getClass()) {
                assertFalse(property.isParentReaderAvailable());
                assertFalse(property.isParentWriterAvailable());
                ITableKey tableKey = new BITableKey("abc");
                ITableKey tableKey2 = new BITableKey("dfg");
                List<ITableKey> parents = new ArrayList<ITableKey>();
                parents.add(tableKey);
                parents.add(tableKey2);
                property.recordParentsTable(parents);
                assertFalse(property.isParentReaderAvailable());
                assertTrue(property.isParentWriterAvailable());
                List<ITableKey> parentsTable = property.getParentsTable();
                assertEquals(tableKey, parentsTable.get(0));
                assertEquals(tableKey2, parentsTable.get(1));
            }

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        } finally {
            property.forceRelease();
            File file = new File(location.getAbsolutePath());
            if (file.exists()) {
                BIFileUtils.delete(file);
            }
        }
    }
}
