package com.fr.swift.db;

import com.fr.general.ComparatorUtils;
import com.fr.swift.config.TestConfDb;
import com.fr.swift.config.conf.bean.MetaDataColumnBean;
import com.fr.swift.config.conf.bean.SwiftMetaDataBean;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author anchore
 * @date 2018/4/4
 */
public class DatabaseTest {
    private Database db = SwiftDatabase.getInstance();

    @BeforeClass
    public static void beforeClass() throws Exception {
        TestConfDb.setConfDb();
    }

    @Test
    public void tableOpFlow() throws SQLException {
        List<SwiftMetaDataColumn> columnMetas = Arrays.asList(
                new MetaDataColumnBean("A", Types.VARCHAR),
                new MetaDataColumnBean("B", Types.DATE),
                new MetaDataColumnBean("C", Types.INTEGER)
        );
        SourceKey tableKey = new SourceKey("a");
        SwiftMetaData meta = new SwiftMetaDataBean("a", columnMetas);

        if (db.existsTable(tableKey)) {
            db.dropTable(tableKey);
        }

        db.createTable(tableKey, meta);
        assertTrue(db.existsTable(tableKey));

        assertEquals(tableKey, db.getTable(tableKey).getSourceKey());
        assertTrue(equals(meta, db.getTable(tableKey).getMeta()));

        SwiftMetaData changedMeta = new SwiftMetaDataBean("b", columnMetas.subList(1, 3));
        db.alterTable(tableKey, changedMeta);
        assertTrue(equals(changedMeta, db.getTable(tableKey).getMeta()));

        // assertEquals(1, db.getAllTables().size());

        db.dropTable(tableKey);
        assertFalse(db.existsTable(tableKey));
    }

    private static boolean equals(SwiftMetaData meta1, SwiftMetaData meta2) throws SwiftMetaDataException {
        if (meta1 == meta2) {
            return true;
        }
        if (meta1 == null || meta2 == null) {
            return false;
        }
        if (meta1.getColumnCount() != meta2.getColumnCount()) {
            return false;
        }
        for (int i = 1; i <= meta1.getColumnCount(); i++) {
            SwiftMetaDataColumn columnMeta1 = meta1.getColumn(i);
            SwiftMetaDataColumn columnMeta2 = meta1.getColumn(i);
            if (!ComparatorUtils.equals(columnMeta1.getName(), columnMeta2.getName())) {
                return false;
            }
            if (!ComparatorUtils.equals(columnMeta1.getRemark(), columnMeta2.getRemark())) {
                return false;
            }
            if (columnMeta1.getType() != columnMeta2.getType()) {
                return false;
            }
            if (columnMeta1.getPrecision() != columnMeta2.getPrecision()) {
                return false;
            }
            if (columnMeta1.getScale() != columnMeta2.getScale()) {
                return false;
            }
            if (!ComparatorUtils.equals(columnMeta1.getColumnId(), columnMeta2.getColumnId())) {
                return false;
            }
        }
        return true;
    }
}