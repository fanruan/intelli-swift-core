package excelExport.widget;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import excelExport.TableTestWidget;
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
        widgetJson.put("dimensions", new JSONObject("{\"b5735b947e7d47de\":{\"dimensionMap\":{\"2e00940856ddb764\":{\"target_relation\":[[{\"foreignKey\":{\"fieldId\":\"f27bec32b697d0cc医药_客户地区维度记录数\",\"tableId\":\"f27bec32b697d0cc\"},\"primaryKey\":{\"fieldId\":\"937680acc018e05f\",\"tableId\":\"f27bec32b697d0cc\"}}]],\"_src\":{\"fieldId\":\"937680acc018e05f\",\"id\":\"937680acc018e05f\",\"tableId\":\"f27bec32b697d0cc\"}}},\"name\":\"客户名称\",\"_src\":{\"fieldId\":\"937680acc018e05f\",\"id\":\"937680acc018e05f\",\"tableId\":\"f27bec32b697d0cc\"},\"used\":true,\"type\":1,\"did\":\"b5735b947e7d47de\"},\"8b978d3875b2aaec\":{\"dimensionMap\":{\"2e00940856ddb764\":{\"target_relation\":[[{\"foreignKey\":{\"fieldId\":\"f27bec32b697d0cc医药_客户地区维度记录数\",\"tableId\":\"f27bec32b697d0cc\"},\"primaryKey\":{\"fieldId\":\"3aa6965e7d6b7ed9\",\"tableId\":\"f27bec32b697d0cc\"}}]],\"_src\":{\"fieldId\":\"3aa6965e7d6b7ed9\",\"id\":\"3aa6965e7d6b7ed9\",\"tableId\":\"f27bec32b697d0cc\"}}},\"name\":\"省区\",\"_src\":{\"fieldId\":\"3aa6965e7d6b7ed9\",\"id\":\"3aa6965e7d6b7ed9\",\"tableId\":\"f27bec32b697d0cc\"},\"used\":true,\"type\":1,\"did\":\"8b978d3875b2aaec\"},\"2e00940856ddb764\":{\"name\":\"医药_客户地区维度记录数\",\"_src\":{\"fieldId\":\"f27bec32b697d0cc医药_客户地区维度记录数\",\"id\":\"f27bec32b697d0cc医药_客户地区维度记录数\",\"tableId\":\"f27bec32b697d0cc\"},\"used\":true,\"type\":4,\"did\":\"2e00940856ddb764\"}}"));
        widget.parseJSON(widgetJson, -999);
        return widget;
    }


    public static TableWidget createCrossTableTableWidget() throws Exception {
        TableTestWidget widget = new TableTestWidget();
        JSONObject widgetJson = new JSONObject().put("view", new JSONObject("{\"20000\":[\"84c1602343fb45dc\"],\"30000\":[\"a032435fe0c7e8d4\"],\"10000\":[\"d74efd6bed33e3c5\"]}"));
        widgetJson.put("dimensions", new JSONObject("{\"a032435fe0c7e8d4\":{\"name\":\"城市地区维度表记录数\",\"_src\":{\"fieldId\":\"7cd5b786dea3e625城市地区维度表记录数\",\"id\":\"7cd5b786dea3e625城市地区维度表记录数\",\"tableId\":\"7cd5b786dea3e625\"},\"used\":true,\"type\":4,\"did\":\"a032435fe0c7e8d4\"},\"d74efd6bed33e3c5\":{\"settings\":{},\"dimensionMap\":{\"a032435fe0c7e8d4\":{\"target_relation\":[[{\"foreignKey\":{\"fieldId\":\"7cd5b786dea3e625城市地区维度表记录数\",\"tableId\":\"7cd5b786dea3e625\"},\"primaryKey\":{\"fieldId\":\"27d7b7b8cf560a6b\",\"tableId\":\"7cd5b786dea3e625\"}}]],\"_src\":{\"fieldId\":\"27d7b7b8cf560a6b\",\"id\":\"27d7b7b8cf560a6b\",\"tableId\":\"7cd5b786dea3e625\"}}},\"name\":\"省\",\"_src\":{\"fieldId\":\"27d7b7b8cf560a6b\",\"id\":\"27d7b7b8cf560a6b\",\"tableId\":\"7cd5b786dea3e625\"},\"used\":true,\"sort\":{\"type\":3},\"type\":1,\"did\":\"d74efd6bed33e3c5\",\"group\":{}},\"84c1602343fb45dc\":{\"settings\":{},\"dimensionMap\":{\"a032435fe0c7e8d4\":{\"target_relation\":[[{\"foreignKey\":{\"fieldId\":\"7cd5b786dea3e625城市地区维度表记录数\",\"tableId\":\"7cd5b786dea3e625\"},\"primaryKey\":{\"fieldId\":\"92d40e1276caacd9\",\"tableId\":\"7cd5b786dea3e625\"}}]],\"_src\":{\"fieldId\":\"92d40e1276caacd9\",\"id\":\"92d40e1276caacd9\",\"tableId\":\"7cd5b786dea3e625\"}}},\"name\":\"市\",\"_src\":{\"fieldId\":\"92d40e1276caacd9\",\"id\":\"92d40e1276caacd9\",\"tableId\":\"7cd5b786dea3e625\"},\"used\":true,\"sort\":{\"type\":3},\"type\":1,\"did\":\"84c1602343fb45dc\",\"group\":{}}}"));



        widget.parseJSON(widgetJson, -999);
        return widget;
    }

    public static TableWidget createComplexTableTableWidget() throws Exception {
        TableTestWidget widget = new TableTestWidget();
        JSONObject widgetJson = new JSONObject("{\"settings\":{},\"clickvalue\":\"\",\"_page_\":{},\"linkedWidget\":{},\"sessionID\":\"95120\",\"type\":3,\"expander\":{\"x\":{\"type\":false,\"value\":[[],[]]},\"y\":{\"type\":false,\"value\":[[]]}},\"clicked\":{},\"filter\":{\"filterValue\":[],\"filterType\":80},\"view\":{\"20001\":[\"cbaf7f2345851c5d\"],\"20000\":[\"c7cc0209a05bfe6e\"],\"30000\":[\"f874913d72363f59\"],\"10000\":[\"20ab4f0f6dae6ae9\"]},\"filterValue\":{},\"requestURL\":\"http://localhost:8080/WebReport/ReportServer\",\"name\":\"统计组件\",\"init_time\":1491979564016,\"bounds\":{\"top\":0,\"left\":318,\"width\":636,\"height\":450},\"realData\":true,\"scopes\":{},\"page\":0,\"linkages\":[],\"dimensions\":{\"cbaf7f2345851c5d\":{\"dimensionMap\":{\"f874913d72363f59\":{\"target_relation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"7227b8606a9c9d15\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"feedfdda95a40f54\"}}]],\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"feedfdda95a40f54\",\"fieldId\":\"feedfdda95a40f54\"}}},\"name\":\"合同付款类型\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"feedfdda95a40f54\",\"fieldId\":\"feedfdda95a40f54\"},\"used\":true,\"type\":1},\"c7cc0209a05bfe6e\":{\"dimensionMap\":{\"f874913d72363f59\":{\"target_relation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"7227b8606a9c9d15\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"846bf845825c8a62\"}}]],\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"846bf845825c8a62\",\"fieldId\":\"846bf845825c8a62\"}}},\"name\":\"合同类型\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"846bf845825c8a62\",\"fieldId\":\"846bf845825c8a62\"},\"used\":true,\"type\":1},\"20ab4f0f6dae6ae9\":{\"settings\":{},\"dimensionMap\":{\"f874913d72363f59\":{\"target_relation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"7227b8606a9c9d15\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"73172ca0de3cd1a0\"}}]],\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"73172ca0de3cd1a0\",\"fieldId\":\"73172ca0de3cd1a0\"}}},\"name\":\"购买的产品\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"73172ca0de3cd1a0\",\"fieldId\":\"73172ca0de3cd1a0\"},\"used\":true,\"sort\":{\"sortTarget\":\"20ab4f0f6dae6ae9\",\"type\":0},\"type\":2,\"group\":{\"details\":[],\"type\":14}},\"f874913d72363f59\":{\"name\":\"合同金额\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"7227b8606a9c9d15\",\"fieldId\":\"7227b8606a9c9d15\"},\"used\":true,\"type\":2}},\"status\":2}");

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

}
