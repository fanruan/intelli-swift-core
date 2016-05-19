//package com.fr.bi.conf.base.pack;
//
//import com.fr.bi.base.BIUser;
//import com.fr.bi.conf.provider.BISystemPackAndAuthConfigurationProvider;
//import com.fr.json.JSONArray;
//
//import java.util.Set;
//
///**
// * Created by wuk on 16/5/8.
// */
//public class BISystemPackAndAuthConfigurationManagerTest {
//    private BISystemPackAndAuthConfigurationManager manager;
//    private BIUser user;
//
//    protected void setUp() throws Exception {
//
//    }
//
//    //@org.junit.Test
//    public void getAllPackages() throws Exception {
//        JSONArray roleInfojo = new JSONArray("[3,5,4]");
//
//        String[] rolesArray = new String[roleInfojo.length()];
//        for (int i = 0; i < roleInfojo.length(); i++) {
//            rolesArray[i] = String.valueOf(roleInfojo.getString(i));
//        }
//
//        BISystemPackAndAuthConfigurationProvider packageAndAuthorityManager = new BISystemPackAndAuthConfigurationManager();
//
//
//        BIPackAndAuthority biPackAndAuthority = new BIPackAndAuthority();
//        biPackAndAuthority.setBiPackageID("1111");
//        biPackAndAuthority.setRoleIdArray(rolesArray);
//        packageAndAuthorityManager.addPackage(-999, biPackAndAuthority);
//
//        biPackAndAuthority.setBiPackageID("2222");
//        boolean isExisted = packageAndAuthorityManager.containPackage(-999, biPackAndAuthority);
//        if (isExisted) {
//            packageAndAuthorityManager.updateAuthority(-999, biPackAndAuthority);
//        } else {
//            packageAndAuthorityManager.addPackage(-999, biPackAndAuthority);
//        }
//
//        Set<BIPackAndAuthority> allPackages = packageAndAuthorityManager.getAllPackages(-999);
//        System.out.println(allPackages);
//    }
//
//    //@org.junit.Test
//    public void getPackage() throws Exception {
//        String roles="[3,5,4]";
//        JSONArray roleInfojo=new JSONArray(roles);
//        String[] packageIdArray=new String[roleInfojo.length()];
//        for (int i = 0; i < roleInfojo.length(); i++) {
//            packageIdArray[i]= String.valueOf(roleInfojo.getString(i));
//        }
//    }
//
//    //@org.junit.Test
//    public void addPackage() throws Exception {
//
//    }
//
//    //@org.junit.Test
//    public void removePackage() throws Exception {
//
//    }
//
//}
