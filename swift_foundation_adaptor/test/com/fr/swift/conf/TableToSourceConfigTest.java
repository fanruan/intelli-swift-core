package com.fr.swift.conf;

import com.fr.swift.conf.business.table2source.TableToSource;
import com.fr.swift.conf.business.table2source.dao.TableToSourceConfigDao;
import com.fr.swift.conf.business.table2source.dao.TableToSourceConfigDaoImpl;
import com.fr.swift.config.TestConfDb;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * @author yee
 * @date 2018/3/14
 */
@RunWith(Parameterized.class)
public class TableToSourceConfigTest {
    private TableToSourceConfigDao config;

    @Before
    public void setUp() throws Exception {
        TestConfDb.setConfDb();
    }

    @Parameterized.Parameters
    public static List<TableToSourceConfigDao> data() {
        return Arrays.asList(new TableToSourceConfigDao[] {new TableToSourceConfigDaoImpl()});
    }

    public TableToSourceConfigTest(TableToSourceConfigDao config) {
        this.config = config;
    }

    @Test
    public void testAddAndGet() {
        int size = 10;
        List<TableToSource> conf = Table2SourceCreator.create(size);
        TableToSource[] array = new TableToSource[conf.size()];
        config.addConfig(conf.toArray(array));

        assertEquals(size, config.getAllConfig().size());
        for (TableToSource tableToSource : conf) {
            assertEquals(tableToSource.getSourceKey(), config.getConfigByTableId(tableToSource.getTableId()));
        }
    }

    @Test
    public void testModifyAndGet() {
        int size = 3;
        List<TableToSource> modify = Table2SourceCreator.modify(size);
        List<TableToSource> conf = Table2SourceCreator.create(10);
        TableToSource[] array = new TableToSource[conf.size()];
        config.addConfig(conf.toArray(array));

        assertEquals(10, config.getAllConfig().size());
        for (TableToSource tableToSource : conf) {
            assertEquals(tableToSource.getSourceKey(), config.getConfigByTableId(tableToSource.getTableId()));
        }
        array = new TableToSource[size];
        config.updateConfig(modify.toArray(array));

        for (int i = 0; i < 10; i++) {
            TableToSource value = conf.get(i);
            if (i < 3) {
                assertEquals(modify.get(i).getSourceKey(), config.getConfigByTableId(value.getTableId()));
            } else {
                assertEquals(value.getSourceKey(), config.getConfigByTableId(value.getTableId()));
            }
        }
    }

    @Test
    public void testRemoveAndGet() {
        int size = 10;
        List<TableToSource> conf = Table2SourceCreator.create(size);
        List<TableToSource> removeKey = conf.subList(0, 3);
        TableToSource[] array = new TableToSource[conf.size()];
        config.addConfig(conf.toArray(array));

        assertEquals(size, config.getAllConfig().size());
        for (TableToSource key : removeKey) {
            config.removeConfig(key.getTableId());
        }

        assertEquals(size - 3, config.getAllConfig().size());

        for (TableToSource key : removeKey) {
            assertNull(config.getConfigByTableId(key.getTableId()));
        }

    }

    @Test
    public void testRemoveAll() {
        int size = 10;
        List<TableToSource> conf = Table2SourceCreator.create(size);
        TableToSource[] array = new TableToSource[conf.size()];
        config.addConfig(conf.toArray(array));
        assertEquals(size, config.getAllConfig().size());
        config.removeAllConfigs();
        assertEquals(0, config.getAllConfig().size());
    }
}