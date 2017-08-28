package com.fr.bi.field.dimension.dimension;

import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.dimension.filter.DimensionFilterFactory;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.group.BIGroupFactory;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.operation.group.group.NoGroup;
import com.fr.bi.stable.operation.sort.BISortFactory;
import com.fr.bi.stable.operation.sort.BISortUtils;
import com.fr.bi.stable.operation.sort.ISort;
import com.fr.bi.stable.operation.sort.sort.NoSort;
import com.fr.bi.report.result.BINode;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class BIAbstractDimension extends BIAbstractTargetAndDimension implements BIDimension {

    private static final long serialVersionUID = -1049362619688281145L;

    @BICoreField
    protected DimensionFilter filter;

    @BICoreField
    protected ISort sort = new NoSort();

    @BICoreField
    protected IGroup group = new NoGroup();

    @BICoreField
    private String sortTarget;

    private BITableRelationPath selfToSelfRelationPath;

    /**
     * 是否显示空值
     */
    private boolean notShowNull = false;

    @Override
    public IGroup getGroup() {

        return group;
    }

    @Override
    public void setGroup(IGroup group) {

        this.group = group;
    }

    @Override
    public ISort getSort() {

        return sort;
    }

    @Override
    public int getSortType() {

        return getSort().getSortType();
    }

    @Override
    public void setSortType(int sortType) {

        this.sort.setSortType(sortType);
    }

    public int getGroupType() {

        return getGroup().getType();
    }


    @Override
    public String getSortTarget() {

        return sortTarget;
    }

    @Override
    public Object toFilterObject(Object data) {

        return data;
    }

    @Override
    public DimensionFilter getFilter() {

        return filter;
    }

    @Override
    public void setFilter(DimensionFilter filter) {

        this.filter = filter;
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

        super.parseJSON(jo, userId);
        if (jo.has("sort")) {
            JSONObject sortJo = jo.optJSONObject("sort");
            sortJo.put("type", BISortUtils.getSortTypeByDimensionType(sortJo.optInt("type", BIReportConstant.SORT.NONE), jo.optInt("type")));
            this.sort = BISortFactory.parseSort(sortJo);
            JSONObject s = jo.getJSONObject("sort");
            if (sort.getSortType() != BIReportConstant.SORT.CUSTOM && s.has("sortTarget")) {
                this.sortTarget = s.optString("sortTarget");
            }
        }
        if (jo.has("filterValue")) {
            this.filter = DimensionFilterFactory.parseFilter(jo.getJSONObject("filterValue"), userId);
        }
        if (jo.has("group")) {
            this.group = BIGroupFactory.parseGroup(jo.optJSONObject("group"));
        }
        if (jo.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
            JSONObject fieldJo = jo.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
            if (fieldJo.has("targetRelation")) {
                JSONArray relationArray = fieldJo.getJSONArray("targetRelation");
                BITableRelation[] tableRelationArray = new BITableRelation[relationArray.length()];
                for (int i = 0; i < relationArray.length(); i++) {
                    tableRelationArray[i] = BITableRelationHelper.getAnalysisRelation(relationArray.getJSONObject(i));
                }
                this.selfToSelfRelationPath = new BITableRelationPath(tableRelationArray);
            }
        }
        /**
         * 是否显示空值
         */
        if (jo.has("notShowNull")) {
            notShowNull = jo.optBoolean("notShowNull", false);
        }
    }

    @Override
    public String toString(Object v) {
        // 空值給前台的時候需要進行转换一下
        return BICollectionUtils.cubeValueToWebDisplay(v) == null ? StringUtils.EMPTY : v.toString();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof BIAbstractDimension)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BIAbstractDimension that = (BIAbstractDimension) o;

        if (filter != null ? !ComparatorUtils.equals(filter, that.filter) : that.filter != null) {
            return false;
        }
        if (sortTarget != null ? !ComparatorUtils.equals(sortTarget, that.sortTarget) : that.sortTarget != null) {
            return false;
        }
        if (group != null ? !ComparatorUtils.equals(group, that.group) : that.group != null) {
            return false;
        }
        if (sort != null ? !ComparatorUtils.equals(sort, that.sort) : that.sort != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();
        result = 31 * result + (sortTarget != null ? sortTarget.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        return result;
    }

    @Override
    public List<String> getUsedTargets() {

        List<String> res = new ArrayList<String>();
        if (this.filter != null) {
            res.addAll(this.filter.getUsedTargets());
        }
        res = res == null ? new ArrayList<String>() : res;
        if (sortTarget != null) {
            res.add(sortTarget);
        }
        res.remove(id);
        return res;
    }

    @Override
    public boolean useTargetSort() {

        return (getSortType() == BIReportConstant.SORT.ASC || getSortType() == BIReportConstant.SORT.DESC
                || getSortType() == BIReportConstant.SORT.NUMBER_ASC || getSortType() == BIReportConstant.SORT.NUMBER_DESC) && sortTarget != null && !ComparatorUtils.equals(sortTarget, id);
    }


    @Override
    public boolean showNode(BINode node, Map<String, TargetCalculator> targetsMap) {

        if (filter != null) {
            return filter.showNode(node, targetsMap, null);
        }
        return true;
    }


    @Override
    public Object getValueByType(Object data) {

        return data == null ? StringUtils.EMPTY : data.toString();
    }

    public BITableRelationPath getSelfToSelfRelationPath() {

        return selfToSelfRelationPath;
    }

    /**
     * 是否显示空值
     *
     * @return
     */
    public boolean showNullValue() {

        return !notShowNull;
    }
}