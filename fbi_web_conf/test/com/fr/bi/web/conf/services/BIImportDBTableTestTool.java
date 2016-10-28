package com.fr.bi.web.conf.services;

import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.stable.data.db.BIDBTableField;
import com.fr.bi.web.conf.utils.BIImportDBTableConnectionRelationTool;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by naleite on 16/3/2.
 */
public class BIImportDBTableTestTool extends BIImportDBTableConnectionRelationTool {

    Map<String, Set<BIDBTableField>> tableRelationA;
    Map<String, Set<BIDBTableField>> tableRelationB;
    Map<String, Set<BIDBTableField>> tableRelationC;

    public BIImportDBTableTestTool() {

        tableRelationA = new HashMap<String, Set<BIDBTableField>>();
        Set<BIDBTableField> fieldSetA = new HashSet<BIDBTableField>();
        fieldSetA.add(new BIDBTableField("b",null,"idb"));
        tableRelationA.put("id",fieldSetA);

        tableRelationB = new HashMap<String, Set<BIDBTableField>>();
        Set<BIDBTableField> fieldSetB = new HashSet<BIDBTableField>();
        fieldSetB.add(new BIDBTableField("c",null,"idc"));
        tableRelationB.put("idb",fieldSetB);

        tableRelationC = new HashMap<String, Set<BIDBTableField>>();
        Set<BIDBTableField> fieldSetC = new HashSet<BIDBTableField>();
        tableRelationC.put("idb",fieldSetC);

    }

    @Override
    public Map<String, DBTableSource> getAllBusinessPackDBSourceMap(long userId) {

        return new HashMap<String, DBTableSource>();
    }

    @Override
    public Map<String, Set<BIDBTableField>> getAllRelationOfConnection(Connection conn, String schemaName, String tableName) {

        if(tableName.charAt(0)=='a'){
            return tableRelationA;
        }
        else if(tableName.charAt(0)=='b'){
            return tableRelationB;
        }

        else if(tableName.charAt(0)=='c'){
            return tableRelationC;
        }
        else {
            return null;
        }

    }

    @Override
    public boolean putConnection(String connectionName, Map<String, Connection> connMap) throws Exception {
        return true;
    }


}
