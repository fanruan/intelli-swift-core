package com.fr.bi.web.conf.services.datalink;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.base.datasource.BIConnection;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class BIGetDataLinkAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_data_link";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = BIConnectionManager.getBIConnectionManager().createJSON();
        JSONObject links = new JSONObject();
        if (ComparatorUtils.equals(userId, UserControl.getInstance().getSuperManagerID())) {
            links = jo;
        } else {
            Iterator<String> names = jo.keys();
            Map<String, Object> map = new HashMap<String, Object>();
            Set<BIDataConfigAuthority> dataConfigAuthoritySet = BIConfigureManagerCenter.getDataConfigAuthorityManager().getDataConfigAuthoritiesByUserId(userId);
            for (BIDataConfigAuthority authority : dataConfigAuthoritySet) {
                map.put(authority.getId(), authority.createJSON());
            }
            while (names.hasNext()) {
                String name = names.next();
                JSONObject connJO = jo.getJSONObject(name);
                BIConnection connection = BIConnectionManager.getInstance().getBIConnection(connJO.getString("name"));
                if (map.get(DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION.NODE + connection.getInitTime()) != null ||
                        ComparatorUtils.equals(userId, connection.getCreateBy())) {
                    links.put(name, connJO);
                }
            }
        }

        WebUtils.printAsJSON(res, JSONObject.create().put("links", links));
    }
}