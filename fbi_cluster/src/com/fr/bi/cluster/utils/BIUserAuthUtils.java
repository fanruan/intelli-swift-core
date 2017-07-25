package com.fr.bi.cluster.utils;

import com.fr.bi.cluster.ClusterAdapter;
import com.fr.file.DefaultClusterServerProcessor;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.plugin.ExtraClassManager;
import com.fr.stable.Constants;
import com.fr.stable.fun.ClusterServerProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by wang on 2017/4/18.
 */
public class BIUserAuthUtils {
    public static long NO_AUTHENTICATED_USER = -99L;
    public static long getCurrentUserID(HttpServletRequest req) {
        FSAuthentication authentication = getFSAuthentication(req);
        return authentication != null?authentication.getUserInfo().getId():NO_AUTHENTICATED_USER;
    }
    public static FSAuthentication getFSAuthentication(HttpServletRequest req){
//        集群模式下，子节点通过cookie方式获取auth
        if (ClusterEnv.isCluster()&&!(ClusterAdapter.getManager().getHostManager().isSelf())) {
            ClusterServerProcessor processor = ExtraClassManager.getInstance().getSingle(ClusterServerProcessor.XML_TAG, DefaultClusterServerProcessor.class);
            Map<String, Object> extraPara = processor.getUseInfoParaByCookie(req);
            return (FSAuthentication) extraPara.get(Constants.P.PRIVILEGE_AUTHENCATION_KEY);
        }else {
            FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
            return authentication;
        }
    }
    public static String getCurrentUserName(HttpServletRequest req) {
        FSAuthentication authentication = getFSAuthentication(req);
        return authentication != null?authentication.getUserInfo().getUsername():"";
    }
}