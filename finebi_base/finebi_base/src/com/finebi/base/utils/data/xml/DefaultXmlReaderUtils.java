package com.finebi.base.utils.data.xml;

import java.io.File;

/**
 * Created by andrew_asa on 2017/9/29.
 * xml读写
 */
public class DefaultXmlReaderUtils {

    public static Object readXml(File f, Class clazz) throws Exception {

        Object obj = clazz.newInstance();
        return obj;
    }
}
