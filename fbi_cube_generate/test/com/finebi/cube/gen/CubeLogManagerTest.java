package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.arrange.BICubeOperationManager4Test;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.fr.bi.cal.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.stable.bridge.StableFactory;
import org.junit.Test;

import java.util.Set;

/**
 * Created by wuk on 16/6/3.
 */
public class CubeLogManagerTest extends BICubeTestBase {
    private BICubeOperationManager4Test operationManager;
    public CubeLogManagerTest() {
        StableFactory.registerMarkedObject(BILogManagerProvider.XML_TAG, new BILogManager());
    }

    public void testLogStart() throws Exception {
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        biLogManager.logStart(-999);
        Set<CubeTableSource> dataSourceSet = BIMemoryDataSourceFactory.getDataSourceSet();
        for (CubeTableSource cubeTableSource : dataSourceSet) {
            PersistentTable persistentTable = new PersistentTable(null, cubeTableSource.getSourceID(), null);
            biLogManager.infoTable(persistentTable,1000,-999);
        }

        assertTrue(biLogManager.createJSON(-999).getJSONArray("tables").length()==dataSourceSet.size());
        biLogManager.logEnd(-999);
        
    }
    

    
    @Test
    public void logRelationStart() throws Exception {

    }

    @Test
    public void logEnd() throws Exception {

    }

    @Test
    public void logIndexStart() throws Exception {

    }

    @Test
    public void errorTable() throws Exception {

    }

    @Test
    public void infoTable() throws Exception {

    }

    @Test
    public void infoTableReading() throws Exception {

    }

    @Test
    public void infoTableReading1() throws Exception {

    }

    @Test
    public void infoTable1() throws Exception {

    }

    @Test
    public void infoTableIndex() throws Exception {

    }

    @Test
    public void infoColumn() throws Exception {

    }

    @Test
    public void infoColumn1() throws Exception {

    }

    @Test
    public void errorRelation() throws Exception {

    }

    @Test
    public void infoRelation() throws Exception {

    }

    @Test
    public void infoRelation1() throws Exception {

    }

    @Test
    public void loopRelation() throws Exception {

    }

    @Test
    public void createJSON() throws Exception {

    }

    @Test
    public void getBILog() throws Exception {

    }

    @Test
    public void getCubeEnd() throws Exception {

    }

}
