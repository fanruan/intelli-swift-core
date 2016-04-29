package com.fr.bi.conf.fs;

import com.fr.file.XMLFileManager;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**

 * fs的设置属性
 *
 * @author Daniel
 */
public class FBIConfig extends XMLFileManager {
    private static final String XML_TAG = "fbiConfig";
    private static FBIConfig SC = null;
    //图标样式
    private BIChartStyleAttr chartStyleAttr = new BIChartStyleAttr();
    //用户权限
    private BIUserAuthorAttr userAuthorAttr = new BIUserAuthorAttr();
    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                SC = null;
            }
        });
    }

    public static FBIConfig getInstance() {
        if (SC != null) {
            return SC;
        }
        refreshFBIConfig();
        return SC;
    }

    /**
     * 释放
     */
    public static void release() {
        SC = null;
    }

    private static void refreshFBIConfig() {
        synchronized (FBIConfig.class) {
            if (SC == null) {
                SC = new FBIConfig();
            }
            SC.userAuthorAttr.refreshLimitedUser();
            SC.readXMLFile();
        }
    }

    public BIChartStyleAttr getChartStyleAttr() {
        return chartStyleAttr;
    }


    private void readChild(XMLableReader reader) {
        String tagName = reader.getTagName();
        if (ComparatorUtils.equals(BIUserAuthorAttr.XML_TAG, tagName)) {
            userAuthorAttr = new BIUserAuthorAttr();
            userAuthorAttr.readXML(reader);
        } else if (ComparatorUtils.equals(tagName, BIChartStyleAttr.XML_TAG)) {
            chartStyleAttr = new BIChartStyleAttr();
            chartStyleAttr.readXML(reader);
        }
    }

    /**
     * 文件名
     *
     * @return 文件名
     */
    @Override
    public String fileName() {
        return "FBIConfig.xml";
    }

    /**
     * 读xml方法
     *
     * @param reader
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            readChild(reader);
        }
    }

    /**
     * 写xml方法
     *
     * @param writer
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writeChartAttr(writer);
        writeUserAuthorAttr(writer);
        writer.end();
    }

    private void writeChartAttr(XMLPrintWriter writer){
        chartStyleAttr.writeXML(writer);
    }

    private void writeUserAuthorAttr(XMLPrintWriter writer){
        userAuthorAttr.writeXML(writer);
    }

    public BIUserAuthorAttr getUserAuthorAttr() {
        return userAuthorAttr;
    }
}