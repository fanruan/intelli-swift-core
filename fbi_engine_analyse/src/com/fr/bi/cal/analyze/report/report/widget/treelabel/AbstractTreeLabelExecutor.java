package com.fr.bi.cal.analyze.report.report.widget.treelabel;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.treelabel.TreeLabelExecutor;
import com.fr.bi.cal.analyze.report.report.widget.TreeLabelWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by fay on 2016/10/14.
 */
public class AbstractTreeLabelExecutor extends TreeLabelExecutor {
    private int searchFloor = 0;
    protected String selectedValuesString;

    public AbstractTreeLabelExecutor(TreeLabelWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }

    public void parseJSON(JSONObject jo) throws JSONException {
        if(jo.has("selectedValues")) {
            selectedValuesString = jo.getString("selectedValues");
        }
    }

    protected List<String> createData(String[] parentValues,int floor, int times) throws JSONException {
        List<String> dataList = new ArrayList<String>();
        searchFloor = floor;
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
            BIDimension[] dimensions = Arrays.copyOfRange(widget.getViewDimensions(), searchFloor, widget.getViewDimensions().length);
            //BIDimension[] dimensions = widget.getViewDimensions();
            BIDimension dimension = dimensions[floors];
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
                        String k = e.getKey().toString();
                        dataList.add(k);
                    }
                }
                if (dimension.getSortType() == BIReportConstant.SORT.DESC) {
                    Collections.reverse(dataList);
                }
            } else {
                int count = 0;
                for (int i = 0; i < dataReader.sizeOfGroup(); i++) {
                    if (count >= BIReportConstant.TREE_LABEL.TREE_LABEL_ITEM_COUNT_NUM) {
                        break;
                    }
                    Object[] rowValue = new Object[1];
                    rowValue[0] = dataReader.getGroupValue(i);
                    if (!filterGvi.AND(dataReader.getGroupIndex(rowValue)[0]).isAllEmpty()) {
                        count++;
                        String k = dataReader.getGroupValue(i).toString();
                        dataList.add(k);
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
            BIDimension dimension = widget.getViewDimensions()[floors + searchFloor];
            ICubeTableService ti = getLoader().getTableIndex(dimension.createTableKey().getTableSource());
            final ICubeColumnIndexReader dataReader = ti.loadGroup(new IndexKey(dimension.createColumnKey().getFieldName()), widget.getTableSourceRelationList(dimension, session.getUserId()));
            GroupValueIndex gvi = dataReader.getGroupIndex(groupValue)[0].AND(filterGvi);
            createGroupValueWithParentValues(dataList, parentValues, gvi, floors + 1, times);
        }
    }
}
