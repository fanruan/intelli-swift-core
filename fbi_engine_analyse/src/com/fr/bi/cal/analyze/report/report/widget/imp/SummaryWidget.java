package com.fr.bi.cal.analyze.report.report.widget.imp;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
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
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.AllShowRoaringGroupValueIndex;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.collections.array.IntArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SummaryWidget extends AbstractBIWidget {

    private static final long serialVersionUID = -4264115812022703958L;

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

    private int maxRow = PagingFactory.PAGE_PER_GROUP_20;    //单页最大行数

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
            if (dimensions[i].getStatisticElement() != null) {
                result.add(dimensions[i].getStatisticElement().getTableBelongTo());
            }
        }
        BISummaryTarget[] targets = getTargets();
        for (int i = 0; i < targets.length; i++) {
            BISummaryTarget st = targets[i];
            if (st.getStatisticElement() != null) {
                result.add(st.getStatisticElement().getTableBelongTo());
            }
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
                    throw BINonValueUtils.beyondControl(BIStringUtils.append("relation not exist \n",
                                                                             "the relation: ", logRelation(r)));
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

    private String logRelation(BITableRelation relation) {

        try {
            CubeTableSource primaryTableSource = BusinessTableHelper.getAnalysisBusinessTable(relation.getPrimaryTable().getID()).getTableSource();
            CubeTableSource foreignTableSource = BusinessTableHelper.getAnalysisBusinessTable(relation.getForeignTable().getID()).getTableSource();
            return BIStringUtils.append(
                    " Primary Table:" + primaryTableSource.getTableName() + " " + primaryTableSource.getSourceID(),
                    ",primary field :" + relation.getPrimaryField().getFieldName(),
                    ",foreign table:" + foreignTableSource.getTableName() + " " + foreignTableSource.getSourceID(),
                    ",foreign filed:" + relation.getForeignField().getFieldName());
        } catch (Exception e) {
            BILoggerFactory.getLogger(BITableRelation.class).error(e.getMessage(), e);
            try {
                return BIStringUtils.append("relation not exist,",
                                            "the relation:Primary Table:" + relation.getPrimaryTable().getTableName(),
                                            ",primary field :" + relation.getPrimaryField().getFieldName(),
                                            ",foreign table:" + relation.getForeignTable().getTableName(),
                                            ",foreign filed:" + relation.getForeignField().getFieldName());
            } catch (Exception innerException) {
                BILoggerFactory.getLogger(BITableRelation.class).error(innerException.getMessage(), innerException);

                return "";

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
            int regionValue = Integer.parseInt(region);
            if (regionValue >= Integer.parseInt(BIReportConstant.REGION.DIMENSION1) && regionValue < Integer.parseInt(BIReportConstant.REGION.TARGET1)) {
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
            dimJo.put("dId", dimensionIds.getString(i));
            dims.add(BIDimensionFactory.parseDimension(dimJo, userId));
        }
        this.dimensions = dims.toArray(new BIDimension[dims.size()]);
        List<BISummaryTarget> tars = new ArrayList<BISummaryTarget>();
        for (int j = 0; j < targetIds.length(); j++) {
            JSONObject tarJo = dimAndTar.getJSONObject(targetIds.getString(j));
            tarJo.put("did", targetIds.getString(j));
            tarJo.put("dId", targetIds.getString(j));
            BISummaryTarget target = BITargetFactory.parseTarget(tarJo, userId);
            if (target != null) {
                tars.add(target);
            }
        }
        initTargets(tars);
        this.parseSortFilter(jo, userId);
        parseSettingMap(jo);
        parseDimensionMap(dimAndTar, userId);
    }

    private void initTargets(List<BISummaryTarget> tars) {

        this.targets = tars.toArray(new BISummaryTarget[tars.size()]);
        Map<String, BITarget> targetMap = new ConcurrentHashMap<String, BITarget>();
        for (int i = 0; i < targets.length; i++) {
            targets[i].setSummaryIndex(i);
            targetMap.put(targets[i].getValue(), targets[i]);
            targets[i].setTargetMap(targetMap);
        }
    }

    @Override
    public GroupValueIndex createFilterGVI(DimensionCalculator[] row, BusinessTable targetKey, ICubeDataLoader loader, long userId) {

        GroupValueIndex gvi = super.createFilterGVI(row, targetKey, loader, userId);
        for (DimensionCalculator r : row) {
            if (r.getDirectToDimensionRelationList().isEmpty() && !r.getRelationList().isEmpty()) {
                try {
                    GroupValueIndex n = loader.getTableIndex(r.getField().getTableBelongTo().getTableSource()).ensureBasicIndex(r.getRelationList()).getNullIndex();
                    if (n.getRowsCountWithData() != 0) {
                        gvi = GVIUtils.AND(gvi, n.NOT(loader.getTableIndex(targetKey.getTableSource()).getRowCount()));
                    }
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error("relation " + r.getRelationList().toString() + " nullindex missed " + "direct relation is " + r.getDirectToDimensionRelationList(), e);
                }
            }
        }
        return gvi;
    }

    private void parseSettingMap(JSONObject jo) throws Exception {

        if (jo.has("settings")) {
            JSONObject settings = jo.getJSONObject("settings");
            if (settings.has("maxRow")) {
                this.maxRow = settings.getInt("maxRow");
            }
            if (settings.has("maxCol")) {
                this.maxCol = settings.getInt("maxCol");
            }
        }
    }

    private void parseSortFilter(JSONObject jo, long userId) throws Exception {

        if (jo.has("sort")) {
            JSONObject targetSort = (JSONObject) jo.get("sort");
            if (targetSort.has("type") && targetSort.has("sortTarget")) {
                int sortType = targetSort.getInt("type");
                this.targetSort = new TargetSort(targetSort.getString("sortTarget"), sortType);
                if (sortType == BIReportConstant.SORT.NONE) {
                    this.targetSort = null;
                }
            }
        }
        if (jo.has("filterValue")) {
            JSONObject targetFilter = jo.getJSONObject("filterValue");
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
            if (dims.has("dimensionMap")) {
                JSONObject dimMap = dims.getJSONObject("dimensionMap");
                Iterator iterator = dimMap.keys();
                while (iterator.hasNext()) {
                    String targetId = (String) iterator.next();
                    JSONObject targetRelationJo = dimMap.getJSONObject(targetId);
                    if (targetRelationJo.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
                        Map<String, BusinessField> dimensionMap = dimensionsMap.get(dimensionId);
                        if (dimensionMap == null) {
                            dimensionMap = new LinkedHashMap<String, BusinessField>();
                            dimensionsMap.put(dimensionId, dimensionMap);
                        }
                        JSONObject srcJo = targetRelationJo.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
                        String fieldId = srcJo.getString("fieldId");
                        dimensionMap.put(targetId, BIModuleUtils.getAnalysisBusinessFieldById(new BIFieldID(fieldId)));
                    }
                    if (targetRelationJo.has("targetRelation")) {
                        JSONArray dimensionAndTargetPathsJa = this.createDimensionAndTargetPathsJa(dimensionId, targetId, dims, targetRelationJo);
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
                                dimensionRelationList.add(BITableRelationHelper.getAnalysisRelation(dimensionRelationsJa.optJSONObject(j)));
                            }
                            dimensionRelationMap.put(targetId, dimensionRelationList);
                        }
                    } else {
                        BILoggerFactory.getLogger().error("error missing field:" + targetRelationJo.toString() + this.getClass().getName());
                    }
                }
            }
        }
    }

    private JSONArray createDimensionAndTargetPathsJa(String dimensionId, String targetId, JSONObject dims, JSONObject targetRelationJo) throws JSONException {

        Map<String, List<BITableRelation>> relationMap = relationsMap.get(dimensionId);
        if (relationMap == null) {
            relationMap = new LinkedHashMap<String, List<BITableRelation>>();
            relationsMap.put(dimensionId, relationMap);
        }
        JSONArray dimensionAndTargetPathsJa = targetRelationJo.getJSONArray("targetRelation");
        List<BITableRelation> relationList = new ArrayList<BITableRelation>();
        //指标的关联关系
        JSONArray targetRelationsJa = dimensionAndTargetPathsJa.getJSONArray(targetRelationIndex);
        JSONObject primaryKeyJo = targetRelationsJa.optJSONObject(0).optJSONObject("primaryKey");
        JSONObject foreignKeyJo = targetRelationsJa.optJSONObject(targetRelationsJa.length() - 1).optJSONObject("foreignKey");
        String primaryFieldId = primaryKeyJo.optString("fieldId");
        String foreignFieldId = foreignKeyJo.optString("fieldId");
        String primaryTableId = primaryKeyJo.has("tableId") ? primaryKeyJo.getString("tableId") : null;
        String foreignTableId = foreignKeyJo.has("tableId") ? foreignKeyJo.getString("tableId") : null;

        JSONObject srcJo = dims.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
        if (primaryTableId != null && foreignTableId != null) {
            if (ComparatorUtils.equals(BIModuleUtils.getAnalysisBusinessTableById(new BITableID(primaryTableId)), BIModuleUtils.getAnalysisBusinessTableById(new BITableID(foreignTableId)))) {
                relationMap.put(targetId, relationList);
            } else {
                for (int j = 0; j < targetRelationsJa.length(); j++) {
                    relationList.add(BITableRelationHelper.getAnalysisRelation(targetRelationsJa.optJSONObject(j)));
                }
                relationMap.put(targetId, relationList);
            }
        } else {
            if (ComparatorUtils.equals(BIModuleUtils.getAnalysisBusinessFieldById(new BIFieldID(primaryFieldId)).getTableBelongTo(), BIModuleUtils.getAnalysisBusinessFieldById(new BIFieldID(foreignFieldId)).getTableBelongTo()) && !srcJo.has("targetRelation")) {
                relationMap.put(targetId, relationList);
            } else {
                for (int j = 0; j < targetRelationsJa.length(); j++) {
                    relationList.add(BITableRelationHelper.getAnalysisRelation(targetRelationsJa.optJSONObject(j)));
                }
                relationMap.put(targetId, relationList);
            }
        }

        return dimensionAndTargetPathsJa;
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
            keys[i] = targets[i].createTargetGettingKey();
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

    @Override
    public Object clone() {
        SummaryWidget widget = (SummaryWidget) super.clone();
        widget.targetFilterMap = new LinkedHashMap<String, DimensionFilter>();
        widget.targetFilterMap.putAll(targetFilterMap);
        return widget;
    }

    private class TargetSort extends NameObject {

        private static final long serialVersionUID = -3319190000338485415L;

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

    /**
     * 螺旋分析在更新完cube以后需要把dimensionsMap中的tableSource刷新来保证缓存的正常使用(etl会改变field的tableSource)
     */
    @Override
    public void refreshColumns() {

        super.refreshColumns();

        Iterator<Map.Entry<String, Map<String, BusinessField>>> it = dimensionsMap.entrySet().iterator();
        LinkedHashMap<String, Map<String, BusinessField>> refreshedDimensionsMap = new LinkedHashMap<String, Map<String, BusinessField>>();
        while (it.hasNext()) {
            Map.Entry<String, Map<String, BusinessField>> dimensionsMapEntry = it.next();
            Map<String, BusinessField> dimensionFieldOfTargetsMap = dimensionsMapEntry.getValue();
            Map<String, BusinessField> refreshedDimensionFieldOfTargetsMap = new LinkedHashMap<String, BusinessField>();
            Iterator<Map.Entry<String, BusinessField>> dimensionFieldOfTargetIterator = dimensionFieldOfTargetsMap.entrySet().iterator();
            while (dimensionFieldOfTargetIterator.hasNext()) {
                Map.Entry<String, BusinessField> entry = dimensionFieldOfTargetIterator.next();
                BusinessField dimensionFieldOfTarget = entry.getValue();
                if (dimensionFieldOfTarget != null) {
                    refreshedDimensionFieldOfTargetsMap.put(entry.getKey(), BIModuleUtils.getAnalysisBusinessFieldById(dimensionFieldOfTarget.getFieldID()));
                }
            }
            refreshedDimensionsMap.put(dimensionsMapEntry.getKey(), refreshedDimensionFieldOfTargetsMap);
        }
        dimensionsMap.clear();
        dimensionsMap = refreshedDimensionsMap;
    }

    public abstract class SimpleIntArray {

        public abstract int get(int index);

        public abstract int size();
    }

    /**
     * @param start
     * @param end
     * @param limitStarts start和end可以是数组
     * @param limitEnds
     * @param getter
     * @param gvi
     * @return
     */
    protected SimpleIntArray createGroupArray(int start, int end, final int[] limitStarts, final int[] limitEnds, final ICubeValueEntryGetter getter, GroupValueIndex gvi) {

        if (gvi instanceof AllShowRoaringGroupValueIndex) {
            int size = 0;
            if (limitStarts.length == 0) {
                size = end;
            }
            final int[] intevals = new int[limitStarts.length];
            for (int i = 0, len = limitStarts.length; i < len; i++) {
                size += (limitStarts[i] == -1 ? 0 : limitEnds[i] - limitStarts[i]);
                intevals[i] = size;
            }
            final int fsize = size, fstart = start;
            return new SimpleIntArray() {

                @Override
                public int get(int index) {

                    for (int i = intevals.length - 1; i >= 0; i--) {
                        if (i == 0) {
                            return index + limitStarts[0];
                        }
                        if (index < intevals[i] && index >= intevals[i - 1]) {
                            return index - intevals[i - 1] + limitStarts[i];
                        }
                    }
                    return index + fstart;
                }

                @Override
                public int size() {

                    return fsize;
                }
            };
        } else {
            final int[] groupIndex = new int[getter.getGroupSize()];
            Arrays.fill(groupIndex, NIOConstant.INTEGER.NULL_VALUE);
            gvi.Traversal(new SingleRowTraversalAction() {

                @Override
                public void actionPerformed(int row) {

                    int groupRow = getter.getPositionOfGroupByRow(row);
                    if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                        groupIndex[groupRow] = groupRow;
                    }
                }
            });
            final IntArray array = new IntArray();
            if (limitStarts.length > 0) {
                for (int i = 0, len = limitStarts.length; i < len; i++) {
                    start = limitStarts[i];
                    end = limitEnds[i];
                    if (start != -1) {
                        for (int j = start; j < end; j++) {
                            // BI-6383 BI-6361的问题已在SingleSliderWidget 中做处理,没有影响
                            if (groupIndex[j] != NIOConstant.INTEGER.NULL_VALUE) {
                                // 这里需要加的是分组的值...
                                array.add(groupIndex[j]);
                            }
                        }
                    }
                }
            } else {
                if (start != -1) {
                    for (int j = start; j < end; j++) {
                        if (groupIndex[j] != NIOConstant.INTEGER.NULL_VALUE) {
                            array.add(groupIndex[j]);
                        }
                    }
                }
            }
            return new SimpleIntArray() {

                @Override
                public int get(int index) {

                    return array.get(index);
                }

                @Override
                public int size() {

                    return array.size;
                }
            };
        }

    }

    /**
     * 是否可以进行补全缺失时间
     *
     * @return
     */
    public boolean canCompleteMissTime() {

        return false;
    }
}