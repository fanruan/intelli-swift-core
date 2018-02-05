package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.service.engine.pack.EnginePackageManager;
import com.finebi.conf.structure.bean.group.FinePackageGroup;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.finebi.conf.utils.FineGroupUtils;
import com.fr.general.ComparatorUtils;
import com.fr.swift.conf.business.ISwiftXmlWriter;
import com.fr.swift.conf.business.pack.PackXmlWriter;
import com.fr.swift.conf.business.pack.PackageParseXml;
import com.fr.swift.conf.business.pack.SwiftPackageDao;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.ProviderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-23 13:58:01
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */

public class SwiftPackageConfProvider implements EnginePackageManager {

    private SwiftPackageDao businessPackDAO;

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ProviderManager.class);

    private String xmlFileName = "package.xml";

    public SwiftPackageConfProvider() {
        PackageParseXml xmlHandler = new PackageParseXml();
        ISwiftXmlWriter swiftXmlWriter = new PackXmlWriter();
        businessPackDAO = new SwiftPackageDao(xmlHandler, xmlFileName, swiftXmlWriter);
    }

    @Override
    public List<FineBusinessPackage> getAllPackage() {
        return businessPackDAO.getAllConfig();
    }

    @Override
    public List<FineBusinessPackage> getAllPackageByGroupId(String groupId) {
        List<FineBusinessPackage> resultPackageList = new ArrayList<FineBusinessPackage>();
        List<FinePackageGroup> groupList = FineGroupUtils.getAllGroups();
        for (FinePackageGroup group : groupList) {
            if (ComparatorUtils.equals(group.getGroupId(), groupId)) {
                List<String> packageIdList = group.getAllPackageId();
                List<FineBusinessPackage> businessPackageList = businessPackDAO.getAllConfig();
                for (FineBusinessPackage businessPackage : businessPackageList) {
                    if (packageIdList.contains(businessPackage.getId())) {
                        resultPackageList.add(businessPackage);
                    }
                }
                break;
            }
        }
        return resultPackageList;
    }

    @Override
    public FineBusinessPackage getSinglePackage(String packageId) {
        List<FineBusinessPackage> businessPackageList = businessPackDAO.getAllConfig();
        for (FineBusinessPackage businessPackage : businessPackageList) {
            if (ComparatorUtils.equals(businessPackage.getId(), packageId)) {
                return businessPackage;
            }
        }
        return null;
    }

    @Override
    public FineBusinessPackage getPackageByName(String packageName) {
        List<FineBusinessPackage> businessPackageList = businessPackDAO.getAllConfig();
        for (FineBusinessPackage businessPackage : businessPackageList) {
            if (ComparatorUtils.equals(businessPackage.getName(), packageName)) {
                return businessPackage;
            }
        }
        return null;
    }

    @Override
    public boolean addPackage(FineBusinessPackage pack, String groupId) {
        try {
//            List<FinePackageGroup> groups = FineGroupUtils.getAllGroups();
//            for (FinePackageGroup group : groups) {
//                if (ComparatorUtils.equals(group.getGroupId(), groupId)) {
//                    group.addPackage(pack.getId());
//                    FineGroupUtils.updateGroups(group);
//                }
//            }
            return businessPackDAO.saveConfig(pack);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean removePackage(String packageId) {
        FineBusinessPackage fineBusinessPackage = this.getSinglePackage(packageId);
        if (fineBusinessPackage != null) {
            return businessPackDAO.removeConfig(fineBusinessPackage);
        }
        return false;
    }

    @Override
    public boolean updatePackages(List<FineBusinessPackage> needUpdatePackages) {
        return businessPackDAO.updateConfigs(needUpdatePackages);
    }

    @Override
    public boolean isPackageExist(String packageId) {
        return this.getSinglePackage(packageId) != null;
    }

    @Override
    public boolean moveTable(String tableId, String nowPackageId, String newPackageId) {
        try {
            FineBusinessPackage nowPackage = this.getSinglePackage(nowPackageId);
            FineBusinessPackage newPackage = this.getSinglePackage(newPackageId);
            nowPackage.removeTable(tableId);
            newPackage.addTable(tableId);
            List<FineBusinessPackage> packageList = new ArrayList<FineBusinessPackage>();
            packageList.add(nowPackage);
            packageList.add(newPackage);
            return businessPackDAO.updateConfigs(packageList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
