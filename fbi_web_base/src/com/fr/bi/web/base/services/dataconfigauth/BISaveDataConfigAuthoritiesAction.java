package com.fr.bi.web.base.services.dataconfigauth;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * 保存数据配置权限
 * Created by Young's on 2017/1/17.
 */
public class BISaveDataConfigAuthoritiesAction extends AbstractBIBaseAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String authoritiesStr = WebUtils.getHTTPRequestParameter(req, "authorities");
        Set<BIDataConfigAuthority> authorities = new HashSet<BIDataConfigAuthority>();
        if (authoritiesStr != null) {
            try {
                JSONArray authoritiesJA = new JSONArray(authoritiesStr);
                for (int i = 0; i < authoritiesJA.length(); i++) {
                    JSONObject authorityJO = authoritiesJA.getJSONObject(i);
                    BIDataConfigAuthority authority = new BIDataConfigAuthority();
                    authority.parseJSON(authorityJO);
                    authorities.add(authority);
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage());
            }
        }
        BIConfigureManagerCenter.getDataConfigAuthorityManager().saveDataConfigAuthorities(authorities);
        synchronized (this) {
            BIConfigureManagerCenter.getDataConfigAuthorityManager().persistData(userId);
        }
    }

    @Override
    public String getCMD() {
        return "save_data_config_authorities";
    }
}
