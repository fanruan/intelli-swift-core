package com.fr.bi.web.conf.services.cubetask;

import com.fr.base.FRContext;
import com.fr.bi.stable.conf.cubeconf.CubeConfManager;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
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
        String fileName = WebUtils.getHTTPRequestParameter(req, "fileName");
        setCubePath(fileName);

        WebUtils.printAsJSON(res, new JSONObject().put("status", "success"));
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
                FRContext.getLogger().error(e.getMessage(), e);
            }
        }

    }

}