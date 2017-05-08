package com.fr.bi.cal.analyze.report.report;

import com.fr.base.ScreenResolution;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.general.IOUtils;
import com.fr.json.JSONObject;
import com.fr.report.cell.FloatElement;
import com.fr.report.poly.PolyECBlock;
import com.fr.stable.CodeUtils;
import com.fr.stable.Constants;
import com.fr.stable.StringUtils;
import com.fr.stable.unit.FU;
import com.fr.stable.unit.UnitRectangle;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by astronaut007 on 2017/4/19.
 */
public class BIReportExportExcelUtils {
    private static int bytesLength = 256;
    private static int daysOfFebruary = 29;
    private static int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static int timeOut = 5000;
    private static int year4 = 4;
    private static int year100 = 100;
    private static int year400 = 400;

    private static String phantomIp = PerformancePlugManager.getInstance().getPhantomServerIP();
    private static int phantomPort = PerformancePlugManager.getInstance().getPhantomServerPort();

    static FloatElement createFloatElement(BufferedImage bufferedImage, Rectangle rect) {
        FloatElement floatElement = new FloatElement(bufferedImage);
        return formatFloatElement(floatElement, rect);
    }

    static FloatElement createFloatElement(BufferedImage bufferedImage, JSONObject bounds) {
        return createFloatElement(bufferedImage, getWidgetRect(bounds));
    }

    static FloatElement createFloatElement(String base64, Rectangle rect) {
        return createFloatElement(base64Decoder(base64), rect);
    }

    static FloatElement createFloatElement(String base64, JSONObject bounds) {
        return createFloatElement(base64, getWidgetRect(bounds));
    }

    static FloatElement createFloatElement4String(String value, JSONObject bounds) {
        FloatElement floatElement = new FloatElement(value);
        return formatFloatElement(floatElement, getWidgetRect(bounds));
    }

    static FloatElement formatFloatElement(FloatElement floatElement, Rectangle rect) {
        int resolution = ScreenResolution.getScreenResolution();
//        int resolution = Constants.DEFAULT_PRINT_AND_EXPORT_RESOLUTION;
        floatElement.setWidth(FU.valueOfPix((int) rect.getWidth(), resolution));
        floatElement.setHeight(FU.valueOfPix((int) rect.getHeight(), resolution));
        floatElement.setLeftDistance(FU.valueOfPix((int) rect.getX() + 10, resolution));
        floatElement.setTopDistance(FU.valueOfPix((int) rect.getY() + 10, resolution));
        return floatElement;
    }

    static String postMessage(String message) throws IOException {
        URL url = new URL("http://" + phantomIp + ":" + phantomPort + "/");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setConnectTimeout(timeOut);
        connection.setReadTimeout(timeOut);

        OutputStream out = connection.getOutputStream();
        out.write(message.getBytes("utf-8"));
        out.close();

        //get base64 picture
        InputStream in = connection.getInputStream();
        String response = IOUtils.inputStream2String(in);
        in.close();

        return response;
    }

