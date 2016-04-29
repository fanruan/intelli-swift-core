package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取表格字符串字段的action
 */
public class BIGetTableStringFieldAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_table_string_field";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

//		String table = WebUtils.getHTTPRequestParameter(req, "table");
//		long userId = ServiceUtils.getCurrentUserID(req);
//		JSONObject jo = new JSONObject(table);
//		BITableConf td = new BITableConf();
//		td.parseJSON(jo);
//		BITable t = BIAbstractDataSource.createBIDataSource(td, userId).getBaseBITable();
//		JSONArray ja = new JSONArray();
//		if(t != null){
//			Iterator iter = t.getBIColumnIterator();
//			while(iter.hasNext()){
//				BIColumn col = (BIColumn) iter.next();
//				BIDataColumn dataColumn = new BIDataColumn(td.getDbName(), td.getSchema(), td.getTableName(), col.getFieldName(), BIBaseConstant.checkColumnClassTypeFromSQL(col.getType()), col.getColumnSize(), userId);
//				if(dataColumn.getType() == BIBaseConstant.COLUMN.STRING){
//					ja.put(dataColumn.createJSON());
//				}
//			}
//		}
//		WebUtils.printAsJSON(res, ja);
    }

}