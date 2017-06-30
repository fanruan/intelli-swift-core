package com.fr.bi.cal.analyze.report.report.widget.tree;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.tree.TreeExecutor;
import com.fr.bi.cal.analyze.report.report.widget.TreeWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.cal.DimensionIteratorCreator;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by roy on 16/4/21.
 */
public class AbstractTreeNodeExecutor extends TreeExecutor {
    protected int floors;
    protected String selectedValuesString;

    public AbstractTreeNodeExecutor(TreeWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }


    public void parseJSON(JSONObject jo) throws JSONException {
        if (jo.has("floors")) {
            floors = jo.getInt("floors");
        }
        if (jo.has("selectedValues")) {
            selectedValuesString = jo.getString("selectedValues");
        }
    }


    protected List<String> createData(String[] parentValues, int times) throws JSONException {
        List<String> dataList = new ArrayList<String>();
        BIDimension[] rowDimension = widget.getViewDimensions();
        DimensionCalculator[] row = new DimensionCalculator[widget.getViewDimensions().length];
        for (int i = 0; i < widget.getViewDimensions().length; i++) {
            row[i] = rowDimension[i].createCalculator(rowDimension[i].getStatisticElement(), widget.getTableSourceRelationList(rowDimension[i], session.getUserId()));
        }
        GroupValueIndex gvi = widget.createFilterGVI(row, widget.getTargetTable(), session.getLoader(), session.getUserId());
        createGroupValueWithParentValues(dataList, parentValues, gvi, 0, times);
        return dataList;
    }


    private void createGroupValueWithParentValues(final List<String> dataList, String[] parentValues, GroupValueIndex filterGvi, int floors, int times) {
        if (floors == parentValues.length) {
            BIDimension[] dimensions = widget.getViewDimensions();
            BIDimension dimension = dimensions[floors];
            ICubeTableService ti = getLoader().getTableIndex(dimension.createTableKey().getTableSource());
            List<BITableSourceRelation> list = widget.getTableSourceRelationList(dimension, session.getUserId());
            ICubeColumnIndexReader dataReader = ti.loadGroup(new IndexKey(dimension.createColumnKey().getFieldName()), list);
            ICubeValueEntryGetter getter = ti.getValueEntryGetter(new IndexKey(dimension.createColumnKey().getFieldName()), list);

            if (times == -1) {
                Iterator<Map.Entry<Object, GroupValueIndex>> it = DimensionIteratorCreator.createValueMapIterator(getter, filterGvi, dimension.getSortType() != BIReportConstant.SORT.DESC);
                while (it.hasNext()) {
                    Map.Entry<Object, GroupValueIndex> e = it.next();
                    Object k = e.getKey();
                    // BI-6180 TODO 尚需要一套统一的逻辑...
                    if(BICollectionUtils.isNotCubeNullKey(k)){
                        dataList.add(k.toString());
                    }
                }
            }
            if (times > 0 && (times - 1) * BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE < dataReader.sizeOfGroup()) {
                int start = (times - 1) * BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE;
                int count = 0;
                Iterator<Map.Entry<Object, GroupValueIndex>> it = DimensionIteratorCreator.createValueMapIterator(getter, filterGvi, dimension.getSortType() != BIReportConstant.SORT.DESC);
                while (it.hasNext()){
                    if (count >= start + BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE) {
                        break;
                    }
                    Map.Entry<Object, GroupValueIndex> e = it.next();
                    count++;
                    if (count > start) {
                        Object k = e.getKey();
                        // BI-6180
                        if(BICollectionUtils.isNotCubeNullKey(k)){
                            dataList.add(k.toString());
                        }
                    }
                }
            }
            ti.clear();
        }
        if (floors < parentValues.length) {
            String[] groupValue = new String[1];
            groupValue[0] = parentValues[floors];
            BIDimension dimension = widget.getViewDimensions()[floors];
            ICubeTableService ti = getLoader().getTableIndex(dimension.createTableKey().getTableSource());
            final ICubeColumnIndexReader dataReader = ti.loadGroup(new IndexKey(dimension.createColumnKey().getFieldName()), widget.getTableSourceRelationList(dimension, session.getUserId()));
            GroupValueIndex gvi = dataReader.getGroupIndex(groupValue)[0].AND(filterGvi);
            createGroupValueWithParentValues(dataList, parentValues, gvi, floors + 1, times);
        }
    }


}
