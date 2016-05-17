package com.fr.bi.field.target.filter.general;


import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

public class GeneralANDFilter extends GeneralFilter {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7086752683319046487L;
	private static String XML_TAG = "GeneralANDFilter";

    /**
     * 创建过滤条件的index，用于指标过滤
     *
     * @return 分组索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader, long userId) {
        GroupValueIndex res = null;
        if (childs == null){
            return res;
        }
        for (int i = 0, len = childs.length; i < len; i++) {
            if(childs[i] == null) {
                continue;
            }
            GroupValueIndex gvi = childs[i].createFilterIndex(dimension, target, loader, userId);
            if (res == null) {
                res = gvi;
            } else {
                res = res.AND(gvi);
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
    public GroupValueIndex createFilterIndex(Table target, ICubeDataLoader loader, long userID) {
        GroupValueIndex res = null;
        for (int i = 0, len = childs.length; i < len; i++) {
            GroupValueIndex gvi = childs[i].createFilterIndex(target, loader, userID);
            if (res == null) {
                res = gvi;
            } else {
                res = res.AND(gvi);
            }
        }
        return res;
    }
}