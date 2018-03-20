package com.fr.swift.conf.business.path;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.service.dao.provider.BusinessConfigDAO;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.swift.conf.business.relation.SwiftRelationDao;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018/03/19
 */
public class SwiftRelationPathDao implements BusinessConfigDAO<FineBusinessTableRelationPath> {

    SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftRelationPathDao.class);
    private RelationPathContainer container = RelationPathContainer.getContainer();
    private PathAnalyser analyser;
    private volatile boolean relationRegisted = false;

    public SwiftRelationPathDao(SwiftRelationDao relationDao) {
        this.analyser = new PathAnalyser(relationDao);
    }

    @Override
    public boolean saveConfig(FineBusinessTableRelationPath path) {
        List<FineBusinessTableRelationPath> paths = getAllConfig();
        paths.add(path);
        container.saveResources(paths);
        return true;
    }

    @Override
    public boolean updateConfig(FineBusinessTableRelationPath relationPath) {
        List<FineBusinessTableRelationPath> paths = getAllConfig();
        Iterator<FineBusinessTableRelationPath> iterator = paths.iterator();
        boolean contains = false;
        while (iterator.hasNext() && !contains) {
            FineBusinessTableRelationPath path = iterator.next();
            if (path.getPathName().equals(relationPath.getPathName())) {
                iterator.remove();
                paths.add(relationPath);
                container.saveResources(paths);
                contains = true;
            }
        }
        if (!contains) {
            return saveConfig(relationPath);
        }
        return true;
    }

    @Override
    public boolean removeConfig(FineBusinessTableRelationPath relationPath) {
        List<FineBusinessTableRelationPath> paths = getAllConfig();
        Iterator<FineBusinessTableRelationPath> iterator = paths.iterator();
        boolean remove = false;
        while (iterator.hasNext() && !remove) {
            FineBusinessTableRelationPath path = iterator.next();
            if (path.getPathName().equals(relationPath.getPathName())) {
                iterator.remove();
                container.saveResources(paths);
                remove = true;
            }
        }
        return remove;
    }

    @Override
    public boolean removeAllConfig() {
        try {
            container.saveResources(new ArrayList<FineBusinessTableRelationPath>());
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<FineBusinessTableRelationPath> getAllConfig() {
        List<FineBusinessTableRelationPath> result = container.getResources();
        if (relationRegisted) {
            List<FineBusinessTableRelationPath> conf = analyser.getAllPaths();
            relationRegisted = false;

//            for (FineBusinessTableRelationPath path : conf) {
//                if (!result.contains(path)) {
//                    result.add(path);
//                }
//            }
            // fixme 不应该addAll。 pathname 都是 "" equals 方法判断的是pathname是否相等
            result.addAll(conf);
            container.saveResources(result);
        }
        return result;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.NONE;
    }

    @Override
    public boolean saveConfigs(List<FineBusinessTableRelationPath> businessConfigList) {
        for (FineBusinessTableRelationPath path :
                businessConfigList) {
            saveConfig(path);
        }
        return true;
    }

    @Override
    public boolean updateConfigs(List<FineBusinessTableRelationPath> businessConfigList) {
        for (FineBusinessTableRelationPath path :
                businessConfigList) {
            updateConfig(path);
        }
        return true;
    }

    @Override
    public boolean removeConfigs(List<FineBusinessTableRelationPath> businessConfigList) {
        for (FineBusinessTableRelationPath path :
                businessConfigList) {
            removeConfig(path);
        }
        return true;
    }

    public void registRelations(List<FineBusinessTableRelation> allConfig) {
        boolean addNewRelation = analyser.registRelation(allConfig);
        relationRegisted = relationRegisted || addNewRelation;
    }
}