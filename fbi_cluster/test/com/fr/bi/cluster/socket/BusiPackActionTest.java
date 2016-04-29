package com.fr.bi.cluster.socket;

import com.fr.bi.aconfig.BIBusiPack;
import com.fr.bi.cluster.socket.manager.BIClusterBusiPackManager;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;


/**
 * Created by FineSoft on 2015/5/12.
 */
public class BusiPackActionTest extends TestCase {

//    public void testRemovePack() {
//        BIClusterBusiPackManager manager = new BIClusterBusiPackManager();
//        manager.setMasterIP("127.0.0.1");
//        String[] names = new String[2];
//        names[0] = "a";
//        names[1] = "b";
//        manager.removePackagesByNames(names, new Long(-999));
//    }

//    public void testaddEmptyPack() {
//        BIClusterBusiPackManager manager = new BIClusterBusiPackManager();
//        manager.setMasterIP("127.0.0.1");
//
//        manager.addAnEmptyPack("a", -999, new Locale("cn"));
//    }

    //    public void testhasAvailableAnalysisPack() {
//        BIClusterBusiPackManager manager = new BIClusterBusiPackManager();
//        manager.setMasterIP("127.0.0.1");
//
//        boolean result = manager.hasAvailableAnalysisPacks(-999);
//        assertEquals(true, result);
//    }
//    public void testRenameBusiPack() {
//        BIClusterBusiPackManager manager = new BIClusterBusiPackManager();
//        manager.setMasterIP("127.0.0.1");
//
//        manager.renameBusiPack("Package1", "Package2", -999);
//    }
//    public void testloadAllAvailablePackages() {
//        BIClusterBusiPackManager manager = new BIClusterBusiPackManager();
//        manager.setMasterIP("127.0.0.1");
//
//        Iterator<String> result = manager.loadAllAvailablePackages(-999).iterator();
//        while (result.hasNext()) {
//            System.out.println(result.next());
//        }
//    }
//    public void testRenameBusiPack() {
//        BIClusterBusiPackManager manager = new BIClusterBusiPackManager();
//        manager.setMasterIP("127.0.0.1");
//        try {
//            JSONArray result = manager.asJsonWithPackageGroup(-999);
//            System.out.println(result.toString());
//        } catch (Exception ex) {
//             BILogger.getLogger().error(e.getMessage(), e);
//        }
//
//
//    }

    public void testGetFinalVersionOfPackByName() {
        BIClusterBusiPackManager manager = new BIClusterBusiPackManager();
        manager.setMasterIP("127.0.0.1");
        try {
            BIBusiPack result = manager.getFinalVersionOfPackByName("Package2", -999);
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

}