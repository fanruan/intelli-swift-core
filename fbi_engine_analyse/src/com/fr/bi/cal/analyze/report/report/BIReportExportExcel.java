package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
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
import com.fr.json.JSONException;
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
            JSONObject exp = new JSONObject("{ type: true, value: [[]]}");
            widgetJSON.put("page", BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE);
            widgetJSON.put("expander", new JSONObject("{ x:" + exp + ", y:" + exp + "}"));
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
                polyECBlock.addFloatElement(renderChartPic(widget));
            } else {
                polyECBlock.addFloatElement(renderDefaultChartPic(widget));
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

    private FloatElement renderChartPic (BIWidget widget) throws JSONException {
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
        Rectangle rect = widget.getRect();
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        String postOptions = new JSONObject("{" + "options:" + chartOptions + ", width:" + rect.getWidth() + ", height:" + rect.getHeight() + "}").toString();
        String base64 = null;
        try {
            base64 = postMessage(PerformancePlugManager.getInstance().getPhantomServerIP(), PerformancePlugManager.getInstance().getPhantomServerPort(), postOptions);
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return createFloatElement(base64Decoder(base64), rect);
    }

    private FloatElement renderDefaultChartPic (BIWidget widget) {
        String imageFolder = FRContext.getCurrentEnv().getPath() + "/classes/com/fr/bi/web/images/background/charts";
        BufferedImage img = getDefaultImage(widget.getType(), imageFolder);
        return createFloatElement(img, widget.getRect());
    }

    private FloatElement createFloatElement (BufferedImage img, Rectangle rect) {
        FloatElement floatElement = new FloatElement(img);
        int resolution = ScreenResolution.getScreenResolution();
        floatElement.setWidth(FU.valueOfPix(img.getWidth(null), resolution));
        floatElement.setHeight(FU.valueOfPix(img.getHeight(null), resolution));
        floatElement.setLeftDistance(FU.valueOfPix((int) rect.getX(), resolution));
        floatElement.setTopDistance(FU.valueOfPix((int) rect.getY(), resolution));
        return floatElement;
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

    private BufferedImage getDefaultImage (int type, String imageFolder) {
        BufferedImage img = null;
        switch (type) {
            case BIReportConstant.WIDGET.ACCUMULATE_AXIS:
                img = IOUtils.readImage(imageFolder + "/axis_accu.png");
                break;
            case BIReportConstant.WIDGET.ACCUMULATE_AREA:
                img = IOUtils.readImage(imageFolder + "/area_accu.png");
                break;
            case BIReportConstant.WIDGET.ACCUMULATE_RADAR:
                img = IOUtils.readImage(imageFolder + "/radar_accu.png");
                break;
            case BIReportConstant.WIDGET.AXIS:
                img = IOUtils.readImage(imageFolder + "/axis.png");
                break;
            case BIReportConstant.WIDGET.LINE:
                img = IOUtils.readImage(imageFolder + "/line.png");
                break;
            case BIReportConstant.WIDGET.AREA:
                img = IOUtils.readImage(imageFolder + "/area.png");
                break;
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AXIS:
                img = IOUtils.readImage(imageFolder + "/axis_percent.png");
                break;
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AREA:
                img = IOUtils.readImage(imageFolder + "/area_percent.png");
                break;
            case BIReportConstant.WIDGET.COMPARE_AXIS:
                img = IOUtils.readImage(imageFolder + "/axis_compare.png");
                break;
            case BIReportConstant.WIDGET.COMPARE_AREA:
                img = IOUtils.readImage(imageFolder + "/area_compare.png");
                break;
            case BIReportConstant.WIDGET.FALL_AXIS:
                img = IOUtils.readImage(imageFolder + "/axis_fall.png");
                break;
            case BIReportConstant.WIDGET.RANGE_AREA:
                img = IOUtils.readImage(imageFolder + "/area_range.png");
                break;
            case BIReportConstant.WIDGET.BAR:
                img = IOUtils.readImage(imageFolder + "/bar.png");
                break;
            case BIReportConstant.WIDGET.ACCUMULATE_BAR:
                img = IOUtils.readImage(imageFolder + "/bar_accu.png");
                break;
            case BIReportConstant.WIDGET.COMPARE_BAR:
                img = IOUtils.readImage(imageFolder + "/bar_compare.png");
                break;
            case BIReportConstant.WIDGET.COMBINE_CHART:
                img = IOUtils.readImage(imageFolder + "/combine.png");
                break;
            case BIReportConstant.WIDGET.DONUT:
                img = IOUtils.readImage(imageFolder + "/donut.png");
                break;
            case BIReportConstant.WIDGET.RADAR:
                img = IOUtils.readImage(imageFolder + "/radar.png");
                break;
            case BIReportConstant.WIDGET.PIE:
                img = IOUtils.readImage(imageFolder + "/pie.png");
                break;
            case BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART:
                img = IOUtils.readImage(imageFolder + "/combine_m.png");
                break;
            case BIReportConstant.WIDGET.FORCE_BUBBLE:
                img = IOUtils.readImage(imageFolder + "/bubble_force.png");
                break;
            case BIReportConstant.WIDGET.DASHBOARD:
                img = IOUtils.readImage(imageFolder + "/dashboard.png");
                break;
            case BIReportConstant.WIDGET.BUBBLE:
                img = IOUtils.readImage(imageFolder + "/bubble.png");
                break;
            case BIReportConstant.WIDGET.SCATTER:
                img = IOUtils.readImage(imageFolder + "/scatter.png");
                break;
            case BIReportConstant.WIDGET.MAP:
                img = IOUtils.readImage(imageFolder + "/map.png");
                break;
            case BIReportConstant.WIDGET.GIS_MAP:
                img = IOUtils.readImage(imageFolder + "/map_gis.png");
                break;
            case BIReportConstant.WIDGET.TABLE:
                img = IOUtils.readImage(imageFolder + "/table_group.png");
                break;
            case BIReportConstant.WIDGET.CROSS_TABLE:
                img = IOUtils.readImage(imageFolder + "/table_cross.png");
                break;
            case BIReportConstant.WIDGET.COMPLEX_TABLE:
                img = IOUtils.readImage(imageFolder + "/table_complex.png");
                break;
            case BIReportConstant.WIDGET.FUNNEL:
                img = IOUtils.readImage(imageFolder + "/funnel.png");
                break;
            case BIReportConstant.WIDGET.PARETO:
                //todo 添加默认图片
                img = IOUtils.readImage(imageFolder + "/map_gis.png");
                break;
            case BIReportConstant.WIDGET.HEAT_MAP:
                //todo 添加默认图片
                img = IOUtils.readImage(imageFolder + "/map_gis.png");
                break;
            case BIReportConstant.WIDGET.MULTI_PIE:
                img = IOUtils.readImage(imageFolder + "/multi_pie.png");
                break;
            case BIReportConstant.WIDGET.RECT_TREE:
                img = IOUtils.readImage(imageFolder + "/rect_tree.png");
                break;

        }
        return img;
    }
}
