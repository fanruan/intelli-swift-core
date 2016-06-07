package com.finebi.cube.relation;


import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceRelation extends BIBasicRelation<CubeTableSource, ICubeFieldSource> {
    public BITableSourceRelation(ICubeFieldSource primaryField, ICubeFieldSource foreignField, CubeTableSource primaryTable, CubeTableSource foreignTable) {
        super(primaryField, foreignField, primaryTable, foreignTable);
    }
}
