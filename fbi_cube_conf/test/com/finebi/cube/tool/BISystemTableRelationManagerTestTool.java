package com.finebi.cube.tool;


import com.finebi.cube.conf.relation.BISystemTableRelationManager;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.tool.BIFieldTestTool;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Connery on 2016/1/28.
 */
public class BISystemTableRelationManagerTestTool extends BISystemTableRelationManager {

    @Override
    protected Set<BusinessTable> getAllTables(long userId) {
        Set<BusinessTable> result = new HashSet<BusinessTable>();
        result.add(BIFieldTestTool.getAa().getTableBelongTo());
        result.add(BIFieldTestTool.getBa().getTableBelongTo());
        result.add(BIFieldTestTool.getCa().getTableBelongTo());
        result.add(BIFieldTestTool.getDa().getTableBelongTo());
        return result;
    }
}