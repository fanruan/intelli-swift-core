package com.fr.bi.field.target.target;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.Collection;
import java.util.Map;

/**
 * TODO 代码质量
 *
 * @author Daniel
 */
public abstract class BISummaryTarget extends BIAbstractTarget {
    private static final long serialVersionUID = 6939140333031697910L;
    @BICoreField
    private TargetFilter filter;


    @Override
    public TargetFilter getTargetFilter() {
        return filter;
    }

    @Override
    public int getTargetType() {
        return DBConstant.COLUMN.NUMBER;
    }

    public void setTargetMap(Map<String, BITarget> targetMap) {
    }

    public Collection<String> getCalculateUseTargetIDs(){
        return null;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("filterValue")) {
            filter = TargetFilterFactory.parseFilter(jo.getJSONObject("filterValue"), userId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BISummaryTarget)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BISummaryTarget that = (BISummaryTarget) o;

        if (filter != null ? !ComparatorUtils.equals(filter, that.filter) : that.filter != null) {
            return false;
        }

        return true;
    }

    public TargetType getType(){
        return TargetType.NORMAL;
    }

    /**
     * hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }

    @Override
    public boolean calculateAllNode() {
        return false;
    }

    @Override
    public boolean calculateSingleNode(BITarget[] usedTargets) {
        return false;
    }

}