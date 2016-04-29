package com.fr.bi.web.conf.services.datalink;


import com.fr.bi.web.conf.services.datalink.data.BIConnectionTestByNameUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Sheldon
 * Date: 13-11-1
 * Time: 下午3:06
 * To change this template use File | Settings | File Templates.
 */
public class BITestDataLinkByNameAction extends BITestDataLinkAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String name = WebUtils.getHTTPRequestParameter(req, "name");
        BIConnectionTestByNameUtils utils = new BIConnectionTestByNameUtils();
        JSONObject jo = utils.processConnectionTest(name);
        WebUtils.printAsJSON(res, jo);

    }

    @Override
    public String getCMD() {
        return "test_data_link_name";
    }
}