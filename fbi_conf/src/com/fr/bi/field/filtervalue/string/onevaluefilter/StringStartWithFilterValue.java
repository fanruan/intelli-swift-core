package com.fr.bi.field.filtervalue.string.onevaluefilter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
import com.fr.bi.stable.report.result.DimensionCalculator;

public class StringStartWithFilterValue extends StringOneValueFilterValue {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8978803144186435914L;
	private static String XML_TAG = "StringStartWithFilterValue";

    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        ICubeTableService ti = loader.getTableIndex(target.getTableSource());
        if (value == null || value.isEmpty()) {
            return ti.getAllShowIndex();
        }

        ICubeColumnIndexReader sgm = dimension.createNoneSortNoneGroupValueMapGetter(target, loader);
        int start = ArrayLookupHelper.getStartIndex4StartWith(sgm, value, dimension.getComparator());
        int end = ArrayLookupHelper.getEndIndex4StartWith(sgm, value, dimension.getComparator()) + 1;
        if (start == -1){
            return GVIFactory.createAllEmptyIndexGVI();
        }
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        for (int i = start; i < end; i ++){
            gvi.or(sgm.getGroupValueIndex(i));
        }
        return gvi;
    }

    /**
     * 是否包含
     *
     * @param key 字段
     * @return 包含
     */
    @Override
    public boolean isMatchValue(String key) {
        return key.startsWith(value);
    }

}