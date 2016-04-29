package com.fr.bi.web.conf.services.cubeconf;

/**
 * Created by neo on 15/6/30.
 */

import com.fr.base.FRContext;
import com.fr.bi.stable.conf.cubeconf.CubeConfManager;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;

public class BICubePathAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "cube_path_action";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
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
            setCubePath(cubePath);
        }
    }

    public void setCubePath(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return;
        }

        String path = CubeConfManager.getInstance().checkCubePath(fileName);

        if (!StringUtils.isEmpty(path)) {
            CubeConfManager.getInstance().setCubePath(path);
            try {
                FRContext.getCurrentEnv().writeResource(CubeConfManager.getInstance());
            } catch (Exception e) {
                FRContext.getLogger().log(Level.WARNING, e.getMessage(), e);
            }
        }

    }
}