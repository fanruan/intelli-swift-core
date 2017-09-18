package com.fr.bi.fs;

import com.fr.data.impl.EmbeddedTableData;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.dao.tabledata.TableDataDAOControl;

import java.util.List;
import java.util.Map;

/**
 * Created by wang on 2016/12/5.
 */
public interface BIDAOProvider {
    String XML_TAG = "BIDAO";

    List<BIReportNode> findByUserID(long userId) throws Exception;

    List<BIReportNode> findByParentID(long userId, String parentId) throws Exception;

    BIReportNode findByID(long id, long userId) throws Exception;

    void saveOrUpDate(BIReportNode node, long userId) throws Exception;

    List<BIReportNode> getBIReportNodesByShare2User(long userId) throws Exception;

    List<BIReportNode> getAll() throws Exception;

    List<User> getSharedUsersByReport(long reportId, long createBy);

    boolean deleteBIReportById(long userId, long id) throws Exception;

    long generateID(Map idMap);

    EmbeddedTableData initEmbeddedTableData();

    EmbeddedTableData initTableDataEmbeddedTableData();

    EmbeddedTableData initTableData(TableDataDAOControl.ColumnColumn[] columns);

    EmbeddedTableData initTableDataSharedEmbeddedTableData();
}
