package com.fr.bi.cal.analyze.report.report.widget.tree;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIPhoneticismUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 16/4/21.
 */
public class GetTreeSelectTreeNodeExecutor extends AbstractTreeNodeExecutor {
    private String notSelectedValueString;
    private String toSelectedValueString;
    private String keyword;
    private String parentValues;

    public GetTreeSelectTreeNodeExecutor(TreeWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }

    @Override
    public void parseJSON(JSONObject jo) throws JSONException {
        super.parseJSON(jo);
        if (jo.has("notSelectedValue")) {
            notSelectedValueString = jo.getString("notSelectedValue");
        }

        if (jo.has("currentSelectValue")) {
            toSelectedValueString = jo.getString("currentSelectValue");
        }

        if (jo.has("keyword")) {
            keyword = jo.getString("keyword");
        }

        if (jo.has("parentValues")) {
            parentValues = jo.getString("parentValues");
        }
    }

    public JSONObject getResultJSON() throws JSONException {
        String[] parent = new String[0];
        JSONObject jo = new JSONObject();
        if (parentValues != null) {
            JSONArray parentObject = new JSONArray(parentValues);
            parent = BIJsonUtils.jsonArray2StringArray(parentObject);
        }

        JSONObject selectedValues = new JSONObject();
        if (selectedValuesString != null) {
            selectedValues = new JSONObject(selectedValuesString);
        }
        if (selectedValues.length() == 0 && notSelectedValueString != null) {
            return jo;
        }

        jo = selectedValues;
        if (notSelectedValueString != null) {
            dealWithSelectValues(jo, notSelectedValueString, parent, floors, keyword);
        }
        if (toSelectedValueString != null) {
            dealWithUnselectValues(jo, toSelectedValueString, parent, floors, keyword);
        }
        return jo;
    }

    private void dealWithSelectValues(JSONObject selectedValues, String notSelectedValueString, String[] parent, int floors, String keyword) throws JSONException {
        String[] p = new String[parent.length + 1];
        System.arraycopy(parent, 0, p, 0, parent.length);
        p[parent.length] = notSelectedValueString;

        //存储的值中存在这个值就把它删掉
        //例如选中了中国-江苏-南京， 取消中国或江苏或南京
        if (canFindKey(selectedValues, p)) {
            //如果搜索的值在父亲链中
            if (isSearchValueInParents(p)) {
                //例如选中了 中国-江苏， 搜索江苏， 取消江苏
                //例如选中了 中国-江苏， 搜索江苏， 取消中国
                deleteNode(selectedValues, p);
            } else {
                //找到所有搜索到的节点删掉
                List<String[]> result = new ArrayList<String[]>();
                List<String[]> searched = new ArrayList<String[]>();
                //从当前值开始搜
                boolean finded = search(parent.length + 1, floors, parent, notSelectedValueString, keyword, result, searched);
                if (finded && !searched.isEmpty()) {
                    for (String[] arr : searched) {
                        JSONObject jo = getNode(selectedValues, arr);
                        if (jo != null) {
                            //例如选中了 中国-江苏-南京，搜索南京，取消中国
                            deleteNode(selectedValues, arr);
                        } else {
                            //例如选中了 中国-江苏，搜索南京，取消中国
                            expandSelectValues(selectedValues, arr, arr[arr.length - 1]);
                        }
                    }
                }
            }
        }
        //存储的值中不存在这个值，但父亲节点是全选的情况
        //例如选中了中国-江苏，取消南京
        //important 选中了中国-江苏，取消了江苏，但是搜索的是南京
        if (isChild(selectedValues, p)) {//如果有父亲节点是全选的状态
            parentAllSelected(selectedValues, notSelectedValueString, parent, floors, keyword, p);
        }
    }

    private void parentAllSelected(JSONObject selectedValues, String notSelectedValueString, String[] parent, int floors, String keyword, String[] p) throws JSONException {
        List<String[]> result = new ArrayList<String[]>();
        boolean finded;
        //如果parentValues中有匹配的值，说明搜索结果不在当前值下
        if (isSearchValueInParents(p)) {
            finded = true;
        } else {
            //从当前值开始搜
            finded = search(parent.length + 1, floors, parent, notSelectedValueString, keyword, result, new ArrayList<String[]>());
            p = parent;
        }
        if (finded) {
            //去掉点击的节点之后的结果集
            //处理 选中了 中国， 取消南京， 搜索江苏
            expandSelectValues(selectedValues, p, notSelectedValueString);

            //填充去掉notSelectedValue之后的所有值
            //处理 选中了 中国， 取消江苏， 搜索南京
            if (!result.isEmpty()) {
                for (String[] arr : result) {
                    buildTree(selectedValues, arr);
                }
            }
        }
    }

    private void expandSelectValues(JSONObject selectedValues, String[] p, String notSelectedValueString) throws JSONException {
        JSONObject next = selectedValues;
        List<Integer> childrenCount = new ArrayList<Integer>();
        List<String[]> path = new ArrayList<String[]>();
        int i;

        //去掉点击的节点之后的结果集
        for (i = 0; i < p.length; i++) {
            String v = p[i];
            JSONObject t = next.optJSONObject(v);
            if (t == null) {
                if (i == 0) {
                    break;
                }
                if (next.length() == 0) {
                    String[] split = new String[i];
                    System.arraycopy(p, 0, split, 0, i);
                    List<String> expanded = createData(split, -1);
                    path.add(split);
                    childrenCount.add(expanded.size());
                    //如果只有一个值且取消的就是这个值
                    if (i == p.length - 1 && expanded.size() == 1 && ComparatorUtils.equals(expanded.get(0), notSelectedValueString)) {
                        for (int j = childrenCount.size() - 1; j >= 0; j--) {
                            if (childrenCount.get(j) == 1) {
                                deleteNode(selectedValues, path.get(j));
                            } else {
                                break;
                            }
                        }
                    } else {
                        for (String ex : expanded) {
                            if (i == p.length - 1 && ComparatorUtils.equals(ex, notSelectedValueString)) {
                                continue;
                            }
                            next.put(ex, new JSONObject());
                        }
                    }
                    next = next.optJSONObject(v);
                } else {
                    break;
//                    next.put(v, next = new JSONObject());
                }
            } else {
                next = t;
            }
        }
    }

