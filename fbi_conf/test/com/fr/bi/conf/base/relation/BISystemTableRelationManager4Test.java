package com.fr.bi.conf.base.relation;


import com.fr.bi.stable.data.Table;

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
    protected Set<Table> getAllTables(long userId) {
        Set<Table> result = new HashSet<Table>();
        result.add(BIFieldTestTool.getAa().getTableBelongTo());
        result.add(BIFieldTestTool.getBa().getTableBelongTo());
        result.add(BIFieldTestTool.getCa().getTableBelongTo());
        result.add(BIFieldTestTool.getDa().getTableBelongTo());
        return result;
    }
}