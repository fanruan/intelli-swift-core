package com.fr.bi.fe.service.noneedlogin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.base.Utils;
import com.fr.bi.conf.aconfig.BIAbstractBusiTable;
import com.fr.bi.conf.aconfig.BIBusiPack;
import com.fr.bi.conf.aconfig.BIBusiPackManager;
import com.fr.bi.conf.aconfig.BIInterfaceAdapter;
import com.fr.bi.conf.aconfig.singleuser.SingleUserBIBusiPackManager;
import com.fr.bi.conf.util.BIConfUtils;
import com.fr.bi.excel.BIGetImportExcelDataValue;
import com.fr.bi.excel.FEGetImportExcelDataValue;
import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

/**
 * Created by young on 14-10-25.
 */
public class FEGetSelectedExcelJSONAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	long userId = ServiceUtils.getCurrentUserID(req);
        String fileName = WebUtils.getHTTPRequestParameter(req, "full_file_name");
        String ids = WebUtils.getHTTPRequestParameter(req,"ids");
        boolean isAll = Boolean.valueOf(WebUtils.getHTTPRequestParameter(req,"is_all"));
        JSONArray jaId = new JSONArray(ids);
        if( StringUtils.isNotBlank( fileName ) ) {
            BIGetImportExcelDataValue excelDataValue = new BIGetImportExcelDataValue( fileName );
            JSONArray jaexcel;
            if(isAll){
            	jaexcel = excelDataValue.getJSONFromExcel();
            }else{
            	jaexcel = excelDataValue.getJSONFromExcel(jaId);
            }
            String [] names = excelDataValue.createSelectedCellExcel(fileName, jaexcel);
            
            String fullFileName = names[0];
            String tabName = names[2];
        	String tableName = names[1];
        	//避免表名重复
        	BIBusiPackManager bpm = new BIBusiPackManager();
        	BIBusiPack[] bbp = bpm.getAllAnalysisUseBIPack(userId);
        	if(bbp.length > 0){
        		BIAbstractBusiTable [] tables = bbp[0].getBusiTablesByConnectionName("__FR_BI_EXCEL__");
        		List<String> tableNames = new ArrayList<String>();
        		if(tables != null){
        			for(int i = 0; i < tables.length; i++){
        				tableNames.add(tables[i].getName());
        			}
        			int index = 1;
        			while(tableNames.contains(tableName)){
        				if(index == 1){
        					tableName = tableName + index;        				
        				}else{
        					tableName = tableName.substring(0, tableName.length()-1) + index;        				        				
        				}
        				index ++;
        			}       		    			
        		}
        	}
        	
            JSONObject jo = new JSONObject();
        	jo.put("connection_name", "__FR_BI_EXCEL__");
        	jo.put("excel_field", new JSONArray());
        	jo.put("modified_relation_array", new JSONArray());
        	jo.put("file_name", fileName.substring(18));
        	jo.put("full_file_name", fullFileName);
        	jo.put("package_name","Excel数据集");
        	jo.put("tab_name", tabName);
        	JSONArray jaFields = new JSONArray();
        	
        	JSONArray ja_ = new JSONArray();
        	ja_ = jaexcel.getJSONArray(0);
        	for(int col = 0; col < ja_.length(); col ++){
        	
        		JSONObject joField = new JSONObject();
        		joField.put("connection_name","__FR_BI_EXCEL__");
        		joField.put("join_analyse",true);
        		joField.put("md5_table_name",fileName.substring(18));
        		joField.put("py","xx");
        		joField.put("table_name",tableName);
        		
        		int type = 0;
        		for(int row = 1; row < jaexcel.length(); row++){
        			if(jaexcel.getJSONArray(row).length() != 0){
        				try {
        					if(!((jaexcel.getJSONArray(row).get(col).toString()).matches("^[+-]?([1-9][0-9]*|0)(\\.[0-9]+)?%?$") ||
									(jaexcel.getJSONArray(row).get(col).toString()).equals(""))) {
        						type = 1;
        						break;
        					}							
						} catch (Exception e) {
							type = 1;
							break;
						}
        			}
        		}
        		
        		joField.put("field_type",type);
        		if(ja_.getString(col).equals("")){
        			joField.put("field_name",Utils.replaceAllString(ja_.getString(col),new String [] {"*","|",":","\"","<",">","?","/"}, new String [] {"","","","","","","",""}));
        		}else{
        			joField.put("field_name",Utils.replaceAllString(ja_.getString(col),new String [] {"*","|",":","\"","<",">","?","/"}, new String [] {"","","","","","","",""}));
        		}
        		jaFields.put(joField);
        	}
        	jo.put("fields", jaFields);
        	jo.put("table_name", tableName);
            
            BIAbstractBusiTable table = BIConfUtils.getTableByJsonObject(jo, -999);
            if (table == null){
//                BIBusiPack pack = BIInterfaceAdapter.getBIBusiPackAdapter().getFinalVersionOfPackByName(jo.getString("package_name"), -999);
                BIBusiPack pack = BIInterfaceAdapter.getBIBusiPackAdapter().getLastestedPackage(userId)[0];
                JSONObject jo_ = new JSONObject();
                jo_.put("connection_name", jo.getString("connection_name"));
                jo_.put("table", jo);
                table =  pack.addTableByJSONArrayWithoutGenerateCube(jo_, true, -999);
                pack.removeTableByTableName(table.getConnectionName(), table.getMd5TableName(), table.getSchema());
            }

            jo.put("md5_table_name",table.getMd5TableName());
            WebUtils.printAsJSON(res, jo);
        }
    }

    @Override
    public String getCMD() {
        return "get_selected_excel_JSON";
    }
}