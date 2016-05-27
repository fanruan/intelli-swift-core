package com.fr.bi.field.target.filter.general;


import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

public class GeneralORFilter extends GeneralFilter {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8809645781028497710L;
	private static String XML_TAG = "GeneralORFilter";

    /**
     * 创建过滤条件的index，用于指标过滤
     *
     * @return 分组索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {
        GroupValueIndex res = null;
        for (int i = 0, len = childs.length; i < len; i++) {
            if(childs[i] == null) {
                continue;
            }
            GroupValueIndex gvi = childs[i].createFilterIndex(dimension, target, loader, userId);
            if (res == null) {
                res = gvi;
            } else {
                res = res.OR(gvi);
            }
        }
        return res;
    }

    /**
     * 指标上加的过滤
     *
     * @param target
     * @param loader
     * @param userID
     * @return
     */
    @Override
    public GroupValueIndex createFilterIndex(BusinessTable target, ICubeDataLoader loader, long userID) {
        GroupValueIndex res = null;
        for (int i = 0, len = childs.length; i < len; i++) {
            GroupValueIndex gvi = childs[i].createFilterIndex(target, loader, userID);
            if (res == null) {
                res = gvi;
            } else {
                res = res.OR(gvi);
            }
        }
        return res;
    }
}