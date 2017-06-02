package com.finebi.integration.cube.custom.stuff;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.BITableRelationConfigurationProvider;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.integration.cube.custom.it.CustomCubeConfManager;
import com.finebi.integration.cube.custom.it.CustomDataSourceManager;
import com.fr.bi.conf.base.cube.BISystemCubeConfManager;
import com.fr.bi.conf.manager.update.BIUpdateSettingManager;
import com.fr.bi.conf.provider.BIUpdateFrequencyManagerProvider;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.control.UserControl;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lucifer on 2017-6-2.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class AbstractCustomStuffTest extends TestCase {

    protected BILogger logger = BILoggerFactory.getLogger(SingleCustomStuffTest.class);

    protected long userId = UserControl.getInstance().getSuperManagerID();
    //所有tablesource和基础表sourceId对应关系。
    protected Map<CubeTableSource, Set<String>> tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
    //所有sourceId和updateType对应关系
    protected Map<String, Integer> sourceIdUpdateTypeMap = new HashMap<String, Integer>();
    protected Set<CubeTableSource> tableSources = new HashSet<CubeTableSource>();
    protected Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
    protected Set<BITableSourceRelationPath> paths = new HashSet<BITableSourceRelationPath>();
    protected Set<CubeTableSource> absentTables = new HashSet<CubeTableSource>();
    protected Set<BITableSourceRelation> absentRelations = new HashSet<BITableSourceRelation>();
    protected Set<BITableSourceRelationPath> absentPaths = new HashSet<BITableSourceRelationPath>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        StableFactory.registerMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, new StuffPackageProvider());
        StableFactory.registerMarkedObject(BITableRelationConfigurationProvider.XML_TAG, new StuffRelationProvider());
        StableFactory.registerMarkedObject(BISystemCubeConfManager.XML_TAG, new CustomCubeConfManager());
        StableFactory.registerMarkedObject(BIDataSourceManagerProvider.XML_TAG, new CustomDataSourceManager());
        StableFactory.registerMarkedObject(BIUpdateFrequencyManagerProvider.XML_TAG, new BIUpdateSettingManager());
    }

    protected void clearAll() {
        tableBaseSourceIdMap.clear();
        sourceIdUpdateTypeMap.clear();
        tableSources.clear();
        relations.clear();
        paths.clear();
        absentTables.clear();
        absentRelations.clear();
        absentPaths.clear();
    }

    public void test() {
    }
}
