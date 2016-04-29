package com.fr.bi.fs;

import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.cache.list.IntList;
import com.fr.data.dao.DataAccessObjectSession;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.dao.PlatformDataAccessObject;

import java.util.List;

/**
 * @author richie
 * @date 2015-04-28
 * @since 8.0
 */
public class HSQLBIReportDAO extends PlatformDataAccessObject implements BIReportDAO, BISharedReportDAO {

    private static HSQLBIReportDAO dao = null;

    public static HSQLBIReportDAO getInstance() {
        dao =  BIConstructorUtils.constructObject(HSQLBIReportDAO.class, dao);
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
	public void resetSharedByReportIdAndUsers(long reportId, long[] userids) {
        java.util.List sharedNodes = createSession().listByFieldValue(BISharedReportNode.class, BITableMapper.BI_SHARED_REPORT_NODE.FIELD_SHARED_REPORT_ID, new Long(reportId));

        IntList indicesOfNewUserIdsMatchedInOld = new IntList();
        for (int i = 0, len = sharedNodes.size(); i < len; i++) {
            BISharedReportNode node = (BISharedReportNode) sharedNodes.get(i);
            long userIdShared = node.getUserId();

            boolean findMatch = false;
            for (int ui = 0; ui < userids.length; ui++) {
                if (userids[ui] == userIdShared) {
                    indicesOfNewUserIdsMatchedInOld.add(ui);
                    findMatch = true;
                    break;
                }
            }
            if (!findMatch) {
                createSession().delete(node);
            }
        }

        for (int i = 0; i < userids.length; i++) {
            if (!indicesOfNewUserIdsMatchedInOld.contain(i)) {
                long newUserId2Add = userids[i];
                createSession().saveOrUpdate(new BISharedReportNode(reportId, newUserId2Add));
            }
        }
    }

    @Override
	public User[] findUsersByReportId(long id) {
        java.util.List l = createSession().listByFieldValue(BISharedReportNode.class, BITableMapper.BI_SHARED_REPORT_NODE.FIELD_SHARED_REPORT_ID, new Long(id));
        java.util.List users = new java.util.ArrayList();
        for (int i = 0; i < l.size(); i++) {
            BISharedReportNode node = (BISharedReportNode) l.get(i);
            try {
                User user = UserControl.getInstance().getUser(node.getUserId());
                if (user != null) {
                    users.add(user);
                }
            } catch (Exception e) {
            }
        }

        return (User[]) users.toArray(new User[users.size()]);
    }

    @Override
	public long[] findTemplateIdsByUserId(long userId) {
        java.util.List l = createSession().listByFieldValue(BISharedReportNode.class, BITableMapper.BI_SHARED_REPORT_NODE.FIELD_SHARED_USER_ID, new Long(userId));

        long[] templateIds = new long[l.size()];
        for (int i = 0; i < l.size(); i++) {
            BISharedReportNode node = (BISharedReportNode) l.get(i);
            templateIds[i] = node.getBid();
        }

        return templateIds;
    }
}