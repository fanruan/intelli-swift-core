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
 * 检测是否已经生成cubBIPackageTableSourceConfigManagere
 */
public class BICheckGenerateCubeAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String tableJson = WebUtils.getHTTPRequestParameter(req, "table");
        long userId = ServiceUtils.getCurrentUserID(req);
        CubeTableSource source = TableSourceFactory.createTableSource(new JSONObject(tableJson), userId);
        JSONObject jo = new JSONObject();
        BILoggerFactory.getLogger(BICheckGenerateCubeAction.class).debug("Check the Table:" + source.getSourceID());
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
        return "check_generate_cube";
    }
}
