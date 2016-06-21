package com.fr.bi.web.base.services;

import com.fr.fs.FSConfig;
import com.fr.fs.FSRegisterForBI;
import com.fr.privilege.PrivilegeManager;
import com.fr.stable.StringUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 小灰灰 on 2016/6/21.
 */
public class BISignInAction extends ActionNoSessionCMD {
    /**
     * fs_signin's cmd
     * @return
     */
    public String getCMD() {
        return "fs_signin";
    }

    /**
     * 操作的执行方法
     * @param req http请求对象
     * @param res http响应对象
     * @throws Exception 抛出异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("company", FSConfig.getProviderInstance().getSystemAttr().getLoginTitle4FS());
        java.util.Map<String, Object> parameterMap = WebUtils.parameters4SessionIDInfor(req);
        map.put("isSupportFS", FSRegisterForBI.isSupportFS());
        map.put("backgroundImageID",FSConfig.getProviderInstance().getSystemAttr().getBgImageID4FS());
        map.putAll(parameterMap);
        if (!PrivilegeManager.getProviderInstance().hasSetFSSystemPW()) {
            WebUtils.writeOutTemplate("/com/fr/fs/web/system_manager_set.html", res, map);
        } else {
            String loginUrl = FSConfig.getProviderInstance().getSystemAttr().getLoginUrl4FS();
            if(StringUtils.isNotBlank(loginUrl)) {
                res.sendRedirect(loginUrl);
            }else {
                WebUtils.writeOutTemplate("/com/fr/bi/web/html/bi_login.html", res, map);
            }
        }
    }
}
