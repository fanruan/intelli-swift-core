package com.fr.bi.fs;

import com.fr.fs.base.entity.User;

/**
 * @author richie
 * @date 2015-04-28
 * @since 8.0
 */
public interface BISharedReportDAO {

    void resetSharedByReportIdAndUsers(long reportId, long[] userids) throws Exception;

    long[] findTemplateIdsByUserId(long userId);

    User[] findUsersByReportId(long id) throws Exception;
}