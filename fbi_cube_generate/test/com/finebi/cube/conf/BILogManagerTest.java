package com.finebi.cube.conf;

import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kary on 2016/6/12.
 */
public class BILogManagerTest extends TestCase {
    protected BILogManagerProvider biLogManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StableFactory.registerMarkedObject(BILogManagerProvider.XML_TAG, new BILogManager());
        biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
    }

    public void testLog() {
        try {
            biLogManager.logStart(-999);
            Set<CubeTableSource> dataSourceSet = BIMemoryDataSourceFactory.getDataSourceSet();
            for (CubeTableSource cubeTableSource : dataSourceSet) {
                boolean isException = Math.random() > 0.3;
                if (isException) {
                    ICubeFieldSource[] fieldsArray = cubeTableSource.getFieldsArray(dataSourceSet);
                    biLogManager.infoTable(cubeTableSource.getPersistentTable(), 1000, -999);
                    for (ICubeFieldSource iCubeFieldSource : fieldsArray) {
                        biLogManager.infoColumn(iCubeFieldSource.getTableBelongTo().getPersistentTable(), iCubeFieldSource.getFieldName(), 100, -999);
                    }
                }else {
                    biLogManager.errorTable(cubeTableSource.getPersistentTable(),"error",-999);
                }
            }
            Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
            relations.add(BITableSourceRelationTestTool.getABWithBICubeFieldSource());
            for (BITableSourceRelation relation : relations) {
                biLogManager.infoRelation(getRelaionColumeKeyInfo(relation), 1000, -999);
            }
            biLogManager.cubeTableSourceSet(dataSourceSet, -999);
            Set<BITableSourceRelationPath> pathSet = new HashSet<BITableSourceRelationPath>();
            pathSet.add(BITableSourceRelationPathTestTool.getABCPath());
            biLogManager.reLationPathSet(pathSet, -999);
            biLogManager.logEnd(-999);
            biLogManager.logVersion(-999);
            JSONObject json = biLogManager.createJSON(-999);
            JSONArray tablesJa= (JSONArray) json.get("tables");
            JSONArray errTablesJa= (JSONArray) json.get("errors");
            assertTrue(tablesJa.length()+errTablesJa.length()==dataSourceSet.size());
            JSONObject o = (JSONObject) json.get("allTableInfo");
            assertTrue(o.length()==dataSourceSet.size());
            JSONArray allRelationInfoJa= (JSONArray) json.get("allRelationInfo");
            assertTrue(allRelationInfoJa.length()==relations.size());
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }



    private RelationColumnKey getRelaionColumeKeyInfo(BITableSourceRelation relation) {
        BITableSourceRelation tableRelation = getTableRelation(relation);
        ICubeFieldSource field = tableRelation.getPrimaryField();
        List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
        relations.add(tableRelation);
        return new RelationColumnKey(field, relations);
    }

    private BITableSourceRelation getTableRelation(BITableSourceRelation relation) {
        ICubeFieldSource primaryField = relation.getPrimaryField();
        ICubeFieldSource foreignField = relation.getForeignField();
        CubeTableSource primaryTable = relation.getPrimaryTable();
        CubeTableSource foreignTable = relation.getForeignTable();
        BITableSourceRelation biTableSourceRelation = new BITableSourceRelation(primaryField, foreignField, primaryTable, foreignTable);
        return biTableSourceRelation;
    }
}
