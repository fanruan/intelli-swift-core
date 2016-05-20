package com.fr.bi.conf.data.source;

import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.ITableSource;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/30.
 * 生成cube用
 */
public class SingleOpratorETLTableSource extends ETLTableSource {
    private IETLOperator operator;

    public SingleOpratorETLTableSource(List<ITableSource> parents, IETLOperator operator) {
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
            PersistentTable dbTable = createBITable();
            PersistentTable[] ptables = new PersistentTable[parents.size()];
            for (int i = 0; i < ptables.length; i++) {
                ptables[i] = parents.get(i).getDbTable();
            }
            for (int i = 0; i < oprators.size(); i++) {
                PersistentTable ctable = oprators.get(i).getBITable(ptables);
                Iterator<BIColumn> it = ctable.getBIColumnIterator();
                while (it.hasNext()) {
                    BIColumn column = it.next();
                    dbTable.addColumn(column);
                }
            }
            DBField[] result = new DBField[dbTable.getColumnArray().length];

            Iterator<BIColumn> it = dbTable.getBIColumnIterator();
            int count = 0;
            while (it.hasNext()) {
                result[count++] = (it.next().toDBField(new BITable(this.getSourceID())));
            }
            return result;
        }
        return new DBField[0];

    }

}