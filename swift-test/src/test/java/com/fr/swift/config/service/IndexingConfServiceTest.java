package com.fr.swift.config.service;

import com.fr.swift.config.TestConfDb;
import com.fr.swift.config.indexing.ColumnIndexingConf;
import com.fr.swift.config.indexing.TableIndexingConf;
import com.fr.swift.config.indexing.impl.SwiftColumnIndexingConf;
import com.fr.swift.config.indexing.impl.SwiftTableIndexingConf;
import com.fr.swift.generate.conf.service.SwiftIndexingConfService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.test.Preparer;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author anchore
 * @date 2018/7/16
 */
public class IndexingConfServiceTest {
    private final IndexingConfService service = SwiftIndexingConfService.get();

    @BeforeClass
    public static void boot() throws Exception {
        Preparer.prepareFrEnv();
        TestConfDb.setConfDb(SwiftTableIndexingConf.class, SwiftColumnIndexingConf.class);
        Preparer.prepareContext();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void setAndGet() {
        SourceKey a = new SourceKey("A");
        TableIndexingConf tableConf = new SwiftTableIndexingConf(a, new LineAllotRule(1024));
        service.setTableConf(tableConf);
        assertEquals(tableConf, service.getTableConf(a));

        tableConf = new SwiftTableIndexingConf(a, new LineAllotRule(2048));
        service.setTableConf(tableConf);
        assertEquals(tableConf, service.getTableConf(a));

        ColumnIndexingConf columnConf = new SwiftColumnIndexingConf(a, "a", false, false);
        service.setColumnConf(columnConf);
        assertEquals(columnConf, service.getColumnConf(a, "a"));

        columnConf = new SwiftColumnIndexingConf(a, "a", true, false);
        service.setColumnConf(columnConf);
        assertEquals(columnConf, service.getColumnConf(a, "a"));
    }

    @Test(expected = RuntimeException.class)
    public void wrongSet() {
        SourceKey a = new SourceKey("A");

        ColumnIndexingConf columnConf = new SwiftColumnIndexingConf(a, "a", false, true);
        service.setColumnConf(columnConf);
        assertEquals(columnConf, service.getColumnConf(a, "a"));
    }

    public static void assertEquals(TableIndexingConf conf1, TableIndexingConf conf2) {
        Assert.assertEquals(conf1.getTable(), conf2.getTable());

        AllotRule allotRule1 = conf1.getAllotRule();
        AllotRule allotRule2 = conf2.getAllotRule();
        Assert.assertEquals(allotRule1.getType(), allotRule2.getType());

        Assert.assertEquals(((LineAllotRule) allotRule1).getStep(), ((LineAllotRule) allotRule2).getStep());
    }

    public static void assertEquals(ColumnIndexingConf conf1, ColumnIndexingConf conf2) {
        Assert.assertEquals(conf1.getTable(), conf2.getTable());

        Assert.assertEquals(conf1.getColumn(), conf2.getColumn());

        Assert.assertEquals(conf1.requireIndex(), conf1.requireIndex());

        Assert.assertEquals(conf1.requireGlobalDict(), conf1.requireGlobalDict());
    }
}