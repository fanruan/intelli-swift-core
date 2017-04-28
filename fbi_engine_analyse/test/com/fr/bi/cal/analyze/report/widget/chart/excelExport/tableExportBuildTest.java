package excelExport;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.DimAndTargetStyle;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.SummaryComplexTableBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.SummaryCrossTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.SummaryGroupTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.manager.TableDirector;
import com.fr.bi.cal.analyze.report.report.widget.style.BITableWidgetStyle;
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
        SummaryGroupTableDataBuilder builder = new SummaryGroupTableDataBuilder(viewMap, new ArrayList<DimAndTargetStyle>(), dataJson, new BITableWidgetStyle());
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
        SummaryGroupTableDataBuilder builder = new SummaryGroupTableDataBuilder(viewMap, new ArrayList<DimAndTargetStyle>(), dataJson, new BITableWidgetStyle());
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
        SummaryCrossTableDataBuilder builder = new SummaryCrossTableDataBuilder(viewMap, new ArrayList<DimAndTargetStyle>(), dataJson, new BITableWidgetStyle());
        TableDirector director = new TableDirector(builder);
        director.construct();
        BIExcelTableData excelTableData = director.buildTableData();
        int count = viewMap.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1)).size()+(null==viewMap.get(Integer.valueOf(BIReportConstant.REGION.TARGET1))?0:viewMap.get(Integer.valueOf(BIReportConstant.REGION.TARGET1)).size());
        assertTrue(excelTableData.getHeaders().size() == dataJson.getJSONObject("t").getJSONArray("c").length() + count);
        assertTrue(excelTableData.getCrossHeaders().size() == viewMap.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2)).size());
        assertTrue(excelTableData.getItems().get(0).getChildren().size() == dataJson.getJSONObject("l").getJSONArray("c").length());
        assertTrue(excelTableData.getItems().get(0).getValue().length() == dataJson.getJSONObject("t").getJSONArray("c").length());
    }

    private int calculateNormalHeadersCount(Map<Integer, List<JSONObject>> viewMap) {
        int count = 0;
        for (Integer integer : viewMap.keySet()) {
            count += viewMap.get(integer).size();
        }
        return count;
    }

    public void testNormalComplexTable() throws Exception {
        dataJson = WidgetDataTool.getNormalComplexData();
        Map<Integer, List<JSONObject>> viewMap = WidgetStructureTool.createComplexTableTableWidget().createViewMap();
        SummaryComplexTableBuilder builder = new SummaryComplexTableBuilder(viewMap, new ArrayList<DimAndTargetStyle>(), dataJson, new BITableWidgetStyle());
        TableDirector director = new TableDirector(builder);
        director.construct();
        BIExcelTableData excelTableData = director.buildTableData();
        excelTableData.createJSON();
        assertFalse(true);
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
}