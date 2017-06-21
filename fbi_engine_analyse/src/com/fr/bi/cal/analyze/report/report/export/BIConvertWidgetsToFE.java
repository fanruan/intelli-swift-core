package com.fr.bi.cal.analyze.report.report.export;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.base.PaperSize;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.report.report.export.utils.BIReportExportExcelUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.bi.util.BIReadReportUtils;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.cell.FloatElement;
import com.fr.stable.Constants;
import com.fr.stable.unit.FU;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * 通过phantomjs将report上的widgets转化成floatElement
 * Created by astronaut007 on 2017/6/21.
 */
public class BIConvertWidgetsToFE {

    private BISession session;
    private BIReportNode node;
    private ArrayList<BIWidget> widgets = new ArrayList<BIWidget>();
    private JSONArray specialWidgets = JSONArray.create();
    private ArrayList<FloatElement> floatElements = new ArrayList<FloatElement>();
    private JSONObject paperSize = JSONObject.create();
    private int namePosLeft = BIStyleConstant.DASHBOARD_WIDGET_NAME_POS_LEFT;
    private int adjustedPix = 55;
    private int offSet1 = 1;
    private int offSet3 = 3;
    private int offSet7 = 7;

    public BIConvertWidgetsToFE(BISession session, HttpServletRequest req) throws Exception {
        this.session = session;
        this.node = session.getReportNode();
        JSONObject widgetsJSON = BIReadReportUtils.getInstance().getBIReportNodeJSON(node).optJSONObject("widgets");
        Iterator it = widgetsJSON.keys();

        while (it.hasNext()) {
            JSONObject widgetJSON = widgetsJSON.getJSONObject((String) it.next());
            int type = widgetJSON.optInt("type");
            setPaperSize(widgetJSON);
            parseWidget(type, widgetJSON);
        }

        renderSpecialWidgetFL();
        renderCommonWidgetsFL(req);
    }

    public ArrayList<BIWidget> getCommonWidgets() {
        return widgets;
    }

    public ArrayList<FloatElement> getFloatElements() {
        return floatElements;
    }

    public boolean isReportEmpty() {
        return widgets.size() == 0 && specialWidgets.length() == 0;
    }

    public PaperSize getPaperSize() {
        int resolution = Constants.DEFAULT_WEBWRITE_AND_SCREEN_RESOLUTION;
        return new PaperSize(FU.valueOfPix(paperSize.optInt("width") + adjustedPix, resolution), FU.valueOfPix(paperSize.optInt("height") + adjustedPix, resolution));
    }

