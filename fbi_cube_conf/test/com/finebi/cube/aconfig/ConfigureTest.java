package com.finebi.cube.aconfig;

import com.fr.bi.common.persistent.xml.reader.BIBeanXMLReaderWrapper;
import com.fr.bi.common.persistent.xml.reader.XMLPersistentReader;
import com.fr.bi.conf.base.pack.BIUserPackageConfigurationManager;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.StableUtils;
import com.fr.stable.xml.XMLTools;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

/**
 * Created by Connery on 2016/1/5.
 */
public class ConfigureTest extends TestCase {
    public void testGget() {
        try {
            BIUserPackageConfigurationManager manager = new BIUserPackageConfigurationManager(-991l);
            File var3 = new File("D:\\FineBI\\code\\WebReport\\WEB-INF\\resources\\BusinessPackage.xml");
            StableUtils.makesureFileExist(var3);
            XMLPersistentReader reader = new XMLPersistentReader(new HashMap<String, BIBeanXMLReaderWrapper>(), new BIBeanXMLReaderWrapper(manager));
            XMLTools.readInputStreamXML(reader, new FileInputStream(var3));
            BILogger.getLogger().info("hello");
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}