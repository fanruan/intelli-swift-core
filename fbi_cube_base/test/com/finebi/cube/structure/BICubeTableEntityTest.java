package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.structure.table.BICubeTableEntity;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.DBFieldTestTool;
import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.location.*;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.finebi.cube.structure.column.date.BIDateColumnTool;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableEntityTest extends TestCase {
    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    private BICubeTableEntity tableEntity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cubeConfiguration = new BICubeConfigurationTest();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        tableEntity = new BICubeTableEntity(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()), retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
    }

    public void testMainData() {

        List<DBField> fields = new ArrayList<DBField>();
        fields.add(DBFieldTestTool.generateDATE());
        fields.add(DBFieldTestTool.generateSTRING());
        tableEntity.recordTableStructure(fields);
        assertTrue(tableEntity.tableDataAvailable());
    }

    public void testFieldData() {
        try {
            List<DBField> fields = new ArrayList<DBField>();
            fields.add(DBFieldTestTool.generateDATE());
            fields.add(DBFieldTestTool.generateSTRING());
            fields.add(DBFieldTestTool.generateBOOLEAN());
            fields.add(DBFieldTestTool.generateDECIMAL());
            fields.add(DBFieldTestTool.generateDOUBLE());
            fields.add(DBFieldTestTool.generateFLOAT());
            fields.add(DBFieldTestTool.generateINTEGER());
            fields.add(DBFieldTestTool.generateLONG());
            fields.add(DBFieldTestTool.generateTIME());
            fields.add(DBFieldTestTool.generateTIMESTAMP());

            tableEntity.recordTableStructure(fields);
            ICubeColumnReaderService readerService = tableEntity.getColumnDataGetter(BIDateColumnTool.generateYearMonthDay(DBFieldTestTool.generateTIME()));

            assertTrue(tableEntity.tableDataAvailable());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    /**
     * 异常原因，第一次写入table的info已经将reader的初始化了一个小文件的Map，
     * 而再一次记录一个大数据量的table的info时候，由于page相同，于是没有更新缓存，所有读取超过上一次
     * 初始化的大小时候便会抛错
     */
    public void testPropertyExceptionData() {
        try {

            List<DBField> fields = new ArrayList<DBField>();
            fields.add(DBFieldTestTool.generateDATE());
            tableEntity.recordTableStructure(fields);
            assertTrue(tableEntity.tableDataAvailable());


            fields.clear();
            fields.add(DBFieldTestTool.generateDATE());
            fields.add(DBFieldTestTool.generateSTRING());
            fields.add(DBFieldTestTool.generateBOOLEAN());
            fields.add(DBFieldTestTool.generateDECIMAL());
            fields.add(DBFieldTestTool.generateDOUBLE());
            fields.add(DBFieldTestTool.generateFLOAT());
            fields.add(DBFieldTestTool.generateINTEGER());
            fields.add(DBFieldTestTool.generateLONG());
            fields.add(DBFieldTestTool.generateTIME());

            tableEntity.recordTableStructure(fields);
            ICubeColumnReaderService readerService = tableEntity.getColumnDataGetter(BIDateColumnTool.generateYearMonthDay(DBFieldTestTool.generateTIME()));

            assertTrue(tableEntity.tableDataAvailable());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testField() {
        assertTrue(tableEntity.tableDataAvailable());
    }


}
