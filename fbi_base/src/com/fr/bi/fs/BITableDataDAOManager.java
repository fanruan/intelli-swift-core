package com.fr.bi.fs;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.file.XMLFileManager;
import com.fr.fs.base.entity.User;
import com.fr.fs.cache.tabledata.TableDataSyncDB;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.ArrayUtils;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.Primitive;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/8/5.
 */
public class BITableDataDAOManager extends XMLFileManager {
    private static final String XML_TAG = "BITableDataDAOManager";
    private static BITableDataDAOManager manager;
    private final Map<Long, BIReportNode> tdBIReport_idMap = new Hashtable<Long, BIReportNode>();
    private final Map<Long, BIReportNode> tdBISharedReport_idMap = new Hashtable<Long, BIReportNode>();
    private EmbeddedTableData biReportTableData;
    private EmbeddedTableData biSharedReportTableData;

    private BITableDataDAOManager() {
        readXMLFile();
    }

    public static BITableDataDAOManager getInstance() {
        manager =  BIConstructorUtils.constructObject(BITableDataDAOManager.class, manager);
        return manager;
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                BITableDataDAOManager.getInstance().envChanged();
            }
        });
    }

    public void saveOrUpdateBIReportNode(BIReportNode node) throws Exception {
        long userId = node.getUserId();
        User tdUser = TableDataSyncDB.getInstance().findUserByUserId(userId);
        if (tdUser != null) {
            long id = node.getId() < 0 ? BIDAOUtils.generateID(tdBIReport_idMap) : node.getId();
            node.setId(id);
            node.setUsername(tdUser.getUsername());
            tdBIReport_idMap.put(id, node);
            writeTableDataBIReportMap(getTdBIReport_idEntrySet());
            FRContext.getCurrentEnv().writeResource(BITableDataDAOManager.getInstance());
        }
    }

    private Set<Map.Entry<Long, BIReportNode>> getTdBIReport_idEntrySet() {
        synchronized (TableDataBIReportDAO.class) {
            return tdBIReport_idMap.entrySet();
        }
    }

    private Set<Map.Entry<Long, BIReportNode>> getTdBISharedReport_idEntrySet() {
        synchronized (TableDataBIReportDAO.class) {
            return tdBISharedReport_idMap.entrySet();
        }
    }

    public void writeTableDataBIReportMap(Set<Map.Entry<Long, BIReportNode>> set) {
        synchronized (BITableDataDAOManager.class){
            this.biReportTableData = BIDAOUtils.initTableDataEmbeddedTableData();
            for (Map.Entry<Long, BIReportNode> entry : set) {
                BIReportNode tdNode = entry.getValue();
                List<String> rowList = new ArrayList<String>();
                rowList.add(String.valueOf(tdNode.getId()));
                rowList.add(String.valueOf(tdNode.getParentid()));
                rowList.add(tdNode.getUsername());
                rowList.add(tdNode.getPath());
                rowList.add(tdNode.getDisplayName());
                rowList.add(String.valueOf(tdNode.getCreatetime().getTime()));
                rowList.add(String.valueOf(tdNode.getLastModifyTime().getTime()));
                rowList.add(String.valueOf(-1));
                rowList.add(tdNode.getDescription());
                rowList.add(String.valueOf(tdNode.getStatus()));
                rowList.add(String.valueOf(tdNode.getUserId()));
                this.biReportTableData.addRow(rowList);
            }
        }
    }

    public List findBIReportNodeByUserId(long userid) throws Exception {
        List<BIReportNode> resList = new ArrayList<BIReportNode>();
        User tdUser = TableDataSyncDB.getInstance().findUserByUserId(userid);
        if (tdUser != null) {
            for (Map.Entry<Long, BIReportNode> entry : getTdBIReport_idEntrySet()) {
                BIReportNode tdNode = entry.getValue();
                if (ComparatorUtils.equals( tdUser.getUsername(), tdNode.getUsername())) {
                    resList.add(tdNode);
                }
            }
        }
        return resList;
    }

    public BIReportNode findBIReportNodeById(long id) throws Exception {
        BIReportNode tdNode = tdBIReport_idMap.get(id);
        User tdUser = TableDataSyncDB.getInstance().findUserByUserName(tdNode.getUsername());
        if (tdUser != null) {
            tdNode.setUserId(tdUser.getId());
            return tdNode;
        }
        return null;
    }

    public boolean deleteBIReportNodeById(long id) throws Exception {
        tdBIReport_idMap.remove(id);
        writeTableDataBIReportMap(getTdBIReport_idEntrySet());
        return FRContext.getCurrentEnv().writeResource(BITableDataDAOManager.getInstance());
    }

    /* (non-Javadoc)
     * @see com.fr.bi.fs.control.dao.BIReportDAO#findByName(java.lang.String)
     */
    public BIReportNode findBIReportNodeByName(String name) throws Exception {
        BIReportNode res = null;
        for (Map.Entry<Long, BIReportNode> entry : getTdBIReport_idEntrySet()) {
            BIReportNode tdNode = entry.getValue();
            if (ComparatorUtils.equals(name, tdNode.getReportName())) {
                // 有多个相同文件名,则返回null.
                if (res != null) {
                    return null;
                } else {
                    User tdUser = TableDataSyncDB.getInstance().findUserByUserName(tdNode.getUsername());
                    if (tdUser != null) {
                        tdNode.setUserId(tdUser.getId());
                        res = tdNode;
                    }
                }
            }
        }
        return res;
    }

    public List<BIReportNode> findByParentID(String pId) throws Exception {
        List<BIReportNode> res = null;
        for (Map.Entry<Long, BIReportNode> entry : getTdBIReport_idEntrySet()) {
            BIReportNode tdNode = entry.getValue();
            if (ComparatorUtils.equals(pId, tdNode.getParentid())) {
                res.add(tdNode);
            }
        }
        return res;
    }

    public List findAllBIReportNode() throws Exception {
        List<BIReportNode> resList = new ArrayList<BIReportNode>();
        for (Map.Entry<Long, BIReportNode> entry : getTdBIReport_idEntrySet()) {
            BIReportNode tdNode = entry.getValue();
            User tdUser = TableDataSyncDB.getInstance().findUserByUserName(tdNode.getUsername());
            if (tdUser != null) {
                tdNode.setUserId(tdUser.getId());
                resList.add(tdNode);
            }
        }
        return resList;
    }

    /**
     * 重置共享
     *
     * @param reportId 模板id
     * @param userIds  用户id
     */
    public void resetSharedByReportIdAndUsers(long reportId, long[] userIds) throws Exception {
        synchronized (BITableDataDAOManager.class) {
            long[] userIdsNeedToAddIntoMapLeft = userIds;
            Iterator iter = getTdBISharedReport_idEntrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                BIReportNode node = (BIReportNode) entry.getValue();
                if (node.getTemplateid() == reportId) {
                    long userId = node.getUserId();
                    int existIndex = ArrayUtils.indexOf(userIdsNeedToAddIntoMapLeft, userId);
                    if (existIndex >= 0) {
                        userIdsNeedToAddIntoMapLeft = ArrayUtils.remove(userIdsNeedToAddIntoMapLeft, existIndex);
                    } else {
                        iter.remove();
                    }
                }
            }
            for (long userId : userIdsNeedToAddIntoMapLeft) {
                BIReportNode newNode = new BIReportNode(BIDAOUtils.generateID(tdBISharedReport_idMap));
                newNode.setTemplateid(reportId);
                newNode.setUserId(userId);
                tdBISharedReport_idMap.put(newNode.getId(), newNode);
            }
            writeTableDataBISharedReportMap(getTdBISharedReport_idEntrySet());
            FRContext.getCurrentEnv().writeResource(BITableDataDAOManager.getInstance());
        }
    }

    /**
     * 保存BI共享表
     *
     * @param set BI共享集合
     */
    public void writeTableDataBISharedReportMap(Set<Map.Entry<Long, BIReportNode>> set) {
        synchronized (BITableDataDAOManager.class){
            this.biSharedReportTableData = BIDAOUtils.initTableDataSharedEmbeddedTableData();
            for (Map.Entry<Long, BIReportNode> entry : set) {
                BIReportNode tdNode = entry.getValue();
                List<String> rowList = new ArrayList<String>();
                rowList.add(String.valueOf(tdNode.getId()));
                rowList.add(String.valueOf(tdNode.getTemplateid()));
                rowList.add(String.valueOf(tdNode.getUserId()));
                this.biSharedReportTableData.addRow(rowList);
            }
        }
    }

    public User[] findUsersAccessibleOfTemplateId(long templateId) {
        synchronized (this) {
            List<User> userList = new ArrayList<User>();
            for (Map.Entry<Long, BIReportNode> entry : getTdBISharedReport_idEntrySet()) {
                BIReportNode node = entry.getValue();
                if (node.getTemplateid() == templateId) {
                    long userId = node.getUserId();
                    User userById = TableDataSyncDB.getInstance().findUserByUserId(userId);
                    if (userById != null) {
                        userList.add(userById);
                    }
                }
            }
            return userList.toArray(new User[userList.size()]);
        }
    }

    public long[] findTemplateIdsAccessible4UserId(long userId) {
        long[] ret = new long[0];
        for (Map.Entry<Long, BIReportNode> entry : getTdBISharedReport_idEntrySet()) {
            BIReportNode node = entry.getValue();
            if (node.getUserId() == userId) {
                ret = ArrayUtils.add(ret, node.getTemplateid());
            }
        }
        return ret;
    }

    /**
     * 环境改变
     */
    public void envChanged() {
        tdBIReport_idMap.clear();
        tdBISharedReport_idMap.clear();
        readXMLFile();
    }

    @Override
    public String fileName() {
        return "tabledatadao.xml";
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if ("BIReport".equals(tagName)) {
                reader.readXMLObject(this.getBIReportTabledata());
                initNode();
            } else if ("BISharedReport".equals(tagName)) {
                reader.readXMLObject(this.getBISharedReportTableData());
                initShare();
            }
        }
    }

    private void initNode() {
        tdBIReport_idMap.clear();
        try {
            synchronized (tdBIReport_idMap) {
                if (tdBIReport_idMap.isEmpty()) {
                    EmbeddedTableData tableData = getBIReportTabledata();
                    for (int i = 0, len = tableData.getRowCount(); i < len; i++) {
                        try {
                            Long id = new Long(tableData.getValueAt(i, 0).toString());
                            BIReportNode tdNode = new BIReportNode(id);
                            tdNode.setParentid(tableData.getValueAt(i, 1).toString());
                            tdNode.setUsername(tableData.getValueAt(i, 2).toString());
                            tdNode.setPath(tableData.getValueAt(i, 3).toString());
                            tdNode.setReportName(tableData.getValueAt(i, 4).toString());
                            tdNode.setCreatetime(new Date(Long.parseLong((String) tableData.getValueAt(i, 5))));
                            tdNode.setLastModifyTime(new Date(Long.parseLong((String) tableData.getValueAt(i, 6))));
                            //中间有删除的属性所以空掉一个
                            Object des = tableData.getValueAt(i, 8);
                            if (des != Primitive.NULL && des != null) {
                                tdNode.setDescription(des.toString());
                            }
                            int status = BIReportConstant.BI_REPORT.NULL;
                            try {
                            	Object ss = tableData.getValueAt(i, 9);
                            	 if (ss != Primitive.NULL && ss != null) {
                                     status = Integer.valueOf(ss.toString());
                            	 }
                            } catch (Exception e){
                            }
                            tdNode.setStatus(status);
                            tdNode.setUserId(Long.valueOf(tableData.getValueAt(i, 10).toString()));
                            tdBIReport_idMap.put(id, tdNode);
                        } catch (Exception e) {
                            FRContext.getLogger().error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage());
        }
    }

    private void initShare() {
        tdBISharedReport_idMap.clear();
        try {
            synchronized (tdBISharedReport_idMap) {
                if (tdBISharedReport_idMap.isEmpty()) {
                    EmbeddedTableData tableData = getBISharedReportTableData();
                    for (int i = 0, len = tableData.getRowCount(); i < len; i++) {
                        try {
                            Long id = new Long(tableData.getValueAt(i, 0).toString());
                            BIReportNode tdNode = new BIReportNode(id);
                            tdNode.setTemplateid(new Long(tableData.getValueAt(i, 1).toString()));
                            tdNode.setUserId(new Long(tableData.getValueAt(i, 2).toString()));
                            tdBISharedReport_idMap.put(id, tdNode);
                        } catch (Exception e) {
                            FRContext.getLogger().error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage());
        }
    }

    public EmbeddedTableData getBIReportTabledata() {
        synchronized (BITableDataDAOManager.class){
            if (biReportTableData == null) {
                biReportTableData = BIDAOUtils.initTableDataEmbeddedTableData();
            }
            return biReportTableData;
        }
    }

    public EmbeddedTableData getBISharedReportTableData() {
        synchronized (BITableDataDAOManager.class) {
            if (biSharedReportTableData == null) {
                biSharedReportTableData = BIDAOUtils.initTableDataSharedEmbeddedTableData();
            }
            return biSharedReportTableData;
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        if (this.biReportTableData != null) {
            writer.startTAG("BIReport");
            biReportTableData.writeXML(writer);
            writer.end();
        }
        if (this.biSharedReportTableData != null) {
            writer.startTAG("BISharedReport");
            biSharedReportTableData.writeXML(writer);
            writer.end();
        }
        writer.end();
    }
}