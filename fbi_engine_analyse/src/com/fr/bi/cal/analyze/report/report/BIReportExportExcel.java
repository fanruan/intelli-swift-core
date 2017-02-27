package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.base.ScreenResolution;
import com.fr.bi.cal.analyze.cal.result.ComplexAllExpalder;
import com.fr.bi.cal.analyze.report.BIReportor;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.main.impl.BIWorkBook;
import com.fr.bi.cal.report.report.poly.BIPolyWorkSheet;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.general.IOUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.cell.FloatElement;
import com.fr.report.poly.PolyECBlock;
import com.fr.stable.CodeUtils;
import com.fr.stable.Constants;
import com.fr.stable.StringUtils;
import com.fr.stable.unit.FU;
import com.fr.stable.unit.UnitRectangle;
import com.fr.web.core.SessionDealWith;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by AstronautOO7 on 2017/1/11.
 */
public class BIReportExportExcel {

    private String sessionID;
    private BISession session;
    private BIReportNode node;
    private static int bytesLength = 256;
    private ArrayList<BIWidget> widgets = new ArrayList<BIWidget>();
    private JSONArray specialWidgets = JSONArray.create();
    private String PhantomIp = PerformancePlugManager.getInstance().getPhantomServerIP();
    private int PhantomPort = PerformancePlugManager.getInstance().getPhantomServerPort();

    protected BIReport report = new BIReportor();

    public BIReportExportExcel(String sessionID) throws Exception {
        this.sessionID = sessionID;
        this.session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        this.node = session.getReportNode();
        JSONObject widgetsJSON = BIReadReportUtils.getBIReportNodeJSON(node).optJSONObject("widgets");
        Iterator it = widgetsJSON.keys();

        while (it.hasNext()) {
            JSONObject widgetJSON = widgetsJSON.getJSONObject((String) it.next());
            int type = widgetJSON.optInt("type");
            if (BIReportConstant.WIDGET.CONTENT <= type && type <= BIReportConstant.WIDGET.WEB) {
                specialWidgets.put(widgetJSON);
            } else {
                JSONObject exp = new JSONObject("{ type: true, value: [[]]}");
                widgetJSON.put("page", BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE);
                widgetJSON.put("expander", new JSONObject("{ x:" + exp + ", y:" + exp + "}"));
                widgets.add(BIWidgetFactory.parseWidget(widgetJSON, node.getUserId()));
            }
        }
    }

    public ResultWorkBook getExportBook() throws Exception {
        if (widgets.size() == 0 && specialWidgets.length() == 0) {
            return null;
        }
        BIWorkBook wb = new BIWorkBook();
        BIPolyWorkSheet reportSheet = new BIPolyWorkSheet();
        PolyECBlock polyECBlock = createPolyECBlock("Dashboard");
        if (widgets.size() != 0) {
            for (BIWidget widget : widgets) {
                if (widgetHasData(widget)) {
                    if (widget instanceof TableWidget) {
                        polyECBlock.addFloatElement(renderChartPic(widget));
                    } else {
                        polyECBlock.addFloatElement(renderChartPic(widget));
                    }
                } else {
                    polyECBlock.addFloatElement(renderDefaultChartPic(widget));
                }
            }
        }

        if (specialWidgets.length() != 0) {
            for (int i = 0; i < specialWidgets.length(); i++) {
                JSONObject jo = specialWidgets.getJSONObject(i);
                switch (jo.optInt("type")) {
                    case BIReportConstant.WIDGET.CONTENT:
                        polyECBlock.addFloatElement(renderContentWidget(specialWidgets.getJSONObject(i)));
                        break;
                    case BIReportConstant.WIDGET.IMAGE:
                        polyECBlock.addFloatElement(renderImageWidget(specialWidgets.getJSONObject(i)));
                        break;
                    case BIReportConstant.WIDGET.WEB:
                        polyECBlock.addFloatElement(renderWebWidget(specialWidgets.getJSONObject(i)));
                        break;
                }
            }
        }
        //dashboard
        reportSheet.addBlock(polyECBlock);
        wb.addReport("Dashboard", reportSheet);

        createOtherSheets(wb);

        return wb.execute4BI(session.getParameterMap4Execute());
    }

