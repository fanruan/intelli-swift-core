package com.fr.bi.property;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.common.persistent.xml.reader.BIBeanXMLReaderWrapper;
import com.fr.bi.common.persistent.xml.reader.XMLPersistentReader;
import com.fr.bi.common.persistent.xml.writer.BIBeanXMLWriterWrapper;
import com.fr.bi.common.persistent.xml.writer.XMLPersistentWriter;
import com.fr.stable.project.ProjectConstants;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLTools;
import com.fr.stable.xml.XMLableReader;

import java.io.*;
import java.util.*;

/**
 * This class created on 2017/8/16.
 *
 * @author Each.Zhang
 */
public class BIPropertyOperate implements PropertyOperate {

    private static final String FILE_NAME = "systemconfig.xml";
    private static final String XML_FILE_TAG = "Properties";
    private static final File PROPERTY_FILE = new File(FRContext.getCurrentEnv().getPath() + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + FILE_NAME);

    /**
     * 读取配置文件，获取到一个集合
     *
     * @return
     */
    @Override
    public List<PropertiesConfig> read() {
        List<PropertiesConfig> propertiesConfigList = new ArrayList<PropertiesConfig>();
        try {
            XMLableReader xmLableReader = XMLableReader.createXMLableReader(new FileReader(PROPERTY_FILE));
            BIPropertyHelper propertyHelper = BIPropertyHelper.getInstance();
            propertyHelper.readXML(xmLableReader);
            propertiesConfigList = propertyHelper.getPropertyList();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return propertiesConfigList;
    }

    /**
     * 调用write方法的时候，传递需要写入的文件中的内容，然后进行写入操作。
     *
     * @return
     */
    @Override
    public void write(List propertiesConfigList) {
        try {
            BIBeanXMLWriterWrapper writerWrapper = new BIBeanXMLWriterWrapper(propertiesConfigList);
            writerWrapper.setTag(XML_FILE_TAG);
            writerWrapper.setTagAvailable(true);
            writerWrapper.setProperty(false);
            XMLPersistentWriter persistentWriter = new XMLPersistentWriter(writerWrapper);
            XMLPrintWriter printWriter = XMLPrintWriter.create(new FileOutputStream(PROPERTY_FILE));
            persistentWriter.writeXML(printWriter);
        } catch (FileNotFoundException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }
}