    private void setPaperSize(JSONObject widgetJSON) throws JSONException {
        long paperWidth = paperSize.optLong("width", 0);
        long paperHeight = paperSize.optLong("height", 0);

        JSONObject rect = widgetJSON.optJSONObject("bounds");
        long width = rect.getLong("width") + rect.optLong("left");
        long height = rect.optLong("height") + rect.optLong("top");
        paperSize.put("width", paperWidth < width ? width : paperWidth);
        paperSize.put("height", paperHeight < height ? height : paperHeight);
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
            case CONTENT:
            case IMAGE:
            case WEB:
                specialWidgets.put(widgetJo);
                break;
            default:
                JSONObject exp = new JSONObject("{ type: true, value: [[]]}");
                widgetJo.put("page", BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE);
                widgetJo.put("expander", new JSONObject("{ x:" + exp + ", y:" + exp + "}"));
                widgets.add(BIWidgetFactory.parseWidget(widgetJo, node.getUserId()));
        }
    }

    //fixme wiget缺乏一个统一入口
    private void renderCommonWidgetsFL(HttpServletRequest req) throws Exception {
        for (BIWidget widget : widgets) {
            if (!BIReportExportExcelUtils.widgetHasData(widget)) {
                floatElements.add(BIReportExportExcelUtils.createFloatElement("", widget.getRect()));
                return;
            }
            JSONObject options;
            String key;
            if (widget instanceof VanChartWidget) {
                JSONObject jo = JSONObject.create();
                try {
                    jo = widget.createDataJSON(session, req);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
                JSONObject plotOptions = (JSONObject) jo.opt("plotOptions");
                plotOptions.put("animation", false);
                options = jo.put("plotOptions", plotOptions);
                key = "vanChartOptions";
            } else {
                options = widget.getPostOptions(session, req);
                key = "tableOptions";
            }
            JSONObject titleParams = JSONObject.create();
            titleParams.put("text", widget.getWidgetName());
            if (widget instanceof TableWidget) {
                String nameTextAlign = ((TableWidget) widget).getChartSetting().getDetailChartSetting().optInt("namePos", namePosLeft) == namePosLeft ? "left" : "center";
                titleParams.put("textAlign", nameTextAlign);
            }
            Rectangle rect = widget.getRect();
            String postOptions = new JSONObject("{" + key + ":" + options + ", width:" + rect.getWidth() +
                    ", height:" + rect.getHeight() + ", titleParams:" + titleParams + "}").toString();
            String base64 = null;
            try {
                base64 = BIReportExportExcelUtils.postMessage(postOptions);
            } catch (IOException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            floatElements.add(BIReportExportExcelUtils.createFloatElement(base64, rect));
        }
    }

    private void renderSpecialWidgetFL() throws JSONException {
        if (specialWidgets.length() != 0) {
            for (int i = 0; i < specialWidgets.length(); i++) {
                JSONObject jo = specialWidgets.getJSONObject(i);
                switch (WidgetType.parse(jo.optInt("type"))) {
                    case CONTENT:
                        renderContentWidget(jo);
                        break;
                    case IMAGE:
                        renderImageWidget(jo);
                        break;
                    case WEB:
                        renderWebWidget(jo);
                        break;
                    case STRING:
                    case LIST_LABEL:
                    case STRING_LIST:
                        renderStringListWidget(jo);
                        break;
                    case NUMBER:
                    case INTERVAL_SLIDER:
                        renderNumberWidget(jo);
                        break;
                    case TREE:
                    case TREE_LIST:
                        renderTreeWidget(jo);
                        break;
                    case TREE_LABEL:
                        renderTreeLabelWidget(jo);
                        break;
                    case YEAR:
                        renderYearWidget(jo);
                        break;
                    case MONTH:
                        renderMonthWidget(jo);
                        break;
                    case QUARTER:
                        renderQuarterWidget(jo);
                        break;
                    case YMD:
                        renderYMDWidget(jo);
                    case DATE_PANE:
                        renderDatePaneWidget(jo);
                        break;
                    case DATE:
                        renderDateWidget(jo);
                        break;
                }
            }
        }
    }

    private void renderContentWidget(JSONObject wjo) throws JSONException {
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

        floatElements.add(BIReportExportExcelUtils.createFloatElement(base64, bounds));
    }

    private void renderImageWidget(JSONObject wjo) {
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
        floatElements.add(BIReportExportExcelUtils.createFloatElement(sourceImg, bounds));
    }

    private void renderWebWidget(JSONObject wjo) throws JSONException {
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

        floatElements.add(BIReportExportExcelUtils.createFloatElement(base64, bounds));
    }

    private void renderWidget(String v, JSONObject jo) {
        String name = jo.optString("name");
        floatElements.add(BIReportExportExcelUtils.createFloatElement4String(name + "\r\n" + v, jo.optJSONObject("bounds")));
    }

    private void renderStringListWidget(JSONObject jo) throws JSONException {
        JSONObject valueJo = jo.optJSONObject("value");
        String v;
        if (jo.optJSONObject("dimensions").length() == 0) {
            v = "";
        } else if (valueJo.length() == 0) {
            v = Inter.getLocText("BI-Basic_Unrestricted");
        } else {
            String selected = Inter.getLocText("BI-Basic_Selected");
            String unchosen = Inter.getLocText("BI-Basic_Unchosen");
            v = (valueJo.optInt("type") == 1 ? selected : unchosen) + ":" + valueJo.optJSONArray("value").join(",");
        }
        renderWidget(v, jo);
    }

    private void renderNumberWidget(JSONObject jo) {
        JSONObject valueJo = jo.optJSONObject("value");
        String v;
        if (jo.optJSONObject("dimensions").length() == 0) {
            v = "";
        } else if (valueJo.length() == 0) {
            v = Inter.getLocText("BI-Basic_Unrestricted");
        } else {
            String min = valueJo.optString("min", "");
            String closeMin = valueJo.optBoolean("closemin", false) == true ? "<=" : "<";
            String max = valueJo.optString("max", "");
            String closeMax = valueJo.optBoolean("closemax", false) == true ? "<=" : "<";
            String str1 = Inter.getLocText("BI-Basic_Unrestricted");
            String str2 = Inter.getLocText("BI-Basic_Value");
            String str3 = Inter.getLocText("BI-Basic_Unrestricted");
            v = (min == "" ? str1 : min) + closeMin + str2 + closeMax + (max == "" ? str3 : max);
        }

        renderWidget(v, jo);
    }

    private void renderTreeWidget(JSONObject jo) throws JSONException {
        JSONObject joValue = jo.optJSONObject("value");
        String str = joValue.toString();
        renderWidget(str.substring(1, str.length() - 1).replace(":{}", ""), jo);
    }

    private void renderTreeLabelWidget(JSONObject jo) throws JSONException {
        JSONArray joValue = jo.optJSONArray("value");
        JSONObject dimensions = jo.optJSONObject("dimensions");
        JSONObject view = jo.optJSONObject("view");
        String value = "";
        JSONArray viewArray = view.optJSONArray(BIReportConstant.REGION.DIMENSION1);
        for (int i = 0; i < viewArray.length(); i++) {
            value += dimensions.optJSONObject(viewArray.getString(i)).optString("name") + ":";
            if (joValue.length() == 0 || joValue.getJSONArray(i).getString(0).contains("_*_")) {
                value += Inter.getLocText("BI-Basic_Unrestricted");
            } else {
                String v = joValue.getJSONArray(i).toString();
                value += v.substring(1, v.length() - 1);
            }
            value += "\n\r";
        }

        renderWidget(value, jo);
    }

    private void renderYearWidget(JSONObject jo) {
        String value = "";
        if (jo.optString("value") != null) {
            String str = Inter.getLocText("BI-Basic_Year");
            value = jo.optString("value") + str;
        }
        renderWidget(value, jo);
    }

    private void renderMonthWidget(JSONObject jo) {
        String value = "";
        JSONObject joValue = jo.optJSONObject("value");
        if (joValue.length() != 0) {
            String str1 = Inter.getLocText("BI-Basic_Year");
            String str2 = Inter.getLocText("BI-Multi_Date_Month");
            value = joValue.optString("year") + str1 + (joValue.optInt("month") + 1) + str2;
        }
        renderWidget(value, jo);
    }

    private void renderQuarterWidget(JSONObject jo) {
        JSONObject joValue = jo.optJSONObject("value");
        String value = "";
        if (joValue.length() != 0) {
            String str1 = Inter.getLocText("BI-Basic_Year");
            String str2 = Inter.getLocText("BI-Basic_Quarter");
            value = joValue.optString("year") + str1 + joValue.optString("quarter") + str2;
        }
        renderWidget(value, jo);
    }

    private void renderYMDWidget(JSONObject jo) {
        renderWidget(formatDate(jo.optJSONObject("value")), jo);
    }

    private String formatDate(JSONObject joValue) {
        //Todo 刚拖进来得时候joValue为空
        String dateValue = "";
        if (joValue.length() != 0) {
            if (joValue.has("year")) {
                dateValue = joValue.optString("year") + "-" + (joValue.optInt("month") + 1) + "-" + joValue.optString("day");
            } else {
                Calendar calendar = Calendar.getInstance();
                int type = joValue.optInt("type");
                switch (type) {
                    case BIReportConstant.DATE_TYPE.MULTI_DATE_YEAR_PREV:
                        calendar.add(Calendar.YEAR, 0 - offSet1);
                        break;
                    case BIReportConstant.DATE_TYPE.MULTI_DATE_YEAR_AFTER:
                        calendar.add(Calendar.YEAR, offSet1);
                        break;
                    case BIReportConstant.DATE_TYPE.MULTI_DATE_YEAR_BEGIN:
                        calendar.set(calendar.get(Calendar.YEAR), 0, 1);
                        break;
                    case BIReportConstant.DATE_TYPE.MULTI_DATE_YEAR_END:
                        calendar.set(calendar.get(Calendar.YEAR), 11, 31);
                        break;
                }
                _formatData(type, calendar);
                dateValue = DateUtils.DATEFORMAT2.format(calendar.getTime());
            }
        }
        return dateValue;
    }

    private void _formatData(int type, Calendar calendar) {
        switch (type) {
            case BIReportConstant.DATE_TYPE.MULTI_DATE_MONTH_PREV:
                calendar.add(Calendar.MONTH, 0 - offSet1);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_MONTH_AFTER:
                calendar.add(Calendar.MONTH, offSet1);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_MONTH_BEGIN:
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_MONTH_END:
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_QUARTER_PREV:
                calendar.add(Calendar.MONTH, 0 - offSet3);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_QUARTER_AFTER:
                calendar.add(Calendar.MONTH, offSet3);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_QUARTER_BEGIN:
                calendar.set(calendar.get(Calendar.YEAR), BIReportExportExcelUtils.getQuarterStartMonth(calendar.get(Calendar.MONTH)), 1);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_QUARTER_END:
                int month = BIReportExportExcelUtils.getQuarterStartMonth(calendar.get(Calendar.MONTH)) + 2;
                int year = calendar.get(Calendar.YEAR);
                calendar.set(year, month, BIReportExportExcelUtils.getMonthDays(year, month));
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_WEEK_PREV:
                calendar.add(Calendar.DATE, 0 - offSet7);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_WEEK_AFTER:
                calendar.add(Calendar.DATE, offSet7);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_DAY_PREV:
                calendar.add(Calendar.DATE, 0 - offSet1);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_DAY_AFTER:
                calendar.add(Calendar.DATE, offSet1);
                break;
            case BIReportConstant.DATE_TYPE.MULTI_DATE_DAY_TODAY:
                break;
        }
    }

    private void renderDatePaneWidget(JSONObject jo) {
        String value = "";
        JSONObject joValue = jo.optJSONObject("value");
        if (joValue.length() != 0) {
            String str1 = Inter.getLocText("BI-Basic_Year");
            String str2 = Inter.getLocText("BI-Multi_Date_Month");
            String str3 = Inter.getLocText("BI-Day_Ri");
            value = joValue.optString("year") + str1 + (joValue.optInt("month") + 1) + str2 + joValue.optString("day") + str3;
        }
        renderWidget(value, jo);
    }

    private void renderDateWidget(JSONObject jo) {
        JSONObject joValue = jo.optJSONObject("value");
        String startValue, endValue;
        if (joValue.optJSONObject("start") != null) {
            startValue = formatDate(joValue.optJSONObject("start"));
        } else {
            startValue = Inter.getLocText("BI-Basic_Unrestricted");

        }

        if (joValue.optJSONObject("end") != null) {
            endValue = formatDate(joValue.optJSONObject("end"));
        } else {
            endValue = Inter.getLocText("BI-Basic_Unrestricted");
        }

        String value = startValue + " -- " + endValue;
        renderWidget(value, jo);
    }
}
