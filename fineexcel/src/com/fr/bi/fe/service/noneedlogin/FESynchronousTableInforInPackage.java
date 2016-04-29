package com.fr.bi.fe.service.noneedlogin;

import com.fr.base.FRContext;
import com.fr.bi.cal.generate.engine.SingleTableTask;
import com.fr.bi.conf.aconfig.BIAbstractBusiTable;
import com.fr.bi.conf.aconfig.BIBusiPack;
import com.fr.bi.conf.aconfig.BIExcelBusiTable;
import com.fr.bi.conf.aconfig.BIIDBusiTable;
import com.fr.bi.conf.aconfig.BIInterfaceAdapter;
import com.fr.bi.conf.util.BIConfUtils;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.bi.stable.aconfig.*;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BITableKey;
import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by young on 2014/12/16.
 */
public class FESynchronousTableInforInPackage extends AbstractBIConfigureAction {
    @Override
	public String getCMD() {
        return "fe_synchronous_table_infor_in_package";
    }

    /**
     *    行为
     * @param req    数据
     * @param res      返回值
     * @throws Exception
     */
    @Override
	protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String translatedJsonString = WebUtils.getHTTPRequestParameter(req, "new_table");
        boolean isAdd = Boolean.valueOf(WebUtils.getHTTPRequestParameter(req, "is_add"));

        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject tableJson = new JSONObject(translatedJsonString);

        BIAbstractBusiTable table = BIConfUtils.getTableByJsonObject(tableJson, userId);
        if (table == null){
            BIBusiPack pack = BIInterfaceAdapter.getBIBusiPackAdapter().getFinalVersionOfPackByName(tableJson.getString("package_name"), userId);
            JSONObject jo = new JSONObject();
            jo.put("connection_name", tableJson.getString("connection_name"));
            jo.put("table", tableJson);
            table = pack.addTableByJSONArrayWithoutGenerateCube(jo, isAdd, userId);
        }

            BIIDBusiTable biIDTable = (BIIDBusiTable)table;
//    	BIExcelBusiTable table_excel = (BIExcelBusiTable) biIDTable.getBiTable();
        BIExcelBusiTable table_excel = (BIExcelBusiTable)BIInterfaceAdapter.getDataSourceAdapter().getMD5TableByID(((BIIDBusiTable) table).getId(), userId );
    	table_excel.setFieldTypes(tableJson.getJSONArray("fields"), tableJson.getString("connection_name"));

        tableJson.put("md5_table_name", table.getMd5TableName());
        BIInterfaceAdapter.getBIConnectionAdapter().saveTableTranslaterByJson(tableJson, table, userId);
        if (table != null) {
            //TODO 代码质量 重复计算了
            table.synchronousFieldsUsability(tableJson.getJSONArray("fields"), tableJson.getString("connection_name"));
        }
        if (ComparatorUtils.equals(BIBaseConstant.CUBEINDEX.EXCELCONNECTION, table.getConnectionName())){
            System.out.print("////////////");
            BIInterfaceAdapter.getCubeAdapter().addTask(new SingleTableTask( new BITableKey(table.getConnectionName(), null, table.getMd5TableName(), table.getName(), null), userId), userId);
        }

        FRContext.getCurrentEnv().writeResource(BIInterfaceAdapter.getBIConnectionAdapter().getBIConnectionManager(userId));
        FRContext.getCurrentEnv().writeResource(BIInterfaceAdapter.getBIBusiPackAdapter().getBusiPackageManager(userId));
        FRContext.getCurrentEnv().writeResource(BIInterfaceAdapter.getDataSourceAdapter().getBIDataSourceManager(userId));
        if(isAdd){
            FineExcelUserService fineExcelUserService = new FineExcelUserService();
            User user = UserControl.getInstance().getUser(userId);
            if(user != null){
                fineExcelUserService.updateImportedExcel(user.getUsername());
            }
        }
        WebUtils.printAsJSON(res, tableJson);
    }
}