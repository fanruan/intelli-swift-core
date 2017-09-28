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
    private static final long serialVersionUID = -6975326218721468708L;

    public BITableSourceRelation(ICubeFieldSource primaryField, ICubeFieldSource foreignField, CubeTableSource primaryTable, CubeTableSource foreignTable) {
        super(primaryField, foreignField, primaryTable, foreignTable);
    }

    @Override
    public String toString() {
        return getPrimaryTable().toString() + getForeignTable().toString() + getPrimaryField().toString() + getForeignField().toString();
    }
}
