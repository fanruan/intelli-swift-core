package com.fr.bi.fs;

import com.fr.fs.base.entity.User;
import com.fr.stable.file.RemoteXMLFileManagerProvider;
import java.util.List;

/**
 * Created by wang on 2017/4/28.
 */
public interface BITableDataDAOProvider extends RemoteXMLFileManagerProvider {
    String XML_TAG = "BITableDataDAOProvider";

    void saveOrUpdateBIReportNode(BIReportNode node) throws Exception;

    List findBIReportNodeByUserId(long userid) throws Exception;

    BIReportNode findBIReportNodeById(long id) throws Exception;

    boolean deleteBIReportNodeById(long id) throws Exception;

    BIReportNode findBIReportNodeByName(String name) throws Exception;

    List<BIReportNode> findByParentID(String pId) throws Exception;

    List findAllBIReportNode() throws Exception;

    void resetSharedByReportIdAndUsers(long reportId, long createBy, long[] userIds, boolean isReset) throws Exception;

    List<User> findUsersAccessibleOfTemplateId(long reportId, long createBy);

    List<BISharedReportNode> findReportsByShare2User(long userId);

    void removeSharedByReport(long reportId, long createBy);


}