package com.finebi.cube;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.tools.BICubeConfigurationTool;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.table.BICubeTableEntity;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.DBFieldTestTool;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTestBase extends TestCase {

    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    protected BICube cube;
    protected BICubeTableEntity tableEntity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cubeConfiguration = new BICubeConfigurationTool();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        tableEntity = (BICubeTableEntity) cube.getCubeTableWriter(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()));
        StableFactory.registerMarkedObject(BILogManagerProvider.XML_TAG, new BILogManager());
    }

    public void testVoid() {
    }

    public static void main(String[]args) {
        ICubeConfiguration cubeConfiguration = new BICubeConfigurationTool();
        ICubeResourceRetrievalService retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        BICube cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        BICubeTableEntity tableEntity = (BICubeTableEntity) cube.getCubeTableWriter(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()));
        StableFactory.registerMarkedObject(BILogManagerProvider.XML_TAG, new BILogManager());

        List<ICubeFieldSource> fields = new ArrayList<ICubeFieldSource>();
        fields.add(DBFieldTestTool.generateDATE());
        tableEntity.recordTableStructure(fields);
        System.out.println("BICubeTest=========test:"+fields.get(0).getFieldName());
        System.out.println("BICubeTest=========test: column info:"+((tableEntity).getCubeColumnInfo().size()));
        System.out.println("BICubeTest=========test: column info:"+tableEntity.currentLocation.getBaseLocation().toString());
        System.out.println("BICubeTest=========test: column info:"+tableEntity.currentLocation.getChildLocation().toString());
        assertFalse(tableEntity.tableDataAvailable());
    }
}
