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
        JSONObject widgetJson = new JSONObject("{\"settings\":{\"column_size\":[80,80,114,224,263,360,473,721,93]},\"clickvalue\":\"\",\"_page_\":{},\"linkedWidget\":{},\"sessionID\":\"1321\",\"type\":3,\"expander\":{\"x\":{\"type\":false,\"value\":[[],[]]},\"y\":{\"type\":false,\"value\":[[]]}},\"clicked\":{},\"filter\":{\"filterValue\":[],\"filterType\":80},\"view\":{\"20001\":[\"f2bedd74bd8320d6\"],\"20000\":[\"e1470820d4002f54\"],\"30000\":[\"871104b8dd2e255c\",\"fb32c3e441af4ada\"],\"10000\":[\"51a19db3af3c9fe1\"]},\"filterValue\":{},\"requestURL\":\"http://localhost:8080/WebReport/ReportServer\",\"name\":\"统计组件\",\"bounds\":{\"top\":0,\"left\":0,\"width\":2164,\"height\":1055},\"scopes\":{},\"realData\":true,\"page\":0,\"initTime\":1492074181907,\"linkages\":[],\"dimensions\":{\"51a19db3af3c9fe1\":{\"dimensionMap\":{\"871104b8dd2e255c\":{\"targetRelation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"7227b8606a9c9d15\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"a94b4f32ff962cac\"}}]],\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"a94b4f32ff962cac\",\"fieldId\":\"a94b4f32ff962cac\"}},\"fb32c3e441af4ada\":{\"targetRelation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"69a45b2ed6606a39合同信息记录数\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"a94b4f32ff962cac\"}}]],\"_src\":{\"fieldId\":\"a94b4f32ff962cac\"}}},\"name\":\"购买数量\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"a94b4f32ff962cac\",\"fieldId\":\"a94b4f32ff962cac\"},\"used\":true,\"sort\":{\"sortTarget\":\"51a19db3af3c9fe1\",\"type\":4},\"type\":2,\"did\":\"51a19db3af3c9fe1\"},\"871104b8dd2e255c\":{\"name\":\"合同金额\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"7227b8606a9c9d15\",\"fieldId\":\"7227b8606a9c9d15\"},\"used\":true,\"type\":2,\"did\":\"871104b8dd2e255c\"},\"fb32c3e441af4ada\":{\"name\":\"合同信息记录数\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"69a45b2ed6606a39合同信息记录数\",\"fieldId\":\"69a45b2ed6606a39合同信息记录数\"},\"used\":false,\"type\":4,\"did\":\"fb32c3e441af4ada\",\"group\":{\"type\":0}},\"e1470820d4002f54\":{\"dimensionMap\":{\"871104b8dd2e255c\":{\"targetRelation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"7227b8606a9c9d15\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"feedfdda95a40f54\"}}]],\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"feedfdda95a40f54\",\"fieldId\":\"feedfdda95a40f54\"}},\"fb32c3e441af4ada\":{\"targetRelation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"69a45b2ed6606a39合同信息记录数\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"feedfdda95a40f54\"}}]],\"_src\":{\"fieldId\":\"feedfdda95a40f54\"}}},\"name\":\"合同付款类型\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"feedfdda95a40f54\",\"fieldId\":\"feedfdda95a40f54\"},\"used\":true,\"sort\":{\"sortTarget\":\"e1470820d4002f54\",\"type\":1},\"type\":1,\"did\":\"e1470820d4002f54\"},\"f2bedd74bd8320d6\":{\"settings\":{},\"dimensionMap\":{\"871104b8dd2e255c\":{\"targetRelation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"7227b8606a9c9d15\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"846bf845825c8a62\"}}]],\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"846bf845825c8a62\",\"fieldId\":\"846bf845825c8a62\"}},\"fb32c3e441af4ada\":{\"targetRelation\":[[{\"foreignKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"69a45b2ed6606a39合同信息记录数\"},\"primaryKey\":{\"tableId\":\"69a45b2ed6606a39\",\"fieldId\":\"846bf845825c8a62\"}}]],\"_src\":{\"fieldId\":\"846bf845825c8a62\"}}},\"name\":\"合同类型\",\"_src\":{\"tableId\":\"69a45b2ed6606a39\",\"id\":\"846bf845825c8a62\",\"fieldId\":\"846bf845825c8a62\"},\"used\":true,\"sort\":{\"type\":3},\"type\":1,\"did\":\"f2bedd74bd8320d6\",\"group\":{}}},\"status\":2}");
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
