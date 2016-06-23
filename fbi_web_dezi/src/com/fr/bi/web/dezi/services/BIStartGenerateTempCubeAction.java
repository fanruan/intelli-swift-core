package com.fr.bi.web.dezi.services;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerTableSource;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.TempCubeManager;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.cal.stable.engine.TempPathGenerator;
import com.fr.bi.cal.stable.loader.CubeTempModelReadingTableIndexLoader;
import com.fr.bi.common.inter.Release;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URI;

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
        final String fileName = TempPathGenerator.createTempPath();
        CubeTableSource source = BIModuleUtils.getSourceByID(new BITableID(tableId), new BIUser(userId));
        final String cubePath = BIBaseConstant.CACHE.getCacheDirectory() + BIPathUtils.tablePath(source.fetchObjectCore().getID().getIdentityValue()) + File.separator + fileName;
        final TempCubeTask task = new TempCubeTask(source.getSourceID(), tableId, userId);
        final CubeTempModelReadingTableIndexLoader loader = (CubeTempModelReadingTableIndexLoader) CubeTempModelReadingTableIndexLoader.getInstance(task);

        loader.setReleaseObject(new Release() {
            @Override
            public void clear() {
                TempCubeManager.release(task);//释放cube
                TempPathGenerator.release(task);//释放临时路径
            }
        });

        CubeBuildStuffManagerTableSource cubeBuildStuff = new CubeBuildStuffManagerTableSource(source, new ICubeConfiguration() {
            @Override
            public URI getRootURI() {
                return URI.create(cubePath);
            }
        }, userId);
        TempCubeManager manager = TempCubeManager.getInstance(task);
        if (manager.addLoader(cubeBuildStuff, new Release() {
            @Override
            public void clear() {
                loader.update();
            }
        })) {
            loader.addCubePath(fileName);//确认是生成的cube后才把路径放进去
        }

        session.setIsRealTime(true);
        session.setTempTableMd5(source.getSourceID());
        session.setTempTableId(tableId);
        JSONObject jo = new JSONObject();
        jo.put("percent", loader.hasStoredIndexes() ? 100 : 0);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return CMD;
    }
}
