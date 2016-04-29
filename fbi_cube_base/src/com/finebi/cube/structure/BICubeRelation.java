package com.finebi.cube.structure;

import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.stable.relation.BIBasicRelation;

/**
 * This class created on 2016/4/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelation extends BIBasicRelation<ITableKey, BIColumnKey> {
    public BICubeRelation() {
    }

    public BICubeRelation(BIColumnKey primaryField, BIColumnKey foreignField, ITableKey primaryTable, ITableKey foreignTable) {
        super(primaryField, foreignField, primaryTable, foreignTable);
    }



}
