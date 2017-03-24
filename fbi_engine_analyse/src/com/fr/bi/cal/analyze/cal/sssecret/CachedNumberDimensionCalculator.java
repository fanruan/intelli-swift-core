package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.field.dimension.calculator.NumberDimensionCalculator;

import java.util.List;

/**
 * Created by 小灰灰 on 2017/3/22.
 */
public class CachedNumberDimensionCalculator extends NumberDimensionCalculator{
    private ICubeColumnIndexReader cache;
    public CachedNumberDimensionCalculator(NumberDimensionCalculator numberDimensionCalculator, List<BITableSourceRelation> relations) {
        super(numberDimensionCalculator.getDimension(), numberDimensionCalculator.getField(), relations);
    }

    @Override
    public ICubeColumnIndexReader createNoneSortGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader) {
        if (cache == null){
            cache = super.createNoneSortGroupValueMapGetter(target, loader);
        }
        return cache;
    }
}
