package com.fr.bi.conf.data.source;

import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.ITableSource;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/30.
 * 生成cube用
 */
public class SingleOperatorETLTableSource extends ETLTableSource {
    private IETLOperator operator;

    public SingleOperatorETLTableSource(List<ITableSource> parents, IETLOperator operator) {
        this.operator = operator;
        this.parents = parents;
        oprators.add(operator);
        fetchObjectCore();
    }

    @Override
    public DBField[] getFieldsArray(Set<ITableSource> sources) {
        if (isAllAddColumnOperator()) {
            return getAddedField();
        } else {
            return super.getFieldsArray(sources);
        }
    }

    public DBField[] getAddedField() {
        if (isAllAddColumnOperator()) {
            IPersistentTable dbTable = createBITable();
            IPersistentTable[] ptables = new IPersistentTable[parents.size()];
            for (int i = 0; i < ptables.length; i++) {
                ptables[i] = parents.get(i).getDbTable();
            }
            for (int i = 0; i < oprators.size(); i++) {
                IPersistentTable ctable = oprators.get(i).getBITable(ptables);
                Iterator<PersistentField> it = ctable.getFieldList().iterator();
                while (it.hasNext()) {
                    PersistentField column = it.next();
                    dbTable.addColumn(column);
                }
            }
            DBField[] result = new DBField[dbTable.getFieldSize()];

            Iterator<PersistentField> it = dbTable.getFieldList().iterator();
            int count = 0;
            while (it.hasNext()) {
                result[count++] = (it.next().toDBField(new BITable(this.getSourceID())));
            }
            return result;
        }
        return new DBField[0];

    }

}