/**
 *
 */
package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.generate.SingleTableTask;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BIAddSingleTableUpdateTaskAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "add_single_table_task";
    }


    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String tableString = WebUtils.getHTTPRequestParameter(req, "table");
        boolean isAdd = WebUtils.getHTTPRequestBoolParameter(req, "add");
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject out = new JSONObject();
        if (tableString != null) {
            BusinessTable table = new BIBusinessTable("", "");
            table.parseJSON(new JSONObject(tableString));
            BusinessTable biTable = table;

            out.put("status", "success");

            if (isAdd) {
                boolean added = BICubeConfigureCenter.getCubeManager().addTask(new SingleTableTask(biTable, userId), userId);
                out.put("hasTask", added);
                WebUtils.printAsJSON(res, out);

            } else {
                boolean hasTask = BICubeConfigureCenter.getCubeManager().hasTask(new SingleTableTask(biTable, userId), userId);
                out.put("hasTask", hasTask);
                WebUtils.printAsJSON(res, out);
            }
        }
        out.put("status", "failed").put("message", "table is not defined");
        WebUtils.printAsJSON(res, out);
    }

}
