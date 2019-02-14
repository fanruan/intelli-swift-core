package com.fr.swift;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.container.DataSourceContainer;
import com.fr.swift.source.container.PathSourceContainer;
import com.fr.swift.source.container.RelationSourceContainer;
import junit.framework.TestCase;
import org.easymock.EasyMock;
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
public class SourceContainerManagerTest extends TestCase {

    private DataSourceContainer dataSourceContainer;
    private RelationSourceContainer relationSourceContainer;
    private PathSourceContainer pathSourceContainer;

    private DataSource tableA = EasyMock.mock("A", DataSource.class);
    private DataSource tableB = EasyMock.mock("B", DataSource.class);
    private DataSource tableC = EasyMock.mock("C", DataSource.class);
    private DataSource tableD = EasyMock.mock("D", DataSource.class);
    private DataSource tableE = EasyMock.mock("E", DataSource.class);

    private RelationSource relationAB = EasyMock.mock(RelationSource.class);
    private RelationSource relationBC = EasyMock.mock(RelationSource.class);
    private RelationSource relationCD = EasyMock.mock(RelationSource.class);
    private RelationSource relationDE = EasyMock.mock(RelationSource.class);

    private SourcePath pathSourceABC = EasyMock.mock(SourcePath.class);
    private SourcePath pathSourceBCD = EasyMock.mock(SourcePath.class);
    private SourcePath pathSourceCDE = EasyMock.mock(SourcePath.class);
    private SourcePath pathSourceABCD = EasyMock.mock(SourcePath.class);
    private SourcePath pathSourceBCDE = EasyMock.mock(SourcePath.class);

    @Override
    public void setUp() throws Exception {
        dataSourceContainer = new DataSourceContainer();
        relationSourceContainer = new RelationSourceContainer();
        pathSourceContainer = new PathSourceContainer();
        EasyMock.expect(tableA.getSourceKey()).andReturn(new SourceKey("A")).anyTimes();
        EasyMock.expect(tableB.getSourceKey()).andReturn(new SourceKey("B")).anyTimes();
        EasyMock.expect(tableC.getSourceKey()).andReturn(new SourceKey("C")).anyTimes();
        EasyMock.expect(tableD.getSourceKey()).andReturn(new SourceKey("D")).anyTimes();
        EasyMock.expect(tableE.getSourceKey()).andReturn(new SourceKey("E")).anyTimes();

        EasyMock.expect(relationAB.getSourceKey()).andReturn(new SourceKey("AB")).anyTimes();
        EasyMock.expect(relationBC.getSourceKey()).andReturn(new SourceKey("BC")).anyTimes();
        EasyMock.expect(relationCD.getSourceKey()).andReturn(new SourceKey("CD")).anyTimes();
        EasyMock.expect(relationDE.getSourceKey()).andReturn(new SourceKey("DE")).anyTimes();

        EasyMock.expect(pathSourceABC.getSourceKey()).andReturn(new SourceKey("ABC")).anyTimes();
        EasyMock.expect(pathSourceBCD.getSourceKey()).andReturn(new SourceKey("BCD")).anyTimes();
        EasyMock.expect(pathSourceCDE.getSourceKey()).andReturn(new SourceKey("CDE")).anyTimes();
        EasyMock.expect(pathSourceABCD.getSourceKey()).andReturn(new SourceKey("ABCD")).anyTimes();

        EasyMock.replay(tableA, tableB, tableC, tableD, tableE, relationAB, relationBC, relationCD, relationDE);
        EasyMock.replay(pathSourceABC, pathSourceBCD, pathSourceCDE, pathSourceABCD, pathSourceBCDE);

        dataSourceContainer.addSource(tableA);
        dataSourceContainer.addSource(tableB);
        dataSourceContainer.addSource(tableC);

        relationSourceContainer.addSource(relationAB);
        relationSourceContainer.addSource(relationBC);

        pathSourceContainer.addSource(pathSourceABC);

    }

    @Test
    public void testDataSourceContainer() {
        dataSourceContainer.addSource(tableD);
        assertTrue(dataSourceContainer.getAllSources().size() == 4);

        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        dataSourceList.add(tableA);
        dataSourceList.add(tableE);
        dataSourceContainer.addSources(dataSourceList);
        assertTrue(dataSourceContainer.getAllSources().size() == 5);
        assertTrue(dataSourceContainer.getSourceByKey(tableA.getSourceKey().getId()) != null);
        assertTrue(dataSourceContainer.getSourceByKey("123") == null);

        List<String> getSourceList = new ArrayList<String>();
        getSourceList.add(tableA.getSourceKey().getId());
        getSourceList.add(tableB.getSourceKey().getId());
        getSourceList.add("456");
        assertTrue(dataSourceContainer.getSourcesByKeys(getSourceList).size() == 2);

        assertTrue(dataSourceContainer.removeSource("123") == null);
        assertTrue(dataSourceContainer.removeSource(tableC.getSourceKey().getId()) != null);
        assertTrue(dataSourceContainer.removeSource(tableD) != null);
        assertTrue(dataSourceContainer.size() == 3);

        assertTrue(dataSourceContainer.removeSourcesByKeys(getSourceList).size() == 2);
        assertTrue(dataSourceContainer.size() == 1);

        List<DataSource> removeList = new ArrayList<DataSource>();
        removeList.add(tableE);
        assertTrue(dataSourceContainer.removeSources(removeList).size() == 1);
        assertTrue(dataSourceContainer.size() == 0);
    }
}