    private void dealWithUnselectValues(JSONObject selectedValues, String toSelectedValueString, String[] parent, int floors, String keyword) throws JSONException {
        String[] p = new String[parent.length + 1];
        System.arraycopy(parent, 0, p, 0, parent.length);
        p[parent.length] = toSelectedValueString;

        List<String[]> result = new ArrayList<String[]>();
        boolean finded = search(parent.length + 1, floors, parent, toSelectedValueString, keyword, new ArrayList<String[]>(), result);
        if (finded && result.size() > 0) {
            int i;
            for (i = 0; i < result.size(); i++) {
                String[] strs = result.get(i);
                buildTree(selectedValues, strs);
                boolean isSelectedAll = true;
                int j = strs.length - 1;
                while (isSelectedAll && j > 0) {
                    String str = strs[j];
                    String preStr = strs[j - 1];
                    isSelectedAll = dealWithIsSelectedAll(selectedValues, strs, str, preStr);
                }
            }
        }

    }

    private boolean dealWithIsSelectedAll(JSONObject selectedValues, String[] strs, String str, String preStr) {
        JSONObject preSelectedValue = new JSONObject();
        String[] newParents = new String[strs.length -1];
        System.arraycopy(strs, 0, newParents, 0, strs.length-1);
        for (String thisStr : strs) {
            if (thisStr == preStr) {
                preSelectedValue = selectedValues;
            }
            if (thisStr != str) {
                try {
                    selectedValues = selectedValues.getJSONObject(thisStr);
                } catch (JSONException e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
            } else {
                break;
            }
        }
        int childsLength = 0;
        try {
            childsLength = getChildCount(newParents);
        } catch (JSONException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        if (selectedValues.length() == childsLength) {
            try {
                preSelectedValue.put(preStr, new JSONObject());
            } catch (JSONException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            return true;
        } else {
            return false;
        }
    }

    private int getChildCount(String[] values) throws JSONException {
        return createData(values, -1).size();

    }

    private JSONObject getNode(JSONObject jo, String[] parents) {
        JSONObject t = jo;
        for (String v : parents) {
            if (t == null) {
                return null;
            }
            t = t.optJSONObject(v);
        }
        return t;
    }

    private boolean isSearchValueInParents(String[] parents) {
        for (String v : parents) {
            if (isMatch(v, keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean canFindKey(JSONObject jo, String[] parents) {
        JSONObject t = jo;
        for (String v : parents) {
            if (!t.has(v)) {
                return false;
            }
            t = t.optJSONObject(v);
        }
        return true;
    }

    private boolean isChild(JSONObject jo, String[] parents) {
        JSONObject t = jo;
        for (String v : parents) {
            if (!t.has(v)) {
                return false;
            }
            t = t.optJSONObject(v);
            if (t.length() == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean search(int deep, int floor, String[] parents, String value, String keyword, List<String[]> result, List<String[]> match) throws JSONException {

        String[] newParents = new String[parents.length + 1];
        System.arraycopy(parents, 0, newParents, 0, parents.length);
        newParents[parents.length] = value;
        if (isMatch(value, keyword)) {
            match.add(newParents);
            return true;
        }


        if (deep >= floor) {
            return false;
        }

        List<String> vl = createData(newParents, -1);

        List<String> notSearch = new ArrayList<String>();
        boolean can = false;

        for (int i = 0, len = vl.size(); i < len; i++) {
            if (search(deep + 1, floor, newParents, vl.get(i), keyword, result, match)) {
                can = true;
            } else {
                notSearch.add(vl.get(i));
            }
        }
        if (can) {
            for (String v : notSearch) {
                String[] next = new String[newParents.length + 1];
                System.arraycopy(newParents, 0, next, 0, newParents.length);
                next[newParents.length] = v;
                result.add(next);
            }
        }
        return can;
    }

    private boolean isMatch(String name, String keyword) {
        if (keyword == null) {
            return true;
        }
        String py = BIPhoneticismUtils.getPingYin(name);
        if (name.toUpperCase().contains(keyword.toUpperCase())
                || py.toUpperCase().contains(keyword.toUpperCase())) {
            return true;
        }
        return false;
    }

    private void deleteNode(JSONObject selectedValues, String[] parents) {
        String name = parents[parents.length - 1];
        String[] tp = new String[parents.length - 1];
        System.arraycopy(parents, 0, tp, 0, parents.length - 1);
        JSONObject pNode = getNode(selectedValues, tp);
        if (pNode != null && pNode.has(name)) {
            pNode.remove(name);
            //递归删掉空父节点
            while (tp.length > 0 && pNode.length() == 0) {
                name = tp[tp.length - 1];
                String[] nextP = new String[tp.length - 1];
                System.arraycopy(tp, 0, nextP, 0, tp.length - 1);
                tp = nextP;
                pNode = getNode(selectedValues, tp);
                if (pNode != null) {
                    pNode.remove(name);
                }
            }
        }
    }

    private void buildTree(JSONObject jo, String[] values) throws JSONException {
        JSONObject t = jo;
        for (String v : values) {
            if (!t.has(v)) {
                t.put(v, new JSONObject());
            }
            t = t.optJSONObject(v);
        }
    }
}
