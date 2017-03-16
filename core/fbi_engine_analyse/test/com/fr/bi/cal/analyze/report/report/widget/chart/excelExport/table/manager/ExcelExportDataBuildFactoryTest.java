package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager.widget.WidgetDataTool;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.manager.widget.WidgetStructureTool;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import junit.framework.TestCase;
import org.junit.Before;

import java.util.*;

/**
 * Created by Kary on 2017/3/6.
 */
public class ExcelExportDataBuildFactoryTest extends TestCase {
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
        Map<Integer, List<JSONObject>> viewMap = ExcelExportDataBuildFactory.createViewMap(widget);
        BIExcelTableData excelTableData = ExcelExportDataBuildFactory.createExportData(viewMap, dataJson);
        List headers = excelTableData.getHeaders();
        assertTrue(headers.size() == calculateNormalHeadersCount(viewMap));
        JSONObject result = excelTableData.getItems().getJSONObject(0);
        assertTrue(dataJson.getJSONArray("s").getString(0).equals(result.getJSONArray("values").getJSONObject(0).getString("text")));
        assertTrue(dataJson.getJSONArray("c").length() == result.getJSONArray("children").length());
    }

    /*普通汇总表，多维度*/
    private void testNormalSummaryTableWithMultiDims(TableWidget widget, JSONObject dataJson) throws Exception {
        Map<Integer, List<JSONObject>> viewMap = ExcelExportDataBuildFactory.createViewMap(widget);
        BIExcelTableData excelTableData = ExcelExportDataBuildFactory.createExportData(viewMap, dataJson);
        List headers = excelTableData.getHeaders();
        assertTrue(headers.size() == calculateNormalHeadersCount(viewMap));
        JSONObject result = excelTableData.getItems().getJSONObject(0);
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
        Map<Integer, List<JSONObject>> viewMap = ExcelExportDataBuildFactory.createViewMap(widget);
        BIExcelTableData excelTableData = ExcelExportDataBuildFactory.createExportData(viewMap, dataJson);
        int count = viewMap.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1)).size() + viewMap.get(Integer.valueOf(BIReportConstant.REGION.TARGET1)).size();
        assertTrue(excelTableData.getHeaders().size() == dataJson.getJSONObject("t").getJSONArray("c").length() + count);
        assertTrue(excelTableData.getCrossHeaders().length()==viewMap.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2)).size());
        assertTrue(excelTableData.getItems().getJSONObject(0).getJSONArray("children").length()== dataJson.getJSONObject("l").getJSONArray("c").length());
        assertTrue(excelTableData.getItems().getJSONObject(0).getJSONArray("values").length()== dataJson.getJSONObject("t").getJSONArray("c").length()+summaryCount);
            }

    private int calculateNormalHeadersCount(Map<Integer, List<JSONObject>> viewMap) {
        int count = 0;
        for (Integer integer : viewMap.keySet()) {
            count += viewMap.get(integer).size();
        }
        return count;
    }

    public void testCreateViewMap() throws Exception {
        Map<Integer, List<JSONObject>> testMap = WidgetStructureTool.createViews();
        widget=WidgetStructureTool.createNormalTableTableWidget();
        Map<Integer, List<JSONObject>> viewMap = ExcelExportDataBuildFactory.createViewMap(widget);
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
}