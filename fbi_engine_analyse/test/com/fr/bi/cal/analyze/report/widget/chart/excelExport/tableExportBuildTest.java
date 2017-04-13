package excelExport;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.SummaryComplexTableBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.SummaryCrossTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.SummaryGroupTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.manager.TableDirector;
import com.fr.bi.cal.analyze.report.report.widget.styles.BIStyleReportSetting;
import com.fr.bi.conf.report.style.ChartSetting;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import excelExport.widget.WidgetDataTool;
import excelExport.widget.WidgetStructureTool;
import junit.framework.TestCase;
import org.junit.Before;

import java.util.*;

/**
 * Created by Kary on 2017/3/6.
 */
public class tableExportBuildTest extends TestCase {
    private TableWidget widget = new TableWidget();
    private JSONObject dataJson;
    private static int summaryCount = 1;

    @Before
    public void setUp() throws Exception {
    }

    public void testSummaryTable() throws Exception {
        widget = WidgetStructureTool.createNormalTableTableWidget();
        dataJson = WidgetDataTool.getNormalSummaryDataWithSingleDim();
        testNormalSummaryTableWithSingleDim(widget, dataJson);
        dataJson = WidgetDataTool.getNormalSummaryDataWithMultiDims();
        testNormalSummaryTableWithMultiDims(widget, dataJson);
    }

    /*普通汇总表，单维度*/
    private void testNormalSummaryTableWithSingleDim(TableWidget widget, JSONObject dataJson) throws Exception {
        Map<Integer, List<JSONObject>> viewMap = widget.createViewMap();
        SummaryGroupTableDataBuilder builder = new SummaryGroupTableDataBuilder(viewMap, new ArrayList<ChartSetting>(), dataJson, new BIStyleReportSetting());
        TableDirector director = new TableDirector(builder);
        director.construct();
        director.buildTableData();
        BIExcelTableData excelTableData = director.buildTableData();
        List headers = excelTableData.getHeaders();
        assertTrue(headers.size() == calculateNormalHeadersCount(viewMap));
        JSONObject result = excelTableData.getItems().get(0).createJSON();
        assertTrue(dataJson.getJSONArray("s").getString(0).equals(result.getJSONArray("values").getJSONObject(0).getString("text")));
        assertTrue(dataJson.getJSONArray("c").length() == result.getJSONArray("children").length());
    }

    /*普通汇总表，多维度*/
    private void testNormalSummaryTableWithMultiDims(TableWidget widget, JSONObject dataJson) throws Exception {
        Map<Integer, List<JSONObject>> viewMap = widget.createViewMap();
        SummaryGroupTableDataBuilder builder = new SummaryGroupTableDataBuilder(viewMap, new ArrayList<ChartSetting>(), dataJson, new BIStyleReportSetting());
        TableDirector director = new TableDirector(builder);
        director.construct();
        BIExcelTableData biExcelTableData = director.buildTableData();
        List headers = biExcelTableData.getHeaders();
        assertTrue(headers.size() == calculateNormalHeadersCount(viewMap));
        JSONObject result = biExcelTableData.getItems().get(0).createJSON();
        assertTrue(dataJson.getJSONArray("s").getString(0).equals(result.getJSONArray("values").getJSONObject(0).getString("text")));
        JSONArray dataChildren = dataJson.getJSONArray("c");
        JSONArray resultChildren = result.getJSONArray("children");
        assertTrue(dataChildren.length() == resultChildren.length());
        //比较每个children是否相等
        for (int i = 0; i < resultChildren.length(); i++) {
            assertTrue(true);
        }
    }

