/**
 * 
 */
package com.fr.bi.field.filtervalue.string.onevaluefilter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
import com.fr.bi.stable.report.result.DimensionCalculator;

/**
 * @author Daniel
 *
 */
public class StringNotStartWithFilterValue extends StringOneValueFilterValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674486222319107741L;

    @BICoreField
    private String CLASS_TYPE = "StringNotStartWithFilterValue";
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        ICubeTableService ti = loader.getTableIndex(target.getTableSource());
        if (value == null || value.isEmpty()) {
            return ti.getAllShowIndex();
        }

        ICubeColumnIndexReader sgm = dimension.createNoneSortNoneGroupValueMapGetter(target, loader);
        int start = ArrayLookupHelper.getStartIndex4StartWith(sgm, value, dimension.getComparator());
        int end = ArrayLookupHelper.getEndIndex4StartWith(sgm, value, dimension.getComparator());
        if (start == -1){
            return ti.getAllShowIndex();
        }
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        for (int i = 0; i < start; i ++){
            gvi.or(sgm.getGroupValueIndex(i));
        }
        for (int i = end + 1; i < sgm.sizeOfGroup(); i ++){
            gvi.or(sgm.getGroupValueIndex(i));
        }
        return gvi;
    }

	/* (non-Javadoc)
	 * @see com.fr.bi.cal.analyze.report.report.widget.field.filtervalue.string.onevaluefilter.StringOneValueFilterValue#contains(java.lang.String)
	 */
	@Override
	public boolean isMatchValue(String key) {
		return !key.startsWith(value);
	}

}