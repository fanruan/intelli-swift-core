package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.dimension.BIDimensionFactory;
import com.fr.bi.field.dimension.filter.DimensionFilterFactory;
import com.fr.bi.field.target.BITargetFactory;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.structure.collection.map.ConcurrentCacheHashMap;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

public abstract class BISummaryWidget extends BIAbstractWidget {
    @BICoreField
    protected BISummaryTarget[] targets;
    @BICoreField
    protected BIDimension[] dimensions;
    @BICoreField
    protected TargetSort targetSort;
    @BICoreField
    protected Map<String, DimensionFilter> targetFilterMap = new LinkedHashMap<String, DimensionFilter>();
    @BICoreField
    protected Map<String, Map<String, BusinessField>> dimensionsMap = new LinkedHashMap<String, Map<String, BusinessField>>();
    @BICoreField
    protected Map<String, Map<String, List<BITableRelation>>> relationsMap = new LinkedHashMap<String, Map<String, List<BITableRelation>>>();
    @BICoreField
    protected Map<String, Map<String, List<BITableRelation>>> directToDimensionRelationsMap = new LinkedHashMap<String, Map<String, List<BITableRelation>>>();
    protected Object[] clickValue;

    protected ComplexExpander complexExpander = new ComplexExpander();
    private int maxCol = 7;     //单页最大列数
    private int maxRow = 20;    //单页最大行数
    private int dimensionRelationIndex = 1;
    private int targetRelationIndex = 0;

    @Override
    public BIDimension[] getDimensions() {
        return dimensions == null ? new BIDimension[0] : dimensions;
    }

    @Override
    public BISummaryTarget[] getTargets() {
        return targets == null ? new BISummaryTarget[0] : targets;
    }


    @Override
    public List<BusinessTable> getUsedTableDefine() {
        List<BusinessTable> result = new ArrayList<BusinessTable>();
        BIDimension[] dimensions = getDimensions();
        for (int i = 0; i < dimensions.length; i++) {
            result.add(dimensions[i].getStatisticElement().getTableBelongTo());
        }
        BISummaryTarget[] targets = getTargets();
        for (int i = 0; i < targets.length; i++) {
            BISummaryTarget st = targets[i];
            result.add(st.getStatisticElement().getTableBelongTo());
        }
        return result;
    }

    public void setPageSpinner(int index, int value) {
    }

    @Override
    public List<BusinessField> getUsedFieldDefine() {
        List<BusinessField> result = new ArrayList<BusinessField>();
        BIDimension[] dimensions = getDimensions();
        for (int i = 0; i < dimensions.length; i++) {
            result.add(dimensions[i].getStatisticElement());
        }
        BISummaryTarget[] targets = getTargets();
        for (int i = 0; i < targets.length; i++) {
            BISummaryTarget st = targets[i];
            result.add(st.getStatisticElement());
        }
        return result;
    }

    private List<BITableSourceRelation> getDimRelations(String dimId, String tarId) {
        Map<String, List<BITableRelation>> relMap = relationsMap.get(dimId);
        if (relMap == null) {
            return new ArrayList<BITableSourceRelation>();
        }
        List<BITableRelation> relationList = relMap.get(tarId);
        checkRelationExist(relationList, dimId, tarId);
        return relationList == null ? new ArrayList<BITableSourceRelation>() : BIConfUtils.convert2TableSourceRelation(relationList);
    }

    private List<BITableSourceRelation> getDimDirectToDimensionRelations(String dimId, String tarId) {
        Map<String, List<BITableRelation>> relMap = directToDimensionRelationsMap.get(dimId);
        if (relMap == null) {
            return new ArrayList<BITableSourceRelation>();
        }
        List<BITableRelation> relationList = relMap.get(tarId);
        return relationList == null ? new ArrayList<BITableSourceRelation>() : BIConfUtils.convert2TableSourceRelation(relationList);
    }


