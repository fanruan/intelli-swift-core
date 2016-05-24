package com.fr.bi.cal.analyze.report.report.widget.tree;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.tree.TreeExecutor;
import com.fr.bi.cal.analyze.report.report.widget.TreeWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
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
        if (jo.has("selected_values")) {
            selectedValuesString = jo.getString("selected_values");
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
            BIDimension dimension = widget.getViewDimensions()[floors];
            ICubeTableService targetTi = getLoader().getTableIndex(widget.getTargetTable().getTableSource());
            ICubeTableService ti = getLoader().getTableIndex(dimension.createTableKey().getTableSource());
            List<BITableSourceRelation> list = widget.getTableSourceRelationList(dimension, session.getUserId());
            ICubeColumnIndexReader dataReader = ti.loadGroup(new IndexKey(dimension.createColumnKey().getFieldName()), list);
            if (times == -1) {
                Iterator<Map.Entry> it = dataReader.iterator();
                while (it.hasNext()) {
                    Map.Entry e = it.next();
                    Object[] groupValue = new Object[1];
                    groupValue[0] = e.getKey();
                    if (!filterGvi.AND((GroupValueIndex) e.getValue()).isAllEmpty()) {
                        dataList.add(e.getKey().toString());
                    }
                }
                if (dimension.getSortType() == BIReportConstant.SORT.DESC) {
                    Collections.reverse(dataList);
                }
            }
            if (times > 0 && (times - 1) * BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE < dataReader.sizeOfGroup()) {
                int start = (times - 1) * BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE;
                int end = Math.min(dataReader.sizeOfGroup(), start + 100);
                for (int i = start; i < end; i++) {
                    Object[] rowValue = new Object[1];
                    rowValue[0] = dataReader.getGroupValue(i);
                    if (!filterGvi.AND(dataReader.getGroupIndex(rowValue)[0]).isAllEmpty()) {
                        dataList.add(dataReader.getGroupValue(i).toString());
                    }
                }
                if (dimension.getSortType() == BIReportConstant.SORT.DESC) {
                    Collections.reverse(dataList);
                }
            }
            ti.clear();
            targetTi.clear();
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
