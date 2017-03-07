package com.finebi.cube.structure;

import com.finebi.cube.relation.BIBasicRelation;
import com.finebi.cube.structure.column.BIColumnKey;


/**
 * This class created on 2016/4/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelation extends BIBasicRelation<ITableKey, BIColumnKey> {
    private static final long serialVersionUID = 5327046609553351146L;

    public BICubeRelation() {
    }

    public BICubeRelation(BIColumnKey primaryField, BIColumnKey foreignField, ITableKey primaryTable, ITableKey foreignTable) {
        super(primaryField, foreignField, primaryTable, foreignTable);
    }



}
