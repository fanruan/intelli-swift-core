package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.stable.constant.BIXMLConstant;
import com.fr.stable.xml.XMLPrintWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLBasicValueWriter extends XMLValueWriter {
    public static String BASIC_TAG = "BI_XML_BASIC_TYPE_TAG";
    private static String[] invalidCharReplaceValueArray;

    public XMLBasicValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        super(beanWrapper, disposedBeans);
        initInvalidCharReplaceValueArray();
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
        StringBuilder stringBuilder = new StringBuilder();
        char[] originalValueCharArray = originalValue.toCharArray();
        for (int i = 0; i < originalValueCharArray.length; i++) {
            char c = originalValueCharArray[i];
            if (c <= 0x1f) {
                if (invalidCharReplaceValueArray[c] != null) {
                    stringBuilder.append(invalidCharReplaceValueArray[c]);
                } else {
                    stringBuilder.append(c);
                }
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    private void initInvalidCharReplaceValueArray() {
        invalidCharReplaceValueArray = getInvalidCharArray();
    }

    private String[] getInvalidCharArray() {
        String[] invalidString = new String[32];
        invalidString[0x0000] = BIXMLConstant.INVALID_VALUE.U0000;
        invalidString[0x0001] = BIXMLConstant.INVALID_VALUE.U0001;
        invalidString[0x0002] = BIXMLConstant.INVALID_VALUE.U0002;
        invalidString[0x0003] = BIXMLConstant.INVALID_VALUE.U0003;
        invalidString[0x0004] = BIXMLConstant.INVALID_VALUE.U0004;
        invalidString[0x0005] = BIXMLConstant.INVALID_VALUE.U0005;
        invalidString[0x0006] = BIXMLConstant.INVALID_VALUE.U0006;
        invalidString[0x0007] = BIXMLConstant.INVALID_VALUE.U0007;
        invalidString[0x0008] = BIXMLConstant.INVALID_VALUE.U0008;
        invalidString[0x000b] = BIXMLConstant.INVALID_VALUE.U000B;
        invalidString[0x000c] = BIXMLConstant.INVALID_VALUE.U000C;
        invalidString[0x000e] = BIXMLConstant.INVALID_VALUE.U000E;
        invalidString[0x000f] = BIXMLConstant.INVALID_VALUE.U000F;
        invalidString[0x0010] = BIXMLConstant.INVALID_VALUE.U0010;
        invalidString[0x0011] = BIXMLConstant.INVALID_VALUE.U0011;
        invalidString[0x0012] = BIXMLConstant.INVALID_VALUE.U0012;
        invalidString[0x0013] = BIXMLConstant.INVALID_VALUE.U0013;
        invalidString[0x0014] = BIXMLConstant.INVALID_VALUE.U0014;
        invalidString[0x0015] = BIXMLConstant.INVALID_VALUE.U0015;
        invalidString[0x0016] = BIXMLConstant.INVALID_VALUE.U0016;
        invalidString[0x0017] = BIXMLConstant.INVALID_VALUE.U0017;
        invalidString[0x0018] = BIXMLConstant.INVALID_VALUE.U0018;
        invalidString[0x0019] = BIXMLConstant.INVALID_VALUE.U0019;
        invalidString[0x001a] = BIXMLConstant.INVALID_VALUE.U001A;
        invalidString[0x001b] = BIXMLConstant.INVALID_VALUE.U001B;
        invalidString[0x001c] = BIXMLConstant.INVALID_VALUE.U001C;
        invalidString[0x001d] = BIXMLConstant.INVALID_VALUE.U001D;
        invalidString[0x001e] = BIXMLConstant.INVALID_VALUE.U001E;
        invalidString[0x001f] = BIXMLConstant.INVALID_VALUE.U001F;
        return invalidString;
    }
}