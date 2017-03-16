package com.fr.bi.common.persistent.xml.reader;


import com.fr.bi.common.persistent.xml.writer.XMLBasicValueWriter;
import com.fr.bi.stable.constant.BIXMLConstant;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Connery on 2016/1/2.
 */
public class XMLBasicValueReader extends XMLValueReader {
    private Map<String, String> invalidValueFormatMap = new HashMap<String, String>();

    public XMLBasicValueReader(BIBeanXMLReaderWrapper beanWrapper, Map<String, BIBeanXMLReaderWrapper> generatedBean) {
        super(beanWrapper, generatedBean);
        initInvalidValueMap();
    }

    @Override
    protected void readerContent(XMLableReader xmLableReader) {
        if (xmLableReader.isChildNode()) {
            if (ComparatorUtils.equals(xmLableReader.getTagName(), XMLBasicValueWriter.BASIC_TAG)) {

                String strValue = compatibleValue(xmLableReader);
                if (!isCompatible(strValue)) {
                    strValue = xmLableReader.getElementValue();
                }
                /**
                 * 空字符串读出来是Null
                 */
                if (strValue == null && ComparatorUtils.equals(String.class, beanWrapper.getBeanClass())) {
                    strValue = "";
                }
                String formatValue = getFormatValue(strValue);
                Object value = BITypeUtils.stringConvert2BasicType(beanWrapper.getBeanClass(), formatValue);

                beanWrapper.setBean(value);
            }
        }
    }

    private String compatibleValue(XMLableReader xmLableReader) {
        return xmLableReader.getAttrAsString("value", "not_compatible");
    }

    private boolean isCompatible(String strValue) {
        return !ComparatorUtils.equals(strValue, "not_compatible");
    }

    private String getFormatValue(String originalValue) {
        String strCopy = originalValue;
        Iterator<Map.Entry<String, String>> it = invalidValueFormatMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (strCopy.contains(entry.getKey())) {
                strCopy = strCopy.replace(entry.getKey(), entry.getValue());
            }
        }
        return strCopy;
    }

    private void initInvalidValueMap() {
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0000, "\u0000");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0001, "\u0001");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0002, "\u0002");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0003, "\u0003");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0004, "\u0004");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0005, "\u0005");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0006, "\u0006");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0007, "\u0007");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0008, "\u0008");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U000B, "\u000b");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U000C, "\u000c");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U000E, "\u000e");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U000F, "\u000f");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0010, "\u0010");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0011, "\u0011");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0012, "\u0012");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0013, "\u0013");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0014, "\u0014");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0015, "\u0015");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0016, "\u0016");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0017, "\u0017");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0018, "\u0018");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U0019, "\u0019");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U001A, "\u001a");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U001B, "\u001b");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U001C, "\u001c");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U001D, "\u001d");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U001E, "\u001e");
        invalidValueFormatMap.put(BIXMLConstant.INVALID_VALUE.U001F, "\u001f");
    }
}