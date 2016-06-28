package com.fr.bi.fs;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.control.dao.tabledata.TableDataDAOControl;
import com.fr.function.DATE;
import com.fr.stable.CodeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/8/4.
 */
public class BIDAOUtils {

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
            new TableDataDAOControl.ColumnColumn("status", Integer.class),
            new TableDataDAOControl.ColumnColumn("userid", String.class),
    };
    private final static TableDataDAOControl.ColumnColumn[] TABLEDATA_SHARED_COLUMNS = {
            new TableDataDAOControl.ColumnColumn("id", String.class),
            new TableDataDAOControl.ColumnColumn("reportId", String.class),
            new TableDataDAOControl.ColumnColumn("createBy", String.class),
            new TableDataDAOControl.ColumnColumn("shareTo", String.class)
    };

    private static BIReportDAO getReportDao(long userId) {
        if (userId == UserControl.getInstance().getSuperManagerID()) {
            return BISuperManagetDAOManager.getInstance();
        }
        if (userId < 0) {
            return null;
        }
        return UserControl.getInstance().getOpenDAO(BIReportDAO.class);
    }

    public static List<BIReportNode> findByUserID(long userId) throws Exception {
        BIReportDAO dao = getReportDao(userId);
        if (dao == null) {
            return null;
        }
        return dao.findByUserID(userId);
    }
    public static List<BIReportNode> findByParentID(long userId, String parentId) throws Exception {
        BIReportDAO dao = getReportDao(userId);
        if (dao == null) {
            return null;
        }
        return dao.findByParentID(parentId);
    }

    public static BIReportNode findByID(long id, long userId) throws Exception {
        BIReportDAO dao = getReportDao(userId);
        if (dao == null) {
            return null;
        }
        return dao.findByID(id);
    }

    public static void saveOrUpDate(BIReportNode node, long userId) throws Exception {
        BIReportDAO dao = getReportDao(userId);
        if (dao != null) {
            node.updateLastModifyTime();
            dao.saveOrUpdate(node);
        }
    }

    public static List<BIReportNode> getBIReportNodesByShare2User(long userId) throws Exception {
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
            nodes.add(BIDAOUtils.findByID(sNode.getReportId(), sNode.getCreateBy()));
        }
        return nodes;
    }

    public static List<User> getSharedUsersByReport(long reportId, long createBy) {
        List<User> users = null;
        try {
            users = UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).findUsersByReport(reportId, createBy);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return users;
    }

    public static boolean deleteBIReportById(long userId, long id) throws Exception {
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

    public static long generateID(Map idMap) {
        long i = 1;
        while (idMap.get(new Long(i)) != null) {
            i++;
        }
        return i;
    }

    public static EmbeddedTableData initEmbeddedTableData() {
        return initTableData(COLUMNS);
    }

    public static EmbeddedTableData initTableDataEmbeddedTableData() {
        return initTableData(TABLEDATA_COLUMNS);
    }

    private static EmbeddedTableData initTableData(TableDataDAOControl.ColumnColumn[] columns) {
        EmbeddedTableData emb = new EmbeddedTableData();
        for (int i = 0; i < columns.length; i++) {
            emb.addColumn(columns[i].getName(), columns[i].getClazz());
        }
        return emb;
    }

    public static EmbeddedTableData initTableDataSharedEmbeddedTableData() {
        return initTableData(TABLEDATA_SHARED_COLUMNS);
    }

}