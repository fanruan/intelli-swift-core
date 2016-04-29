package com.fr.bi.fs;

import com.fr.fs.control.dao.OpenDAO;

import java.util.List;

/**
 * @author richie
 * @date 2015-04-28
 * @since 8.0
 */
public interface BIReportDAO extends OpenDAO<BIReportNode> {
    @Override
    void saveOrUpdate(BIReportNode biReportNode) throws Exception;

    @Override
    List<BIReportNode> findByUserID(long l) throws Exception;

    @Override
    BIReportNode findByID(long l) throws Exception;

    @Override
    BIReportNode findByName(String s) throws Exception;

    List<BIReportNode> findByParentID(String l) throws Exception;

    @Override
    void delete(BIReportNode biReportNode) throws Exception;

    @Override
    boolean deleteByID(long l) throws Exception;

    @Override
    List<BIReportNode> listAll() throws Exception;

}