package com.fr.bi.cal.analyze.session;

import com.fr.bi.fs.BIReportNodeLock;
import com.fr.bi.fs.BIReportNodeLockDAO;
import com.fr.fs.control.UserControl;
import com.fr.web.core.SessionDealWith;

import java.util.List;

/**
 * Created by GUY on 2015/4/8.
 */
public class BISessionUtils {

    public static BISession getSession(String sessionID) {
        return (BISession) SessionDealWith.getSessionIDInfor(sessionID);
    }

    public static String getCurrentEditingUserByReport(long reportId, long createBy) throws Exception{
        List<BIReportNodeLock> locks = BIReportNodeLockDAO.getInstance().getLock(createBy, reportId);
        String userName = null;
        //随便取一个了
        if(locks.size() > 0) {
            BIReportNodeLock lock = locks.get(0);
            BISession session = getSession(lock.getSessionId());
            long userId = session.getUserId();
            userName = UserControl.getInstance().getUser(userId).getUsername();
        }
        return userName;
    }
}