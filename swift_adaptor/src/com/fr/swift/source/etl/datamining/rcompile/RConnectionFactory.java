package com.fr.swift.source.etl.datamining.rcompile;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.internalimp.bean.rinfo.RInfoBean;
import com.finebi.conf.service.rlink.FineSaveRLinkService;
import com.fr.swift.log.SwiftLoggers;
import org.rosuda.REngine.Rserve.RConnection;

/**
 * Created by Handsome on 2018/4/11 0011 15:04
 */
public class RConnectionFactory {

    private static RConnection conn;

    public static RConnection getRConnection() {
        if(null == conn) {
            try {
                FineSaveRLinkService service = StableManager.getContext().getObject("fineSaveRLinkServiceImpl");
                RInfoBean infoBean = service.getRLink();
                if(null != infoBean) {
                    boolean needPasswd = infoBean.isNeedPasswd();
                    String ip = infoBean.getIp();
                    int port = infoBean.getPort();
                    if(null != ip && port > 0) {
                        if(needPasswd) {
                            String userName = infoBean.getUserName();
                            String passwd = infoBean.getPasswd();
                            conn = new RConnector().getNewConnection(true, ip, port, userName, passwd);
                        } else {
                            conn = new RConnector().getNewConnection(true, ip, port);
                        }
                    }
                }
            } catch(Exception e) {
                SwiftLoggers.getLogger().error("failed to get R link information", e);
            }
            try {
                if(null == conn) {
                    conn = new RConnector().getNewConnection(true);
                }
            } catch(Exception e) {
            }
        }
        return conn;
    }
}
