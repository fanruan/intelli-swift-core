package com.finebi.conf.table;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.exception.FineTableAbsentException;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.provider.SwiftTableManager;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-23 16:45:26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class BusinessTableProviderTest extends TestCase {

    public void testBusinessTable() throws FineTableAbsentException {
        SwiftTableManager provider = new SwiftTableManager();
        List<String> tableIdList = new ArrayList<String>();
        for (FineBusinessTable fineBusinessTable : provider.getAllTable()) {
            tableIdList.add(fineBusinessTable.getId());
        }
        provider.removeTable(tableIdList);
        assertEquals(provider.getAllTable().size(), 0);

        FineBusinessTable fineBusinessTableA = new FineDBBusinessTable("A", FineEngineType.Cube, "local", "A", "A");
        FineBusinessTable fineBusinessTableB = new FineDBBusinessTable("B", FineEngineType.Cube, "local", "B", "B");
        FineBusinessTable fineBusinessTableC = new FineDBBusinessTable("C", FineEngineType.Cube, "local", "C", "C");
        FineBusinessTable fineBusinessTableD = new FineDBBusinessTable("D", FineEngineType.Cube, "local", "D", "D");
        FineBusinessTable fineBusinessTableE = new FineDBBusinessTable("E", FineEngineType.Cube, "local", "E", "E");

        List<FineBusinessTable> tableList = new ArrayList<FineBusinessTable>();
        tableList.add(fineBusinessTableA);
        tableList.add(fineBusinessTableB);
        tableList.add(fineBusinessTableC);
        tableList.add(fineBusinessTableD);
        tableList.add(fineBusinessTableE);

//        provider.addTables(tableList);
//        assertEquals(provider.getAllTable().size(), 5);
//        assertNotNull(provider.getSingleTable("A"));
//        assertNotNull(provider.getSingleTable("E"));
//
//
//        tableIdList.clear();
//        tableIdList.add("D");
//        tableIdList.add("E");
//        provider.removeTable(tableIdList);
//        assertEquals(provider.getAllTable().size(), 3);
//        assertNotNull(provider.getSingleTable("A"));
//        assertNull(provider.getSingleTable("E"));
    }
}
