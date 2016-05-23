package com.finebi.cube.conf.relation;


import com.finebi.cube.conf.table.IBusinessTable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Connery on 2016/1/28.
 */
public class BISystemTableRelationManager4Test extends BISystemTableRelationManager {
    //    @Override
    protected void initialUserManager(BIUserTableRelationManager biUserTableRelationManager) {

    }

    @Override
    protected Set<IBusinessTable> getAllTables(long userId) {
        Set<IBusinessTable> result = new HashSet<IBusinessTable>();
        result.add(BIFieldTestTool.getAa().getTableBelongTo());
        result.add(BIFieldTestTool.getBa().getTableBelongTo());
        result.add(BIFieldTestTool.getCa().getTableBelongTo());
        result.add(BIFieldTestTool.getDa().getTableBelongTo());
        return result;
    }
}