package com.fr.bi.web.conf.services.cubeconf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class created on 2016/11/18.
 *
 * @author Connery
 * @since 4.0
 */
public class BICheckCubeTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String tableJson = WebUtils.getHTTPRequestParameter(req, "table");
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = new JSONObject();
        CubeTableSource source = TableSourceFactory.createTableSource(new JSONObject(tableJson), userId);
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        BILoggerFactory.getLogger(BICheckCubeTableAction.class).debug("Check the Table Exists:" + source.getSourceID());
        boolean tableExisted = BITableKeyUtils.isTableExisted(source, cubeConfiguration);
        try {
            jo.put("exists", tableExisted);
        } catch (Exception e) {
            jo.put("exists", false);
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "check_table_exist";
    }
}
