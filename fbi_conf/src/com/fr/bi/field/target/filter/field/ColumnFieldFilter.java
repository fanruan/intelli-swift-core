/**
 *
 */
package com.fr.bi.field.target.filter.field;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ColumnFieldFilter extends ColumnFilter {
    /**
     *
     */
    protected BusinessField dataColumn;

    public ColumnFieldFilter() {
    }

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataColumn == null) ? 0 : dataColumn.hashCode());
        result = prime * result
                + ((filterValue == null) ? 0 : filterValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        ColumnFieldFilter other = (ColumnFieldFilter) obj;
        if (dataColumn == null) {
            if (other.dataColumn != null) {
                return false;
            }

        } else if (!ComparatorUtils.equals(dataColumn, other.dataColumn)) {
            return false;
        }
        if (filterValue == null) {
            if (other.filterValue != null) {
                return false;
            }

        } else if (!ComparatorUtils.equals(filterValue, other.filterValue)) {
            return false;
        }

        return true;
    }

    /**
     * 解析json
     *
     * @param jo json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
            JSONObject fieldJo = jo.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
            dataColumn = BIModuleUtils.getBusinessFieldById(new BIFieldID(fieldJo.getString("field_id")));
        }
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
        GroupValueIndex gvi = null;
        if (filterValue != null) {
            try {
                Set<BITableRelationPath> pathSet = BICubeConfigureCenter.getTableRelationManager().getAllAvailablePath(userID, target, dataColumn.getTableBelongTo());
                if (valueJo.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
                    JSONObject srcJo = valueJo.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
                    if (srcJo.has("target_relation")) {
                        BITableRelationPath usedPath = new BITableRelationPath();
                        JSONArray selfRelationJa = srcJo.getJSONArray("target_relation");
                        for (int i = 0; i < selfRelationJa.length(); i++) {
                            BITableRelation selfRelation = BITableRelationHelper.getRelation(selfRelationJa.getJSONObject(i));
                            if (BICubeConfigureCenter.getTableRelationManager().containTableRelation(userID, selfRelation)) {
                                usedPath.addRelationAtTail(selfRelation);
                            } else {
                                break;
                            }
                        }
                        Set<BITableRelationPath> usedPathSet = new HashSet<BITableRelationPath>();
                        usedPathSet.add(usedPath);
                        pathSet = usedPathSet;
                    }
                }
                if (ComparatorUtils.equals(dataColumn.getTableBelongTo(), target) && pathSet.isEmpty()) {
                    gvi = filterValue.createFilterIndex(new NoneDimensionCalculator(dataColumn, new ArrayList<BITableSourceRelation>()), target, loader, userID);
                } else {
                    for (BITableRelationPath path : pathSet) {
                        gvi = GVIUtils.OR(gvi, filterValue.createFilterIndex(new NoneDimensionCalculator(dataColumn, BIConfUtils.convert2TableSourceRelation(path.getAllRelations())), target, loader, userID));
                    }
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return gvi;
    }

    /**
     * 创建过滤条件的index，用于指标过滤
     *
     * @return 分组索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        if (dataColumn != null && filterValue != null) {
            try {
                JSONObject srcJo = valueJo.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
                //恶心的自循环列处理
                if (ComparatorUtils.equals(dimension.getField(), dataColumn) && !srcJo.has("target_relation")) {
                    return filterValue.createFilterIndex(dimension, target, loader, userId);
                }
                return createFilterIndex(target, loader, userId);
            } catch (JSONException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }

        }
        return null;
    }
}