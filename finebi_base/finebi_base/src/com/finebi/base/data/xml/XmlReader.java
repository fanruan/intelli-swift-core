package com.finebi.base.data.xml;

import java.io.File;

/**
 * Created by andrew_asa on 2017/9/28.
 */
public interface XmlReader {

    <T> T readXml(File src, Class clazz) throws Exception;
}
