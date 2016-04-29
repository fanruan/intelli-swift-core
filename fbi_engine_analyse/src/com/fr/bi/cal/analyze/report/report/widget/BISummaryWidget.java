package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.conf.report.widget.BIDataColumnFactory;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.dimension.BIDimensionFactory;
import com.fr.bi.field.dimension.filter.DimensionFilterFactory;
import com.fr.bi.field.target.BITargetFactory;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BISimpleRelation;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.structure.collection.map.ConcurrentCacheHashMap;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

public abstract class BISummaryWidget extends BIAbstractWidget {

    protected BISummaryTarget[] targets;
    protected BIDimension[] dimensions;
    protected NameObject targetSort;
    protected Map<String, DimensionFilter> targetFilterMap = new HashMap<String, DimensionFilter>();
    protected Map<String, Map<String, BIDataColumn>> dimensionsMap = new HashMap<String, Map<String, BIDataColumn>>();
    protected Map<String, Map<String, List<BISimpleRelation>>> relationsMap = new HashMap<String, Map<String, List<BISimpleRelation>>>();
    protected Object[] clickValue;
    protected ComplexExpander complexExpander = new ComplexExpander();
    private int maxCol = 7;     //单页最大列数
    private int maxRow = 20;    //单页最大行数

    @Override
    public BIDimension[] getDimensions() {
        return dimensions == null ? new BIDimension[0] : dimensions;
    }

    @Override
    public BISummaryTarget[] getTargets() {
        return targets == null ? new BISummaryTarget[0] : targets;
    }


    @Override
    public List<Table> getUsedTableDefine() {
        List<Table> result = new ArrayList<Table>();
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
    public List<BIField> getUsedFieldDefine() {
        List<BIField> result = new ArrayList<BIField>();
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
        Map<String, List<BISimpleRelation>> relMap = relationsMap.get(dimId);
        if (relMap == null) {
            return new ArrayList<BITableSourceRelation>();
        }
        List<BISimpleRelation> relationList = relMap.get(tarId);
        return relationList == null ? new ArrayList<BITableSourceRelation>() : BIConfUtils.convertToMD5RelationFromSimpleRelation(relationList, new BIUser(this.getUserId()));
    }

    private BIDataColumn getDimDataColumn(BIDimension dim, String tarId) {
        String dimId = dim.getValue();
        Map<String, BIDataColumn> dimMap = dimensionsMap.get(dimId);
        if (dimMap == null) {
            return dim.getStatisticElement();
        }
        BIDataColumn column = dimMap.get(tarId);
        return column == null ? dim.getStatisticElement() : column;
    }

    public DimensionCalculator createDimCalculator(BIDimension dimension, BITarget target) {
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
            this.targetSort = new NameObject(targetSort.getString("sort_target"), targetSort.getInt("type"));
        }
        if (jo.has("filter_value")) {
            JSONObject targetFilter = (JSONObject) jo.get("filter_value");
            Iterator it = targetFilter.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                JSONObject filter = targetFilter.getJSONObject(key);
                filter.put("target_id", key);
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
                    JSONObject tar = dimMap.getJSONObject(targetId);
                    Map<String, BIDataColumn> dimensionMap = new HashMap<String, BIDataColumn>();
                    dimensionsMap.put(dimensionId, dimensionMap);
                    if (tar.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
                        Object ob = tar.get(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
                        if (ob instanceof JSONObject) {
                            JSONObject j = (JSONObject) ob;
                            String fieldId = j.getString("field_id");
                            dimensionMap.put(targetId, BIDataColumnFactory.createBIDataColumnByFieldID(fieldId, new BIUser(userId)));
                        }
                    }
                    if (tar.has("target_relation")) {
                        Map<String, List<BISimpleRelation>> relationMap = new HashMap<String, List<BISimpleRelation>>();
                        relationsMap.put(dimensionId, relationMap);
                        Object t = tar.get("target_relation");
                        if (t instanceof JSONArray) {
                            JSONArray rel = (JSONArray) t;
                            int lens = rel.length();
                            List<BISimpleRelation> relationList = new ArrayList<BISimpleRelation>();
                            if (lens == 1) {
                                String primaryFieldId = rel.optJSONObject(0).optJSONObject("primaryKey").optString("field_id");
                                String foreignFieldId = rel.optJSONObject(0).optJSONObject("foreignKey").optString("field_id");
                                if (ComparatorUtils.equals(BIIDUtils.getTableIDFromFieldID(primaryFieldId), BIIDUtils.getTableIDFromFieldID(foreignFieldId))) {
                                    relationMap.put(targetId, relationList);
                                    continue;
                                }
                            }
                            for (int j = 0; j < lens; j++) {
                                BISimpleRelation relation = new BISimpleRelation();
                                relation.parseJSON(rel.optJSONObject(j));
                                relationList.add(relation);
                            }
                            relationMap.put(targetId, relationList);
                        }
                    } else {
                        BILogger.getLogger().error("error missing field:" + tar.toString() + this.getClass().getName());
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
}