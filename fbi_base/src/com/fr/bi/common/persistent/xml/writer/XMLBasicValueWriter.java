package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.stable.constant.BIXMLConstant;
import com.fr.stable.xml.XMLPrintWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLBasicValueWriter extends XMLValueWriter {
    public static String BASIC_TAG = "BI_XML_BASIC_TYPE_TAG";
    private Map<String, String> invalidValueFormatMap = new HashMap<String, String>();

    public XMLBasicValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        super(beanWrapper, disposedBeans);
        initInvalidValueMap();
    }


    @Override
    void writeContent(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
        if (beanWrapper.getBean() != null) {
//            if (beanWrapper.getPropertyType() != null && BITypeUtils.isBasicValue(beanWrapper.getPropertyType())) {
//                writer.attr("class", beanWrapper.getPropertyType().getName());
//            } else {
//                writer.attr("class", beanWrapper.getBean().getClass().getName());
//            }
            writer.attr("class", beanWrapper.getBean().getClass().getName());
            writer.startTAG(BASIC_TAG);

            String writeValue = beanWrapper.getBean().toString();
            String formatValue = getFormatValue(writeValue);
            if (XMLNormalValueWriter.USE_CONTENT_SAVE_VALUE) {
                writer.textNode(formatValue);
            } else {
                writer.attr("value", formatValue);
            }
            writer.end();
        }
    }

    private String getFormatValue(String originalValue) {
        String strCopy = originalValue;
        String reg = "[\u0000-\u0008\u000b-\u000c\u000e-\u001f]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(originalValue);
        while (matcher.find()) {
            String invalidValue = matcher.group();
            String replaceValue = invalidValueFormatMap.get(invalidValue);
            strCopy = strCopy.replaceFirst(invalidValue, replaceValue);
        }
        return strCopy;
    }


    private void initInvalidValueMap() {
        invalidValueFormatMap.put("\u0000", BIXMLConstant.INVALID_VALUE.U0000);
        invalidValueFormatMap.put("\u0001", BIXMLConstant.INVALID_VALUE.U0001);
        invalidValueFormatMap.put("\u0002", BIXMLConstant.INVALID_VALUE.U0002);
        invalidValueFormatMap.put("\u0003", BIXMLConstant.INVALID_VALUE.U0003);
        invalidValueFormatMap.put("\u0004", BIXMLConstant.INVALID_VALUE.U0004);
        invalidValueFormatMap.put("\u0005", BIXMLConstant.INVALID_VALUE.U0005);
        invalidValueFormatMap.put("\u0006", BIXMLConstant.INVALID_VALUE.U0006);
        invalidValueFormatMap.put("\u0007", BIXMLConstant.INVALID_VALUE.U0007);
        invalidValueFormatMap.put("\u0008", BIXMLConstant.INVALID_VALUE.U0008);
        invalidValueFormatMap.put("\u000b", BIXMLConstant.INVALID_VALUE.U000B);
        invalidValueFormatMap.put("\u000c", BIXMLConstant.INVALID_VALUE.U000C);
        invalidValueFormatMap.put("\u000e", BIXMLConstant.INVALID_VALUE.U000E);
        invalidValueFormatMap.put("\u000f", BIXMLConstant.INVALID_VALUE.U000F);
        invalidValueFormatMap.put("\u0010", BIXMLConstant.INVALID_VALUE.U0010);
        invalidValueFormatMap.put("\u0011", BIXMLConstant.INVALID_VALUE.U0011);
        invalidValueFormatMap.put("\u0012", BIXMLConstant.INVALID_VALUE.U0012);
        invalidValueFormatMap.put("\u0013", BIXMLConstant.INVALID_VALUE.U0013);
        invalidValueFormatMap.put("\u0014", BIXMLConstant.INVALID_VALUE.U0014);
        invalidValueFormatMap.put("\u0015", BIXMLConstant.INVALID_VALUE.U0015);
        invalidValueFormatMap.put("\u0016", BIXMLConstant.INVALID_VALUE.U0016);
        invalidValueFormatMap.put("\u0017", BIXMLConstant.INVALID_VALUE.U0017);
        invalidValueFormatMap.put("\u0018", BIXMLConstant.INVALID_VALUE.U0018);
        invalidValueFormatMap.put("\u0019", BIXMLConstant.INVALID_VALUE.U0019);
        invalidValueFormatMap.put("\u001a", BIXMLConstant.INVALID_VALUE.U001A);
        invalidValueFormatMap.put("\u001b", BIXMLConstant.INVALID_VALUE.U001B);
        invalidValueFormatMap.put("\u001c", BIXMLConstant.INVALID_VALUE.U001C);
        invalidValueFormatMap.put("\u001d", BIXMLConstant.INVALID_VALUE.U001D);
        invalidValueFormatMap.put("\u001e", BIXMLConstant.INVALID_VALUE.U001E);
        invalidValueFormatMap.put("\u001f", BIXMLConstant.INVALID_VALUE.U001F);
    }
}