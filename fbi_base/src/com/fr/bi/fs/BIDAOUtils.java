package com.fr.bi.fs;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.control.dao.tabledata.TableDataDAOControl;
import com.fr.function.DATE;
import com.fr.stable.CodeUtils;
import com.fr.stable.bridge.StableFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/8/4.
 */
public class BIDAOUtils implements BIDAOProvider{
    public static final String XML_TAG = "BIDAOUtils";
    private static BIDAOUtils manager;

    public static BIDAOProvider getBIDAOManager(){
        return StableFactory.getMarkedObject(BIDAOProvider.XML_TAG,BIDAOProvider.class);
    }
    public static BIDAOUtils getInstance() {
        synchronized (BIDAOUtils.class) {
            if (manager == null) {
                manager = new BIDAOUtils();
            }
            return manager;
        }
    }
    private final static TableDataDAOControl.ColumnColumn[] COLUMNS = {
            new TableDataDAOControl.ColumnColumn("id", String.class),
            new TableDataDAOControl.ColumnColumn("parentid", String.class),
            new TableDataDAOControl.ColumnColumn("path", String.class),
            new TableDataDAOControl.ColumnColumn("reportName", String.class),
            new TableDataDAOControl.ColumnColumn("createTime", DATE.class),
            new TableDataDAOControl.ColumnColumn("modifyTime", DATE.class),
            new TableDataDAOControl.ColumnColumn("type", String.class),
            new TableDataDAOControl.ColumnColumn("description", String.class),
    };
    private final static TableDataDAOControl.ColumnColumn[] TABLEDATA_COLUMNS = {
            new TableDataDAOControl.ColumnColumn("id", String.class),
            new TableDataDAOControl.ColumnColumn("parentid", String.class),
            new TableDataDAOControl.ColumnColumn("userName", String.class),
            new TableDataDAOControl.ColumnColumn("path", String.class),
            new TableDataDAOControl.ColumnColumn("reportName", String.class),
            new TableDataDAOControl.ColumnColumn("createTime", DATE.class),
            new TableDataDAOControl.ColumnColumn("modifyTime", DATE.class),
            new TableDataDAOControl.ColumnColumn("type", Integer.class),
            new TableDataDAOControl.ColumnColumn("description", String.class),
            new TableDataDAOControl.ColumnColumn("responed", Integer.class),
            new TableDataDAOControl.ColumnColumn("userid", String.class),
    };
    private final static TableDataDAOControl.ColumnColumn[] TABLEDATA_SHARED_COLUMNS = {
            new TableDataDAOControl.ColumnColumn("id", String.class),
            new TableDataDAOControl.ColumnColumn("reportId", String.class),
            new TableDataDAOControl.ColumnColumn("createBy", String.class),
            new TableDataDAOControl.ColumnColumn("shareTo", String.class),
            new TableDataDAOControl.ColumnColumn("createByName", String.class),
            new TableDataDAOControl.ColumnColumn("shareToName", String.class)
    };

    private static BIReportDAO getReportDao(long userId) {
        if (userId == UserControl.getInstance().getSuperManagerID()) {
            return BISuperManagetDAOManager.getBISuperManagetDAOManager();
        }
        if (userId < 0) {
            return null;
        }
        return UserControl.getInstance().getOpenDAO(BIReportDAO.class);
    }

    @Override
    public List<BIReportNode> findByUserID(long userId) throws Exception {
        BIReportDAO dao = getReportDao(userId);
        if (dao == null) {
            return null;
        }
        return dao.findByUserID(userId);
    }
    @Override
    public List<BIReportNode> findByParentID(long userId, String parentId) throws Exception {
        BIReportDAO dao = getReportDao(userId);
        if (dao == null) {
            return null;
        }
        return dao.findByParentID(parentId);
    }

    @Override
    public BIReportNode findByID(long id, long userId) throws Exception {
        BIReportDAO dao = getReportDao(userId);
        if (dao == null) {
            return null;
        }
        return dao.findByID(id);
    }

    @Override
    public void saveOrUpDate(BIReportNode node, long userId) throws Exception {
        BIReportDAO dao = getReportDao(userId);
        if (dao != null) {
            node.updateLastModifyTime();
            dao.saveOrUpdate(node);
        }
    }

    @Override
    public List<BIReportNode> getBIReportNodesByShare2User(long userId) throws Exception {
        if (userId == UserControl.getInstance().getSuperManagerID()) {
            return java.util.Collections.EMPTY_LIST;
        }
        if (userId < 0) {
            return null;
        }
        List<BISharedReportNode> sharedReports = UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).findReportsByShare2User(userId);
        List<BIReportNode> nodes = new ArrayList<BIReportNode>();
        for(int i = 0; i < sharedReports.size(); i++) {
            BISharedReportNode sNode = sharedReports.get(i);
            BIReportNode node = findByID(sNode.getReportId(), UserControl.getInstance().getUser(sNode.getCreateByName()));
            if (node != null) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    @Override
    public List<BIReportNode> getAll() throws Exception {
        BIReportDAO dao = getReportDao(UserControl.getInstance().getSuperManagerID());
        if (dao == null) {
            return null;
        }
        return dao.listAll();
    }

    @Override
    public List<User> getSharedUsersByReport(long reportId, long createBy) {
        List<User> users = null;
        try {
            users = UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).findUsersByReport(reportId, createBy);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return users;
    }

    @Override
    public boolean deleteBIReportById(long userId, long id) throws Exception {
        BIReportNode node = null;
        boolean isDeleted = false;
        BIReportDAO dao = getReportDao(userId);
        if (dao != null) {
            node = dao.findByID(id);
            isDeleted = dao.deleteByID(id);
        }
        if (node != null && isDeleted) {
            String nodePath = CodeUtils.decodeText(node.getPath());
            /**
             * 兼容以前的绝对路径
             */
            String dirString = BIFileRepository.getInstance().getBIDirName(userId);
            int nodeLength = nodePath.lastIndexOf("\\" + dirString + "\\");
            if (nodeLength > -1) {
                nodePath = nodePath.substring(nodeLength + ("\\" + dirString).length());
            }
            File parent = BIFileRepository.getInstance().getBIDirFile(userId);
            new File(parent, nodePath).delete();
        }

        return isDeleted;
    }

    @Override
    public long generateID(Map idMap) {
        long i = 1;
        while (idMap.get(new Long(i)) != null) {
            i++;
        }
        return i;
    }

    @Override
    public EmbeddedTableData initEmbeddedTableData() {
        return initTableData(COLUMNS);
    }

    @Override
    public EmbeddedTableData initTableDataEmbeddedTableData() {
        return initTableData(TABLEDATA_COLUMNS);
    }

    @Override
    public EmbeddedTableData initTableData(TableDataDAOControl.ColumnColumn[] columns) {
        EmbeddedTableData emb = new EmbeddedTableData();
        for (int i = 0; i < columns.length; i++) {
            emb.addColumn(columns[i].getName(), columns[i].getClazz());
        }
        return emb;
    }

    @Override
    public EmbeddedTableData initTableDataSharedEmbeddedTableData() {
        return initTableData(TABLEDATA_SHARED_COLUMNS);
    }

}