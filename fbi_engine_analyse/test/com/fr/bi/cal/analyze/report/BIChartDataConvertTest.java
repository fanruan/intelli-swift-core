package cal.analyze.report;

import com.fr.base.CoreDecimalFormat;
import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.AccumulateAxisChartSetting;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.json.JSONException;
import junit.framework.TestCase;

import java.text.DecimalFormat;

/**
 *
 */
public class BIChartDataConvertTest extends TestCase {

    /**
     * CoreDecimalFormat的format
     */
    public void testDecimalFormat () {
        DecimalFormat format = new CoreDecimalFormat(new DecimalFormat("#,###.##%"), "");
        format.setGroupingUsed(false);
        System.out.println(format.format(8.326672684688674e-16));
        System.out.println(format.format(0.47566));
        System.out.println(format.format(1234567));
    }

    public void testAccumulateAxisStructure(){
        double a = Double.NaN;
        System.out.println(Double.isNaN(a));
    }
}