package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item;

import com.fr.bi.conf.report.style.table.ITableStyle;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/26.
 */
public interface ITableItem extends JSONCreator {
    void setStyles(ITableStyle style);

//    void setType(String type);

    String getDId();

    Object getValue();

    List<ITableItem> getValues();

    boolean isNeedExpand();

    boolean isExpanded();

    List<ITableItem> getChildren();

    void setDId(String dId);

    void setValue(Object text);

    void setValues(List<ITableItem> values);

    void setNeedExpand(boolean needExpand);

    void setExpanded(boolean expanded);

    void setChildren(List<ITableItem> children);

    boolean hasValues();

    ITableStyle getStyles();

    void setdId(String dId);

    void setText(String text);

    String getText();

    boolean isSum();

    void setSum(boolean sum);

    void setTextStyles(JSONObject textStyles);

    void mergeItems(ITableItem newItem) throws Exception;

    void parseJSON(JSONObject jo) throws Exception;
}
