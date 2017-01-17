package com.fr.bi.web.base.services;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Young's on 2017/1/17.
 */
public class BIGetDataConfigAuthoritiesAction extends AbstractBIBaseAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONArray ja = new JSONArray();
        Set<BIDataConfigAuthority> authoritySet = BIConfigureManagerCenter.getDataAuthorityManager().getAllDataConfigAuthorities();
        Iterator<BIDataConfigAuthority> authorityIterator = authoritySet.iterator();
        while (authorityIterator.hasNext()) {
            BIDataConfigAuthority authority = authorityIterator.next();
            if (authority != null) {
                ja.put(authority.createJSON());
            }
        }

        WebUtils.printAsJSON(res, ja);

    }

    @Override
    public String getCMD() {
        return "get_data_config_authorities";
    }
}
