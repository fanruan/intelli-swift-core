package com.fr.bi.web.conf.services.packs;

import com.fr.bi.cal.generate.CheckTask;
import com.fr.bi.conf.base.pack.data.BIBusinessPackageGetterService;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BISavePackageAuthorityAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "save_package_authority";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String packageName = WebUtils.getHTTPRequestParameter(req, "packageNames");
        String authorityJsonString = WebUtils.getHTTPRequestParameter(req, "authorityJsonString");
        long userId = ServiceUtils.getCurrentUserID(req);
        savePackageAuthority(packageName, new JSONArray(authorityJsonString), userId);
    }

    /**
     * 保存业务包权限
     *
     * @param packageName 业务包名字
     * @param authString  权限的字符串
     * @throws Exception
     */
    public void savePackageAuthority(String packageName, JSONArray authString, long userId) throws Exception {
        JSONArray packNamejo = new JSONArray(packageName);
        /**
         * TODO CONNERY 这个地方应该是传递了Packagename了
         */
        for (int i = 0; i < packNamejo.length(); i++) {
            BIBusinessPackageGetterService pack = BIConfigureManagerCenter.getPackageManager().getPackage(userId, new BIPackageID(packNamejo.getString(i)));
            if (pack != null) {
                //       pack.parseJSONWithAuthority(authString);
//                FRContext.getCurrentEnv().writeResource(BIConfigureDataManager.getBusiPackManager().getInstance(userId));
            }
        }

        BIConfigureManagerCenter.getCubeManager().addTask(new CheckTask(userId), userId);
    }
}