package com.fr.bi.fs;

import com.fr.fs.base.entity.User;

import java.util.List;

/**
 * @author richie
 * @date 2015-04-28
 * @since 8.0
 */
public interface BISharedReportDAO {

    void resetSharedByReportIdAndUsers(long reportId, long createBy, long[] userids) throws Exception;

    List<User> findUsersByReport(long reportId, long createBy) throws Exception;

    List<BISharedReportNode> findReportsByShare2User(long userId);

    void removeSharedByReport(long reportId, long createBy);
}