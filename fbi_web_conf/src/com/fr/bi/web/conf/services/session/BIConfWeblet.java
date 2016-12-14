package com.fr.bi.web.conf.services.session;

import com.fr.fs.web.service.ServiceUtils;
import com.fr.stable.web.SessionProvider;
import com.fr.stable.web.Weblet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Young's on 2016/12/14.
 */
public class BIConfWeblet implements Weblet {

    private Long userId;

    public BIConfWeblet() {

    }

    public BIConfWeblet(long userId) {
        this.userId = userId;
    }

    @Override
    public SessionProvider createSessionIDInfor(HttpServletRequest req, String remoteAddress, Map map) throws Exception {
        if(userId == null) {
            userId = ServiceUtils.getCurrentUserID(req);
        }
        return new BIConfSession(remoteAddress, userId);
    }

    /**
     * 是否占并发
     */
    @Override
    public boolean isSessionOccupy() {
        return false;
    }

    @Override
    public void setTplPath(String s) {

    }

    @Override
    public void setParameterMap(Map map) {

    }

    @Override
    public void dealWeblet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

    }
}
