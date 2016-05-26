package com.fr.bi.field.target.filter.field;

import com.fr.bi.base.BIUser;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.ValueCreator;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.report.key.NumberSummaryFilterKey;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.structure.collection.map.lru.LRUWithKHashMap;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.List;


public class SummaryNumberFilter extends ColumnFieldFilter {
    /**
     *
     */
    private static final long serialVersionUID = 399517487692690993L;
    private static final int CACHE_SIZE = 16;
    private static String XML_TAG = "SummaryNumberFilter";
    @BIIgnoreField
    private LRUWithKHashMap<NumberSummaryFilterKey, GroupValueIndex> numberSummaryIndexMap = new LRUWithKHashMap<NumberSummaryFilterKey, GroupValueIndex>(CACHE_SIZE);
    @BICoreField
    private int SUMMARY_TYPE;
    @BICoreField
    private TargetFilter filter;


    public SummaryNumberFilter() {
    }

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((filterValue == null) ? 0 : filterValue.hashCode());
        result = prime * result
                + ((filter == null) ? 0 : filter.hashCode());
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        SummaryNumberFilter other = (SummaryNumberFilter) obj;
        if (filter == null) {
            if (other.filter != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(filter, other.filter)) {
            return false;
        }

        return true;
    }

    /**
     * 解析json
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("field_type")) {
            SUMMARY_TYPE = jo.getInt("field_type");
        }
        filter = null;
        if (jo.has("field_value")) {
            filter = TargetFilterFactory.parseFilter(jo.getJSONObject("field_value"), userId);
        }
    }

    /**
     * 创建过滤条件的index，用于指标过滤
     *
     * @return 分组索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader, long userId) {
        return createFilterIndex(target, loader, userId);
    }

    private GroupValueIndex getSummaryNumberFilterGVI(final List<BITableSourceRelation> relation, final Table target, final ICubeDataLoader loader, final long userID) {
        NumberSummaryFilterKey key = new NumberSummaryFilterKey(dataColumn.getFieldName(), relation, filter);
        GroupValueIndex res = numberSummaryIndexMap.get(key, new ValueCreator<GroupValueIndex>() {

            @Override
            public GroupValueIndex createNewObject() {
                return createNumberSummaryFilterIndex(relation, target, loader, userID);
            }
        });
        return res;
    }


    private GroupValueIndex createNumberSummaryFilterIndex(final List<BITableSourceRelation> relation, final Table target, final ICubeDataLoader loader, final long userID) {
        final ICubeTableService ti = loader.getTableIndex(target);
        final ICubeTableService si = loader.getTableIndex(dataColumn);
        final ICubeTableIndexReader reader = ti.ensureBasicIndex(relation);
        final GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        ti.getAllShowIndex().Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int rowIndices) {
                GroupValueIndex tgvi = reader.get(rowIndices);
                if (filter != null) {
                    GroupValueIndex cgvi = filter.createFilterIndex(target, loader, userID);
                    tgvi = GVIUtils.AND(tgvi, cgvi);
                }
                double value = getValueBySumType(tgvi, si);
                if (filterValue.isMatchValue(value)) {
                    gvi.addValueByIndex(rowIndices);
                }
            }
        });
        GroupValueIndex currentIndex = gvi;
        NumberSummaryFilterKey key = new NumberSummaryFilterKey(dataColumn.getFieldName(), relation, filter);
        this.numberSummaryIndexMap.putWeakValue(key, currentIndex);
        return gvi;
    }

    private double getValueBySumType(GroupValueIndex tgvi, ICubeTableService si) {
        double value;
        switch (SUMMARY_TYPE) {
            case BIReportConstant.FILTER_TYPE.NUMBER_SUM:
                value = si.getSUMValue(tgvi, new IndexKey(dataColumn.getFieldName()));
                break;
            case BIReportConstant.FILTER_TYPE.NUMBER_AVG:
                value = si.getSUMValue(tgvi, new IndexKey(dataColumn.getFieldName())) / tgvi.getRowsCountWithData();
                break;
            case BIReportConstant.FILTER_TYPE.NUMBER_MAX:
                value = si.getMAXValue(tgvi, new IndexKey(dataColumn.getFieldName()));
                break;
            case BIReportConstant.FILTER_TYPE.NUMBER_MIN:
                value = si.getMINValue(tgvi, new IndexKey(dataColumn.getFieldName()));
                break;
            case BIReportConstant.FILTER_TYPE.NUMBER_COUNT:
                value = tgvi.getRowsCountWithData();
                break;
            default:
                value = si.getSUMValue(tgvi, new IndexKey(dataColumn.getFieldName()));
        }
        return value;
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
        if (filterValue != null) {
            try {
                List<BITableRelation> relation = BIConfigureManagerCenter.getTableRelationManager().getFirstPath(userID, new BITable(dataColumn.getTableBelongTo()), target).getAllRelations();
                return getSummaryNumberFilterGVI(BIConfUtils.convert2TableSourceRelation(relation, new BIUser(userID)), target, loader, userID);
            } catch (Exception e) {
                BILogger.getLogger().info("sum filter failed");
                return null;
            }
        }
        return null;
    }
}