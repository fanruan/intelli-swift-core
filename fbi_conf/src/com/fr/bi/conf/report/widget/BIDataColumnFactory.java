package com.fr.bi.conf.report.widget;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.utils.BIIDUtils;

/**
 * Created by 小灰灰 on 2016/3/15.
 */
public class BIDataColumnFactory {
    public static BIDataColumn createBIDataColumnByFieldID(String fieldId, BIUser user){
        String tableId = BIIDUtils.getTableIDFromFieldID(fieldId);
        String fieldName = BIIDUtils.getFieldNameFromFieldID(fieldId);
        IPersistentTable table =   BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(new BITableID(tableId), user).getDbTable();
        PersistentField c = table.getField(fieldName);
        if(c == null){
            return new BIDataColumn(new BIBasicField(tableId, "pony"));
        }
        return new BIDataColumn(new BIBasicField(tableId, fieldName, c.getType(), c.getColumnSize()));
    }
}
