/**
 *
 */
package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;


public class BIGetCubeTaskListAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_cube_task_list";
    }


    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = new JSONObject();
        CubeTask generated =  BIConfigureManagerCenter.getCubeManager().getGeneratedTask(userId);
        if (generated != null) {
            jo.put("generated", generated.createJSON());
        }
        CubeTask generating =  BIConfigureManagerCenter.getCubeManager().getGeneratingTask(userId);
        if (generating != null) {
            jo.put("generating", generating.createJSON());
        }
        Iterator<CubeTask> iter =  BIConfigureManagerCenter.getCubeManager().getWaitingTaskIterator(userId);
        JSONArray ja = new JSONArray();
        jo.put("waiting", ja);
        while (iter.hasNext()) {
            ja.put(iter.next().createJSON());
        }
        WebUtils.printAsJSON(res, new JSONObject().put("cubeTaskList", jo));
    }

}