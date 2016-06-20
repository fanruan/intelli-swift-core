package com.fr.bi.conf.data.source.operator.add.selfrelation;

import com.fr.bi.stable.data.db.PersistentField;

/**
 * Created by User on 2016/1/14.
 */
public class UnionRelationPersistentField extends PersistentField {
    public UnionRelationPersistentField(String columnName, int type, int columnSize) {
        super(columnName, type, columnSize);
    }

    @Override
    public boolean canSetUsable() {
        return false;
    }


}