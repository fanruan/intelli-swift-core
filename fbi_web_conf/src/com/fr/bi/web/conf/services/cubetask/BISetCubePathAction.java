package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BISetCubePathAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "set_cube_path";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String fileName = WebUtils.getHTTPRequestParameter(req, "fileName");
        setCubePath(fileName, userId);
        WebUtils.printAsJSON(res, new JSONObject().put("status", "success"));
    }

    private void setCubePath(String fileName, long userId) {
        if (StringUtils.isEmpty(fileName)) {
            return;
        }
        String oPath = BIConfigureManagerCenter.getCubeConfManager().getCubePath();
        String path = BIConfigurePathUtils.checkCubePath(fileName);
        if (!StringUtils.isEmpty(path)) {
            BIConfigureManagerCenter.getCubeConfManager().saveCubePath(fileName);
            try {
                BIConfigureManagerCenter.getCubeConfManager().persistData(userId);
                if (!ComparatorUtils.equals(oPath, path)) {
                    try {
                        //释放
                        BICubeDiskPrimitiveDiscovery.getInstance().forceRelease();
                        BIFileUtils.moveFile(oPath, path);
                    } finally {
                        BICubeDiskPrimitiveDiscovery.getInstance().finishRelease();
                    }
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    }

}