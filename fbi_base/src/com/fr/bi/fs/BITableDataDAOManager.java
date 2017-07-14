package com.fr.bi.fs;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.file.XMLFileManager;
import com.fr.fs.base.entity.User;
import com.fr.fs.cache.tabledata.TableDataSyncDB;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.ArrayUtils;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/8/5.
 */
public class BITableDataDAOManager extends XMLFileManager implements BITableDataDAOProvider{
    private static final String XML_TAG = "BITableDataDAOManager";
    private static BITableDataDAOManager manager;
    private final Map<Long, BIReportNode> tdBIReport_idMap = new Hashtable<Long, BIReportNode>();
    private final Map<Long, BISharedReportNode> tdBISharedReport_idMap = new Hashtable<Long, BISharedReportNode>();
    private EmbeddedTableData biReportTableData;
    private EmbeddedTableData biSharedReportTableData;

    private BITableDataDAOManager() {
        readXMLFile();
    }
    public static BITableDataDAOManager getInstance() {
        manager = BIConstructorUtils.constructObject(BITableDataDAOManager.class, manager);
        return manager;
    }
    public static BITableDataDAOProvider getProviderInstance(){
        return  StableFactory.getMarkedObject(BITableDataDAOProvider.XML_TAG,BITableDataDAOProvider.class);
    }
    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                BITableDataDAOManager.getInstance().envChanged();
            }
        });
    }

    @Override
    public void saveOrUpdateBIReportNode(BIReportNode node) throws Exception {
        long userId = node.getUserId();
        User tdUser = TableDataSyncDB.getInstance().findUserByUserId(userId);
        if (tdUser != null) {
            long id = node.getId() < 0 ? BIDAOUtils.getBIDAOManager().generateID(tdBIReport_idMap) : node.getId();
            node.setId(id);
            node.setUsername(tdUser.getUsername());
            tdBIReport_idMap.put(id, node);
            writeTableDataBIReportMap(getTdBIReport_idEntrySet());
            FRContext.getCurrentEnv().writeResource(BITableDataDAOManager.getProviderInstance());
        }
    }

    private Set<Map.Entry<Long, BIReportNode>> getTdBIReport_idEntrySet() {
        synchronized (TableDataBIReportDAO.class) {
            return tdBIReport_idMap.entrySet();
        }
    }

    private Set<Map.Entry<Long, BISharedReportNode>> getTdBISharedReport_idEntrySet() {
        synchronized (TableDataBIReportDAO.class) {
            return tdBISharedReport_idMap.entrySet();
        }
    }

    private void writeTableDataBIReportMap(Set<Map.Entry<Long, BIReportNode>> set) {
        synchronized (BITableDataDAOManager.class) {
            this.biReportTableData = BIDAOUtils.getBIDAOManager().initTableDataEmbeddedTableData();
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

    @Override
    public List findBIReportNodeByUserId(long userid) throws Exception {
        List<BIReportNode> resList = new ArrayList<BIReportNode>();
        User tdUser = TableDataSyncDB.getInstance().findUserByUserId(userid);
        if (tdUser != null) {
            for (Map.Entry<Long, BIReportNode> entry : getTdBIReport_idEntrySet()) {
                BIReportNode tdNode = entry.getValue();
                if (ComparatorUtils.equals(tdUser.getUsername(), tdNode.getUsername())) {
                    resList.add(tdNode);
                }
            }
        }
        return resList;
    }

    @Override
    public BIReportNode findBIReportNodeById(long id) throws Exception {
        BIReportNode tdNode = tdBIReport_idMap.get(id);
        if (tdNode != null) {
            User tdUser = TableDataSyncDB.getInstance().findUserByUserName(tdNode.getUsername());
            if (tdUser != null) {
                tdNode.setUserId(tdUser.getId());
                return tdNode;
            }
        }
        return null;
    }

    @Override
    public boolean deleteBIReportNodeById(long id) throws Exception {
        tdBIReport_idMap.remove(id);
        writeTableDataBIReportMap(getTdBIReport_idEntrySet());
        return FRContext.getCurrentEnv().writeResource(BITableDataDAOManager.getProviderInstance());
    }

    /* (non-Javadoc)
     * @see com.fr.bi.fs.control.dao.BIReportDAO#findByName(java.lang.String)
     */
    @Override
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

    @Override
    public List<BIReportNode> findByParentID(String pId) throws Exception {
        List<BIReportNode> res = new ArrayList<BIReportNode>();
        for (Map.Entry<Long, BIReportNode> entry : getTdBIReport_idEntrySet()) {
            BIReportNode tdNode = entry.getValue();
            if (ComparatorUtils.equals(pId, tdNode.getParentid())) {
                res.add(tdNode);
            }
        }
        return res;
    }

    @Override
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
    @Override
    public void resetSharedByReportIdAndUsers(long reportId, long createBy, long[] userIds, boolean isReset) throws Exception {
        synchronized (BITableDataDAOManager.class) {
            User createUser = TableDataSyncDB.getInstance().findUserByUserId(createBy);
            Iterator iterator = getTdBISharedReport_idEntrySet().iterator();
            //找到模板 新分享的：看下原来有没有，有的话忽略，没有添加 编辑分享：全部删除后添加
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                BISharedReportNode node = (BISharedReportNode) entry.getValue();
                User shareToUser = TableDataSyncDB.getInstance().findUserByUserName(node.getShareToName());
                if (shareToUser != null) {
                    long shareToId = shareToUser.getId();
                    if (node.getReportId() == reportId && ComparatorUtils.equals(createUser.getUsername(), node.getCreateByName())) {
                        if (isReset) {
                            iterator.remove();
                        } else if (ArrayUtils.contains(userIds, shareToId)) {
                            userIds = ArrayUtils.remove(userIds, ArrayUtils.indexOf(userIds, shareToId));
                        }
                    }
                }
            }
            for (long userId : userIds) {
                BISharedReportNode newNode = new BISharedReportNode(BIDAOUtils.getBIDAOManager().generateID(tdBISharedReport_idMap));
                newNode.setReportId(reportId);
                newNode.setCreateByName(createUser.getUsername());
                newNode.setShareToName(TableDataSyncDB.getInstance().findUserByUserId(userId).getUsername());
                tdBISharedReport_idMap.put(newNode.getId(), newNode);
            }
            writeTableDataBISharedReportMap(getTdBISharedReport_idEntrySet());
            FRContext.getCurrentEnv().writeResource(BITableDataDAOManager.getProviderInstance());
        }
    }

    /**
     * 保存BI共享表
     *
     * @param set BI共享集合
     */
    private void writeTableDataBISharedReportMap(Set<Map.Entry<Long, BISharedReportNode>> set) {
        synchronized (BITableDataDAOManager.class) {
            this.biSharedReportTableData = BIDAOUtils.getBIDAOManager().initTableDataSharedEmbeddedTableData();
            for (Map.Entry<Long, BISharedReportNode> entry : set) {
                BISharedReportNode tdNode = entry.getValue();
                List<String> rowList = new ArrayList<String>();
                rowList.add(String.valueOf(tdNode.getId()));
                rowList.add(String.valueOf(tdNode.getReportId()));
                rowList.add(String.valueOf(tdNode.getCreateBy()));
                rowList.add(String.valueOf(tdNode.getShareTo()));
                rowList.add(String.valueOf(tdNode.getCreateByName()));
                rowList.add(String.valueOf(tdNode.getShareToName()));
                this.biSharedReportTableData.addRow(rowList);
            }
        }
    }

    @Override
    public List<User> findUsersAccessibleOfTemplateId(long reportId, long createBy) {
        synchronized (this) {
            User tdUser = TableDataSyncDB.getInstance().findUserByUserId(createBy);
            List<User> users = new ArrayList<User>();
            for (Map.Entry<Long, BISharedReportNode> entry : getTdBISharedReport_idEntrySet()) {
                BISharedReportNode node = entry.getValue();
                if (tdUser != null && node.getReportId() == reportId && ComparatorUtils.equals(node.getCreateByName(), tdUser.getUsername())) {
                    try {
                        User user = TableDataSyncDB.getInstance().findUserByUserName(node.getShareToName());
                        if (user != null) {
                            users.add(user);
                        }
                    } catch (Exception e) {
                        BILoggerFactory.getLogger().error(e.getMessage(), e);
                    }
                }
            }
            return users;
        }
    }

    @Override
    public List<BISharedReportNode> findReportsByShare2User(long userId) {
        List<BISharedReportNode> nodes = new ArrayList<BISharedReportNode>();
        User tdUser = TableDataSyncDB.getInstance().findUserByUserId(userId);
        for (Map.Entry<Long, BISharedReportNode> entry : getTdBISharedReport_idEntrySet()) {
            BISharedReportNode node = entry.getValue();
            if (tdUser != null && ComparatorUtils.equals(tdUser.getUsername(), node.getShareToName())) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    @Override
    public void removeSharedByReport(long reportId, long createBy) {
        Iterator iter = getTdBISharedReport_idEntrySet().iterator();
        User tdUser = TableDataSyncDB.getInstance().findUserByUserId(createBy);
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            BISharedReportNode node = (BISharedReportNode) entry.getValue();
            if (node.getReportId() == reportId && ComparatorUtils.equals(tdUser.getUsername(), node.getShareToName())) {
                tdBISharedReport_idMap.remove(node.getId());
            }
            try {
                writeTableDataBISharedReportMap(getTdBISharedReport_idEntrySet());
                FRContext.getCurrentEnv().writeResource(BITableDataDAOManager.getProviderInstance());
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    }

    /**
     * 环境改变
     */
    private void envChanged() {
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

                            tdNode.setDescription(tableData.getValueAt(i, 8).toString());
                            long userId = Long.valueOf(tableData.getValueAt(i, 10).toString());
                            //挂出状态还要检查是否是存在于挂出目录结构
                            int status = Integer.valueOf(tableData.getValueAt(i, 9).toString());
                            tdNode.setStatus(status);
                            tdNode.setUserId(userId);
                            tdBIReport_idMap.put(id, tdNode);
                        } catch (Exception e) {
                            FRContext.getLogger().error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(),e);
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
                            BISharedReportNode tdNode = new BISharedReportNode(id);
                            tdNode.setReportId(new Long(tableData.getValueAt(i, 1).toString()));
                            tdNode.setCreateByName(tableData.getValueAt(i, 4).toString());
                            tdNode.setShareToName(tableData.getValueAt(i, 5).toString());
                            tdBISharedReport_idMap.put(id, tdNode);
                        } catch (Exception e) {
                            FRContext.getLogger().error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(),e);
        }
    }

    private EmbeddedTableData getBIReportTabledata() {
        synchronized (BITableDataDAOManager.class) {
            if (biReportTableData == null) {
                biReportTableData = BIDAOUtils.getBIDAOManager().initTableDataEmbeddedTableData();
            }
            return biReportTableData;
        }
    }

    private EmbeddedTableData getBISharedReportTableData() {
        synchronized (BITableDataDAOManager.class) {
            if (biSharedReportTableData == null) {
                biSharedReportTableData = BIDAOUtils.getBIDAOManager().initTableDataSharedEmbeddedTableData();
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

    @Override
    public boolean writeResource() throws Exception {
        return FRContext.getCurrentEnv().writeResource(getProviderInstance());
    }
}