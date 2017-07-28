package com.fr.bi.test.etl.analysis.manager;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.fr.bi.cal.BICubeManager;
import com.fr.bi.cal.generate.EmptyCubeTask;
import com.fr.bi.conf.base.auth.BISystemAuthorityManager;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.base.datasource.BIConnectionProvider;
import com.fr.bi.conf.manager.update.BIUpdateSettingManager;
import com.fr.bi.conf.provider.BIAuthorityManageProvider;
import com.fr.bi.conf.provider.BIUpdateFrequencyManagerProvider;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.etl.analysis.manager.UserETLCubeManager;
import com.fr.bi.etl.analysis.manager.UserETLCubeManagerProvider;
import com.fr.cluster.rpc.RPC;
import com.fr.json.JSONException;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

import java.io.*;

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

    public void testUpdateFrequency() {
        BIUpdateFrequencyManagerProvider provider = new BIUpdateSettingManager();
        RPC.registerSkeleton(provider, 1245);
        BIUpdateFrequencyManagerProvider proxy = (BIUpdateFrequencyManagerProvider) RPC.getProxy(BIUpdateSettingManager.class, "127.0.0.1", 12345);
        System.err.println(proxy.toString());
    }

    public void testAuth() {
        BISystemAuthorityManager provider = new BISystemAuthorityManager();
        RPC.registerSkeleton(provider, 12345);
        BIAuthorityManageProvider pp = (BIAuthorityManageProvider) RPC.getProxy(BISystemAuthorityManager.class, "127.0.0.1", 12345);

//      StableFactory.registerMarkedObject(BIAuthorityManageProvider.XML_TAG, new BISystemAuthorityManager());
//        BIAuthorityManageProvider providerLocal = StableFactory.getMarkedObject(BIAuthorityManageProvider.XML_TAG,BISystemAuthorityManager.class);

        System.err.println(pp.toString());

    }


    public void testCubeTask() {
        BICubeManager provider = new BICubeManager();
        provider.addTask(new EmptyCubeTask("123"), 123);
        RPC.registerSkeleton(provider, 12345);
        BICubeManagerProvider proxy = (BICubeManagerProvider) RPC.getProxy(BICubeManager.class, "127.0.0.1", 12345);
        System.out.println(proxy.hasTask(new EmptyCubeTask("123"), 123));
    }

    class FOO implements Runnable, Serializable {

        private static final long serialVersionUID = 4710240579276018302L;

        @Override
        public void run() {
            System.out.println("Lala");
        }
    }

    public void testInnerClassSerialize() {
        FOO foo = new FOO();
        Thread t = new Thread(foo);
//        t.start();
//        t.join();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(foo);
            oos.close();
            FOO foofoo = (FOO) new ObjectInputStream(
                    new ByteArrayInputStream(baos.toByteArray())).readObject();

            t = new Thread(foofoo);
            t.start();
            t.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void testTime() {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.err.println(end - start);
    }

}