    private void checkRelationExist(List<BITableRelation> relationList, String did, String tarId) {
        BITarget target = BITravalUtils.getTargetByName(tarId, targets);
        if (relationList != null && !relationList.isEmpty()) {
            for (int i = 0; i < relationList.size(); i++) {
                BITableRelation r = relationList.get(i);
                if (i == relationList.size() - 1 && !ComparatorUtils.equals(r.getForeignTable().getTableSource(), target.getStatisticElement().getTableBelongTo().getTableSource())) {
                    throw new RuntimeException("relation illegal, incorrect foreignTable");
                }
                if (!BICubeConfigureCenter.getTableRelationManager().containTableRelationship(getUserId(), r)) {
                    throw new RuntimeException("relation not exist");
                }
            }
        } else {
            Map<String, List<BITableRelation>> directToDimRelMap = directToDimensionRelationsMap.get(did);
            if (directToDimRelMap != null && directToDimRelMap.get(tarId) == null) {
                BIDimension dim = BITravalUtils.getTargetByName(did, dimensions);
                BusinessField dimField = getDimDataColumn(dim, tarId);
                if (!ComparatorUtils.equals(target.getStatisticElement().getTableBelongTo().getTableSource(), dimField.getTableBelongTo().getTableSource())) {
                    throw new RuntimeException("relation empty, but source different");
                }
            }
        }
    }

    private BusinessField getDimDataColumn(BIDimension dim, String tarId) {
        String dimId = dim.getValue();
        Map<String, BusinessField> dimMap = dimensionsMap.get(dimId);
        if (dimMap == null) {
            return dim.getStatisticElement();
        }
        BusinessField column = dimMap.get(tarId);
        return column == null ? dim.getStatisticElement() : column;
    }

    public DimensionCalculator createDimCalculator(BIDimension dimension, BITarget target) {
        //多对多
        if (getDimDirectToDimensionRelations(dimension.getValue(), target.getValue()).size() > 0) {
            return dimension.createCalculator(getDimDataColumn(dimension, target.getValue()), getDimRelations(dimension.getValue(), target.getValue()), getDimDirectToDimensionRelations(dimension.getValue(), target.getValue()));
        }
        return dimension.createCalculator(getDimDataColumn(dimension, target.getValue()), getDimRelations(dimension.getValue(), target.getValue()));
    }

    public NameObject getTargetSort() {
        return targetSort;
    }

    /**
     * 指标排序
     *
     * @return true或false
     */
    public boolean useTargetSort() {
        return getTargetSort() != null;
    }

    public Map<String, DimensionFilter> getTargetFilterMap() {
        return targetFilterMap;
    }


