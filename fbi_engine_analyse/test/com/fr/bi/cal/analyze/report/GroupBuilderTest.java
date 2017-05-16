import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.SummaryGroupTableDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BITableDataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.BITableConstructHelper;
import com.fr.bi.cal.analyze.report.report.widget.style.BITableWidgetStyle;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

/**
 * Created by Kary on 2017/5/16.
 */
public class GroupBuilderTest extends TestCase {

    public void testNoneDimDataWithNoExpand() throws Exception {
        String viewMapStr = BuilderDataFactory.GROUP.NODIM_NOEXPAND.VIEWMAP;
        String dataStr = BuilderDataFactory.GROUP.NODIM_NOEXPAND.DATA;
        IExcelDataBuilder builder = new SummaryGroupTableDataBuilder(BuilderTestUtils.createViewMap(viewMapStr), new JSONObject(dataStr), new BITableWidgetStyle());
        BITableDataConstructor data = BITableConstructHelper.buildTableData(builder);
        assertTrue(data.getItems().size()==1);
        assertTrue(data.getItems().get(0).getChildren().get(0).getValues().size()+1==data.getHeaders().size());
    }
    public void testNormalDataWithNoExpand() throws Exception {
        String viewMapStr = BuilderDataFactory.GROUP.NOTAR_NOEXPAND.VIEWMAP;
        String dataStr = BuilderDataFactory.GROUP.NOTAR_NOEXPAND.DATA;
        IExcelDataBuilder builder = new SummaryGroupTableDataBuilder(BuilderTestUtils.createViewMap(viewMapStr), new JSONObject(dataStr), new BITableWidgetStyle());
        BITableDataConstructor data = BITableConstructHelper.buildTableData(builder);
        assertTrue(data.getItems().get(0).getValues().size()== BuilderTestUtils.createViewMap(viewMapStr).get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1)).size());
        assertTrue(data.getItems().get(0).getChildren().size()==data.getHeaders().size());
    }

    public void testNoneTargetlDataWithNoExpand() throws Exception {
        String viewMapStr = BuilderDataFactory.GROUP.NORMAL_NOEXPAND.VIEWMAP;
        String dataStr = BuilderDataFactory.GROUP.NORMAL_NOEXPAND.DATA;
        IExcelDataBuilder builder = new SummaryGroupTableDataBuilder(BuilderTestUtils.createViewMap(viewMapStr), new JSONObject(dataStr), new BITableWidgetStyle());
        BITableDataConstructor data = BITableConstructHelper.buildTableData(builder);
        assertTrue(data.getHeaders().size()== BuilderTestUtils.createViewMap(viewMapStr).get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1)).size());
    }
}
