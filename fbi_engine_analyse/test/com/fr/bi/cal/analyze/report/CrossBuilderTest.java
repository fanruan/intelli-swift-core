import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.SummaryCrossTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.constructor.DataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.BITableConstructHelper;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

/**
 * Created by Kary on 2017/5/16.
 */
public class CrossBuilderTest extends TestCase{
    public void testNormalWithNoExpand() throws Exception {
        String viewMapStr = BuilderDataFactory.CROSS.NORMAL_NOEXPAND.VIEWMAP;
        String dataStr = BuilderDataFactory.CROSS.NORMAL_NOEXPAND.DATA;
        IExcelDataBuilder builder = new SummaryCrossTableDataBuilder(BuilderTestUtils.createViewMap(viewMapStr), new JSONObject(dataStr), new BITableWidgetStyle());
        DataConstructor data = BITableConstructHelper.buildTableData(builder);
        assertTrue(data.getCrossHeaders().size()==BuilderTestUtils.createViewMap(viewMapStr).get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2)).size());
    }

    public void testNoneDimWithNoExpand() throws Exception {
        String viewMapStr = BuilderDataFactory.CROSS.NODIM_NOEXPAND.VIEWMAP;
        String dataStr = BuilderDataFactory.CROSS.NODIM_NOEXPAND.DATA;
        IExcelDataBuilder builder = new SummaryCrossTableDataBuilder(BuilderTestUtils.createViewMap(viewMapStr), new JSONObject(dataStr), new BITableWidgetStyle());
        DataConstructor data = BITableConstructHelper.buildTableData(builder);
        assertTrue(data.getCrossHeaders().size()==BuilderTestUtils.createViewMap(viewMapStr).get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2)).size());
        assertTrue(data.getItems().size()==BuilderTestUtils.createViewMap(viewMapStr).get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2)).size());
    }

    public void testNoneTarWithNoExpand() throws Exception {
        String viewMapStr = BuilderDataFactory.CROSS.NOTAR_DATA_NOEXPAND.VIEWMAP;
        String dataStr = BuilderDataFactory.CROSS.NOTAR_DATA_NOEXPAND.DATA;
        IExcelDataBuilder builder = new SummaryCrossTableDataBuilder(BuilderTestUtils.createViewMap(viewMapStr), new JSONObject(dataStr), new BITableWidgetStyle());
        DataConstructor data = BITableConstructHelper.buildTableData(builder);
        assertTrue(data.getCrossHeaders().size()==BuilderTestUtils.createViewMap(viewMapStr).get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2)).size());
    }
}
