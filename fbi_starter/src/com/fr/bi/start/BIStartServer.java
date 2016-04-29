package com.fr.bi.start;

import com.fr.base.ConfigManager;
import com.fr.base.FRContext;
import com.fr.design.DesignerEnvManager;
import com.fr.design.mainframe.TemplatePane;
import com.fr.env.SignIn;
import com.fr.general.GeneralContext;
import com.fr.start.StartServer;

/**
 * Created by Connery on 2016/1/17.
 */
public class BIStartServer {
    /**
     * 开始执行
     * @param args 参数
     */
    public static void main(String[] args){

        FRContext.setLanguage(DesignerEnvManager.getEnvManager().getLanguage());
        try {
                String current = DesignerEnvManager.getEnvManager().getCurEnvName();
        SignIn.signIn(DesignerEnvManager.getEnvManager().getEnv(current));
    } catch (Exception e) {
        TemplatePane.getInstance().dealEvnExceptionWhenStartDesigner();
    }
    StartServer.browerURLWithLocalEnv("http://localhost:" + DesignerEnvManager.getEnvManager().getJettyServerPort() + "/" + GeneralContext.getCurrentAppNameOfEnv() + "/" + ConfigManager.getInstance().getServletMapping()
                + "?op=fs");
    }

    private static boolean isDebug() {
        return "true".equals(System.getProperty("debug"));
    }
    private static final int DEBUG_PORT = 51463;


}