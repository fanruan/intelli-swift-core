package com.fr.bi.web.conf.utils;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.BIDBTableField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.file.DatasourceManager;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by naleite on 16/3/1.
 */
public class BIImportDBTableConnectionRelationTool {


    public Map<String, DBTableSource> getAllBusinessPackDBSourceMap(long userId) {
        Map<String, DBTableSource> sources = new HashMap<String, DBTableSource>();
        for (IBusinessPackageGetterService pack : BICubeConfigureCenter.getPackageManager().getAllPackages(userId)) {
            for (Object table : pack.getBusinessTables()) {
                BITableID id = ((BIBusinessTable) table).getID();
                CubeTableSource source = null;

                source = BusinessTableHelper.getTableDataSource(id);

                if (source != null && source.getType() == BIBaseConstant.TABLETYPE.DB) {
                    sources.put(id.getIdentityValue(), (DBTableSource) source);
                }
            }
        }
        return sources;
    }



    public Map<String, Set<BIDBTableField>> getAllRelationOfConnection(Connection conn, String schemaName, String tableName) {
        return BIDBUtils.getAllRelationOfConnection(conn, schemaName, tableName);
    }

    public boolean putConnection(String connectionName, Map<String, java.sql.Connection> connMap) throws Exception {
        if (!connMap.containsKey(connectionName)) {
            com.fr.data.impl.Connection dbc = DatasourceManager.getInstance().getConnection(connectionName);
            if (dbc == null) {
                return false;
            }
            connMap.put(connectionName, dbc.createConnection());

        }
        return true;
    }


}
