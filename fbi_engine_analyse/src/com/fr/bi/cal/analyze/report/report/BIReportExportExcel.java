package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.cal.result.ComplexAllExpander;
import com.fr.bi.cal.analyze.report.BIReportor;
import com.fr.bi.cal.analyze.report.report.widget.*;
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
import com.fr.general.Inter;
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
            parseWidget(type, widgetJSON);
        }
    }

    private void parseWidget(int type, JSONObject widgetJo) throws Exception {
        switch (WidgetType.parse(type)) {
            case STRING:
            case NUMBER:
            case TREE:
            case SINGLE_SLIDER:
            case INTERVAL_SLIDER:
            case LIST_LABEL:
            case TREE_LABEL:
            case STRING_LIST:
            case TREE_LIST:
            case DATE:
            case YEAR:
            case QUARTER:
            case MONTH:
            case YMD:
            case DATE_PANE:
//            case QUERY:
//            case RESET:
            case CONTENT:
            case IMAGE:
            case WEB:
//            case GENERAL_QUERY:
//            case TABLE_SHOW:
                specialWidgets.put(widgetJo);
                break;
            default:
                JSONObject exp = new JSONObject("{ type: true, value: [[]]}");
                widgetJo.put("page", BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE);
                widgetJo.put("expander", new JSONObject("{ x:" + exp + ", y:" + exp + "}"));
                widgets.add(BIWidgetFactory.parseWidget(widgetJo, node.getUserId()));
        }
    }

    public ResultWorkBook getExportBook() throws Exception {
        if (widgets.size() == 0 && specialWidgets.length() == 0) {
            return null;
        }
        BIWorkBook wb = new BIWorkBook();
        BIPolyWorkSheet reportSheet = new BIPolyWorkSheet();
        PolyECBlock polyECBlock = BIReportExportExcelUtils.createPolyECBlock("Dashboard");
        polyECBlock.getBlockAttr().setFreezeHeight(true);
        polyECBlock.getBlockAttr().setFreezeWidth(true);
        if (widgets.size() != 0) {
            for (BIWidget widget : widgets) {
                polyECBlock.addFloatElement(renderPicture((TableWidget) widget));
            }
        }
        getSpecialWidgetPic(polyECBlock);

        reportSheet.addBlock(polyECBlock);
        wb.addReport("Dashboard", reportSheet);
        createOtherSheets(wb);
        return wb.execute4BI(session.getParameterMap4Execute());
    }

    private void getSpecialWidgetPic(PolyECBlock polyECBlock) throws JSONException {
        if (specialWidgets.length() != 0) {
            for (int i = 0; i < specialWidgets.length(); i++) {
                JSONObject jo = specialWidgets.getJSONObject(i);
                switch (WidgetType.parse(jo.optInt("type"))) {
                    case CONTENT:
                        polyECBlock.addFloatElement(renderContentWidget(jo));
                        break;
                    case IMAGE:
                        polyECBlock.addFloatElement(renderImageWidget(jo));
                        break;
                    case WEB:
                        polyECBlock.addFloatElement(renderWebWidget(jo));
                        break;
                    case STRING:
                    case LIST_LABEL:
                    case STRING_LIST:
                        renderStringListWidget(polyECBlock, jo);
                        break;
                    case NUMBER:
                    case INTERVAL_SLIDER:
                        renderNumberWidget(polyECBlock, jo);
                    case TREE:
                        renderTreeWidget(polyECBlock, jo);
                    case TREE_LABEL:
                    case TREE_LIST:
                        break;
                    case YEAR:
                        renderYearWidget(polyECBlock, jo);
                        break;
                    case MONTH:
                        renderMonthWidget(polyECBlock, jo);
                        break;
                    case QUARTER:
                        renderQuarterWidget(polyECBlock, jo);
                        break;
                    case YMD:
                    case DATE_PANE:
                        renderDatePaneWidget(polyECBlock, jo);
                        break;
                    case DATE:
                        break;
                }
            }
        }
    }

    private void renderWidget(PolyECBlock polyECBlock, String v, JSONObject jo) {
        String name = jo.optString("name");
        polyECBlock.addFloatElement(BIReportExportExcelUtils.createFloatElement4String(name + "\r\n" + v, jo.optJSONObject("bounds")));
    }

    private void renderStringListWidget(PolyECBlock polyECBlock, JSONObject jo) throws JSONException {
        JSONObject valueJo = jo.optJSONObject("value");
        String v;
        if (jo.optJSONObject("dimensions").length() == 0) {
            v = "";
        } else if (valueJo.length() == 0) {
            v = Inter.getLocText("BI-Unrestricted");
        } else {
            v = (valueJo.optInt("type") == 1 ? Inter.getLocText("BI-Basic_Selected") : Inter.getLocText("BI-Unchosen")) + ":" + valueJo.optJSONArray("value").join(",");
        }
        renderWidget(polyECBlock, v, jo);
    }

    private void renderNumberWidget(PolyECBlock polyECBlock, JSONObject jo) {
        JSONObject valueJo = jo.optJSONObject("value");
        String v;
        if (jo.optJSONObject("dimensions").length() == 0) {
            v = "";
        } else if (valueJo.length() == 0) {
            v = Inter.getLocText("BI-Unrestricted");
        } else {
            String min = valueJo.optString("min", "");
            String closeMin = valueJo.optBoolean("closemin", false) == true ? "<=" : "<";
            String max = valueJo.optString("max", "");
            String closeMax = valueJo.optBoolean("closemax", false) == true ? "<=" : "<";
            v = (min == "" ? Inter.getLocText("BI-Unrestricted") : min) + closeMin + Inter.getLocText("BI-Basic_Value") + closeMax + (max == "" ? Inter.getLocText("BI-Unrestricted") : max);
        }

        renderWidget(polyECBlock, v, jo);
    }

    private void renderTreeWidget(PolyECBlock polyECBlock, JSONObject jo) throws JSONException {
        JSONObject joValue = jo.optJSONObject("value");
        String str = joValue.toString();
        renderWidget(polyECBlock, str.substring(1,str.length()-1).replace(":{}", ""), jo);
    }

    private void renderYearWidget(PolyECBlock polyECBlock, JSONObject jo) {
        String value = "";
        if (jo.optString("value") != null) {
            value = jo.optString("value") + Inter.getLocText("BI-Year");
        }
        renderWidget(polyECBlock, value, jo);
    }

    private void renderMonthWidget(PolyECBlock polyECBlock, JSONObject jo) {
        String value = "";
        JSONObject joValue = jo.optJSONObject("value");
        if (joValue.length() != 0) {
            value = joValue.optString("year") + Inter.getLocText("BI-Year") + (joValue.optInt("month") + 1) + Inter.getLocText("BI-Multi_Date_Month");
        }
        renderWidget(polyECBlock, value, jo);
    }

    private void renderQuarterWidget(PolyECBlock polyECBlock, JSONObject jo) {
        JSONObject joValue = jo.optJSONObject("value");
        String value = "";
        if (joValue.length() != 0) {
            value = joValue.optString("year") + Inter.getLocText("BI-Year") + joValue.optString("quarter") + Inter.getLocText("BI-Basic_Quarter");
        }
        renderWidget(polyECBlock, value, jo);
    }

    private void renderDatePaneWidget(PolyECBlock polyECBlock, JSONObject jo) {
        String value = "";
        JSONObject joValue = jo.optJSONObject("value");
        if (joValue.length() != 0) {
            value = joValue.optString("year") + Inter.getLocText("BI-Year") + (joValue.optInt("month") + 1) + Inter.getLocText("BI-Multi_Date_Month") + joValue.optString("day") + Inter.getLocText("BI-Day_Ri");
        }
        renderWidget(polyECBlock, value, jo);
    }

    private FloatElement renderPicture(TableWidget widget) throws Exception {
        if (BIReportExportExcelUtils.widgetHasData(widget)) {
            return renderDefaultChartPic(widget);
        }

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
