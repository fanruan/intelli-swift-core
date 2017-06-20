package com.fr.bi.cal.analyze.report.report.export.utils;

import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.general.IOUtils;
import com.fr.json.JSONObject;
import com.fr.report.cell.FloatElement;
import com.fr.report.poly.PolyECBlock;
import com.fr.stable.CodeUtils;
import com.fr.stable.Constants;
import com.fr.stable.unit.FU;
import com.fr.stable.unit.UnitRectangle;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

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
    private static int pageMargin = 10;

    private static String phantomIp = PerformancePlugManager.getInstance().getPhantomServerIP();
    private static int phantomPort = PerformancePlugManager.getInstance().getPhantomServerPort();

    static FloatElement createFloatElement(BufferedImage bufferedImage, Rectangle rect) {
        FloatElement floatElement = new FloatElement(bufferedImage);
        return formatFloatElement(floatElement, rect);
    }

    public static FloatElement createFloatElement(BufferedImage bufferedImage, JSONObject bounds) {
        return createFloatElement(bufferedImage, getWidgetRect(bounds));
    }

    public static FloatElement createFloatElement(String base64, Rectangle rect) {
        return createFloatElement(base64Decoder(base64), rect);
    }

    public static FloatElement createFloatElement(String base64, JSONObject bounds) {
        return createFloatElement(base64, getWidgetRect(bounds));
    }

    public static FloatElement createFloatElement4String(String value, JSONObject bounds) {
        FloatElement floatElement = new FloatElement(value);
        return formatFloatElement(floatElement, getWidgetRect(bounds));
    }

    static FloatElement formatFloatElement(FloatElement floatElement, Rectangle rect) {
        int resolution = Constants.DEFAULT_WEBWRITE_AND_SCREEN_RESOLUTION;
        floatElement.setWidth(FU.valueOfPix((int) rect.getWidth(), resolution));
        floatElement.setHeight(FU.valueOfPix((int) rect.getHeight(), resolution));
        floatElement.setLeftDistance(FU.valueOfPix((int) rect.getX() + pageMargin, resolution));
        floatElement.setTopDistance(FU.valueOfPix((int) rect.getY() + pageMargin, resolution));
        return floatElement;
    }

    public static String postMessage(String message) throws IOException {
        URL url = new URL("http://" + phantomIp + ":" + phantomPort + "/");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setConnectTimeout(timeOut);
        connection.setReadTimeout(timeOut);


        OutputStream out = null;
        InputStream in = null;
        try {
            out = connection.getOutputStream();
            out.write(message.getBytes("utf-8"));
            in = connection.getInputStream();
            return IOUtils.inputStream2String(in);
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
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

    public static PolyECBlock createPolyECBlock(String widgetName) {
        PolyECBlock polyECBlock = new PolyECBlock();
        polyECBlock.setBlockName(CodeUtils.passwordEncode(CodeUtils.passwordEncode(widgetName)));
        polyECBlock.getBlockAttr().setFreezeHeight(true);
        polyECBlock.getBlockAttr().setFreezeWidth(true);
        polyECBlock.setBounds(new UnitRectangle(new Rectangle(), Constants.DEFAULT_WEBWRITE_AND_SCREEN_RESOLUTION));
        return polyECBlock;
    }

    public static boolean widgetHasData(BIWidget widget) {
        return (widget.getViewDimensions().length + widget.getViewTargets().length) != 0;
    }

    static Rectangle getWidgetRect(JSONObject bounds) {
        Rectangle rect = new Rectangle(bounds.optInt("left"), bounds.optInt("top"),
                bounds.optInt("width"), bounds.optInt("height"));
        return rect;
    }

    public static int getQuarterStartMonth(int nowMonth) {
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

    public static int getMonthDays(int year, int month) {
        boolean isLeapYear = (0 == (year % year4)) && ((0 != (year % year100)) || (0 == (year % year400)));
        if (isLeapYear && month == 1) {
            return daysOfFebruary;
        } else {
            return daysOfMonth[month];
        }
    }
}
