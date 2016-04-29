package com.fr.bi.fe.service.noneedlogin;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.base.FRContext;
import com.fr.bi.fe.fs.data.BIHandleExcel;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.general.DateUtils;
import com.fr.json.JSONObject;
import com.fr.stable.html.Tag;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

public class FEGetFullImportedExcelAction extends ActionNoSessionCMD {

	@Override
	public String getCMD() {
		return "fe_get_full_imported_excel";
	}

	@Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res ) throws Exception {
		String tableName = WebUtils.getHTTPRequestParameter(req, "full_file_name");
        
		File excelFile = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.EXCELDATA.EXCEL_DATA_PATH, tableName);

        long start = System.currentTimeMillis();
        System.out.println("--开始获取Excel中所有表格--生成相应的标签--");
        BIHandleExcel biHandleExcel = new BIHandleExcel();
		Tag tag = biHandleExcel.getFullExcel4Choose(excelFile, req, null);

        System.out.println("--生成标签完成--耗时--" + DateUtils.timeCostFrom(start) );
        JSONObject jo = new JSONObject();
        jo.put("tag", tag);
        WebUtils.printAsJSON(res, jo);
	}
	

}