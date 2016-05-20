package com.fr.bi.web.conf.services.cubeconf;

/**
 * Created by neo on 15/6/30.
 */

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.conf.cubeconf.CubeConfManager;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BICubePathAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "cube_path_action";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String actionType = WebUtils.getHTTPRequestParameter(req, "actionType");
        String cubePath = WebUtils.getHTTPRequestParameter(req, "cubePath");
        JSONObject jo = new JSONObject();
        if (ComparatorUtils.equals(actionType, "getCubePath")) {
            cubePath = BIPathUtils.createBasePath();
            jo.put("cubePath", cubePath);
            WebUtils.printAsJSON(res, jo);
        } else if (ComparatorUtils.equals(actionType, "checkCubePath")) {
            cubePath = CubeConfManager.getInstance().checkCubePath(cubePath);
            jo.put("pathCheckResult", cubePath);
            WebUtils.printAsJSON(res, jo);
        } else if (ComparatorUtils.equals(actionType, "setCubePath")) {
            setCubePath(cubePath, userId);
        }
    }

    private void setCubePath(String fileName, long userId) {
        if (StringUtils.isEmpty(fileName)) {
            return;
        }

        String path = BIConfigurePathUtils.checkCubePath(fileName);

        if (!StringUtils.isEmpty(path)) {
            BIConfigureManagerCenter.getCubeConfManager().saveCubePath(path);
            try {
                BIConfigureManagerCenter.getCubeConfManager().persistData(userId);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }
}