    /**
     * 转成json
     *
     * @param jo jsonobject对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        JSONObject dimAndTar = jo.optJSONObject("dimensions");
        if (dimAndTar == null) {
            dimAndTar = new JSONObject();
        }
        JSONObject view = jo.optJSONObject("view");
        if (view == null) {
            view = new JSONObject();
        }
        Iterator<String> regions = view.keys();
        JSONArray dimensionIds = new JSONArray();
        JSONArray targetIds = new JSONArray();
        while (regions.hasNext()) {
            String region = regions.next();
            if (ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION1) ||
                    ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION2)) {
                for (int i = 0; i < view.getJSONArray(region).length(); i++) {
                    dimensionIds.put(view.getJSONArray(region).getString(i));
                }
            } else {
                for (int i = 0; i < view.getJSONArray(region).length(); i++) {
                    targetIds.put(view.getJSONArray(region).getString(i));
                }
            }
        }
        List<BIDimension> dims = new ArrayList<BIDimension>();
        for (int i = 0; i < dimensionIds.length(); i++) {
            JSONObject dimJo = dimAndTar.getJSONObject(dimensionIds.getString(i));
            dimJo.put("did", dimensionIds.getString(i));
            dims.add(BIDimensionFactory.parseDimension(dimJo, userId));
        }
        this.dimensions = dims.toArray(new BIDimension[dims.size()]);
        List<BISummaryTarget> tars = new ArrayList<BISummaryTarget>();
        Map<String, TargetGettingKey> targetMap = new ConcurrentCacheHashMap<String, TargetGettingKey>();
        for (int j = 0; j < targetIds.length(); j++) {
            JSONObject tarJo = dimAndTar.getJSONObject(targetIds.getString(j));
            tarJo.put("did", targetIds.getString(j));
            BISummaryTarget target = BITargetFactory.parseTarget(tarJo, userId);
            if (target != null) {
                tars.add(target);
                targetMap.put(target.getValue(), target.createSummaryCalculator().createTargetGettingKey());
                target.setTargetMap(targetMap);
            }
        }
        this.targets = tars.toArray(new BISummaryTarget[tars.size()]);
        this.parseSortFilter(jo, userId);
        parseSettingMap(jo);
        parseDimensionMap(dimAndTar, userId);
    }

    @Override
    public GroupValueIndex createFilterGVI(DimensionCalculator[] row, BusinessTable targetKey, ICubeDataLoader loader, long userId) {
        GroupValueIndex gvi = super.createFilterGVI(row, targetKey, loader, userId);
        for (DimensionCalculator r : row) {
            GroupValueIndex n = r.createNoneSortNoneGroupValueMapGetter(targetKey, loader).getNULLIndex();
            if (n.getRowsCountWithData() != 0){
                gvi = GVIUtils.AND(gvi, n.NOT(loader.getTableIndex(targetKey.getTableSource()).getRowCount()));
            }
        }
        return gvi;
    }

    private void parseSettingMap(JSONObject jo) throws Exception {
        if (jo.has("settings")) {
            JSONObject settings = jo.getJSONObject("settings");
            if (settings.has("max_row")) {
                this.maxRow = settings.getInt("max_row");
            }
            if (settings.has("max_col")) {
                this.maxCol = settings.getInt("max_col");
            }
        }
    }

    private void parseSortFilter(JSONObject jo, long userId) throws Exception {
        if (jo.has("sort")) {
            JSONObject targetSort = (JSONObject) jo.get("sort");
            int sortType = targetSort.getInt("type");
            this.targetSort = new TargetSort(targetSort.getString("sort_target"), sortType);
            if (sortType == BIReportConstant.SORT.NONE) {
                this.targetSort = null;
            }
        }
        if (jo.has("filter_value")) {
            JSONObject targetFilter = jo.getJSONObject("filter_value");
            Iterator it = targetFilter.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                JSONObject filter = targetFilter.getJSONObject(key);
                targetFilterMap.put(key, DimensionFilterFactory.parseFilter(filter, userId));
            }
        }
    }

    private void parseDimensionMap(JSONObject jo, long userId) throws Exception {
        Iterator it = jo.keys();
        while (it.hasNext()) {
            String dimensionId = (String) it.next();
            JSONObject dims = jo.getJSONObject(dimensionId);
            if (dims.has("dimension_map")) {
                JSONObject dimMap = dims.getJSONObject("dimension_map");
                Iterator iterator = dimMap.keys();
                while (iterator.hasNext()) {
                    String targetId = (String) iterator.next();
                    JSONObject targetRelationJo = dimMap.getJSONObject(targetId);
                    Map<String, BusinessField> dimensionMap = new LinkedHashMap<String, BusinessField>();
                    dimensionsMap.put(dimensionId, dimensionMap);
                    if (targetRelationJo.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
                        JSONObject srcJo = targetRelationJo.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
                        String fieldId = srcJo.getString("field_id");
                        dimensionMap.put(targetId, BIModuleUtils.getBusinessFieldById(new BIFieldID(fieldId)));
                    }
                    if (targetRelationJo.has("target_relation")) {
                        Map<String, List<BITableRelation>> relationMap = relationsMap.get(dimensionId);
                        if (relationMap == null) {
                            relationMap = new LinkedHashMap<String, List<BITableRelation>>();
                            relationsMap.put(dimensionId, relationMap);
                        }
                        JSONArray dimensionAndTargetPathsJa = targetRelationJo.getJSONArray("target_relation");
                        List<BITableRelation> relationList = new ArrayList<BITableRelation>();
                        //指标的关联关系
                        JSONArray targetRelationsJa = dimensionAndTargetPathsJa.getJSONArray(targetRelationIndex);
                        JSONObject primaryKeyJo = targetRelationsJa.optJSONObject(0).optJSONObject("primaryKey");
                        JSONObject foreignKeyJo = targetRelationsJa.optJSONObject(0).optJSONObject("foreignKey");
                        String primaryFieldId = primaryKeyJo.optString("field_id");
                        String foreignFieldId = foreignKeyJo.optString("field_id");
                        String primaryTableId = primaryKeyJo.has("table_id") ? primaryKeyJo.getString("table_id") : null;
                        String foreignTableId = foreignKeyJo.has("table_id") ? foreignKeyJo.getString("table_id") : null;

                        JSONObject srcJo = dims.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
                        if (srcJo.has("target_relation")) {
                            JSONArray selfRelationJa = srcJo.getJSONArray("target_relation");
                            for (int i = 0; i < selfRelationJa.length(); i++) {
                                BITableRelation selfRelation = BITableRelationHelper.getRelation(selfRelationJa.getJSONObject(i));
                                if (BICubeConfigureCenter.getTableRelationManager().containTableRelation(userId, selfRelation)) {
                                    relationList.add(selfRelation);
                                }
                            }

                        }
                        if (primaryTableId != null && foreignTableId != null) {
                            if (ComparatorUtils.equals(BIModuleUtils.getBusinessTableById(new BITableID(primaryTableId)), BIModuleUtils.getBusinessTableById(new BITableID(foreignTableId)))) {
                                relationMap.put(targetId, relationList);
                            } else {
                                for (int j = 0; j < targetRelationsJa.length(); j++) {
                                    relationList.add(BITableRelationHelper.getRelation(targetRelationsJa.optJSONObject(j)));
                                }
                                relationMap.put(targetId, relationList);
                            }
                        } else {
                            if (ComparatorUtils.equals(BIModuleUtils.getBusinessFieldById(new BIFieldID(primaryFieldId)).getTableBelongTo(), BIModuleUtils.getBusinessFieldById(new BIFieldID(foreignFieldId)).getTableBelongTo())) {
                                relationMap.put(targetId, relationList);
                            } else {
                                for (int j = 0; j < targetRelationsJa.length(); j++) {
                                    relationList.add(BITableRelationHelper.getRelation(targetRelationsJa.optJSONObject(j)));
                                }
                                relationMap.put(targetId, relationList);
                            }
                        }


                        //多对多时纬度的关联关系
                        if (dimensionAndTargetPathsJa.length() > 1) {
                            Map<String, List<BITableRelation>> dimensionRelationMap = directToDimensionRelationsMap.get(dimensionId);
                            if (dimensionRelationMap == null) {
                                dimensionRelationMap = new LinkedHashMap<String, List<BITableRelation>>();
                                directToDimensionRelationsMap.put(dimensionId, dimensionRelationMap);
                            }
                            List<BITableRelation> dimensionRelationList = new ArrayList<BITableRelation>();
                            JSONArray dimensionRelationsJa = dimensionAndTargetPathsJa.getJSONArray(dimensionRelationIndex);
                            for (int j = 0; j < dimensionRelationsJa.length(); j++) {
                                dimensionRelationList.add(BITableRelationHelper.getRelation(dimensionRelationsJa.optJSONObject(j)));
                            }
                            dimensionRelationMap.put(targetId, dimensionRelationList);
                        }
                    } else {
                        BILogger.getLogger().error("error missing field:" + targetRelationJo.toString() + this.getClass().getName());
                    }
                }
            }
        }
    }

    /**
     * 注释
     *
     * @return 注释
     */
    public TargetGettingKey[] getTargetsKey() {
        BISummaryTarget[] targets = getViewTargets();
        TargetGettingKey[] keys = new TargetGettingKey[targets.length];
        for (int i = 0; i < targets.length; i++) {
            keys[i] = targets[i].createSummaryCalculator().createTargetGettingKey();
        }
        return keys;
    }

    public Object[] getClickValue() {
        return clickValue;
    }

    public int getMaxCol() {
        return this.maxCol;
    }

    public int getMaxRow() {
        return this.maxRow;
    }

    private class TargetSort extends NameObject {
        public TargetSort(String s, Object o) {
            super(s, o);
        }

        @Override
        public String toString() {
            return "TargetSort{" +
                    "name='" + getName() + '\'' +
                    ", ob=" + getObject() +
                    '}';
        }
    }
}