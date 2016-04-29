package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.widget.tree.*;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.conf.report.widget.BIDataColumnFactory;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.relation.BISimpleRelation;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyECBlock;
import com.fr.report.poly.TemplateBlock;

import java.util.*;

/**
 * Created by roy on 16/4/14.
 */
public class TreeWidget extends BISummaryWidget {
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


    @Override
    public BIDimension[] getViewDimensions() {
        return new BIDimension[0];
    }

    @Override
    public BIDimension[] getViewTargets() {
        return new BIDimension[0];
    }

    @Override
    public int isOrder() {
        return 0;
    }

    @Override
    public JSONObject createDataJSON(BISessionProvider session) throws Exception {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        Node tree = CubeIndexLoader.getInstance(session.getUserId()).loadGroup(this, new BISummaryTarget[0], this.getDimensions(), this.getDimensions(), new BISummaryTarget[0], this.page, true, (BISession) session);
        List<List<String>> dataList = new ArrayList<List<String>>();
        createDataList(tree, dataList, 0);
        JSONObject resultJo = new JSONObject();
        switch (data_type) {
            case BIReportConstant.TREE.TREE_REQ_TYPE.INIT_DATA:
                resultJo = getInitDataJSON(dataList);
                break;
            case BIReportConstant.TREE.TREE_REQ_TYPE.DISPLAY_DATA:
                resultJo = getDisplayDataJSON(dataList);
                break;
            case BIReportConstant.TREE.TREE_REQ_TYPE.SEARCH_DATA:
                try {
                    resultJo = getSearchDataJSON(dataList);
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
                break;
            case BIReportConstant.TREE.TREE_REQ_TYPE.SELECTED_DATA:
                resultJo = getSelectedDataJSON(dataList);
                break;
            case BIReportConstant.TREE.TREE_REQ_TYPE.ADJUST_DATA:
                resultJo = getAdjustDataJSON(dataList);
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


    private JSONObject getInitDataJSON(List<List<String>> dataList) throws JSONException {
        GetTreeTreeNodeExecutor executor = new GetTreeTreeNodeExecutor();
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("times", times);
        jo.put("check_state", check_state);
        jo.put("floors", floors);
        jo.put("parent_values", parent_values);
        jo.put("selected_values", selected_values);
        executor.parseJSON(jo, dataList);
        return executor.getResultJSON();
    }

    private JSONObject getSearchDataJSON(List<List<String>> dataList) throws Exception {
        GetSearchTreeNodeExecutor executor = new GetSearchTreeNodeExecutor();
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("keyword", keyword);
        jo.put("last_search_value", last_search_value);
        jo.put("selected_values", selected_values);
        executor.parseJSON(jo, dataList);
        return executor.getResultJSON();


    }

    private JSONObject getDisplayDataJSON(List<List<String>> dataList) throws JSONException {
        GetDisplayTreeNodeExecutor executor = new GetDisplayTreeNodeExecutor();
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("selected_values", selected_values);
        executor.parseJSON(jo, dataList);
        return executor.getResultJSON();
    }

    private JSONObject getSelectedDataJSON(List<List<String>> dataList) throws JSONException {
        GetTreeSelectTreeNodeExecutor executor = new GetTreeSelectTreeNodeExecutor();
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("not_selected_value", not_selected_value);
        jo.put("keyword", keyword);
        jo.put("selected_values", selected_values);
        jo.put("parent_values", parent_values);
        executor.parseJSON(jo, dataList);
        return executor.getResultJSON();

    }

    private JSONObject getAdjustDataJSON(List<List<String>> dataList) throws JSONException {
        AdjustTreeDataStructureExecutorTree executor = new AdjustTreeDataStructureExecutorTree();
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("selected_values", selected_values);
        executor.parseJSON(jo, dataList);
        return executor.getResultJSON();
    }

    private void createDataList(Node tree, List<List<String>> dataList, int floors) {
        if (tree.isEmptyChilds()) {
            return;
        }
        if (dataList.size() <= floors) {
            dataList.add(new ArrayList<String>());
        }
        for (int i = 0; i < tree.getChildLength(); i++) {
            dataList.get(floors).add(tree.getChild(i).getShowValue());
            createDataList(tree.getChild(i), dataList, floors + 1);
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


}
