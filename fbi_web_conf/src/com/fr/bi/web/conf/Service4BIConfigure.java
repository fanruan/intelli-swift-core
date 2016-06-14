package com.fr.bi.web.conf;


import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.conf.services.*;
import com.fr.bi.web.conf.services.cubeconf.*;
import com.fr.bi.web.conf.services.cubetask.*;
import com.fr.bi.web.conf.services.datalink.*;
import com.fr.bi.web.conf.services.dbconnection.BIGetAllTranslatedTablesByConnectionAction;
import com.fr.bi.web.conf.services.dbconnection.BIGetConnectionNamesAction;
import com.fr.bi.web.conf.services.dbconnection.BIGetFieldInfo4NewTableAction;
import com.fr.bi.web.conf.services.dbconnection.BIGetTableFieldsByTableInfoAction;
import com.fr.bi.web.conf.services.packs.*;
import com.fr.fs.FSContext;
import com.fr.fs.base.FSManager;
import com.fr.fs.control.UserControl;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.FSConstants;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.stable.fun.impl.NoSessionIDService;
import com.fr.web.core.WebActionsDispatcher;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BI数据源配置
 *
 * @author Daniel-pc
 */
public class Service4BIConfigure extends NoSessionIDService {

    private static AbstractBIConfigureAction[] actions = new AbstractBIConfigureAction[]{
            new BIInitConfigurePaneAction(),

            new BIGetPackageGroupAction(),
            new BIUpdatePackageGroupAction(),

            new BIGetTablesOfOnePackageAction(),
            new BIGetBriefTablesOfOnePackageAction(),

            new BIGetAllTranslatedTablesByConnectionAction(),
            new BIUpdateTablesInPackageAction(),
            new BIRemoveBusinessPackagesAction(),
            new BIUpdateJarAction(),
            new BIGetCubePathAction(),
            new BISetCubePathAction(),
            new BICheckCubePathAction(),
            new BICheckCubeStatusAction(),
            new BIGetCubeLogAction(),

            new BIGetFieldValueAction(),

            new BIGetDataLinkAction(),
            new BITestDataLinkAction(),
            new BIModifyDataLinkAction(),


            new BIRemoveTableSettedExcelAction(),
            new BIGetPreviewTableDataConfAction(),
            new BIImportDBTableConnectionAction(),

            new BIImportExcel4SetField(),
            new BISetTableFieldAction(),
            new BIGetTableExcelFieldAction(),
            new BISaveExcelViewAction(),

            new BIGetTransFromDBAction(),

            new BIGetFieldsInNewTableAction(),


            new BIModifyGlobalUpdateSettingAction(),
            new BIGetCubeGenerateStatusAction(),
            new BISetCubeGenerateAction(),
            new BIGetCubeTaskListAction(),
            new BIAddSingleTableUpdateTaskAction(),
            new BIRemoveCubeTaskAction(),
            new BIUpdateAccessMultiPathAction(),


            new BISaveFileGetExcelDataAction(),

            new BICheckGenerateCubeAction(),

            new BIGetConnectionNamesAction(),
            new BIGetTableFieldsByTableInfoAction(),
            new BIGetFieldInfo4NewTableAction(),

            new BICreateFieldsUnionAction(),

            new BINumberFieldMaxMinValueAction(),

            new BIGetMultiPathAction(),
            new BIUpdateMultiPathAction(),

            new BIGetExcelDataValueAction(),
            new BIPreviewServerLinkAction(),
            new BITestDataLinkByNameAction(),

            new BIGetSchemasByLinkAction(),

            new BIGetPackageAuthorityAction(),
            new BISavePackageAuthorityAction(),
            new BIGetInfoEnterConfAction(),
            new BIGetTableUpdateSqlAction(),

            new BIGetAllBusinessPackagesAction(),

            new BIGetAllTableNamesOfAllPackageAction(),
            new BIGetFieldValueByFieldIdAction(),
            new BISaveLoginFieldAction()

    };

    /**
     * 返回
     *
     * @return 名称
     */
    @Override
    public String actionOP() {
        return "fr_bi_configure";
    }

    /**
     * 处理HTTP请求
     *
     * @param req HTTP请求
     * @param res HTTP响应
     * @param op  op参数值
     * @throws Exception
     */
    @Override
    public void process(HttpServletRequest req, HttpServletResponse res,
                        String op) throws Exception {
        FSContext.initData();
        res.setHeader("Pragma", "No-cache");
        res.setHeader("Cache-Control", "no-cache, no-store");
        res.setDateHeader("Expires", -10);
        dealServletPriviousUrl(req);
        PrivilegeVote vote = getFSVote(req, res);
        FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
        if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
            vote.action(req, res);
            return;
        }
        long userId = ServiceUtils.getCurrentUserID(req);
        if (UserControl.getInstance().hasModulePrivilege(userId, FSConstants.MODULEID.BI)) {
            WebActionsDispatcher.dealForActionNoSessionIDCMD(req, res, actions);
        }
    }

    private void dealServletPriviousUrl(HttpServletRequest req) {
        String cmd = WebUtils.getHTTPRequestParameter(req, "cmd");
        if (ComparatorUtils.equals(cmd, BIInitConfigurePaneAction.CMD)) {
            BIServiceUtil.setPreviousUrl(req);
        }
    }

    private PrivilegeVote getFSVote(HttpServletRequest req, HttpServletResponse res) throws Exception {
        FSAuthentication authen = FSAuthenticationManager.exAuth4FineServer(req);
        if (authen == null) {
            //b:to improve
            AbstractFSAuthService.dealCookie(req, res);
            authen = FSAuthenticationManager.exAuth4FineServer(req);
        }
        return FSManager.getFSKeeper().access(authen);
    }
}
