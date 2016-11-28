package com.fr.bi.web.conf.services.cubeconf;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 14-9-13.
 * todo 通过生成队列状态，来确定当前生成状态。这样的颗粒度太粗。添加任务ID，通过ID来判断状态。
 * 查看当前cube执行情况BIGetCubeGenerateStatusAction ，查看某张表是否已经生成用BICheckCubeTableAction，
 * todo 可以考虑用来通过tableId查看是否有对应的cube任务
 */
public class BICheckCubeTableStatusAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String tableJson = WebUtils.getHTTPRequestParameter(req, "table");
        long userId = ServiceUtils.getCurrentUserID(req);
        CubeTableSource source = TableSourceFactory.createTableSource(new JSONObject(tableJson), userId);
        JSONObject jo = new JSONObject();
        BILoggerFactory.getLogger(BICheckCubeTableStatusAction.class).debug("Check the Build Status:" + source.getSourceID());
        try {
            BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
            jo.put("isGenerated", !cubeManager.hasTask(userId));
        } catch (Exception e) {
            jo.put("isGenerated", true);
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "check_cube_table_status";
    }
}
