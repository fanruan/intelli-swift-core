package com.fr.bi.web.conf.services.session;

import com.fr.bi.conf.tablelock.BIConfTableLock;
import com.fr.bi.conf.tablelock.BIConfTableLockDAO;
import com.fr.fs.control.UserControl;
import com.fr.web.core.SessionDealWith;

import java.util.List;

/**
 * Created by Young's on 2016/12/21.
 */
public class BIConfSessionUtils {

    public static BIConfSession getSession(String sessionID) {
        return (BIConfSession) SessionDealWith.getSessionIDInfor(sessionID);
    }

    public static String getCurrentEditingUserByTableId(long userId, String tableId) throws Exception{
        List<BIConfTableLock> locks = BIConfTableLockDAO.getInstance().getLock(userId, tableId);
        String userName = null;
        //随便取一个了
        if(locks.size() > 0) {
            BIConfTableLock lock = locks.get(0);
            BIConfSession session = getSession(lock.getSessionId());
            long uId = session.getUserId();
            userName = UserControl.getInstance().getUser(uId).getUsername();
        }
        return userName;
    }
}
