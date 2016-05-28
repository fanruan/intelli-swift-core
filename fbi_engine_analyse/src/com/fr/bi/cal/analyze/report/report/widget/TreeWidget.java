package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.widget.tree.*;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.dimension.BIDimensionFactory;
import com.fr.bi.field.dimension.filter.DimensionFilterFactory;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.NameObject;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyECBlock;
import com.fr.report.poly.TemplateBlock;

import java.util.*;

/**
 * Created by roy on 16/4/14.
 */
public class TreeWidget extends BIAbstractWidget {
    private int page = -1;
    private int data_type = BIReportConstant.TREE.TREE_REQ_TYPE.INIT_DATA;
    private String id;
    private int times;
    private String check_state;
    private int floors;
    private String parent_values;
    private String selected_values;
    private String keyword;
    private String last_search_value;
    private String not_selected_value;
    private String[] viewData;
    private BIDimension[] dimensions;
    private BusinessTable target;
    protected NameObject targetSort;
    protected Map<String, DimensionFilter> targetFilterMap = new HashMap<String, DimensionFilter>();
    protected Map<BIDimension, Map<BusinessTable, BITableRelation>> dimensionMap = new HashMap<BIDimension, Map<BusinessTable, BITableRelation>>();


    @Override
    public BIDimension[] getViewDimensions() {
        String[] array = viewData;
        List<BIDimension> usedDimensions = new ArrayList<BIDimension>();
        for (int i = 0; i < array.length; i++) {
            BIDimension dimension = BITravalUtils.getTargetByName(array[i], dimensions);
            if (dimension.isUsed()) {
                usedDimensions.add(dimension);
            }

        }
        return usedDimensions.toArray(new BIDimension[usedDimensions.size()]);
    }

    @Override
    public BIDimension[] getViewTargets() {
        return new BIDimension[0];
    }

    @Override
    public <T extends BITargetAndDimension> T[] getDimensions() {
        return null;
    }

    @Override
    public <T extends BITargetAndDimension> T[] getTargets() {
        return null;
    }

    @Override
    public List<BusinessTable> getUsedTableDefine() {
        return null;
    }

    @Override
    public List<BusinessField> getUsedFieldDefine() {
        return null;
    }

    @Override
    public int isOrder() {
        return 0;
    }

    @Override
    public JSONObject createDataJSON(BISessionProvider session) throws Exception {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        JSONObject resultJo = new JSONObject();
        switch (data_type) {
            case BIReportConstant.TREE.TREE_REQ_TYPE.INIT_DATA:
                resultJo = getInitDataJSON((BISession) session);
                break;
            case BIReportConstant.TREE.TREE_REQ_TYPE.DISPLAY_DATA:
                resultJo = getDisplayDataJSON((BISession) session);
                break;
            case BIReportConstant.TREE.TREE_REQ_TYPE.SEARCH_DATA:
                try {
                    resultJo = getSearchDataJSON((BISession) session);
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
                break;
            case BIReportConstant.TREE.TREE_REQ_TYPE.SELECTED_DATA:
                resultJo = getSelectedDataJSON((BISession) session);
                break;
            case BIReportConstant.TREE.TREE_REQ_TYPE.ADJUST_DATA:
                resultJo = getAdjustDataJSON((BISession) session);
                break;
        }
        return resultJo;
    }


    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyECBlock();
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        parseDimensions(jo, userId);
        setTargetTable();
        parseSortFilter(jo, userId);

        if (jo.has("tree_options")) {
            JSONObject treeJo = jo.getJSONObject("tree_options");
            if (treeJo.has("type")) {
                data_type = treeJo.getInt("type");
            }
            if (treeJo.has("id")) {
                id = treeJo.getString("id");
            }
            if (treeJo.has("times")) {
                times = treeJo.getInt("times");
            }
            if (treeJo.has("check_state")) {
                check_state = treeJo.getString("check_state");
            }
            if (treeJo.has("floors")) {
                floors = treeJo.getInt("floors");
            }
            if (treeJo.has("parent_values")) {
                parent_values = treeJo.getString("parent_values");
            }
            if (treeJo.has("selected_values")) {
                selected_values = treeJo.getString("selected_values");
            }
            if (treeJo.has("keyword")) {
                keyword = treeJo.getString("keyword");
            }
            if (treeJo.has("last_search_value")) {
                last_search_value = treeJo.getString("last_search_value");
            }

            if (treeJo.has("not_selected_value")) {
                not_selected_value = treeJo.getString("not_selected_value");
            }
        }


    }


    private JSONObject getInitDataJSON(BISession session) throws JSONException {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        GetTreeTreeNodeExecutor executor = new GetTreeTreeNodeExecutor(this, paging, session);
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("times", times);
        jo.put("check_state", check_state);
        jo.put("floors", floors);
        jo.put("parent_values", parent_values);
        jo.put("selected_values", selected_values);
        executor.parseJSON(jo);
        return executor.getResultJSON();
    }

    private JSONObject getSearchDataJSON(BISession session) throws Exception {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        GetSearchTreeNodeExecutor executor = new GetSearchTreeNodeExecutor(this, paging, session);
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("keyword", keyword);
        jo.put("last_search_value", last_search_value);
        jo.put("selected_values", selected_values);
        executor.parseJSON(jo);
        return executor.getResultJSON();


    }

