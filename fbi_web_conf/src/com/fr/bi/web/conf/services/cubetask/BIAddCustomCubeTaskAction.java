package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.cal.generate.CustomTaskPool;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lucifer on 2017-3-22.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class BIAddCustomCubeTaskAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "add_custom_cube_task";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String baseTableSourceId = WebUtils.getHTTPRequestParameter(req, "baseTableSourceId");
        boolean isOk = CustomTaskPool.addCustomGenerateTask(userId,baseTableSourceId);
        JSONObject jsonObject = new JSONObject().put("result", isOk);
        WebUtils.printAsJSON(res, jsonObject);
    }
}
