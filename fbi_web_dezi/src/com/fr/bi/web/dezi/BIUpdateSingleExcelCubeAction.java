package com.fr.bi.web.dezi;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.cal.generate.CubeBuildManager;
import com.fr.bi.conf.data.source.ExcelTableSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zcf on 2017/1/16.
 */
public class BIUpdateSingleExcelCubeAction extends AbstractBIDeziAction{

    public static final String CMD = "update_excel_table_cube_by_table_id";

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId= WebUtils.getHTTPRequestParameter(req, "tableId");
        String newExcelFullName= WebUtils.getHTTPRequestParameter(req, "newExcelFullName");

        CubeTableSource taleSource=BICubeConfigureCenter.getDataSourceManager().getTableSource(new BIBusinessTable(new BITableID(tableId)));

        //这样写不对
//        if(taleSource instanceof ExcelTableSource){
//            ((ExcelTableSource) taleSource).setFullFileName(newExcelFullName);
//            new CubeBuildManager().addSingleTableTask(userId, taleSource.getSourceID(), DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
//        }
    }
}
