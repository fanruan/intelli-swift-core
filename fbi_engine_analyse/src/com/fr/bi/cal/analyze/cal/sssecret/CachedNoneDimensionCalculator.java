package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;

import java.util.List;

/**
 * Created by 小灰灰 on 2017/3/22.
 */
public class CachedNoneDimensionCalculator extends NoneDimensionCalculator{
    private ICubeColumnIndexReader cache;
    public CachedNoneDimensionCalculator(BusinessField column, List<BITableSourceRelation> relations) {
        super(column, relations);
    }


    @Override
    public ICubeColumnIndexReader createNoneSortNoneGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader) {
        if (cache == null){
            cache = super.createNoneSortNoneGroupValueMapGetter(target, loader);
        }
        return cache;
    }
}
