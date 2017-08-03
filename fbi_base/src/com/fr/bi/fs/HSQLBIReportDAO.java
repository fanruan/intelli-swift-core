package com.fr.bi.fs;

import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.cache.list.IntList;
import com.fr.data.dao.DataAccessObjectSession;
import com.fr.data.dao.DatabaseAction;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.dao.PlatformDataAccessObject;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author richie
 * @date 2015-04-28
 * @since 8.0
 */
public class HSQLBIReportDAO extends PlatformDataAccessObject implements BIReportDAO, BISharedReportDAO {

    private static HSQLBIReportDAO dao = null;

    public static HSQLBIReportDAO getInstance() {
        dao = BIConstructorUtils.constructObject(HSQLBIReportDAO.class, dao);
        return dao;
    }

    @Override
    public void saveOrUpdate(BIReportNode biReportNode) throws Exception {
        createSession().saveOrUpdate(biReportNode);
    }

    @Override
    public List<BIReportNode> findByUserID(long userId) throws Exception {
        if (userId < 0) {
            return null;
        }
        return createSession().listByFieldValue(BIReportNode.class, BITableMapper.BI_REPORT_NODE.FIELD_USERID, new Long(userId));
    }

    @Override
    public BIReportNode findByID(long id) throws Exception {
        return (BIReportNode) createSession().load(BIReportNode.class, id);
    }

    @Override
    public BIReportNode findByName(String name) throws Exception {
        if (name == null) {
            return null;
        }
        List<BIReportNode> list = createSession().listByFieldValue(BIReportNode.class, BITableMapper.BI_REPORT_NODE.FIELD_REPORTNAME, name);
        if (list.size() != 1) {
            return null;
        } else {
            return (BIReportNode) list.get(0);
        }
    }

    public List<BIReportNode> findByParentID(String pId) throws Exception {
        return createSession().listByFieldValue(BIReportNode.class, BITableMapper.BI_REPORT_NODE.FIELD_PARENTID, pId);
    }

    @Override
    public void delete(BIReportNode node) throws Exception {
        if (node == null) {
            return;
        }
        deleteByID(node.getId());
    }

    @Override
    public boolean deleteByID(long id) throws Exception {
        if (id < 0) {
            return false;
        }

        DataAccessObjectSession session = null;
        try {
            session = createSession();
            session.beginTransaction();
            boolean success = session.deleteByPrimaryKey(BIReportNode.class, id);

            session.commit();
            return success;
        } catch (Exception e) {
            rollbackSession(session);
            throw e;
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<BIReportNode> listAll() throws Exception {
        return createSession().list(BIReportNode.class);
    }

    @Override
    public void resetSharedByReportIdAndUsers(long reportId, long createBy, long[] userIds, boolean isReset) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        String createName = UserControl.getInstance().getUser(createBy).getUsername();
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_REPORT_ID, reportId);
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_CREATE_BY_NAME, createName);
        List sharedNodes = createSession().listByFieldValues(BISharedReportNode.class, map);
        IntList indicesOfNewUserIdsMatchedInOld = new IntList();
        for (int i = 0, len = sharedNodes.size(); i < len; i++) {
            BISharedReportNode node = (BISharedReportNode) sharedNodes.get(i);

            boolean findMatch = false;
            for (int ui = 0; ui < userIds.length; ui++) {
                User user = UserControl.getInstance().getUser(userIds[ui]);
                if (ComparatorUtils.equals(user.getUsername(), node.getShareToName())) {
                    indicesOfNewUserIdsMatchedInOld.add(ui);
                    findMatch = true;
                    break;
                }
            }
            if (!findMatch && isReset) {
                createSession().delete(node);
            }
        }

        for (int i = 0; i < userIds.length; i++) {
            if (!indicesOfNewUserIdsMatchedInOld.contain(i)) {
                String addUserName = UserControl.getInstance().getUser(userIds[i]).getUsername();
                createSession().saveOrUpdate(new BISharedReportNode(reportId, createName, addUserName));
            }
        }
    }

    @Override
    public List<User> findUsersByReport(long reportId, long createBy) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_REPORT_ID, reportId);
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_CREATE_BY_NAME, UserControl.getInstance().getUser(createBy).getUsername());
        List sReports = createSession().listByFieldValues(BISharedReportNode.class, map);
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < sReports.size(); i++) {
            BISharedReportNode node = (BISharedReportNode) sReports.get(i);
            User user = UserControl.getInstance().getByUserName(node.getShareToName());
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<BISharedReportNode> findReportsByShare2User(long userId) throws Exception {
        User user = UserControl.getInstance().getUser(userId);
        if (user != null) {
            String userName = user.getUsername();
            List<BISharedReportNode> sReports = createSession().listByFieldValue(BISharedReportNode.class, BITableMapper.BI_SHARED_REPORT_NODE.FIELD_SHARE_TO_NAME, userName);
            if (sReports != null) {
                return sReports;
            }
        }
        return new ArrayList<BISharedReportNode>();
    }

    @Override
    public void removeSharedByReport(long reportId, long createBy) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_REPORT_ID, reportId);
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_CREATE_BY_NAME, UserControl.getInstance().getUser(createBy).getUsername());
        List sReports = createSession().listByFieldValues(BISharedReportNode.class, map);
        for (int i = 0; i < sReports.size(); i++) {
            BISharedReportNode node = (BISharedReportNode) sReports.get(i);
            DataAccessObjectSession session = null;
            try {
                session = createSession();
                session.beginTransaction();
                session.deleteByPrimaryKey(BISharedReportNode.class, node.getId());
                session.commit();
            } catch (Exception e) {
                rollbackSession(session);
            } finally {
                closeSession(session);
            }
        }
    }
    public void transferSharedReport(BISharedReportNode var1) throws Exception{
        this.createSession(DatabaseAction.TRANSFER).transfer(var1);
    }

    public void transferReport(BIReportNode var1) throws Exception{
        this.createSession(DatabaseAction.TRANSFER).transfer(var1);
    }

    public List findReportAll() throws Exception {
        return this.createSession().list(BIReportNode.class);
    }
    public List findShareReportAll() throws Exception {
        return this.createSession().list(BISharedReportNode.class);
    }
}