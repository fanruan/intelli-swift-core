/**
 * 判断是否在执行生成cube操作
 * todo 从请求发送到cubeTask构建完毕的所有动作都应该包含在内
 */
package com.fr.bi.web.conf.services.cubetask;


import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BIGetCubeGenerateStatusAction extends AbstractBIConfigureAction {


    @Override
    public String getCMD() {
        return "get_cube_generate_status";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        long useId = ServiceUtils.getCurrentUserID(req);
        BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
        JSONObject jo = new JSONObject();
        jo.put("hasTask", cubeManager.hasTask(useId));
        WebUtils.printAsJSON(res, jo);
    }

}
