package com.fr.bi.fe.service.noneedlogin;

import com.fr.bi.conf.aconfig.BIBusiPack;
import com.fr.bi.conf.aconfig.BIBusiPackManagerInterface;
import com.fr.bi.conf.aconfig.BIInterfaceAdapter;
import com.fr.bi.conf.aconfig.singleuser.SingleUserBIBusiPackManager;
import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FEGetBriefTablesOfOnePackageAction extends AbstractBIConfigureAction {

	@Override
	public String getCMD() {
		return "fe_get_brief_tables";
	}

	@Override
	protected void actionCMDPrivilegePassed(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		long userId = ServiceUtils.getCurrentUserID(req);
		String packageName = "Excel数据集";
		BIBusiPackManagerInterface mgr = BIInterfaceAdapter.getBIBusiPackAdapter();
		SingleUserBIBusiPackManager singleUserBIBusiPackManager = (SingleUserBIBusiPackManager) mgr.getBusiPackageManager(userId);
		if( !singleUserBIBusiPackManager.hasAvailableAnalysisPacks() ){
			singleUserBIBusiPackManager.addAnEmptyPack("Excel数据集", req.getLocale());
			singleUserBIBusiPackManager.getLastestedPackage()[0].setName("Excel数据集");
		}
		BIBusiPack pack = mgr.getFinalVersionOfPackByName(packageName, userId);
		
		WebUtils.printAsJSON(res, pack.asJsonWithTableNames( userId, "" ));
	}
        
}