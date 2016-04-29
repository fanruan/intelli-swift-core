package com.fr.bi.cal.analyze.session;

import com.fr.web.core.SessionDealWith;

/**
 * Created by GUY on 2015/4/8.
 */
public class BISessionUtils {

    public static BISession getSession(String sessionID) {
        return (BISession) SessionDealWith.getSessionIDInfor(sessionID);
    }
}