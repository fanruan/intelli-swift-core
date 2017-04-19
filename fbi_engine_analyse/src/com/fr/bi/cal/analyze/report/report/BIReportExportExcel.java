package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.cal.result.ComplexAllExpander;
import com.fr.bi.cal.analyze.report.BIReportor;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.main.impl.BIWorkBook;
import com.fr.bi.cal.report.report.poly.BIPolyWorkSheet;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.cell.FloatElement;
import com.fr.report.poly.PolyECBlock;
import com.fr.web.core.SessionDealWith;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by AstronautOO7 on 2017/1/11.
 */
public class BIReportExportExcel {

    private String sessionID;
    private BISession session;
    private BIReportNode node;
    private ArrayList<BIWidget> widgets = new ArrayList<BIWidget>();
    private JSONArray specialWidgets = JSONArray.create();

    private int namePosLeft = 21;

    protected BIReport report = new BIReportor();

    public BIReportExportExcel(String sessionID) throws Exception {
        this.sessionID = sessionID;
        this.session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        this.node = session.getReportNode();
        JSONObject widgetsJSON = BIReadReportUtils.getInstance().getBIReportNodeJSON(node).optJSONObject("widgets");
        Iterator it = widgetsJSON.keys();

        while (it.hasNext()) {
            JSONObject widgetJSON = widgetsJSON.getJSONObject((String) it.next());
            int type = widgetJSON.optInt("type");
            if (WidgetType.CONTENT.getType() <= type && type <= WidgetType.WEB.getType()) {
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
        PolyECBlock polyECBlock = BIReportExportExcelUtils.createPolyECBlock("Dashboard");
        if (widgets.size() != 0) {
            for (BIWidget widget : widgets) {
                if (BIReportExportExcelUtils.widgetHasData(widget)) {
                    if (widget instanceof TableWidget) {
                        polyECBlock.addFloatElement(renderPicture((TableWidget) widget));
                    }
                } else {
                    polyECBlock.addFloatElement(renderDefaultChartPic(widget));
                }
            }
        }

        if (specialWidgets.length() != 0) {
            for (int i = 0; i < specialWidgets.length(); i++) {
                JSONObject jo = specialWidgets.getJSONObject(i);
                switch (WidgetType.parse(jo.optInt("type"))) {
                    case CONTENT:
                        polyECBlock.addFloatElement(renderContentWidget(specialWidgets.getJSONObject(i)));
                        break;
                    case IMAGE:
                        polyECBlock.addFloatElement(renderImageWidget(specialWidgets.getJSONObject(i)));
                        break;
                    case WEB:
                        polyECBlock.addFloatElement(renderWebWidget(specialWidgets.getJSONObject(i)));
                        break;
                }
            }
        }
//        DefaultTemplateCellElement cell = new DefaultTemplateCellElement((int) Math.floor(350/100) + 1, (int) Math.floor(550/25) + 1, 1, 1, 123);
//        polyECBlock.addCellElement(cell);

        //dashboard
        reportSheet.addBlock(polyECBlock);
        wb.addReport("Dashboard", reportSheet);

        createOtherSheets(wb);

        return wb.execute4BI(session.getParameterMap4Execute());
    }

    private FloatElement renderPicture(TableWidget widget) throws Exception {

        JSONObject options;
        String key;
        if (widget instanceof VanChartWidget) {
            JSONObject jo = JSONObject.create();
            try {
                jo = widget.createDataJSON(session);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            JSONObject plotOptions = (JSONObject) jo.opt("plotOptions");
            plotOptions.put("animation", false);
            options = jo.put("plotOptions", plotOptions);
            key = "vanChartOptions";
        } else {
            options = widget.getPostOptions(sessionID);
            key = "tableOptions";
        }
        JSONObject titleParams = JSONObject.create();
        titleParams.put("text", widget.getWidgetName());
        String nameTextAlign = widget.getChartSetting().getDetailChartSetting().optInt("namePos", namePosLeft) == namePosLeft ? "left" : "center";
        titleParams.put("textAlign", nameTextAlign);
        Rectangle rect = widget.getRect();
        String postOptions = new JSONObject("{" + key + ":" + options + ", width:" + rect.getWidth() +
                ", height:" + rect.getHeight() + ", titleParams:" + titleParams + "}").toString();
        String base64 = null;
        try {
            base64 = BIReportExportExcelUtils.postMessage(postOptions);
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return BIReportExportExcelUtils.createFloatElement(base64, rect);
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
            base64 = BIReportExportExcelUtils.postMessage(contentOptions.toString());
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return BIReportExportExcelUtils.createFloatElement(base64, bounds);
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
        return BIReportExportExcelUtils.createFloatElement(sourceImg, bounds);
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
            base64 = BIReportExportExcelUtils.postMessage(jo.toString());
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return BIReportExportExcelUtils.createFloatElement(base64, bounds);
    }

    private FloatElement renderDefaultChartPic(BIWidget widget) throws IOException, JSONException {
        String imageFolder = FRContext.getCurrentEnv().getPath() +
                "/classes/com/fr/bi/web/images/background/charts";
        String base64 = BIReportExportExcelUtils.getDefaultImage(widget.getType(), imageFolder);
        JSONObject imgOptions = JSONObject.create();
        imgOptions.put("base64", base64);
        imgOptions.put("width", widget.getRect().getWidth());
        imgOptions.put("height", widget.getRect().getHeight());
        String finalBase64 = null;
        try {
            finalBase64 = BIReportExportExcelUtils.postMessage(imgOptions.toString());
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return BIReportExportExcelUtils.createFloatElement(finalBase64, widget.getRect());
    }

    private BIWorkBook createOtherSheets(BIWorkBook wb) throws CloneNotSupportedException {
        //other sheets
        if (widgets.size() == 0) {
            return wb;
        }
        for (BIWidget widget : widgets) {
            if (BIReportExportExcelUtils.widgetHasData(widget)) {
                widget = (BIWidget) widget.clone();
                switch (widget.getType()) {
                    case TABLE:
                    case CROSS_TABLE:
                    case COMPLEX_TABLE:
                        ((TableWidget) widget).setComplexExpander(new ComplexAllExpander());
                        ((TableWidget) widget).setOperator(BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE);
                        break;
                    case DETAIL:
                        ((BIDetailWidget) widget).setPage(BIExcutorConstant.PAGINGTYPE.NONE);
                        break;
                }

                BIPolyWorkSheet ws = widget.createWorkSheet(session);
                wb.addReport(widget.getWidgetName(), ws);
            } else {
                BIPolyWorkSheet emptySheet = new BIPolyWorkSheet();
                emptySheet.addBlock(BIReportExportExcelUtils.createPolyECBlock(widget.getWidgetName()));
                wb.addReport(widget.getWidgetName(), emptySheet);
            }
        }
        return wb;
    }
}
