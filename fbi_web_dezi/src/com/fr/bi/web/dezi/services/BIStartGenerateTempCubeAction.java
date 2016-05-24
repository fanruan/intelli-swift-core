package com.fr.bi.web.dezi.services;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.TempCubeManager;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.generate.index.TempIndexGenerator;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.cal.stable.engine.TempPathGenerator;
import com.fr.bi.cal.stable.loader.CubeTempModelReadingTableIndexLoader;
import com.fr.bi.common.inter.Release;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Root on 2016/5/10.
 */
public class BIStartGenerateTempCubeAction extends AbstractBIDeziAction {

    public static final String CMD = "start_generate_temp_cube";

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        BISession session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        String tableId = WebUtils.getHTTPRequestParameter(req, "table_id");
        if (session == null) {
            throw new NoneAccessablePrivilegeException();
        }
        long userId = session.getUserId();
        String fileName = TempPathGenerator.createTempPath();
        CubeTableSource source = BIModuleUtils.getSourceByID(new BITableID(tableId), new BIUser(userId));
        final TempCubeTask task = new TempCubeTask(source.getSourceID(), userId);
        final CubeTempModelReadingTableIndexLoader loader = (CubeTempModelReadingTableIndexLoader) CubeTempModelReadingTableIndexLoader.getInstance(task);

        loader.setReleaseObject(new Release() {
            @Override
            public void clear() {
                TempCubeManager.release(task);//释放cube
                TempPathGenerator.release(task);//释放临时路径
            }
        });

        TempIndexGenerator generator = new TempIndexGenerator(source, fileName, userId);
        TempCubeManager manager = TempCubeManager.getInstance(task);
        if (manager.addLoader(generator, new Release() {
            @Override
            public void clear() {
                loader.update();
            }
        })) {
            loader.addCubePath(fileName);//确认是生成的cube后才把路径放进去
        }
        session.setIsRealTime(true);
        session.setTempTableMd5(source.getSourceID());
        session.setProvider(manager.getLoader());
        JSONObject jo = new JSONObject();
        jo.put("percent", session.getProvider().getPercent());
        jo.put("detail", session.getProvider().getDetail());
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return CMD;
    }
}
