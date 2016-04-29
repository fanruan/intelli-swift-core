package com.fr.bi.cluster.manager;

import com.fr.file.XMLFileManager;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 分布式配置，只需要配置master机器
 *
 * @author Daniel
 */
public class BIClusterManager extends XMLFileManager {

    private static BIClusterManager manager = null;
    /**
     * 非master机器均可以作为正常的web服务器使用
     */
    private boolean isMaster = false;
    //应用工程全名:example (http://192.168.100.174/WebReport/ReportServer)
    private List<String> slaveList = new ArrayList<String>();
    private String master;
    /**
     * 该属性由master检测时候分发，不保存到xml文件
     */
    private transient String currentProject;

    private BIClusterManager() {
    }

    public static BIClusterManager getInstance() {
        if (manager == null) {
            manager = HOLDER.m;
        }
        return manager;
    }

    public static void envChanged() {
        manager = null;
        HOLDER.m = new BIClusterManager();
        HOLDER.m.readXMLFile();
    }

    @Override
    public String fileName() {
        return "bi_cluster.xml";
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {

    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.isMaster = reader.getAttrAsBoolean("isMaster", false);
        }
        if (reader.isChildNode()) {
            String tag = reader.getTagName();
            if ("slave".equals(tag)) {
                String slave = reader.getAttrAsString("project", null);
                if (slave != null) {
                    this.slaveList.add(slave);
                }
            } else if ("master".equals(tag)) {
                String master = reader.getAttrAsString("project", null);
                if (master != null) {
                    setMaster(master);
                } else {
                    isMaster = false;
                }
            }
        }
    }

    public Iterator<String> slaveListIterator() {
        return slaveList.iterator();
    }

    public boolean useCluster() {
        return isMaster;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    /**
     * 返回当前slave名字
     *
     * @return
     */
    public String getCurrentProject() {
        return isMaster ? master : currentProject;
    }

    /**
     * 设置当前slave的名字 ，在master检测的时候分发改属性
     *
     * @param project
     */
    public void setCurrentProject(String project) {
        this.currentProject = project;
    }

    private static class HOLDER {
        private static BIClusterManager m = new BIClusterManager();

        static {
            m.readXMLFile();
        }
    }
    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                synchronized (manager) {
                    BIClusterManager.envChanged();
                }
            }
        });
    }

}