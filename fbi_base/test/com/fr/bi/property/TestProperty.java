package com.fr.bi.property;

import com.fr.bi.common.persistent.xml.reader.BIBeanXMLReaderWrapper;
import com.fr.bi.common.persistent.xml.reader.XMLPersistentReader;
import com.fr.bi.common.persistent.xml.writer.BIBeanXMLWriterWrapper;
import com.fr.bi.common.persistent.xml.writer.XMLPersistentWriter;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLTools;
import junit.framework.TestCase;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2017/8/16.
 *
 * @author Each.Zhang
 */
public class TestProperty extends TestCase {

    public void testReadXML() throws Exception {
        List<String> propertiesConfigList = new ArrayList<String>();
        PropertiesConfig config = new PropertiesConfig();
        File file = new File("E:\\test.xml");
        InputStream in = new FileInputStream(file);
        Map<String, BIBeanXMLReaderWrapper> generated = new HashMap<String, BIBeanXMLReaderWrapper>();
        BIBeanXMLReaderWrapper wrapper = new BIBeanXMLReaderWrapper(config);
        generated.put(null, wrapper);
        XMLPersistentReader reader = new XMLPersistentReader(generated, new BIBeanXMLReaderWrapper(propertiesConfigList));
        XMLTools.readInputStreamXML(reader, in);
        propertiesConfigList = (List<String>) reader.getBeanWrapper().getBean();
    }