    private FloatElement renderContentWidget(JSONObject wjo) throws JSONException {
        JSONObject contentOptions = JSONObject.create();
        JSONObject jo = JSONObject.create();
        JSONObject bounds = wjo.optJSONObject("bounds");

        jo.put("width", bounds.optInt("width"));
        jo.put("height", bounds.optInt("height"));
        jo.put("content", wjo.optString("content"));
        jo.put("style", wjo.optJSONObject("style"));

        contentOptions.put("contentOptions", jo);

        String base64 = null;

        try {
            base64 = postMessage(contentOptions.toString());
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return createFloatElement(base64Decoder(base64), getWidgetRect(bounds));
    }

    private FloatElement renderImageWidget(JSONObject wjo) {
        BufferedImage sourceImg = null;
        JSONObject bounds = wjo.optJSONObject("bounds");
        try {
            File imageFile = new File(FRContext.getCurrentEnv().getPath() +
                    BIBaseConstant.UPLOAD_IMAGE.IMAGE_PATH + File.separator + wjo.optString("src"));
            if (imageFile.exists()) {
                sourceImg = ImageIO.read(new FileInputStream(imageFile));
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return createFloatElement(sourceImg, getWidgetRect(bounds));
    }

    private FloatElement renderWebWidget(JSONObject wjo) throws JSONException {
        JSONObject jo = JSONObject.create();
        JSONObject webOptions = JSONObject.create();
        JSONObject bounds = wjo.optJSONObject("bounds");

        webOptions.put("width", bounds.optInt("width"));
        webOptions.put("height", bounds.optInt("height"));
        webOptions.put("src", wjo.optString("url"));

        jo.put("webOptions", webOptions);

        String base64 = null;

        try {
            base64 = postMessage(jo.toString());
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return createFloatElement(base64Decoder(base64), getWidgetRect(bounds));
    }

    private FloatElement renderChartPic(BIWidget widget) throws Exception {
        JSONObject jo = JSONObject.create();
        try {
            jo = widget.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionID));
        } catch (Exception exception) {
            BILoggerFactory.getLogger().error(exception.getMessage(), exception);
            jo.put("error", BIPrintUtils.outputException(exception));
        }
        JSONObject options = ((TableWidget) widget).getPostOptions(sessionID);
        Rectangle rect = widget.getRect();
        String postOptions = new JSONObject("{options:" + options + ", width:" + rect.getWidth() +
                ", height:" + rect.getHeight() + "}").toString();
        String base64 = null;
        try {
            base64 = postMessage(postOptions);
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return createFloatElement(base64Decoder(base64), rect);
    }

    private FloatElement renderDefaultChartPic(BIWidget widget) throws IOException, JSONException {
        String imageFolder = FRContext.getCurrentEnv().getPath() +
                "/classes/com/fr/bi/web/images/background/charts";
        String base64 = getDefaultImage(widget.getType(), imageFolder);
        JSONObject imgOptions = JSONObject.create();
        imgOptions.put("base64", base64);
        imgOptions.put("width", widget.getRect().getWidth());
        imgOptions.put("height", widget.getRect().getHeight());
        String getBase64 = null;
        try {
            getBase64 = postMessage(imgOptions.toString());
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return createFloatElement(base64Decoder(getBase64), widget.getRect());
    }

    private BIWorkBook createOtherSheets(BIWorkBook wb) throws CloneNotSupportedException {
        //other sheets
        if (widgets.size() == 0) {
            return wb;
        }
        for (BIWidget widget : widgets) {
            if (widgetHasData(widget)) {
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
        return wb;
    }

    private FloatElement createFloatElement(BufferedImage bufferedImage, Rectangle rect) {
        FloatElement floatElement = new FloatElement(bufferedImage);
        int resolution = ScreenResolution.getScreenResolution();
        floatElement.setWidth(FU.valueOfPix((int) rect.getWidth(), resolution));
        floatElement.setHeight(FU.valueOfPix((int) rect.getHeight(), resolution));
        floatElement.setLeftDistance(FU.valueOfPix((int) rect.getX(), resolution));
        floatElement.setTopDistance(FU.valueOfPix((int) rect.getY(), resolution));
        return floatElement;
    }

    public String postMessage(String message) throws IOException {
        URL url = new URL("http://" + PhantomIp + ":" + PhantomPort + "/");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

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

    private String getDefaultImage(int type, String imageFolder) throws IOException {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(BIReportConstant.WIDGET.ACCUMULATE_COLUMN, "/column_accu.png");
        map.put(BIReportConstant.WIDGET.ACCUMULATE_AREA, "/area_accu.png");
        map.put(BIReportConstant.WIDGET.ACCUMULATE_RADAR, "/radar_accu.png");
        map.put(BIReportConstant.WIDGET.COLUMN, "/column.png");
        map.put(BIReportConstant.WIDGET.LINE, "/line.png");
        map.put(BIReportConstant.WIDGET.AREA, "/area.png");
        map.put(BIReportConstant.WIDGET.PERCENT_ACCUMULATE_COLUMN, "/column_percent.png");
        map.put(BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AREA, "/area_percent.png");
        map.put(BIReportConstant.WIDGET.COMPARE_COLUMN, "/column_compare.png");
        map.put(BIReportConstant.WIDGET.COMPARE_AREA, "/area_compare.png");
        map.put(BIReportConstant.WIDGET.FALL_COLUMN, "/column_fall.png");
        map.put(BIReportConstant.WIDGET.RANGE_AREA, "/area_range.png");
        map.put(BIReportConstant.WIDGET.BAR, "/bar.png");
        map.put(BIReportConstant.WIDGET.ACCUMULATE_BAR, "/bar_accu.png");
        map.put(BIReportConstant.WIDGET.COMPARE_BAR, "/bar_compare.png");
        map.put(BIReportConstant.WIDGET.COMBINE_CHART, "/combine.png");
        map.put(BIReportConstant.WIDGET.DONUT, "/donut.png");
        map.put(BIReportConstant.WIDGET.RADAR, "/radar.png");
        map.put(BIReportConstant.WIDGET.PIE, "/pie.png");
        map.put(BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART, "/combine_m.png");
        map.put(BIReportConstant.WIDGET.FORCE_BUBBLE, "/bubble_force.png");
        map.put(BIReportConstant.WIDGET.DASHBOARD, "/dashboard.png");
        map.put(BIReportConstant.WIDGET.BUBBLE, "/bubble.png");
        map.put(BIReportConstant.WIDGET.SCATTER, "/scatter.png");
        map.put(BIReportConstant.WIDGET.MAP, "/map.png");
        map.put(BIReportConstant.WIDGET.GIS_MAP, "/map_gis.png");
        map.put(BIReportConstant.WIDGET.TABLE, "/table_group.png");
        map.put(BIReportConstant.WIDGET.CROSS_TABLE, "/table_cross.png");
        map.put(BIReportConstant.WIDGET.COMPLEX_TABLE, "/table_complex.png");
        map.put(BIReportConstant.WIDGET.FUNNEL, "/funnel.png");
        map.put(BIReportConstant.WIDGET.PARETO, "");
        map.put(BIReportConstant.WIDGET.HEAT_MAP, "/funnel.png");
        map.put(BIReportConstant.WIDGET.MULTI_PIE, "/multi_pie.png");
        map.put(BIReportConstant.WIDGET.RECT_TREE, "/rect_tree.png");
        return coderBase64(IOUtils.readImage(imageFolder + map.get(type)));
    }

    private String coderBase64(Image image) throws IOException {
        if (image == null) {
            return StringUtils.EMPTY;
        }
        byte[] data = null;
        // 读取图片字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, "png", bos);
        data = bos.toByteArray();
        bos.close();

        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String result = encoder.encode(data);
        result = result.replace("\r\n", "");
        return result;// 返回Base64编码过的字节数组字符串
    }

    private Rectangle getWidgetRect(JSONObject bounds) {
        Rectangle rect = new Rectangle(bounds.optInt("left"), bounds.optInt("top"),
                bounds.optInt("width"), bounds.optInt("height"));
        return rect;
    }
}
