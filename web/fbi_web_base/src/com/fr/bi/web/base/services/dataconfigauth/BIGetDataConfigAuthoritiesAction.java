package com.fr.bi.web.base.services.dataconfigauth;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.general.ComparatorUtils;
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
            //一些固定的父节点 全选与半选不考虑
            String id = authority.getId();
            if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION.NODE) ||
                    ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER.NODE) ||
                    ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER.DATA_CONNECTION)) {
                authority.setDesign(DBConstant.DATA_CONFIG_DESIGN.NO);
                authority.setView(DBConstant.DATA_CONFIG_DESIGN.NO);
            }
            ja.put(authority.createJSON());
        }
        WebUtils.printAsJSON(res, ja);
    }

    @Override
    public String getCMD() {
        return "get_data_config_authorities";
    }
}
