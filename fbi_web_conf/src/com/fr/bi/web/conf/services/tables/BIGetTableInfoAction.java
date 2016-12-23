package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.conf.manager.excelview.source.ExcelViewSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.services.packs.BIGetTablesOfOnePackageAction;
import com.fr.bi.web.conf.services.session.BIConfSession;
import com.fr.bi.web.conf.services.session.BIConfSessionUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 获取业务包表详细信息
 * 点击进入表，其他用户不可同时操作当前表
 * Created by Young's on 2016/12/15.
 */
public class BIGetTableInfoAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_table_info";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String sessionId = WebUtils.getHTTPRequestParameter(req, "sessionID");
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BIConfSession session = (BIConfSession) SessionDealWith.getSessionIDInfor(sessionId);
        boolean isEditing = session.setEdit(true, tableId);
        if (!isEditing) {
            WebUtils.printAsJSON(res, new JSONObject().put("lockedBy", BIConfSessionUtils.getCurrentEditingUserByTableId(userId, tableId)));
            return;
        }

        //fields
        CubeTableSource source = BusinessTableHelper.getTableDataSource(new BITableID(tableId));
        JSONObject jo = source.createJSON();
        jo.put("fields", getFields(BusinessTableHelper.getBusinessTable(new BITableID(tableId))));

        //excel view
        ExcelViewSource excelView = BIConfigureManagerCenter.getExcelViewManager().getExcelViewManager(userId).getExcelViewByTableId(tableId);
        if (excelView != null) {
            jo.put("excelView", excelView.createJSON());
        }

        //update setting


        WebUtils.printAsJSON(res, jo);

    }

    private JSONArray getFields(BusinessTable businessTable) {
        JSONArray ja = new JSONArray();
        List<JSONObject> stringList = new ArrayList<JSONObject>();
        List<JSONObject> numberList = new ArrayList<JSONObject>();
        List<JSONObject> dateList = new ArrayList<JSONObject>();
        Iterator<BusinessField> it = businessTable.getFields().iterator();
        while (it.hasNext()) {
            try {
                stringList.add(it.next().createJSON());
            } catch (Exception e) {
                BILoggerFactory.getLogger(BIGetTablesOfOnePackageAction.class).error(e.getMessage(), e);
            }
        }
        ja.put(stringList).put(numberList).put(dateList);
        return ja;
    }
}
