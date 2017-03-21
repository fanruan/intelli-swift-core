package com.fr.bi.web.conf;


import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.bi.web.conf.services.*;
import com.fr.bi.web.conf.services.cubeconf.*;
import com.fr.bi.web.conf.services.cubeconf.updatesetting.BIGetUpdateSettingAction;
import com.fr.bi.web.conf.services.cubeconf.updatesetting.BIModifyUpdateSettingAction;
import com.fr.bi.web.conf.services.cubetask.*;
import com.fr.bi.web.conf.services.datalink.*;
import com.fr.bi.web.conf.services.dbconnection.*;
import com.fr.bi.web.conf.services.packs.*;
import com.fr.bi.web.conf.services.session.BIConfUpdateSession;
import com.fr.bi.web.conf.services.tables.*;
import com.fr.fs.FSContext;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.stable.fun.Service;
import com.fr.web.core.WebActionsDispatcher;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BI数据源配置
 *
 * @author Daniel-pc
 */
public class Service4BIConfigure implements Service {

    private static AbstractBIConfigureAction[] actions = {
            new BIInitConfigurePaneAction(),

            new BIGetPackageGroupAction(),
            new BIUpdatePackageGroupAction(),

            new BIGetTablesOfOnePackageAction(),
            new BIGetBriefTablesOfOnePackageAction(),
            new BIGetFieldsOfOneTableAction(),

            new BIGetAllTranslatedTablesByConnectionAction(),
            new BIUpdateTablesInPackageAction(),
            new BIRemoveBusinessPackagesAction(),
            new BIUpdateJarAction(),
            new BIGetCubePathAction(),
            new BISetCubePathAction(),
            new BICheckCubePathAction(),
//            new BICheckCubeStatusAction(),
            new BIGetCubeLogAction(),

            new BIGetFieldValueAction(),

            new BIGetDataLinkAction(),
            new BITestDataLinkAction(),
            new BIModifyDataLinkAction(),


            new BIGetPreviewTableDataConfAction(),
            new BIImportDBTableConnectionAction(),


            new BIModifyUpdateSettingAction(),
            new BIGetUpdateSettingAction(),
            new BIGetCubeGenerateStatusAction(),

            new BIGetTransFromDBAction(),

            new BIModifyGlobalUpdateSettingAction(),

            new BISetCubeGenerateAction(),
            new BIGetCubeTaskListAction(),
            new BIAddSingleTableUpdateTaskAction(),
            new BIRemoveCubeTaskAction(),
            new BIUpdateAccessMultiPathAction(),


            new BISaveFileGetExcelDataAction(),
            new BISaveFileGetExcelViewDataAction(),
            new BIGetExcelHTMLViewAction(),
            new BISaveUploadExcelFileAction(),

            new BICheckCubeTableStatusAction(),
            new BICheckCubeTableAction(),

            new BIGetConnectionNamesAction(),
            new BIGetFieldInfo4NewTableAction(),
            new BIRefreshTableFieldsAction(),

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
            new BISaveLoginFieldAction(),

            new BIConfUpdateSession(),
            new BIGetSimpleTablesOfOnePackageAction(),
            new BIGetTableInfoAction(),
            new BIAddNewTablesAction(),
            new BIRemoveTableAction(),
            new BIUpdateOneTableAction(),
            new BICancelEditTableAction(),
            new BIUpdateRelationAction(),
            new BIUpdatePackageNameAction(),
            new BIUpdateTablesTranOfPackageAction(),
            new BIGetRelationsDetailAction(),
            new BIGetData4SelectFieldAction(),
            new BIGetLinksAndPackagesAction(),
            new BIGetTablesInfoByIdsAction(),

            // 后门
            new BICacheClearAction(),
            new BIUserMapCacheClearAction(),
            new BIChildMapClearAction(),
            new BIRemoveTableInUseCheckAction(),
            new BITurnOffDeployModeAction(),
            new BITurnOnDeployModeAction(),
            new BISetDeployModeLimitValueAction(),
            new BIDisplayDeployModeLimitValueAction(),
            new BIDownloadFineindexLogAction(),
            new BIGetThreadPoolSizeAction(),
            new BISetThreadPoolSizeAction(),
            new BISimpleAPIDemoAction(),
            new BIGetCubeTaskLogsSDKAction(),
            new BISetTransportThreadPoolSizeAction(),
            new BIGetTransportThreadPoolSizeAction(),
            new BISetMinCubeFreeHDSpaceRateAction(),
            new BIGetMinCubeFreeHDSpaceRateAction()
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

    @Override
    public void process(HttpServletRequest req, HttpServletResponse res, String op, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        FSContext.initData();
        res.setHeader("Pragma", "No-cache");
        res.setHeader("Cache-Control", "no-cache, no-store");
        res.setDateHeader("Expires", BIBaseConstant.DATE_HEADER_EXPIRES);
        dealServletPreviousUrl(req);
        PrivilegeVote vote = getFSVote(req, res);
        FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
        if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
            vote.action(req, res);
            return;
        }
        if (hasDataConfigAuth(userId)) {
            WebActionsDispatcher.dealForActionCMD(req, res, sessionID, actions);
        }
    }

    private boolean hasDataConfigAuth(long userId) throws Exception {
        return BIWebUtils.showDataConfig(userId);
    }

    private void dealServletPreviousUrl(HttpServletRequest req) {
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
