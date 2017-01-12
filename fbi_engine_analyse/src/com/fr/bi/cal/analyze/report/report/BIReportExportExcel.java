package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.ScreenResolution;
import com.fr.bi.cal.analyze.cal.result.ComplexAllExpalder;
import com.fr.bi.cal.analyze.report.BIReportor;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.MultiChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartDataConvertFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartSettingFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.main.impl.BIWorkBook;
import com.fr.bi.cal.report.report.poly.BIPolyWorkSheet;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.general.IOUtils;
import com.fr.json.JSONObject;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.cell.FloatElement;
import com.fr.report.poly.PolyECBlock;
import com.fr.stable.CodeUtils;
import com.fr.stable.Constants;
import com.fr.stable.unit.FU;
import com.fr.stable.unit.UnitRectangle;
import com.fr.web.core.SessionDealWith;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by AstronautOO7 on 2017/1/11.
 */
public class BIReportExportExcel {
    protected BIReport report = new BIReportor();

    private String sessionID;

    private BISession session;

    private BIReportNode node;

    private static int bytesLength = 256;

    private ArrayList<BIWidget> widgets = new ArrayList<BIWidget>();

    public BIReportExportExcel(String sessionID) throws Exception {
        this.sessionID = sessionID;
        this.session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        this.node = session.getReportNode();
        JSONObject widgetsJSON = BIReadReportUtils.getBIReportNodeJSON(node).optJSONObject("widgets");
        Iterator it = widgetsJSON.keys();

        while (it.hasNext()) {
            JSONObject widgetJSON = widgetsJSON.getJSONObject((String) it.next());
            widgets.add(BIWidgetFactory.parseWidget(widgetJSON, node.getUserId()));
        }
    }

    public ResultWorkBook getExportBook() throws Exception {
        if (widgets.size() == 0) {
            return null;
        }
        BIWorkBook wb = new BIWorkBook();
        BIPolyWorkSheet reportSheet = new BIPolyWorkSheet();
        PolyECBlock polyECBlock = createPolyECBlock("Dashboard");

        for (BIWidget widget : widgets) {
            if (widgetHasData(widget)) {
                JSONObject jo = JSONObject.create();
                try {
                    jo = widget.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionID));
                } catch (Exception exception) {
                    BILoggerFactory.getLogger().error(exception.getMessage(), exception);
                    jo.put("error", BIPrintUtils.outputException(exception));
                }

                JSONObject configs = BIChartDataConvertFactory.convert((MultiChartWidget) widget, jo.optJSONObject("data"));
                JSONObject chartOptions = BIChartSettingFactory.parseChartSetting((MultiChartWidget) widget, configs.getJSONArray("data"), configs.optJSONObject("options"), configs.getJSONArray("types"));
                //将plotOptions下的animation设为false否则不能截图（只截到网格线）
                JSONObject plotOptions = (JSONObject) chartOptions.get("plotOptions");
                plotOptions.put("animation", false);
                chartOptions.put("plotOptions", plotOptions);
                String base64 = null;
                try {
                    base64 = postMessage(PerformancePlugManager.getInstance().getPhantomServerIP(), PerformancePlugManager.getInstance().getPhantomServerPort(), new JSONObject("{" + "options:" + chartOptions + "}").toString());
                } catch (IOException e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }

                BufferedImage img = base64Decoder(base64);
                FloatElement floatElement = new FloatElement(img);
                int resolution = ScreenResolution.getScreenResolution();
                floatElement.setWidth(FU.valueOfPix(img.getWidth(null), resolution));
                floatElement.setHeight(FU.valueOfPix(img.getHeight(null), resolution));
                polyECBlock.addFloatElement(floatElement);
            } else {
                //todo data 为空时 从resources读取图片

            }

        }

        reportSheet.addBlock(polyECBlock);
        wb.addReport("Dashboard", reportSheet);
        for (BIWidget widget : widgets) {
            if(widgetHasData(widget)) {
                widget = (BIWidget) widget.clone();
                switch (widget.getType()) {
                    case BIReportConstant.WIDGET.TABLE:
                    case BIReportConstant.WIDGET.CROSS_TABLE:
                    case BIReportConstant.WIDGET.COMPLEX_TABLE:
                        ((TableWidget) widget).setComplexExpander(new ComplexAllExpalder());
                        ((TableWidget) widget).setOperator(BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE);
                        break;
                    case BIReportConstant.WIDGET.DETAIL:
                        ((BIDetailWidget) widget).setPage(BIExcutorConstant.PAGINGTYPE.NONE);
                        break;
                }

                BIPolyWorkSheet ws = widget.createWorkSheet(session);
                wb.addReport(widget.getWidgetName(), ws);
            } else {
                BIPolyWorkSheet emptySheet = new BIPolyWorkSheet();
                emptySheet.addBlock(createPolyECBlock(widget.getWidgetName()));
                wb.addReport(widget.getWidgetName(), emptySheet);
            }
        }

        return wb.execute4BI(session.getParameterMap4Execute());
    }

    public String postMessage(String ip, int port, String message) throws IOException {
        URL url = new URL("http://" + ip + ":" + port + "/");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setConnectTimeout(50000);
        connection.setReadTimeout(50000);

        OutputStream out = connection.getOutputStream();
        out.write(message.getBytes("utf-8"));
        out.close();

        //get base64 picture
        InputStream in = connection.getInputStream();
        String response = IOUtils.inputStream2String(in);
        in.close();

        return response;
    }

    public BufferedImage base64Decoder(String base64) {
        BASE64Decoder decoder = new BASE64Decoder();
        BufferedImage img = null;
        try {
            // decode Base64
            byte[] bytes = decoder.decodeBuffer(base64);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += bytesLength;
                }
            }
            InputStream inputStream = new ByteArrayInputStream(bytes, 0, bytes.length);

            img = ImageIO.read(inputStream);
        } catch (Exception e) {
            return null;
        }
        return img;
    }

    private PolyECBlock createPolyECBlock(String widgetName) {
        PolyECBlock polyECBlock = new PolyECBlock();
        polyECBlock.setBlockName(CodeUtils.passwordEncode(CodeUtils.passwordEncode(widgetName)));
        polyECBlock.getBlockAttr().setFreezeHeight(true);
        polyECBlock.getBlockAttr().setFreezeWidth(true);
        polyECBlock.setBounds(new UnitRectangle(new Rectangle(), Constants.DEFAULT_WEBWRITE_AND_SCREEN_RESOLUTION));
        return polyECBlock;
    }

    private boolean widgetHasData(BIWidget widget) {
        return (widget.getViewDimensions().length + widget.getViewTargets().length) != 0;
    }
}
