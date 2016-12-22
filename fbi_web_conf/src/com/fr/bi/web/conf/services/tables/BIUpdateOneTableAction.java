package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.finebi.cube.conf.utils.BIConfUtils;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.services.session.BIConfSessionUtils;
import com.fr.bi.web.conf.utils.BIWriteConfigResourcesUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * 更新一张表信息
 * Created by Young's on 2016/12/21.
 */
public class BIUpdateOneTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String sessionId = WebUtils.getHTTPRequestParameter(req, "sessionID");
        String translationsStr = WebUtils.getHTTPRequestParameter(req, "translations");
        String tableStr = WebUtils.getHTTPRequestParameter(req, "table");
        String updateSettingStr = WebUtils.getHTTPRequestParameter(req, "updateSetting");
        String excelViewStr = WebUtils.getHTTPRequestParameter(req, "excelView");

        JSONObject tableJO = new JSONObject(tableStr);
        BusinessTable table = updateTable(userId, tableJO);
        JSONObject translationsJO = new JSONObject(translationsStr);
        updateTranslation(userId, translationsJO, table);

        //lock
        BIConfSessionUtils.getSession(sessionId).releaseTableLock(table.getID().getIdentityValue());

        BIWriteConfigResourcesUtils.writeResourceAsync(userId);
    }

    private BusinessTable updateTable(long userId, JSONObject tableJO) throws Exception {
        String tableId = tableJO.getString("id");
        BusinessTable table = BusinessTableHelper.getBusinessTable(new BITableID(tableId));
        if (tableJO.has("fields")) {
            List<BusinessField> fields = BIConfUtils.parseField(tableJO.getJSONArray("fields"), table);
            table.setFields(fields);
        }
        CubeTableSource tableSource = TableSourceFactory.createTableSource(tableJO, userId);
        BICubeConfigureCenter.getDataSourceManager().addTableSource(table, tableSource);
        table.setSource(tableSource);
        return table;
    }

    private void updateTranslation(long userId, JSONObject translations, BusinessTable table) throws Exception {
        String tableId = table.getID().getIdentityValue();
        List<BusinessField> fields = table.getFields();
        BICubeConfigureCenter.getAliasManager().removeAliasName(tableId, userId);
        for (BusinessField field : fields) {
            BICubeConfigureCenter.getAliasManager().removeAliasName(field.getFieldID().getIdentityValue(), userId);
        }
        Iterator<String> keys = translations.keys();
        while (keys.hasNext()) {
            String id = keys.next();
            BICubeConfigureCenter.getAliasManager().setAliasName(id, translations.getString(id), userId);
        }
    }

    @Override
    public String getCMD() {
        return "update_one_table";
    }
}
