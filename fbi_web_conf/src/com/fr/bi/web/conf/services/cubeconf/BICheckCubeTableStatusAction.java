package com.fr.bi.web.conf.services.cubeconf;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.generate.EmptyCubeTask;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 14-9-13.
 * 通过tableId查看是否有对应的cube任务
 */
public class BICheckCubeTableStatusAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String tableJson = WebUtils.getHTTPRequestParameter(req, "table");
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = new JSONObject();
        String taskId;
        try {
            String baseTableSourceId = new JSONObject(tableJson).getJSONObject("baseTable").getString("md5");
            String businessTableId;
            boolean isETL = new JSONObject(tableJson).getBoolean("isETL");
            if (isETL) {
                businessTableId = new JSONObject(tableJson).getJSONObject("ETLTable").getString("id");
                CubeTableSource currentTableSource = getCurrentTableSource(userId, businessTableId);
                taskId = BIStringUtils.append(currentTableSource.getSourceID(), baseTableSourceId);
            } else {
                taskId = BIStringUtils.append(baseTableSourceId, baseTableSourceId);
            }
            CubeTask cubeTask = getCubeTask(taskId);
            BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
            jo.put("hasTask", cubeManager.hasTask(cubeTask, userId));
        } catch (Exception e) {
            jo.put("hasTask", false);
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        WebUtils.printAsJSON(res, jo);
    }

    private CubeTableSource getCurrentTableSource(long userId, String businessId) {
        for (BusinessTable table : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            if (table.getID().getIdentity().equals(businessId)) {
                return table.getTableSource();
            }
        }
        return null;
    }

    private CubeTask getCubeTask(final String taskId) {
        return new EmptyCubeTask() {
            @Override
            public String getTaskId() {
                return taskId;
            }
        };
    }

    @Override
    public String getCMD() {
        return "check_cube_table_status";
    }
}
