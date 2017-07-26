package com.fr.bi.cal.analyze.report.report.widget.treelabel;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.treelabel.TreeLabelExecutor;
import com.fr.bi.cal.analyze.report.report.widget.imp.TreeLabelWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by fay on 2016/10/14.
 */
public class AbstractTreeLabelExecutor extends TreeLabelExecutor {
    private int searchFloor = 0;
    private BIDimension[] dimensions;
    protected String selectedValuesString;
    private List<List<String>> pValues;

    public AbstractTreeLabelExecutor(TreeLabelWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }

    public void parseJSON(JSONObject jo) throws JSONException {
        if(jo.has("selectedValues")) {
            selectedValuesString = jo.getString("selectedValues");
        }
    }

    protected List<String> createData(List<List<String>> parentValues,int floor) throws JSONException {
        List<String> dataList = new ArrayList<String>();
        searchFloor = floor;
        dimensions = getWidgetDimensions();
        pValues = parseParentValues(parentValues);
        DimensionCalculator[] row = new DimensionCalculator[widget.getViewDimensions().length];
        for (int i = 0; i < widget.getViewDimensions().length; i++) {
            row[i] = dimensions[i].createCalculator(dimensions[i].getStatisticElement(), widget.getTableSourceRelationList(dimensions[i], session.getUserId()));
        }
        GroupValueIndex gvi = widget.createFilterGVI(row, widget.getTargetTable(), session.getLoader(), session.getUserId());
        createGroupValueWithParentValues(dataList, pValues, gvi, 0);
        return dataList;
    }

    private void createGroupValueWithParentValues(final List<String> dataList, List<List<String>> parentValues, GroupValueIndex filterGvi, int floors) {
        if (floors == parentValues.size()) {
            BIDimension dimension = dimensions[floors];
            ICubeTableService targetTi = getLoader().getTableIndex(widget.getTargetTable().getTableSource());
            ICubeTableService ti = getLoader().getTableIndex(dimension.createTableKey().getTableSource());
            List<BITableSourceRelation> list = widget.getTableSourceRelationList(dimension, session.getUserId());
            ICubeColumnIndexReader dataReader = ti.loadGroup(new IndexKey(dimension.createColumnKey().getFieldName()), list);

            for (int i = 0; i < dataReader.sizeOfGroup(); i++) {
                Object[] rowValue = new Object[1];
                rowValue[0] = dataReader.getGroupValue(i);
                if (!filterGvi.AND(dataReader.getGroupIndex(rowValue)[0]).isAllEmpty()) {
                    String k = dataReader.getGroupValue(i).toString();
                    dataList.add(k);
                }
            }
            if (dimension.getSortType() == BIReportConstant.SORT.DESC) {
                Collections.reverse(dataList);
            }
            ti.clear();
            targetTi.clear();
        }
        if (floors < parentValues.size()) {
            if (!containsAllSelected(parentValues.get(floors))) {
                GroupValueIndex result = generateGVI(parentValues, floors, filterGvi);
                createGroupValueWithParentValues(dataList, parentValues, result, floors + 1);
            } else {
                createGroupValueWithParentValues(dataList, parentValues, filterGvi, floors + 1);
            }
        }
    }

    private GroupValueIndex generateGVI(List<List<String>> parentValues, int floors, GroupValueIndex filterGvi) {
        List<GroupValueIndex> gviList =new ArrayList<GroupValueIndex>();
        GroupValueIndex result;
        for(String str : parentValues.get(floors)) {
            String[] groupValue = new String[1];
            groupValue[0] = str;
            BIDimension dimension = dimensions[floors];
            ICubeTableService ti = getLoader().getTableIndex(dimension.createTableKey().getTableSource());
            final ICubeColumnIndexReader dataReader = ti.loadGroup(new IndexKey(dimension.createColumnKey().getFieldName()), widget.getTableSourceRelationList(dimension, session.getUserId()));
            GroupValueIndex gvi = dataReader.getGroupIndex(groupValue)[0].AND(filterGvi);
            gviList.add(gvi);
        }
        if (gviList.size() <= 0) {
            result = filterGvi;
        } else {
            result = gviList.get(0).AND(filterGvi);
            for(int i = 1; i< gviList.size();i++) {
                result = gviList.get(i).AND(filterGvi).OR(result);
            }
        }
        return result;
    }

    private boolean containsAllSelected(List<String> values) {
        return values.contains("_*_");
    }

    private BIDimension[] getWidgetDimensions() {
        ArrayList<BIDimension> result = new ArrayList<BIDimension>();
        BIDimension[] dimensions = widget.getViewDimensions();
        for (int index = 0; index < dimensions.length; index++) {
            if(index != searchFloor) {
                result.add(dimensions[index]);
            }
        }
        result.add(dimensions[searchFloor]);
        return result.toArray(new BIDimension[0]);
    }

    private List<List<String>>  parseParentValues(List<List<String>> values) {
        List<List<String>> filter = new ArrayList<List<String>>();
        for (int i =0;i < dimensions.length; i++) {
            if( i != searchFloor) {
                filter.add(values.get(i));
            }
        }
        return filter;
    }
}
