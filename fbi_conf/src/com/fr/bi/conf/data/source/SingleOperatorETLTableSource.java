package com.fr.bi.conf.data.source;

import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/30.
 * 生成cube用
 */
public class SingleOperatorETLTableSource extends ETLTableSource {
    private IETLOperator operator;

    public SingleOperatorETLTableSource(List<CubeTableSource> parents, IETLOperator operator) {
        this.operator = operator;
        this.parents = parents;
        oprators.add(operator);
        fetchObjectCore();
    }

    @Override
    public ICubeFieldSource[] getFieldsArray(Set<CubeTableSource> sources) {
        if (isAllAddColumnOperator()) {
            return getAddedField();
        } else {
            return super.getFieldsArray(sources);
        }
    }

    public BICubeFieldSource[] getAddedField() {
        if (isAllAddColumnOperator()) {
            IPersistentTable dbTable = createBITable();
            IPersistentTable[] ptables = new IPersistentTable[parents.size()];
            for (int i = 0; i < ptables.length; i++) {
                ptables[i] = parents.get(i).getPersistentTable();
            }
            for (int i = 0; i < oprators.size(); i++) {
                IPersistentTable ctable = oprators.get(i).getBITable(ptables);
                Iterator<PersistentField> it = ctable.getFieldList().iterator();
                while (it.hasNext()) {
                    PersistentField column = it.next();
                    dbTable.addColumn(column);
                }
            }
            BICubeFieldSource[] result = new BICubeFieldSource[dbTable.getFieldSize()];

            Iterator<PersistentField> it = dbTable.getFieldList().iterator();
            int count = 0;
            while (it.hasNext()) {
                result[count++] = (it.next().toDBField(this));
            }
            return result;
        }
        return new BICubeFieldSource[0];

    }

}