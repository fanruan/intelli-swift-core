package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item;

import com.fr.bi.conf.report.style.table.ITableStyle;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kary on 2017/2/13.
 */
public class BIBasicTableItem implements ITableItem {

    private String dId;
    private String text;
    private List<ITableItem> values;
    private boolean needExpand;
    private boolean isExpanded;
    protected List<ITableItem> children;
    private ITableStyle styles;
    private Object value;
    private boolean isSum;
    //text样式，简单处理
    private JSONObject textStyles;
    //通过tag来标识是否需要合并
    private String tag;

    public BIBasicTableItem() {
        tag = String.valueOf(UUID.randomUUID());
    }

    @Override
    public void setDId(String dId) {
        this.dId = dId;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setValues(List<ITableItem> values) {
        this.values = values;
    }

    @Override
    public void setNeedExpand(boolean needExpand) {
        this.needExpand = needExpand;
    }

    @Override
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void setChildren(List<ITableItem> children) {
        this.children = children;
    }

    @Override
    public boolean hasValues() {
        return null != this.values && values.size() > 0;
    }

    public void setStyles(ITableStyle styles) {
        this.styles = styles;
    }

    @Override
    public String getDId() {
        return dId;
    }

    public String getText() {
        return text;
    }

    @Override
    public List<ITableItem> getValues() {
        return values;
    }

    @Override
    public List<ITableItem> getChildren() {
        return children;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isSum() {
        return isSum;
    }

    @Override
    public void setSum(boolean sum) {
        isSum = sum;
    }

    @Override
    public void setTextStyles(JSONObject textStyles) {
        this.textStyles = textStyles;
    }

    @Override
    public void mergeItems(ITableItem newItem) throws Exception {
        if (newItem == null) {
            return;
        }
        if (getValues() != null) {
            getValues().addAll(newItem.getValues());
        } else {
            setValues(newItem.getValues());
        }
        if (getChildren() != null) {
            for (int i = 0; i < newItem.getChildren().size(); i++) {
                getChildren().get(i).mergeItems(newItem.getChildren().get(i));
            }
        } else {
            setChildren(newItem.getChildren());
        }
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("dId")) {
            dId = jo.optString("dId");
        }
        if (jo.has("text")) {
            text = jo.optString("text");
        }
        if (jo.has("values")) {
            if (null == values) {
                values = new ArrayList<ITableItem>();
            }
            children = new ArrayList<ITableItem>();
            for (int i = 0; i < jo.getJSONArray("values").length(); i++) {
                BIBasicTableItem item = new BIBasicTableItem();
                if (BIJsonUtils.isKeyValueSet(jo.getJSONArray("values").getString(i))) {
                    item.parseJSON(jo.getJSONArray("values").getJSONObject(i));
                }
                values.add(item);
            }
        }
        if (jo.has("tag")) {
            value = jo.getString("tag");
        }

        if (jo.has("value")) {
            value = jo.opt("value");
        }

        if (jo.has("children")) {
            children = new ArrayList<ITableItem>();
            for (int i = 0; i < jo.getJSONArray("children").length(); i++) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.parseJSON(jo.getJSONArray("children").getJSONObject(i));
                children.add(item);
            }
        }

    }


    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (null != this.children && children.size() > 0) {
            JSONArray childrenArray = new JSONArray();
            for (ITableItem child : this.children) {
                childrenArray.put(child.createJSON());
            }
            jo.put("children", childrenArray);
        }
        if (null != dId) {
            jo.put("dId", dId);
        }
        jo.put("text", text);
        if (null != styles) {
            jo.put("styles", null == styles ? new JSONObject() : styles.createJSON());
        }
        if (null != this.values && values.size() > 0) {
            JSONArray TempValues = new JSONArray();
            for (ITableItem item : this.values) {
                TempValues.put(item.createJSON());
            }
            jo.put("values", TempValues);
        }

        jo.put("value", String.valueOf(value));
        jo.put("isSum", isSum);
        jo.put("tag", tag);
        if (textStyles != null) {
            jo.put("textStyle", textStyles);
        }
        return jo;
    }

}
