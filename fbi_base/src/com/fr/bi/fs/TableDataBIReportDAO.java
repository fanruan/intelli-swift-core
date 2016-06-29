package com.fr.bi.fs;

import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.fs.base.entity.User;

import java.util.List;

/**
 * @author richie
 * @date 2015-04-28
 * @since 8.0
 */
public class TableDataBIReportDAO implements BIReportDAO, BISharedReportDAO {
    private static TableDataBIReportDAO SC = null;


    private TableDataBIReportDAO() {

    }

    public static TableDataBIReportDAO getInstance() {
        SC = BIConstructorUtils.constructObject(TableDataBIReportDAO.class, SC);
        return SC;
    }

    /* (non-Javadoc)
     * @see com.fr.fs.control.dao.BIReportDAO#saveOrUpdate(com.fr.bi.fs.BIReportNode)
     */
    @Override
    public void saveOrUpdate(BIReportNode node) throws Exception {
        BITableDataDAOManager.getInstance().saveOrUpdateBIReportNode(node);
    }


    /* (non-Javadoc)
     * @see com.fr.fs.control.dao.BIReportDAO#findByUserID(long)
     */
    @Override
    public List findByUserID(long userid) throws Exception {
        return BITableDataDAOManager.getInstance().findBIReportNodeByUserId(userid);
    }

    /* (non-Javadoc)
     * @see com.fr.fs.control.dao.BIReportDAO#findByID(long)
     */
    @Override
    public BIReportNode findByID(long id) throws Exception {
        return BITableDataDAOManager.getInstance().findBIReportNodeById(id);
    }

    /* (non-Javadoc)
     * @see com.fr.fs.control.dao.BIReportDAO#delete(com.fr.bi.fs.BIReportNode)
     */
    @Override
    public void delete(BIReportNode node) throws Exception {
        if (node == null) {
            throw new RuntimeException("The node don't exist!");
        }
        deleteByID(node.getId());
    }

    /* (non-Javadoc)
     * @see com.fr.fs.control.dao.BIReportDAO#deleteByID(long)
     */
    @Override
    public boolean deleteByID(long id) throws Exception {
        return BITableDataDAOManager.getInstance().deleteBIReportNodeById(id);
    }

    /* (non-Javadoc)
     * @see com.fr.bi.fs.control.dao.BIReportDAO#findByName(java.lang.String)
     */
    @Override
    public BIReportNode findByName(String name) throws Exception {
        return BITableDataDAOManager.getInstance().findBIReportNodeByName(name);
    }

    public List<BIReportNode> findByParentID(String pId) throws Exception {
        return BITableDataDAOManager.getInstance().findByParentID(pId);
    }

    @Override
    public List listAll() throws Exception {
        return BITableDataDAOManager.getInstance().findAllBIReportNode();
    }

    /**
     * 充值共享的报表及用户
     *
     * @param reportId 报表ID
     * @throws Exception
     */
    @Override
    public void resetSharedByReportIdAndUsers(long reportId, long createBy, long[] userIds, boolean isReset) throws Exception {
        BITableDataDAOManager.getInstance().resetSharedByReportIdAndUsers(reportId, createBy, userIds, isReset);
    }

    /**
     * 根据模板ID查找具有权限的用户
     *
     * @return 具有权限的用户数组
     */
    @Override
    public List<User> findUsersByReport(long reportId, long createBy) {
        return BITableDataDAOManager.getInstance().findUsersAccessibleOfTemplateId(reportId, createBy);
    }

    /**
     * 根据用户查找具有权限的模板
     *
     * @param userId 用户ID
     * @return 模板ID数组
     */
    @Override
    public List<BISharedReportNode> findReportsByShare2User(long userId) {
        return BITableDataDAOManager.getInstance().findReportsByShare2User(userId);
    }

    @Override
    public void removeSharedByReport(long reportId, long createBy) {
        BITableDataDAOManager.getInstance().removeSharedByReport(reportId, createBy);
    }


}