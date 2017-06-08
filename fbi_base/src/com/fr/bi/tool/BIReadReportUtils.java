package com.fr.bi.tool;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.BaseUtils;
import com.fr.base.BaseXMLUtils;
import com.fr.bi.fs.BIDesignReport;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.fs.BIFileRepository;
import com.fr.bi.fs.BIReportNode;
import com.fr.json.JSONObject;
import com.fr.stable.CodeUtils;
import com.fr.stable.bridge.StableFactory;

import java.io.File;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Young's on 2016/9/23.
 */

public class BIReadReportUtils implements BIReadReportProvider {
    public static final String XML_TAG = "BIReadReportUtils";
    private static BIReadReportUtils manager;
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static BIReadReportProvider getBIReadReportManager() {
        return StableFactory.getMarkedObject(BIReadReportProvider.XML_TAG, BIReadReportProvider.class);
    }

    public static BIReadReportUtils getInstance() {
        synchronized (BIReadReportUtils.class) {
            if (manager == null) {
                manager = new BIReadReportUtils();
            }
            return manager;
        }
    }

    @Override
    public JSONObject getBIReportNodeJSON(BIReportNode node) throws Exception {
        BIDesignSetting setting = getBIReportNodeSetting(node);
        if (setting.needUpdate()) {
            setting.updateSetting();
            saveReportSetting(node, setting);
        }
        return setting.getReportJSON();
    }

    private BIDesignSetting getBIReportNodeSetting(BIReportNode node) throws Exception {
        File file = getFileLocation(node);
        if (!file.exists()) {
            throw new RuntimeException("can't find file:" + node.getPath() + "! might be delete or move!");
        }
        BIDesignSetting biDesignSetting = (BIDesignSetting) BaseXMLUtils.readXMLFile(
                BaseUtils.readResource(file.getAbsolutePath()),
                new BIDesignSetting());
        return biDesignSetting;
    }

    public void saveReportSetting(BIReportNode node, BIDesignSetting setting) throws Exception {
        try {
            readWriteLock.writeLock().lock();
            File file = getFileLocation(node);
            new BIDesignReport(setting).writeFile(file);
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private File getFileLocation(BIReportNode node) throws Exception {
        String nodePath = CodeUtils.decodeText(node.getPath());
        /**
         * 兼容以前的绝对路径
         */
        String dirString = BIFileRepository.getInstance().getBIDirName(node.getUserId());
        //屏蔽系统文件路径问题
        int nodeLength = nodePath.lastIndexOf("\\" + dirString + "\\");
        nodeLength = nodeLength == -1 ? nodePath.lastIndexOf("/" + dirString + "/") : nodeLength;
        if (nodeLength > -1) {
            // +2 左右两个路径分隔符
            nodePath = nodePath.substring(nodeLength + dirString.length() + 2);
        }
        if (nodePath.startsWith("\\") || nodePath.startsWith("/")) {
            nodePath = nodePath.substring(1);
        }
        File parent = BIFileRepository.getInstance().getBIDirFile(node.getUserId());
        return new File(parent, nodePath);
    }
}
