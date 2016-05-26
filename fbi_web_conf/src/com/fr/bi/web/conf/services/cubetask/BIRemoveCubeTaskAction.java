/**
 *
 */
package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BIRemoveCubeTaskAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "remove_cube_task_by_id";
    }


    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        BICubeConfigureCenter.getCubeManager().removeTask(id, userId);
    }

}