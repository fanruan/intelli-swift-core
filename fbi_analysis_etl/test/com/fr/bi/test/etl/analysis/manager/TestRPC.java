package com.fr.bi.test.etl.analysis.manager;

import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.conf.base.auth.BISystemAuthorityManager;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.base.datasource.BIConnectionProvider;
import com.fr.bi.conf.provider.BIAuthorityManageProvider;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.etl.analysis.manager.UserETLCubeManager;
import com.fr.bi.etl.analysis.manager.UserETLCubeManagerProvider;
import com.fr.cluster.rpc.RPC;
import com.fr.json.JSONException;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

/**
 * Created by wang on 2016/11/22.
 */
public class TestRPC extends TestCase {
    public void testLocalUserETLCubeManagerProvider() {

        StableFactory.registerMarkedObject(UserETLCubeManager.class.getName(), new UserETLCubeManager());

        UserETLCubeManager provider = new UserETLCubeManager();
        RPC.registerSkeleton(provider, 12345);
        UserETLCubeManagerProvider pp = (UserETLCubeManagerProvider) RPC.getProxy(UserETLCubeManager.class, "127.0.0.1", 12345);
        StableFactory.registerMarkedObject(UserETLCubeManagerProvider.class.getName(), pp);

        BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
        UserETLCubeManagerProvider p = BIAnalysisETLManagerCenter.getUserETLCubeCheckManagerProvider();


        System.err.println(p.toString());
    }

    public void testBIConnectionManager() {

        StableFactory.registerMarkedObject(BIConnectionManager.XML_TAG, BIConnectionManager.getInstance());

        BIConnectionManager providerLocal = StableFactory.getMarkedObject(BIConnectionManager.XML_TAG, BIConnectionManager.class);
        System.err.println(providerLocal.fileName());

        BIConnectionManager provider = BIConnectionManager.getInstance();
        RPC.registerSkeleton(provider, 12345);
        BIConnectionProvider pp = (BIConnectionProvider) RPC.getProxy(BIConnectionManager.class, "127.0.0.1", 12345);

        try {
            System.err.println(pp.createJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void testAuth() {
        BISystemAuthorityManager provider = new BISystemAuthorityManager();
        RPC.registerSkeleton(provider, 12345);
        BIAuthorityManageProvider pp = (BIAuthorityManageProvider) RPC.getProxy(BISystemAuthorityManager.class, "127.0.0.1", 12345);

//      StableFactory.registerMarkedObject(BIAuthorityManageProvider.XML_TAG, new BISystemAuthorityManager());
//        BIAuthorityManageProvider providerLocal = StableFactory.getMarkedObject(BIAuthorityManageProvider.XML_TAG,BISystemAuthorityManager.class);

        System.err.println(pp.toString());

    }

    public void testTime(){
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.err.println(end -start);
    }

}