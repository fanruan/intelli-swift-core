package com.finebi.cube.conf.table;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.AbstractTableSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2016/5/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BusinessTableHelper {
    public static List<BusinessField> getTableFields(BusinessTable table) {
        List<BusinessField> fields = table.getFields();
        if (fields == null) {
            fields = new ArrayList<BusinessField>();
            Iterator<Map.Entry<String, ICubeFieldSource>> it = ((AbstractTableSource) table.getTableSource()).getFields().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, ICubeFieldSource> entry = it.next();
                ICubeFieldSource fieldSource = entry.getValue();
                BIFieldID fieldID = new BIFieldID(java.util.UUID.randomUUID().toString());
                BusinessField field = BIFactoryHelper.getObject(BusinessField.class, table, fieldID,
                        fieldSource.getFieldName(), fieldSource.getClassType(), fieldSource.getFieldSize());
                fields.add(field);
            }
            table.setFields(fields);
        }
        return fields;
    }

    public static BusinessTable getBusinessTable(BITableID tableID) {

    }
}