    static BufferedImage base64Decoder(String base64) {
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

    static PolyECBlock createPolyECBlock(String widgetName) {
        PolyECBlock polyECBlock = new PolyECBlock();
        polyECBlock.setBlockName(CodeUtils.passwordEncode(CodeUtils.passwordEncode(widgetName)));
        polyECBlock.getBlockAttr().setFreezeHeight(true);
        polyECBlock.getBlockAttr().setFreezeWidth(true);
        polyECBlock.setBounds(new UnitRectangle(new Rectangle(), Constants.DEFAULT_WEBWRITE_AND_SCREEN_RESOLUTION));
        return polyECBlock;
    }

    static boolean widgetHasData(BIWidget widget) {
        return (widget.getViewDimensions().length + widget.getViewTargets().length) != 0;
    }

    static String getDefaultImage(WidgetType type, String imageFolder) throws IOException {
        Map<WidgetType, String> map = new HashMap<WidgetType, String>();
        map.put(WidgetType.STACKED_COLUMN, "/column_accu.png");
        map.put(WidgetType.STACKED_AREA, "/area_accu.png");
        map.put(WidgetType.STACKED_RADAR, "/radar_accu.png");
        map.put(WidgetType.COLUMN, "/column.png");
        map.put(WidgetType.LINE, "/line.png");
        map.put(WidgetType.AREA, "/area.png");
        map.put(WidgetType.PERCENT_STACKED_COLUMN, "/column_percent.png");
        map.put(WidgetType.PERCENT_STACKED_AREA, "/area_percent.png");
        map.put(WidgetType.COMPARE_COLUMN, "/column_compare.png");
        map.put(WidgetType.COMPARE_AREA, "/area_compare.png");
        map.put(WidgetType.FALL_COLUMN, "/column_fall.png");
        map.put(WidgetType.RANGE_AREA, "/area_range.png");
        map.put(WidgetType.BAR, "/bar.png");
        map.put(WidgetType.STACKED_BAR, "/bar_accu.png");
        map.put(WidgetType.COMPARE_BAR, "/bar_compare.png");
        map.put(WidgetType.COMBINE_CHART, "/combine.png");
        map.put(WidgetType.DONUT, "/donut.png");
        map.put(WidgetType.RADAR, "/radar.png");
        map.put(WidgetType.PIE, "/pie.png");
        map.put(WidgetType.MULTI_AXIS_COMBINE_CHART, "/combine_m.png");
        map.put(WidgetType.FORCE_BUBBLE, "/bubble_force.png");
        map.put(WidgetType.GAUGE, "/gauge.png");
        map.put(WidgetType.DOT, "/bubble.png");
        map.put(WidgetType.MAP, "/map.png");
        map.put(WidgetType.GIS_MAP, "/map_gis.png");
        map.put(WidgetType.TABLE, "/table_group.png");
        map.put(WidgetType.CROSS_TABLE, "/table_cross.png");
        map.put(WidgetType.COMPLEX_TABLE, "/table_complex.png");
        map.put(WidgetType.FUNNEL, "/funnel.png");
        map.put(WidgetType.MULTI_PIE, "/multi_pie.png");
        map.put(WidgetType.TREE_MAP, "/tree_map.png");
        return coderBase64(IOUtils.readImage(imageFolder + map.get(type)));
    }

    static String coderBase64(Image image) throws IOException {
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

    static Rectangle getWidgetRect(JSONObject bounds) {
        Rectangle rect = new Rectangle(bounds.optInt("left"), bounds.optInt("top"),
                bounds.optInt("width"), bounds.optInt("height"));
        return rect;
    }

    static int getQuarterStartMonth(int nowMonth) {
        int quarterStartMonth = DateConstant.CALENDAR.MONTH.JANUARY;
        if (nowMonth < DateConstant.CALENDAR.MONTH.APRIL) {
            quarterStartMonth = DateConstant.CALENDAR.MONTH.JANUARY;
        }
        if (DateConstant.CALENDAR.MONTH.MARCH < nowMonth && nowMonth < DateConstant.CALENDAR.MONTH.JULY) {
            quarterStartMonth = DateConstant.CALENDAR.MONTH.APRIL;
        }
        if (DateConstant.CALENDAR.MONTH.JUNE < nowMonth && nowMonth < DateConstant.CALENDAR.MONTH.OCTOBER) {
            quarterStartMonth = DateConstant.CALENDAR.MONTH.JULY;
        }
        if (nowMonth > DateConstant.CALENDAR.MONTH.SEPTEMBER) {
            quarterStartMonth = DateConstant.CALENDAR.MONTH.SEPTEMBER;
        }
        return quarterStartMonth;
    }

    static int getMonthDays(int year, int month) {
        boolean isLeapYear = (0 == (year % year4)) && ((0 != (year % year100)) || (0 == (year % year400)));
        if (isLeapYear && month == 1) {
            return daysOfFebruary;
        } else {
            return daysOfMonth[month];
        }
    }
}
