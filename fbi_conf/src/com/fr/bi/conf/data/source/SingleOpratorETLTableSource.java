package com.fr.bi.conf.data.source;

import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;

import java.util.Iterator;
import java.util.List;

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
    public DBTable getDbTable() {
        if (dbTable == null) {
            if (isAllAddColumnOperator()) {
                dbTable = createBITable();
                DBTable[] ptables = new DBTable[parents.size()];
                for (int i = 0; i < ptables.length; i ++){
                    ptables[i] = parents.get(i).getDbTable();
                }
                for (int i = 0; i < oprators.size(); i++) {
                    DBTable ctable = oprators.get(i).getBITable(ptables);
                    Iterator<BIColumn> it = ctable.getBIColumnIterator();
                    while (it.hasNext()) {
                        BIColumn column = it.next();
                        dbTable.addColumn(column);
                    }
                }
            } else {
                dbTable = super.getDbTable();
            }
        }
        return dbTable;
    }
}