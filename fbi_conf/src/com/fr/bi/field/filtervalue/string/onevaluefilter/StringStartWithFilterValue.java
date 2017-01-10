package com.fr.bi.field.filtervalue.string.onevaluefilter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexOrHelper;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
import com.fr.bi.stable.report.result.DimensionCalculator;

public class StringStartWithFilterValue extends StringOneValueFilterValue {
    /**
     *
     */
    private static final long serialVersionUID = 8978803144186435914L;
    private static String XML_TAG = "StringStartWithFilterValue";
    @BICoreField
    private String CLASS_TYPE = "StringStartWithFilterValue";

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

        final ICubeColumnIndexReader sgm = dimension.createNoneSortNoneGroupValueMapGetter(target, loader);
        final int start = ArrayLookupHelper.getStartIndex4StartWith(sgm, value, dimension.getComparator());
        final int end = ArrayLookupHelper.getEndIndex4StartWith(sgm, value, dimension.getComparator()) + 1;
        if (start == -1) {
            return GVIFactory.createAllEmptyIndexGVI();
        }
        GroupValueIndexOrHelper orHelper = new GroupValueIndexOrHelper();
        if (end - start > sgm.sizeOfGroup() / 2){
            for (int i = 0; i < start; i ++){
                orHelper.add(sgm.getGroupValueIndex(i));
            }
            for (int i = end + 1; i < sgm.sizeOfGroup(); i ++){
                orHelper.add(sgm.getGroupValueIndex(i));
            }
            return orHelper.compute().NOT(ti.getRowCount());
        } else {
            for (int i = start; i < end; i++) {
                orHelper.add(sgm.getGroupValueIndex(i));
            }
            return orHelper.compute();
        }
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