    private JSONObject getDisplayDataJSON(BISession session) throws JSONException {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        GetDisplayTreeNodeExecutor executor = new GetDisplayTreeNodeExecutor(this, paging, session);
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("selected_values", selected_values);
        executor.parseJSON(jo);
        return executor.getResultJSON();
    }

    private JSONObject getSelectedDataJSON(BISession session) throws JSONException {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        GetTreeSelectTreeNodeExecutor executor = new GetTreeSelectTreeNodeExecutor(this, paging, session);
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("not_selected_value", not_selected_value);
        jo.put("keyword", keyword);
        jo.put("selected_values", selected_values);
        jo.put("parent_values", parent_values);
        executor.parseJSON(jo);
        return executor.getResultJSON();

    }

    private JSONObject getAdjustDataJSON(BISession session) throws JSONException {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        AdjustTreeDataStructureExecutorTree executor = new AdjustTreeDataStructureExecutorTree(this, paging, session);
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("selected_values", selected_values);
        executor.parseJSON(jo);
        return executor.getResultJSON();
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
                this.targetFilterMap.put(key, DimensionFilterFactory.parseFilter(filter, userId));
            }
        }
    }

    private void setTargetTable() {
        if (dimensions.length > 0) {
            BITableID targetTableID = dimensions[0].createTableKey().getID();
            target = new BIBusinessTable(targetTableID);
            for (int i = 0; i < dimensions.length; i++) {
                List<BITableRelation> relations = this.getRelationList(dimensions[i]);
                if (!relations.isEmpty()) {
                    target = relations.get(relations.size() - 1).getForeignTable();
                    break;
                }
            }
        }

    }

    private void parseDimensions(JSONObject jo, long userId) throws Exception {
        JSONObject dims = jo.optJSONObject("dimensions");
        JSONObject viewJo = jo.optJSONObject("view");
        if (viewJo == null) {
            viewJo = new JSONObject();
        }
        JSONArray viewJa = viewJo.optJSONArray(BIReportConstant.REGION.DIMENSION1);
        if (viewJa == null) {
            viewJa = new JSONArray();
        }
        viewData = new String[viewJa.length()];
        for (int i = 0; i < viewJa.length(); i++) {
            viewData[i] = viewJa.getString(i);
        }

        this.dimensions = new BIDimension[viewJa.length()];
        for (int i = 0; i < viewJa.length(); i++) {
            JSONObject dimObject = dims.getJSONObject(viewJa.getString(i));
            dimObject.put("did", viewJa.getString(i));
            this.dimensions[i] = BIDimensionFactory.parseDimension(dimObject, userId);
            JSONObject dimensionMap = dimObject.getJSONObject("dimension_map");
            Iterator it = dimensionMap.keys();
            JSONArray relationJa = dimensionMap.optJSONObject(it.next().toString()).getJSONArray("target_relation");
            Map<BusinessTable, BITableRelation> relationMap = new HashMap<BusinessTable, BITableRelation>();
            for (int j = 0; j < relationJa.length(); j++) {
//                BITableRelation viewRelation = new BITableRelation();
//                viewRelation.parseJSON(relationJa.getJSONObject(j));
                BITableRelation tableRelation = BITableRelationHelper.getRelation(relationJa.getJSONObject(j));
                relationMap.put(tableRelation.getForeignTable(), tableRelation);
            }
            this.dimensionMap.put(this.dimensions[i], relationMap);
        }
    }

    public BusinessTable getTargetTable() {
        return this.target;
    }

    public List<BITableRelation> getRelationList(BIDimension dimension) {
        List<BITableRelation> tableRelationList = new ArrayList<BITableRelation>();
        Map<BusinessTable, BITableRelation> tableRelationMap = this.dimensionMap.get(dimension);
        Iterator<Map.Entry<BusinessTable, BITableRelation>> it = tableRelationMap.entrySet().iterator();
        while (it.hasNext()) {
            BITableRelation tableRelation = it.next().getValue();
            tableRelationList.add(tableRelation);
        }
        Collections.reverse(tableRelationList);
        return tableRelationList;
    }

    public List<BITableSourceRelation> getTableSourceRelationList(BIDimension dimension, long userId) {
        Map<BusinessTable, BITableRelation> tableRelationMap = this.dimensionMap.get(dimension);
        List<BITableSourceRelation> tableSourceRelationList = new ArrayList<BITableSourceRelation>();
        Iterator<Map.Entry<BusinessTable, BITableRelation>> it = tableRelationMap.entrySet().iterator();
        while (it.hasNext()) {
            BITableRelation tableRelation = it.next().getValue();
            BITableSourceRelation tableSourceRelation = BIConfUtils.convert2TableSourceRelation(tableRelation);
            tableSourceRelationList.add(tableSourceRelation);
        }
        return tableSourceRelationList;
    }

    public Map<BusinessTable, BITableRelation> getRelationMap(BIDimension dimension) {
        return this.dimensionMap.get(dimension);
    }

    @Override
    public int getType() {
        return BIReportConstant.WIDGET.TREE;
    }

}
