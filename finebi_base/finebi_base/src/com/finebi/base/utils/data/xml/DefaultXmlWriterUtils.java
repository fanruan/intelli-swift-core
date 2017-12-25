package com.finebi.base.utils.data.xml;


import com.finebi.base.data.xml.imp.DefaultXmlWriter;

import java.io.File;

/**
 * Created by andrew_asa on 2017/9/29.
 */
public class DefaultXmlWriterUtils {

    public static void writeXml(File dst, Object obj) throws Exception {

        DefaultXmlWriter writer = new DefaultXmlWriter();
        writer.write(dst, obj);
    }
}
