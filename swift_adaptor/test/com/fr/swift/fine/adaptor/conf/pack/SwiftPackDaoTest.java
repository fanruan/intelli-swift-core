//package com.fr.swift.fine.adaptor.conf.pack;
//
//import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
//import com.fr.swift.conf.business.ISwiftXmlWriter;
//import com.fr.swift.conf.business.pack.PackXmlWriter;
//import com.fr.swift.conf.business.pack.PackageParseXml;
//import com.fr.swift.conf.business.pack.SwiftPackageDao;
//import com.fr.swift.fine.adaptor.conf.creater.TestPackageCreator;
//import junit.framework.TestCase;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * This class created on 2018-1-24 14:45:18
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI Analysis 1.0
// */
//public class SwiftPackDaoTest extends TestCase {
//
//
//    private PackageParseXml xmlHandler = new PackageParseXml();
//    private ISwiftXmlWriter swiftXmlWriter = new PackXmlWriter();
//    private String fileName = "package.xml";
//
//    private SwiftPackageDao packageDao = new SwiftPackageDao(xmlHandler, fileName, swiftXmlWriter);
//
//    /**
//     * 单业务包的增、改、删
//     */
//    @Test
//    public void testPackage() {
//        packageDao.removeAllConfig();
//
//        packageDao.saveConfig(TestPackageCreator.createP1());
//        packageDao.saveConfig(TestPackageCreator.createP2());
//        assertEquals(packageDao.getAllConfig().size(), 2);
//        assertEquals(packageDao.getAllConfig().get(0).getId(), TestPackageCreator.createP1().getId());
//        assertEquals(packageDao.getAllConfig().get(1).getId(), TestPackageCreator.createP2().getId());
//
//        packageDao.updateConfig(TestPackageCreator.createP1());
//        assertEquals(packageDao.getAllConfig().size(), 2);
//        assertEquals(packageDao.getAllConfig().get(1).getId(), TestPackageCreator.createP1().getId());
//        assertEquals(packageDao.getAllConfig().get(0).getId(), TestPackageCreator.createP2().getId());
//
//        packageDao.removeConfig(TestPackageCreator.createP1());
//        assertEquals(packageDao.getAllConfig().size(), 1);
//        assertEquals(packageDao.getAllConfig().get(0).getId(), TestPackageCreator.createP2().getId());
//    }
//
//    /**
//     * 多业务包的增、改、删
//     */
//    @Test
//    public void testPackages() {
//        packageDao.removeAllConfig();
//
//        List<FineBusinessPackage> packageList = new ArrayList<FineBusinessPackage>();
//        packageList.add(TestPackageCreator.createP1());
//        packageList.add(TestPackageCreator.createP2());
//        packageList.add(TestPackageCreator.createP3());
//        packageDao.saveConfigs(packageList);
//        assertEquals(packageDao.getAllConfig().size(), 3);
//        assertEquals(packageDao.getAllConfig().get(0).getId(), TestPackageCreator.createP1().getId());
//        assertEquals(packageDao.getAllConfig().get(1).getId(), TestPackageCreator.createP2().getId());
//        assertEquals(packageDao.getAllConfig().get(2).getId(), TestPackageCreator.createP3().getId());
//
//        packageDao.updateConfigs(packageList);
//        assertEquals(packageDao.getAllConfig().size(), 3);
//
//        packageList.remove(2);
//        packageDao.removeConfigs(packageList);
//        assertEquals(packageDao.getAllConfig().size(), 1);
//        assertEquals(packageDao.getAllConfig().get(0).getId(), TestPackageCreator.createP3().getId());
//
//    }
//}
