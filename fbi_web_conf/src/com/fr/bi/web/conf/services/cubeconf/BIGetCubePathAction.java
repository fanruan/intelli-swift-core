package com.fr.bi.web.conf.services.cubeconf;

import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetCubePathAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_cube_path";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        String cubePath = getCubePath();
        JSONObject jo = new JSONObject();
        jo.put("cubePath", cubePath);
        WebUtils.printAsJSON(res, jo);
    }

    public String getCubePath() {

        return BIPathUtils.createBasePath();
    }
}