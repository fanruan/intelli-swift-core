package com.fr.bi.fs.entry;

import com.fr.data.dao.DAOException;
import com.fr.fs.dao.EntryDAO;

/**
 * BIreport的DAO操作
 */
public class BIReportEntryDAO extends EntryDAO<BIReportEntry> {
    private static  BIReportEntryDAO dao;

    /**
     * 唯一实例化对象
     * @return
     */
    public static BIReportEntryDAO getInstance() {
        if (dao == null) {
            dao = new BIReportEntryDAO();
        }
        return dao;
    }

    /**
     * 保存
     * @param entry BIReportEntry
     * @throws Exception
     */
    public void save(BIReportEntry entry) throws Exception {
        createSession().save(entry);
    }

    /**
     * 删除
     * @param entry BIReportEntry
     * @return 是否成功
     * @throws Exception
     */
    public boolean delete(BIReportEntry entry) throws Exception {
        long id = entry.getId();
        if (id < 0) {
            throw new DAOException("The object is not a persistent Object. Can not find a right id.");
        }

        return deleteByID(id);
    }

    /**
     * Entry的class
     */
    protected Class getEntryClass() {
        return BIReportEntry.class;
    }

    protected int getEntryType() {
        return EntryConstants.BIREPORT;
    }
}