package com.fr.bi.web.base.services;

import com.fr.bi.conf.base.trans.BIAliasManager;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by GUY on 2015/3/31.
 */
public class BISetTransNameAction extends AbstractBIBaseAction {

    /**
     * trans:[{"id":1,"name":2}];
     *
     * @return
     */
    @Override
    public String getCMD() {
        return "set_trans";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        try {
            String trans = WebUtils.getHTTPRequestParameter(req, "trans");
            JSONArray ja = new JSONArray(trans);
            for (int i = 0, len = ja.length(); i < len; i++) {
                JSONObject jo = ja.optJSONObject(i);
                StableFactory.getMarkedObject(BIAliasManagerProvider.XML_TAG, BIAliasManager.class).setAliasName(jo.optString("id"), jo.optString("name"), userId);
            }
        } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}