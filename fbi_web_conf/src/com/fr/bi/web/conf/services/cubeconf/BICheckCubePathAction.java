package com.fr.bi.web.conf.services.cubeconf;

import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BICheckCubePathAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "check_cube_path";
    }

    //has test
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        String fileName = WebUtils.getHTTPRequestParameter(req, "fileName");

        String returnCubePath = BIConfigurePathUtils.checkCubePath(fileName);
        JSONObject jo = new JSONObject();
        jo.put("cubePath", returnCubePath);
        WebUtils.printAsJSON(res, jo);
    }
}