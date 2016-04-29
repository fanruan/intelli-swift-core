package com.fr.bi.fe.service.noneedlogin;

import com.fr.bi.conf.aconfig.BIAbstractBusiTable;
import com.fr.bi.conf.util.BIConfUtils;
import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FEGetBriefTablesFromFEExcelAction extends AbstractBIConfigureAction {

	@Override
	public String getCMD() {
		return "bi_get_table_detail_infor_from_excel";
	}

	@Override
	protected void actionCMDPrivilegePassed(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		long userId = ServiceUtils.getCurrentUserID(req);
		String excelName = WebUtils.getHTTPRequestParameter(req, "fe_name");
		String md5tableName = WebUtils.getHTTPRequestParameter(req, "md5_table_name");
		BIAbstractBusiTable table_ = BIConfUtils.getTableByPackageNameAndConnectionNameAndTableName("Excel数据集", "__FR_BI_EXCEL__", null, md5tableName, userId);
		
		WebUtils.printAsJSON(res, table_.createJSON());
		
	}
        
}