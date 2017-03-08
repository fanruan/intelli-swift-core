package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager.widget;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager.TableTestWidget;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/3/7.
 * 生成测试用的widget
 */
public class WidgetStructureTool {
    public static TableWidget createNormalTableTableWidget() throws Exception {
        TableTestWidget widget = new TableTestWidget();
        JSONObject widgetJson = new JSONObject().put("view", new JSONObject("{\"30000\":[\"2e00940856ddb764\"],\"10000\":[\"8b978d3875b2aaec\",\"b5735b947e7d47de\"]}"));
        widgetJson.put("dimensions", new JSONObject("{\"b5735b947e7d47de\":{\"dimension_map\":{\"2e00940856ddb764\":{\"target_relation\":[[{\"foreignKey\":{\"field_id\":\"f27bec32b697d0cc医药_客户地区维度记录数\",\"table_id\":\"f27bec32b697d0cc\"},\"primaryKey\":{\"field_id\":\"937680acc018e05f\",\"table_id\":\"f27bec32b697d0cc\"}}]],\"_src\":{\"field_id\":\"937680acc018e05f\",\"id\":\"937680acc018e05f\",\"table_id\":\"f27bec32b697d0cc\"}}},\"name\":\"客户名称\",\"_src\":{\"field_id\":\"937680acc018e05f\",\"id\":\"937680acc018e05f\",\"table_id\":\"f27bec32b697d0cc\"},\"used\":true,\"type\":1,\"did\":\"b5735b947e7d47de\"},\"8b978d3875b2aaec\":{\"dimension_map\":{\"2e00940856ddb764\":{\"target_relation\":[[{\"foreignKey\":{\"field_id\":\"f27bec32b697d0cc医药_客户地区维度记录数\",\"table_id\":\"f27bec32b697d0cc\"},\"primaryKey\":{\"field_id\":\"3aa6965e7d6b7ed9\",\"table_id\":\"f27bec32b697d0cc\"}}]],\"_src\":{\"field_id\":\"3aa6965e7d6b7ed9\",\"id\":\"3aa6965e7d6b7ed9\",\"table_id\":\"f27bec32b697d0cc\"}}},\"name\":\"省区\",\"_src\":{\"field_id\":\"3aa6965e7d6b7ed9\",\"id\":\"3aa6965e7d6b7ed9\",\"table_id\":\"f27bec32b697d0cc\"},\"used\":true,\"type\":1,\"did\":\"8b978d3875b2aaec\"},\"2e00940856ddb764\":{\"name\":\"医药_客户地区维度记录数\",\"_src\":{\"field_id\":\"f27bec32b697d0cc医药_客户地区维度记录数\",\"id\":\"f27bec32b697d0cc医药_客户地区维度记录数\",\"table_id\":\"f27bec32b697d0cc\"},\"used\":true,\"type\":4,\"did\":\"2e00940856ddb764\"}}"));
        widget.parseJSON(widgetJson, -999);
        return widget;
    }

    public static Map<Integer, List<JSONObject>> createViews() throws JSONException {
        List list1 = new ArrayList<JSONObject>();
        list1.add(new JSONObject("{\"text\":\"医药_客户地区维度记录数\",\"type\":0,\"dId\":\"2e00940856ddb764\"}"));
        List list2 = new ArrayList<JSONObject>();
        list2.add(new JSONObject("{\"text\":\"省区\",\"type\":16,\"dId\":\"8b978d3875b2aaec\"}\n"));
        list2.add(new JSONObject("{\"text\":\"客户名称\",\"type\":16,\"dId\":\"b5735b947e7d47de\"}"));
        Map<Integer, List<JSONObject>> dimAndTar = new HashMap<Integer, List<JSONObject>>();
        dimAndTar.put(Integer.valueOf(30000), list1);
        dimAndTar.put(Integer.valueOf(10000), list2);
        return dimAndTar;
    }

    public static TableWidget createCrossTableTableWidget() throws Exception {
        TableTestWidget widget = new TableTestWidget();
        JSONObject widgetJson = new JSONObject().put("view", new JSONObject("{\"20000\":[\"84c1602343fb45dc\"],\"30000\":[\"a032435fe0c7e8d4\"],\"10000\":[\"d74efd6bed33e3c5\"]}"));
        widgetJson.put("dimensions", new JSONObject("{\"a032435fe0c7e8d4\":{\"name\":\"城市地区维度表记录数\",\"_src\":{\"field_id\":\"7cd5b786dea3e625城市地区维度表记录数\",\"id\":\"7cd5b786dea3e625城市地区维度表记录数\",\"table_id\":\"7cd5b786dea3e625\"},\"used\":true,\"type\":4,\"did\":\"a032435fe0c7e8d4\"},\"d74efd6bed33e3c5\":{\"settings\":{},\"dimension_map\":{\"a032435fe0c7e8d4\":{\"target_relation\":[[{\"foreignKey\":{\"field_id\":\"7cd5b786dea3e625城市地区维度表记录数\",\"table_id\":\"7cd5b786dea3e625\"},\"primaryKey\":{\"field_id\":\"27d7b7b8cf560a6b\",\"table_id\":\"7cd5b786dea3e625\"}}]],\"_src\":{\"field_id\":\"27d7b7b8cf560a6b\",\"id\":\"27d7b7b8cf560a6b\",\"table_id\":\"7cd5b786dea3e625\"}}},\"name\":\"省\",\"_src\":{\"field_id\":\"27d7b7b8cf560a6b\",\"id\":\"27d7b7b8cf560a6b\",\"table_id\":\"7cd5b786dea3e625\"},\"used\":true,\"sort\":{\"type\":3},\"type\":1,\"did\":\"d74efd6bed33e3c5\",\"group\":{}},\"84c1602343fb45dc\":{\"settings\":{},\"dimension_map\":{\"a032435fe0c7e8d4\":{\"target_relation\":[[{\"foreignKey\":{\"field_id\":\"7cd5b786dea3e625城市地区维度表记录数\",\"table_id\":\"7cd5b786dea3e625\"},\"primaryKey\":{\"field_id\":\"92d40e1276caacd9\",\"table_id\":\"7cd5b786dea3e625\"}}]],\"_src\":{\"field_id\":\"92d40e1276caacd9\",\"id\":\"92d40e1276caacd9\",\"table_id\":\"7cd5b786dea3e625\"}}},\"name\":\"市\",\"_src\":{\"field_id\":\"92d40e1276caacd9\",\"id\":\"92d40e1276caacd9\",\"table_id\":\"7cd5b786dea3e625\"},\"used\":true,\"sort\":{\"type\":3},\"type\":1,\"did\":\"84c1602343fb45dc\",\"group\":{}}}"));
        widget.parseJSON(widgetJson, -999);
        return widget;
    }
}
