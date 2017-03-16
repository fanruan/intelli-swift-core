package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.BIPackageName;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 修改业务包名称
 * Created by Young's on 2017/2/4.
 */
public class BIUpdatePackageNameAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String packageId = WebUtils.getHTTPRequestParameter(req, "id");
        String packageName = WebUtils.getHTTPRequestParameter(req, "name");
        try {
            BICubeConfigureCenter.getPackageManager().renamePackage(userId, new BIPackageID(packageId), new BIPackageName(packageName));
            BICubeConfigureCenter.getPackageManager().persistData(userId);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public String getCMD() {
        return "update_package_name";
    }
}
