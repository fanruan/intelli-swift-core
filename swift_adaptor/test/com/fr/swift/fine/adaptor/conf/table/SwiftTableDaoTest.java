package com.fr.swift.fine.adaptor.conf.table;

import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.conf.business.ISwiftXmlWriter;
import com.fr.swift.conf.business.table.SwiftTableDao;
import com.fr.swift.conf.business.table.TableParseXml;
import com.fr.swift.conf.business.table.TableXmlWriter;
import com.fr.swift.fine.adaptor.conf.creater.TestTableCreator;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-24 14:45:08
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftTableDaoTest extends TestCase {

    private TableParseXml xmlHandler = new TableParseXml();
    private ISwiftXmlWriter swiftXmlWriter = new TableXmlWriter();
    private String fileName = "table.xml";

    private SwiftTableDao tableDao = new SwiftTableDao(xmlHandler, fileName, swiftXmlWriter);

    /**
     * 单表的增、改、删
     */
    @Test
    public void testTable() {
        tableDao.removeAllConfig();

        tableDao.saveConfig(TestTableCreator.createA());
        tableDao.saveConfig(TestTableCreator.createB());
        assertEquals(tableDao.getAllConfig().size(), 2);
        assertEquals(tableDao.getAllConfig().get(0).getId(), TestTableCreator.createA().getId());
        assertEquals(tableDao.getAllConfig().get(1).getId(), TestTableCreator.createB().getId());

        tableDao.updateConfig(TestTableCreator.createA());
        assertEquals(tableDao.getAllConfig().size(), 2);
        assertEquals(tableDao.getAllConfig().get(1).getId(), TestTableCreator.createA().getId());
        assertEquals(tableDao.getAllConfig().get(0).getId(), TestTableCreator.createB().getId());

        tableDao.removeConfig(TestTableCreator.createA());
        assertEquals(tableDao.getAllConfig().size(), 1);
        assertEquals(tableDao.getAllConfig().get(0).getId(), TestTableCreator.createB().getId());
    }

    /**
     * 多表的增、改、删
     */
    @Test
    public void testTables() {
        tableDao.removeAllConfig();
        List<FineBusinessTable> tables = new ArrayList<FineBusinessTable>();
        tables.add(TestTableCreator.createA());
        tables.add(TestTableCreator.createB());
        tables.add(TestTableCreator.createC());

        tableDao.saveConfigs(tables);
        assertEquals(tableDao.getAllConfig().size(), 3);
        assertEquals(tableDao.getAllConfig().get(0).getId(), TestTableCreator.createA().getId());
        assertEquals(tableDao.getAllConfig().get(1).getId(), TestTableCreator.createB().getId());
        assertEquals(tableDao.getAllConfig().get(2).getId(), TestTableCreator.createC().getId());

        tables.remove(2);
        tableDao.updateConfigs(tables);
        assertEquals(tableDao.getAllConfig().size(), 3);
        assertEquals(tableDao.getAllConfig().get(0).getId(), TestTableCreator.createC().getId());
        assertEquals(tableDao.getAllConfig().get(1).getId(), TestTableCreator.createA().getId());
        assertEquals(tableDao.getAllConfig().get(2).getId(), TestTableCreator.createB().getId());

        tableDao.removeConfigs(tables);
        assertEquals(tableDao.getAllConfig().size(), 1);
        assertEquals(tableDao.getAllConfig().get(0).getId(), TestTableCreator.createC().getId());
    }
}
