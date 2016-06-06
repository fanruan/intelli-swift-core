package com.fr.bi.fs;

import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.cache.list.IntList;
import com.fr.data.dao.DataAccessObjectSession;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.dao.PlatformDataAccessObject;

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
    public void resetSharedByReportIdAndUsers(long reportId, long createBy, long[] userIds) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_REPORT_ID, reportId);
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_CREATE_BY, createBy);
        List sharedNodes = createSession().listByFieldValues(BISharedReportNode.class, map);
        IntList indicesOfNewUserIdsMatchedInOld = new IntList();
        for (int i = 0, len = sharedNodes.size(); i < len; i++) {
            BISharedReportNode node = (BISharedReportNode) sharedNodes.get(i);
            long userIdShared = node.getShareTo();

            boolean findMatch = false;
            for (int ui = 0; ui < userIds.length; ui++) {
                if (userIds[ui] == userIdShared) {
                    indicesOfNewUserIdsMatchedInOld.add(ui);
                    findMatch = true;
                    break;
                }
            }
            if (!findMatch) {
                createSession().delete(node);
            }
        }

        for (int i = 0; i < userIds.length; i++) {
            if (!indicesOfNewUserIdsMatchedInOld.contain(i)) {
                long newUserId2Add = userIds[i];
                createSession().saveOrUpdate(new BISharedReportNode(reportId, createBy, newUserId2Add));
            }
        }
    }

    @Override
    public List<User> findUsersByReport(long reportId, long createBy) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_REPORT_ID, reportId);
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_CREATE_BY, createBy);
        List sReports = createSession().listByFieldValues(BISharedReportNode.class, map);
        List<User> users = new ArrayList<User>();
        for(int i = 0; i < sReports.size(); i++){
            BISharedReportNode node = (BISharedReportNode)sReports.get(i);
            users.add(UserControl.getInstance().getUser(node.getShareTo()));
        }
        return users;
    }

    @Override
    public List<BISharedReportNode> findReportsByShare2User(long userId) {
        List<BISharedReportNode> sReports = createSession().listByFieldValue(BISharedReportNode.class, BITableMapper.BI_SHARED_REPORT_NODE.FIELD_SHARE_TO, userId);
        return sReports;
    }

    @Override
    public void removeSharedByReport(long reportId, long createBy) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_REPORT_ID, reportId);
        map.put(BITableMapper.BI_SHARED_REPORT_NODE.FIELD_CREATE_BY, createBy);
        List sReports = createSession().listByFieldValues(BISharedReportNode.class, map);
        for(int i = 0; i < sReports.size(); i++){
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

}