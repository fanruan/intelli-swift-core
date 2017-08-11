package com.finebi.cube.location.manager;

import com.finebi.common.name.Name;
import com.finebi.common.resource.ResourceName;
import com.finebi.common.resource.ResourceNameImpl;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.convert.BILocationTree;
import com.finebi.cube.location.convert.BILocationTreeNode;
import com.finebi.cube.location.meta.*;
import com.finebi.cube.utils.BILocationUtils;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.stable.StringUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by wang on 2017/6/13.
 */
public class BISingleLocationManager implements BILocationProvider {
    BILocationPool accessMap = new BILocationPoolImp();
    private final String currentFolder;

    private BILocationTree tree = new BILocationTree(StringUtils.EMPTY);

    public BISingleLocationManager(String folder) {
        this.currentFolder = getLocationProviderFolder(folder);
    }

    /**
     * @param key cube的唯一标识
     * @return
     */
    private String getLocationProviderFolder(String key) {
        synchronized (BISingleLocationManager.class) {
            return BIMD5Utils.getMD5String(new String[]{new Date().toString(), key});
        }
    }

    public void initManager(String xmlPath) {
        BILocationDomReader reader = new BILocationDomReader(xmlPath);
        accessMap = reader.read();
//      构建查询树
        constructTree();
    }

    private void constructTree(){
        for(ResourceName name :accessMap.getAllNames()){
            tree.insertTree(name.value());
        }
    }

    @Override
    public ICubeResourceLocation getRealLocation(String folderPath, String child) throws URISyntaxException {
        String logicFolder = BILocationUtils.getLogicPath(folderPath);
        String logicPath = logicFolder + child;
        String basePath;
        String prefixPath = BILocationUtils.getLocationPrefix();
        basePath = prefixPath + getBuildLocation(logicPath, logicFolder, child);
        return new BICubeLocation(basePath, child,this);
    }

    private String getBuildLocation(String logicLocation, String relativePath, String child) {
        if (!accessMap.contain(logicLocation)) {
            tree.insertTree(logicLocation);
            String path = BILocationUtils.LOCATION_SEPERATOR + currentFolder + relativePath;
            BILocationInfo locationInfo = new BILocationInfoImp(logicLocation, path, child);
            accessMap.addResourceItem(locationInfo.getResourceName(), locationInfo);
        }
        return accessMap.getResourceItem(logicLocation).getBasePath();
    }

    @Override
    public void persistData(String xmlPath) {
        BILocationDomWriter writer = new BILocationDomWriter(xmlPath);
        writer.write(accessMap);
    }

    @Override
    public BILocationPool getAccessLocationPool() {
        return accessMap.getBILocationPool();
    }

    @Override
    public BILocationPool getAccessLocationPool(Collection<ResourceName> nameCollection) {
        return accessMap.getBILocationPool(nameCollection);
    }

    @Override
    /**
     *
     * @param parentKeys 各级别的父亲目录 user table column等 一次只能是一张表
     *                   examples
     *                   [-999,TableA,FiledA]
     *                   [-999,TableA,relation,FiledB]
     * @param fileNames  文件类型  detail ,index count 等
     * @return
     */
    public BILocationPool getAccessLocationPool(ArrayList<String> parentKeys, ArrayList<String> fileNames) {
        if(parentKeys.isEmpty()){
            // TODO: 2017/7/18 如果需要按照fileName进行拆分，再处理
            return getAccessLocationPool();
        }
        String searchPath = StringUtils.EMPTY;
        for (String parent : parentKeys) {
            searchPath = searchPath + BILocationUtils.LOCATION_SEPERATOR + parent;
        }
        List<BILocationTreeNode<String>> leafs = tree.getLeafs(searchPath);
        Collection<ResourceName> nameCollection = new ArrayList<ResourceName>();
        for(BILocationTreeNode<String> leaf :leafs){
            String logicLocation = leaf.getLocationPath();
        // TODO: 2017/7/18 多类文件，需要多次匹配
//            if(logicLocation.contains(fileNames.get(0))) {
                nameCollection.add(new ResourceNameImpl(logicLocation));
//            }
        }
        return getAccessLocationPool(nameCollection);
    }


    @Override
    /**
     * 返回需要被删除的文件路径
     */
    public Set<String> updateLocationPool(BILocationPool input) {
        HashSet<String> clearSet = new HashSet<String>();
        for (BILocationInfo ele : input.getAllItems()) {
            String inputResourceName = ele.getResourceName().value();
            if (accessMap.contain(inputResourceName)) {
                String basePath = accessMap.getResourceItem(ele.getResourceName().value()).getBasePath();
                String childPath = accessMap.getResourceItem(ele.getResourceName().value()).getChild();
                String path2Remove = new File(BILocationUtils.getLocationPrefix() + basePath + childPath).getAbsolutePath();
                clearSet.add(path2Remove);
            }
//           更新检索树
            tree.insertTree(inputResourceName);
        }
        accessMap.mergePool(input);
        return clearSet;
    }

    @Override
    public boolean isEmpty() {
        return accessMap.isEmpty();
    }
}
