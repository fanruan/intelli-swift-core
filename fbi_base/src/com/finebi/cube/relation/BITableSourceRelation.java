package com.finebi.cube.relation;


import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceRelation extends BIBasicRelation<CubeTableSource, CubeFieldSource> {
    public BITableSourceRelation(CubeFieldSource primaryField, CubeFieldSource foreignField, CubeTableSource primaryTable, CubeTableSource foreignTable) {
        super(primaryField, foreignField, primaryTable, foreignTable);
    }
}
