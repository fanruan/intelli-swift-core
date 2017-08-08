package com.finebi.cube.location.manager;

import com.finebi.common.name.Name;
import com.finebi.common.resource.ResourceNameImpl;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.location.meta.BILocationInfo;
import com.finebi.cube.location.meta.BILocationInfoImp;
import com.finebi.cube.location.meta.BILocationPool;
import com.finebi.cube.location.meta.BILocationPoolImp;
import com.finebi.cube.utils.BILocationUtils;
import com.fr.base.FRContext;
import com.fr.bi.base.BIBusinessPackagePersistThread;
import com.fr.bi.base.BIBusinessPackagePersistThreadHolder;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wang on 2017/7/6.
 */
public class BILocationManager {
    private String xmlPath ;
    private static BILocationManager instance = new BILocationManager();
    BISingleLocationManager accessProvider;
    public static BILocationManager getInstance(){
        return instance;
    }

    public void setXmlPath(String xmlPath){
        this.xmlPath = xmlPath;
    }

    private void initXmlPath(){
        if(StringUtils.isEmpty(xmlPath)){
            xmlPath = FRContext.getCurrentEnv().getPath() + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + BILocationProvider.XML_TAG + ".xml";
        }
    }

    private BILocationInfo registerFile(File file) {
        String base = file.getParent();
        String logicFolder = BIStringUtils.cutStartSlash(BILocationUtils.getLogicPath(BILocationUtils.replaceSlash(base + File.separator)));
        String child = file.getName();
        String locationKey = logicFolder + child;
        String realFolder = BILocationUtils.replaceSlash(base).replace(BILocationUtils.getLocationPrefix(), "");
        return new BILocationInfoImp(locationKey, realFolder, child);
    }

    private void searchFolder(File folder, BILocationPool pool) {
        if (folder.isDirectory()) {
            String files[] = folder.list();
            for (String file : files) {
                searchFolder(new File(folder, file), pool);
            }
        } else {
            BILocationInfo locationInfo = registerFile(folder);
            pool.addResourceItem(locationInfo.getResourceName(), locationInfo);
        }
    }

    /**
     * 原始的cube路径一次性加载进locationMap
     */
    private BILocationPool registerAnvancedCube() {
        BILocationPool initMap = new BILocationPoolImp();
        searchFolder(new File(BILocationUtils.getLocationPrefix()), initMap);
        return initMap;
    }


    public void initAccessProvider() {
        Name cubeName = new ResourceNameImpl("Advanced");
        accessProvider = new BISingleLocationManager(cubeName.value());
        initXmlPath();
        accessProvider.initManager(xmlPath);
    }

    // TODO: 2017/7/6
    public BILocationProvider getAccessLocationProvider() {
        if (accessProvider==null) {
            initAccessProvider();
        }
        return accessProvider;
    }


    public void removeOldFiles(Set<String> filePath){
//        1移出discovery里的NIORsourceManager 2删除无用文件
        BICubeDiskPrimitiveDiscovery.getInstance().moveDirtyNIOResourceManager(filePath);
        for(String path : filePath){
            BIFileUtils.delete(new File(path));
        }
    }
    public void persistResourceAsync() {
        //单独的线程写业务包配置文件
        BIBusinessPackagePersistThreadHolder.getInstance().getBiBusinessPackagePersistThread().triggerWork(new BIBusinessPackagePersistThread.Action() {
            @Override
            public void work() {
                initXmlPath();
                BILocationManager.getInstance().getAccessLocationProvider().persistData(xmlPath);
            }
        });
    }
}
