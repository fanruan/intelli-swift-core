package com.fr.bi.web.conf.services.datalink;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;


public class BIGetDataLinkAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_data_link";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = new JSONObject();
        JSONObject linksJO = BIConnectionManager.getInstance().createJSON();
        if (ComparatorUtils.equals(userId, UserControl.getInstance().getSuperManagerID())) {
            jo.put("links", linksJO);
        } else {
            JSONObject authLinksJO = new JSONObject();
            Set<BIDataConfigAuthority> authoritySet = BIConfigureManagerCenter.getDataConfigAuthorityManager().getDataConfigAuthoritiesByUserId(userId);
            for (BIDataConfigAuthority authority : authoritySet) {
                if (ComparatorUtils.equals(authority.getView(), DBConstant.DATA_CONFIG_DESIGN.YES)) {
                    String id = authority.getId();
                    String pId = authority.getpId();
                    if (ComparatorUtils.equals(pId, DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION)) {
                        String connName = id.substring(DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION.length());
                        Iterator<String> linkIterator = linksJO.keys();
                        while (linkIterator.hasNext()) {
                            String key = linkIterator.next();
                            JSONObject linkJO = linksJO.getJSONObject(key);
                            if (ComparatorUtils.equals(linkJO.getString("name"), connName) ||
                                    ComparatorUtils.equals(linkJO.optLong("createBy"), userId)) {
                                authLinksJO.put(key, linkJO);
                            }
                        }
                    }
                }
            }
            jo.put("links", authLinksJO);
        }

        WebUtils.printAsJSON(res, jo);
    }

}