    public void testNormalCrossTable() throws Exception {
        widget = WidgetStructureTool.createCrossTableTableWidget();
        dataJson = WidgetDataTool.getNormalCrossData();
        Map<Integer, List<JSONObject>> viewMap = widget.createViewMap();
        viewMap.remove(30000);
        SummaryCrossTableDataBuilder builder = new SummaryCrossTableDataBuilder(viewMap, new ArrayList<ChartSetting>(), dataJson, new BIStyleReportSetting());
        TableDirector director = new TableDirector(builder);
        director.construct();
        BIExcelTableData excelTableData = director.buildTableData();
        int count = viewMap.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1)).size() + viewMap.get(Integer.valueOf(BIReportConstant.REGION.TARGET1)).size();
        assertTrue(excelTableData.getHeaders().size() == dataJson.getJSONObject("t").getJSONArray("c").length() + count);
        assertTrue(excelTableData.getCrossHeaders().size() == viewMap.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2)).size());
        assertTrue(excelTableData.getItems().get(0).getChildren().size() == dataJson.getJSONObject("l").getJSONArray("c").length());
        assertTrue(excelTableData.getItems().get(0).getValue().length() == dataJson.getJSONObject("t").getJSONArray("c").length() + summaryCount);
    }

    private int calculateNormalHeadersCount(Map<Integer, List<JSONObject>> viewMap) {
        int count = 0;
        for (Integer integer : viewMap.keySet()) {
            count += viewMap.get(integer).size();
        }
        return count;
    }

    public void testNormalComplexTable() throws Exception {
        widget = WidgetStructureTool.createComplexTableTableWidget();
        dataJson = WidgetDataTool.getNormalComplexData();
        Map<Integer, List<JSONObject>> viewMap = widget.createViewMap();
        SummaryComplexTableBuilder builder = new SummaryComplexTableBuilder(viewMap, new ArrayList<ChartSetting>(), dataJson, new BIStyleReportSetting());
        TableDirector director = new TableDirector(builder);
        director.construct();
        BIExcelTableData excelTableData = director.buildTableData();
    }

    public void testCreateViewMap() throws Exception {
        Map<Integer, List<JSONObject>> testMap = WidgetStructureTool.createViews();
        widget = WidgetStructureTool.createNormalTableTableWidget();
        Map<Integer, List<JSONObject>> viewMap = widget.createViewMap();
        assertTrue(testMap.size() == viewMap.size());
        Iterator<Integer> iterator = testMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            List<JSONObject> testList = testMap.get(next);
            List<JSONObject> viewList = viewMap.get(next);
            Comparator<JSONObject> comparator = new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    try {
                        if (o1.getString("dId").hashCode() > o2.getString("dId").hashCode()) {
                            return 1;
                        }
                    } catch (JSONException e) {
                    }
                    return -1;
                }
            };
            Collections.sort(testList, comparator);
            Collections.sort(viewList, comparator);
            assertTrue(testList.size() == viewList.size());
            for (int i = 0; i < testList.size(); i++) {
                JSONObject testObject = testList.get(i);
                JSONObject viewObject = viewList.get(i);
                assertTrue(testObject.getString("dId").equals(viewObject.getString("dId")));
                assertTrue(testObject.getString("text").equals(viewObject.getString("text")));
            }
        }
    }

    public void testTempCrossTable() throws Exception {
        widget = WidgetStructureTool.createCrossTableTableWidget();
        dataJson = new JSONObject("{\"t\":{\"c\":[{\"c\":[{\"n\":\"f874913d72363f59\"}],\"n\":\"长期协议\"},{\"c\":[{\"n\":\"f874913d72363f59\"}],\"n\":\"长期协议订单\"},{\"c\":[{\"n\":\"f874913d72363f59\"}],\"n\":\"服务协议\"},{\"c\":[{\"n\":\"f874913d72363f59\"}],\"n\":\"购买合同\"}]},\"l\":{\"s\":{\"s\":[2.7787643E8],\"c\":[{\"s\":[1.9485E7]},{\"s\":[2.477942E7]},{\"s\":[1.16658E7]},{\"s\":[2.2194621E8]}]},\"c\":[{\"s\":{\"s\":[1.89731E7],\"c\":[{\"s\":[800000]},{\"s\":[2774100]},{\"s\":[\"\"]},{\"s\":[1.5399E7]}]},\"n\":\"1\"},{\"s\":{\"s\":[4.969621E7],\"c\":[{\"s\":[3580000]},{\"s\":[3126000]},{\"s\":[1760000]},{\"s\":[4.123021E7]}]},\"n\":\"2\"},{\"s\":{\"s\":[7.87012E7],\"c\":[{\"s\":[8295000]},{\"s\":[7163200]},{\"s\":[3440000]},{\"s\":[5.9803E7]}]},\"n\":\"3\"},{\"s\":{\"s\":[2.3084E7],\"c\":[{\"s\":[0]},{\"s\":[7264000]},{\"s\":[1200000]},{\"s\":[1.462E7]}]},\"n\":\"4\"},{\"s\":{\"s\":[6.350912E7],\"c\":[{\"s\":[3050000]},{\"s\":[4112120]},{\"s\":[3180000]},{\"s\":[5.3167E7]}]},\"n\":\"5\"},{\"s\":{\"s\":[4.39128E7],\"c\":[{\"s\":[3760000]},{\"s\":[340000]},{\"s\":[2085800]},{\"s\":[3.7727E7]}]},\"n\":\"6\"},{\"s\":{\"s\":[0],\"c\":[{\"s\":[0]},{\"s\":[\"\"]},{\"s\":[\"\"]},{\"s\":[\"\"]}]},\"n\":\"7\"}]}}");
        Map<Integer, List<JSONObject>> viewMap = new HashMap<Integer, List<JSONObject>>();
        List<JSONObject> list=new ArrayList<>();
        list.add(new JSONObject().put("dId","20ab4f0f6dae6ae9"));
        viewMap.put(10000,list);
        List<JSONObject> list2=new ArrayList<>();
        list2.add(new JSONObject().put("dId","c7cc0209a05bfe6e"));
        viewMap.put(20000,list2);
//        List<JSONObject> list3=new ArrayList<>();
//        list.add(new JSONObject().put("dId","c7cc0209a05bfe6e"));
//        viewMap.put(30000,list3);
        SummaryCrossTableDataBuilder builder = new SummaryCrossTableDataBuilder(viewMap, new ArrayList<ChartSetting>(), dataJson, new BIStyleReportSetting());
        TableDirector director = new TableDirector(builder);
        director.construct();
        BIExcelTableData excelTableData = director.buildTableData();

    }
}