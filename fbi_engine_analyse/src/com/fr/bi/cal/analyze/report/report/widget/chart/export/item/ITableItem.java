package com.fr.bi.cal.analyze.report.report.widget.chart.export.item;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.style.ITableStyle;
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

    String getValue();

    List<ITableItem> getValues();

    boolean isNeedExpand();

    boolean isExpanded();

    List<ITableItem> getChildren();

    void setDId(String dId);

    void setValue(String text);

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

    void mergeItems(ITableItem newItem) throws Exception;

    void parseJSON(JSONObject jo) throws Exception;
}
