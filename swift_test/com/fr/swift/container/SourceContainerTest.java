package com.fr.swift.container;

import com.fr.swift.creater.StuffSourceCreater;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.container.DataSourceContainer;
import com.fr.swift.source.container.PathSourceContainer;
import com.fr.swift.source.container.RelationSourceContainer;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2017-12-19 14:23:54
 *
 * @author Lucifer
 * @description container测试
 * @since Advanced FineBI Analysis 1.0
 */
public class SourceContainerTest extends TestCase {

    private DataSourceContainer dataSourceContainer;
    private RelationSourceContainer relationSourceContainer;
    private PathSourceContainer pathSourceContainer;

    @Override
    public void setUp() throws Exception {
        dataSourceContainer = new DataSourceContainer();
        relationSourceContainer = new RelationSourceContainer();
        pathSourceContainer = new PathSourceContainer();
        dataSourceContainer.addSource(StuffSourceCreater.createTableA());
        dataSourceContainer.addSource(StuffSourceCreater.createTableB());
        dataSourceContainer.addSource(StuffSourceCreater.createTableC());

        relationSourceContainer.addSource(StuffSourceCreater.createRelationAB());
        relationSourceContainer.addSource(StuffSourceCreater.createRelationBC());

        pathSourceContainer.addSource(StuffSourceCreater.createPathABC());

    }

    @Test
    public void testDataSourceContainer() {
        dataSourceContainer.addSource(StuffSourceCreater.createTableD());
        assertTrue(dataSourceContainer.getAllSources().size() == 4);

        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        dataSourceList.add(StuffSourceCreater.createTableA());
        dataSourceList.add(StuffSourceCreater.createTableE());
        dataSourceContainer.addSources(dataSourceList);
        assertTrue(dataSourceContainer.getAllSources().size() == 5);
        assertTrue(dataSourceContainer.getSourceByKey(StuffSourceCreater.createTableA().getSourceKey().getId()) != null);
        assertTrue(dataSourceContainer.getSourceByKey("123") == null);

        List<String> getSourceList = new ArrayList<String>();
        getSourceList.add(StuffSourceCreater.createTableA().getSourceKey().getId());
        getSourceList.add(StuffSourceCreater.createTableB().getSourceKey().getId());
        getSourceList.add("456");
        assertTrue(dataSourceContainer.getSourcesByKeys(getSourceList).size() == 2);

        assertTrue(dataSourceContainer.removeSource("123") == null);
        assertTrue(dataSourceContainer.removeSource(StuffSourceCreater.createTableC().getSourceKey().getId()) != null);
        assertTrue(dataSourceContainer.removeSource(StuffSourceCreater.createTableD()) != null);
        assertTrue(dataSourceContainer.size() == 3);

        assertTrue(dataSourceContainer.removeSourcesByKeys(getSourceList).size() == 2);
        assertTrue(dataSourceContainer.size() == 1);

        List<DataSource> removeList = new ArrayList<DataSource>();
        removeList.add(StuffSourceCreater.createTableE());
        assertTrue(dataSourceContainer.removeSources(removeList).size() == 1);
        assertTrue(dataSourceContainer.size() == 0);
    }
}
