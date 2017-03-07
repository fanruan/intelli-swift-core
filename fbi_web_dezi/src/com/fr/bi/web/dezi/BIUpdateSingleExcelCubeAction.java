package com.fr.bi.web.dezi;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.base.FRContext;
import com.fr.bi.cal.generate.CubeBuildHelper;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * Created by zcf on 2017/1/16.
 */
public class BIUpdateSingleExcelCubeAction extends AbstractBIDeziAction {

    private static final String CMD = "update_excel_table_cube_by_table_id";
    private static final long userId = -999;//貌似只能用管理员的权限进行单表更新，不然用户数据更新不了
    private static String DATA_PATH = FRContext.getCurrentEnv().getPath() + BIBaseConstant.EXCELDATA.EXCEL_DATA_PATH;
    private static final int SUCCESS = 0;
    private static final int FILE_USING_ERROR = 1;
    private static final int FILE_UPDATE_ERROR = 2;


    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
//        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "tableId");
        String newExcelFullName = WebUtils.getHTTPRequestParameter(req, "newExcelFullName");
        newExcelFullName = newExcelFullName.trim();
        newExcelFullName = newExcelFullName.toLowerCase();

        CubeTableSource tableSource = BICubeConfigureCenter.getDataSourceManager().getTableSource(new BIBusinessTable(new BITableID(tableId)));

        JSONObject oldExcelTableJSON = tableSource.createJSON();
        String oldExcelFullName = oldExcelTableJSON.optString("full_file_name");

        int state = updateExcel(userId, oldExcelFullName, newExcelFullName, tableSource);
        JSONObject jo = new JSONObject();
        jo.put("state", state);
        WebUtils.printAsJSON(res, jo);
    }

    private int updateExcel(long userId, String oldExcelFullName, String newExcelFullName, CubeTableSource tableSource) {
        //更新的逻辑是先删除原来的Excel文件，然后将新的Excel的文件重命名为原来的文件名。避免重新保存关联等其他乱七八糟的
        File oldFile = new File(DATA_PATH, oldExcelFullName);
        File newFile = new File(DATA_PATH, newExcelFullName);
        if (!oldFile.exists()) {
            if (!newFile.renameTo(new File(DATA_PATH, oldExcelFullName))) {
                return FILE_UPDATE_ERROR;
            }
            updateExcelTableDate(userId, tableSource);
            return SUCCESS;
        }
        if (!oldFile.delete()) {
            return FILE_USING_ERROR;
        }
        if (!newFile.renameTo(new File(DATA_PATH, oldExcelFullName))) {
            return FILE_UPDATE_ERROR;
        }
        updateExcelTableDate(userId, tableSource);
        return SUCCESS;
    }

    private void updateExcelTableDate(long userId, CubeTableSource tableSource) {
        try {
            CubeBuildHelper.getInstance().addSingleTableTask2Queue(userId, tableSource.getSourceID(), DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
        } catch (InterruptedException e) {
            BILoggerFactory.getLogger(this.getClass()).error("update excel single table error: " + e.getMessage(), e);
        }
    }
}
