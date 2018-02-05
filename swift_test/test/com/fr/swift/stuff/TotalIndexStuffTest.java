package com.fr.swift.stuff;

import com.fr.swift.creater.StuffSourceCreater;
import com.fr.swift.provider.IndexStuffManager;
import com.fr.swift.provider.TotalIndexStuffTestProvider;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2017-11-29.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TotalIndexStuffTest extends TestCase {

    private TotalIndexStuffTestProvider provider;
    private IndexStuffManager manager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        provider = new TotalIndexStuffTestProvider();
        manager = new IndexStuffManager(provider);
    }

    @Test
    public void testTotalIndexStuff() {
        List<String> updateTableSources = new ArrayList<String>();
        updateTableSources.add(StuffSourceCreater.createTableA().getSourceKey().getId());
        updateTableSources.add(StuffSourceCreater.createTableB().getSourceKey().getId());
        updateTableSources.add(StuffSourceCreater.createTableC().getSourceKey().getId());
        List<String> updateTableSourceRelations = new ArrayList<String>();
        updateTableSourceRelations.add(StuffSourceCreater.createRelationAB().getSourceKey().getId());
        updateTableSourceRelations.add(StuffSourceCreater.createRelationBC().getSourceKey().getId());
        List<String> updateTableSourceRelationPaths = new ArrayList<String>();
        updateTableSourceRelationPaths.add(StuffSourceCreater.createPathABC().getSourceKey().getId());
        HistoryIndexStuffImpl stuff = new HistoryIndexStuffImpl(updateTableSources, updateTableSourceRelations, updateTableSourceRelationPaths);

        assertTrue(manager.getAllTables().size() == 5);
        assertTrue(manager.getAllRelations().size() == 4);
        assertTrue(manager.getAllPaths().size() == 6);
        assertTrue(manager.getTablesByIds(stuff.getUpdateTableSources()).size() == 3);
        assertTrue(manager.getRelationsByIds(stuff.getUpdateTableSourceRelations()).size() == 2);
        assertTrue(manager.getPathsByIds(stuff.getUpdateTableSourceRelationPaths()).size() == 1);
    }
}