    public void testWriteXML() throws FileNotFoundException {
        List<PropertiesConfig> propertiesConfigList = new ArrayList<PropertiesConfig>();
        PropertiesConfig propertiesConfig1 = new PropertiesConfig("BI-UseMultiThreadCal", "useMultiThreadCal", ValueType.BOOLEAN, AvaliableType.TRUE, "计算时是否启用多线程", "启用多线程可提升计算速度，但对服务器性能会有更高的要求。");
        PropertiesConfig propertiesConfig2 = new PropertiesConfig("BI-MinCubeFreeHDSpaceRate", "minCubeFreeHDSpaceRate", ValueType.NUMBER, AvaliableType.TRUE, "FineIndex更新所需空间倍数", "当启用空间检查后，若空间倍数小于该值，则FineIndex不触发更新；FineIndex更新需求空间倍数=当前剩余空间/当前FineIndex占用空间。");
        PropertiesConfig propertiesConfig3 = new PropertiesConfig("BI-BackupWhenStart", "backupWhenStart", ValueType.BOOLEAN, AvaliableType.TRUE, "工程启动时是否备份", "开启后，工程启动时自动备份；备份内容和FineIndex更新后备份内容一样。");
        PropertiesConfig propertiesConfig4 = new PropertiesConfig("BI-BiThreadPoolSize", "biThreadPoolSize", ValueType.NUMBER, AvaliableType.TRUE, "更新FineIndex时可用最大线程数", "线程数越高，处理速度越快，但同时服务器负载压力也越大。故合理设置线程数可在服务器可承受负载压力范围内最大程度地提升性能。");
        PropertiesConfig propertiesConfig5 = new PropertiesConfig("BI-BiTransportThreadPoolSize", "biTransportThreadPoolSize", ValueType.NUMBER, AvaliableType.TRUE, "数据转移最大线程数", "线程数越高，FineIndex更新中数据转移速度也越快。但同时对数据库和服务器性能也有一定要求。");
        PropertiesConfig propertiesConfig6 = new PropertiesConfig("BI-DiskSort", "diskSort", ValueType.BOOLEAN, AvaliableType.TRUE, "是否启用外排", "启用外排可以减少内存压力，但是对速度有一定影响。");
        PropertiesConfig propertiesConfig7 = new PropertiesConfig("BI-DiskSortDumpThreshold", "diskSortDumpThreshold", ValueType.NUMBER, AvaliableType.TRUE, "触发外排阈值", "当容量到达配置的阈值之后，触发外排。");
        PropertiesConfig propertiesConfig8 = new PropertiesConfig("BI-RetryMaxTimes", "retryMaxTimes", ValueType.NUMBER, AvaliableType.TRUE, "最大重试次数", "操作（计算，数据转移等）失败后可自动重新尝试操作，若尝试次数超过该值，重试结束。");
        PropertiesConfig propertiesConfig9 = new PropertiesConfig("BI-RetryMaxSleepTime", "retryMaxSleepTime", ValueType.NUMBER, AvaliableType.TRUE, "重试间隔时间", "操作（计算，数据转移等）失败后可自动重新尝试操作，重试需要设置间隔时间，和retryMaxTimes配套使用。单位为毫秒。");
        PropertiesConfig propertiesConfig10 = new PropertiesConfig("BI-CubeReaderReleaseSleepTime", "cubeReaderReleaseSleepTime", ValueType.NUMBER, AvaliableType.TRUE, "NIO释放等待时间", "NIO文件使用后不会立即释放，此时需定义NIO释放等待时间。释放时间和磁盘IO性能有关，性能较差时可以考虑将NIO释放等待时间延长。单位为毫秒。");
        PropertiesConfig propertiesConfig11 = new PropertiesConfig("BI-UnmapReader", "unmapReader", ValueType.BOOLEAN, AvaliableType.TRUE, "是否释放NIO资源", "NIO资源访问(读)过结束时是否释放。");
        PropertiesConfig propertiesConfig12 = new PropertiesConfig("BI-IsForceWriter", "isForceWriter", ValueType.BOOLEAN, AvaliableType.TRUE, "是否强制释放资源", "NIO资源访问(写)过结束时是否释放。");
        PropertiesConfig propertiesConfig13 = new PropertiesConfig("BI-DeployModeSelectSize", "deployModeSelectSize", ValueType.NUMBER, AvaliableType.FASLE, "表读取最大行数", "用来设置部署模式下每张表读取的最大行数，主要用来对FineIndex进行debug。合理设置该值可以加快FineIndex更新速度，方便调试。当参数等于1时，该功能关闭；当参数不等于1时，开启该功能，开启后只取一部分数据，所以此时数据是不完整的。");
        PropertiesConfig propertiesConfig14 = new PropertiesConfig("BI-ExtremeConcurrency", "extremeConcurrency", ValueType.NUMBER, AvaliableType.FASLE, "Wiget计算结果是否缓存", "开启后，会将所有计算结果都缓存起来，当控件属性和过滤条件都一致时，直接从缓存中获取结果，不用重新计算。");
        propertiesConfigList.add(propertiesConfig1);
        propertiesConfigList.add(propertiesConfig2);
        propertiesConfigList.add(propertiesConfig3);
        propertiesConfigList.add(propertiesConfig4);
        propertiesConfigList.add(propertiesConfig5);
        propertiesConfigList.add(propertiesConfig6);
        propertiesConfigList.add(propertiesConfig7);
        propertiesConfigList.add(propertiesConfig8);
        propertiesConfigList.add(propertiesConfig9);
        propertiesConfigList.add(propertiesConfig10);
        propertiesConfigList.add(propertiesConfig11);
        propertiesConfigList.add(propertiesConfig12);
        propertiesConfigList.add(propertiesConfig13);
        propertiesConfigList.add(propertiesConfig14);
        BIBeanXMLWriterWrapper writerWrapper = new BIBeanXMLWriterWrapper(propertiesConfigList);
        writerWrapper.setTag("Properties");
        writerWrapper.setTagAvailable(true);
        writerWrapper.setProperty(false);
        XMLPersistentWriter persistentWriter = new XMLPersistentWriter(writerWrapper);
        XMLPrintWriter printWriter = XMLPrintWriter.create(new FileOutputStream("E:\\test.xml"));
        persistentWriter.writeXML(printWriter);
    }
}
