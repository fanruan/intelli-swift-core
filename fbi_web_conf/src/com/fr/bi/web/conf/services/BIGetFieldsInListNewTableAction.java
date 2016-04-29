package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 14-8-19.
 */
public class BIGetFieldsInListNewTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
//        String tableString = WebUtils.getHTTPRequestParameter(req, "table");
//        JSONArray tableJa = new JSONArray( tableString );
//        JSONArray ja = new JSONArray();
//        long userId = ServiceUtils.getCurrentUserID(req);
//        for(int i=0;i<tableJa.length();i++){
//            JSONObject tableJo = tableJa.getJSONObject(i);
//            String connectionName = tableJo.getString("connection_name");
//            BIAbstractBusiTable table;
//
//            table = BIAbstractBusiTable.createByJSONObject(tableJo, connectionName, userId);
//
//            JSONObject jo = table.asJsonWithFieldDetailInfor4BIConfigure(null, connectionName, true, userId);
//
//            BIAbstractBusiTable packageTable = BIConfUtils.getTableByPackageNameAndConnectionNameAndTableName(tableJo, userId);
//            if( packageTable != null ) {
//                jo.put( "disabledField", packageTable.createDisabledFieldsJa() );
//            }
//            ja.put(jo);
//        }
//
//
//        WebUtils.printAsJSON(res, ja);
    }

    @Override
    public String getCMD() {
        return "get_fields_list_new_table";
    }
}