package com.fr.bi.web.base.services.dataconfigauth;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 所有权限信息
 * Created by Young's on 2017/2/14.
 */
public class BIGetDataConfigAuthoritiesAction extends AbstractBIBaseAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

        Set<BIDataConfigAuthority> authoritySet = BIConfigureManagerCenter.getDataConfigAuthorityManager().getAllDataConfigAuthorities();
        JSONArray ja = new JSONArray();
        for (BIDataConfigAuthority authority : authoritySet) {
            ja.put(authority.createJSON());
        }
        WebUtils.printAsJSON(res, ja);
    }

    @Override
    public String getCMD() {
        return "get_data_config_authorities";
    }
}
