package com.fr.bi.conf.data.source.operator.add.selfrelation;

import com.fr.bi.stable.data.db.BIColumn;

/**
 * Created by User on 2016/1/14.
 */
public class UnionRelationColumn extends BIColumn {
    public UnionRelationColumn(String columnName, int type, int columnSize) {
        super(columnName, type, columnSize);
    }

    @Override
    public boolean canSetUseable() {
        return false;
    }


}