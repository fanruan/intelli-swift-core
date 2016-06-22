package com.fr.bi.web.dezi.services;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerTableSource;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.TempCubeManager;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.cal.stable.engine.TempPathGenerator;
import com.fr.bi.cal.stable.loader.CubeTempModelReadingTableIndexLoader;
import com.fr.bi.common.factory.BIFactoryHelper;
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
        final TempCubeTask task = new TempCubeTask(source.getSourceID(), userId);
        final CubeTempModelReadingTableIndexLoader loader = (CubeTempModelReadingTableIndexLoader) CubeTempModelReadingTableIndexLoader.getInstance(task);
        BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();

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
        BuildCubeTask buildCubeTask = new BuildCubeTask(new BIUser(userId), cubeBuildStuff);
        cubeManager.addTask(buildCubeTask, userId);
        TempCubeManager manager = TempCubeManager.getInstance(task);
        if (manager.addLoader(cubeBuildStuff, new Release() {
            @Override
            public void clear() {
                loader.update();
            }
        })) {
            loader.addCubePath(fileName);//确认是生成的cube后才把路径放进去
            ICubeConfiguration cubeConfiguration = cubeBuildStuff.getCubeConfiguration();
            BICubeResourceRetrieval resourceRetrieval = new BICubeResourceRetrieval(cubeConfiguration);
            loader.setCube(new BICube(resourceRetrieval, BIFactoryHelper.getObject(ICubeResourceDiscovery.class)));
        }

        session.setIsRealTime(true);
        session.setTempTableMd5(source.getSourceID());
        session.setTempTableId(tableId);
        JSONObject jo = new JSONObject();
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return CMD;
    }
}
