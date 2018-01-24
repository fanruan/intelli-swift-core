//package com.fr.swift.conf.business.path;
//
//import com.finebi.base.constant.FineEngineType;
//import com.finebi.base.utils.data.io.FileUtils;
//import com.finebi.conf.exception.dao.FineRemoveDAOException;
//import com.finebi.conf.exception.dao.FineSaveDAOException;
//import com.finebi.conf.exception.dao.FineSelectDAOException;
//import com.finebi.conf.exception.dao.FineUpdateDAOException;
//import com.finebi.conf.service.dao.provider.BusinessConfigDAO;
//import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
//import com.finebi.log.BILogger;
//import com.finebi.log.BILoggerFactory;
//import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
//import com.fr.third.springframework.stereotype.Component;
//
//import java.io.File;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.HashMap;
//
///**
// * This class created on 2018/1/17
// *
// * @author Each.Zhang
// */
//@Component("fineXmlBusinessRelationPathDAO")
//public class FineXmlBusinessRelationPathDAO implements BusinessConfigDAO<FineBusinessTableRelationPath> {
//
//    private final static String XML_FILE_NAME = "path.xml";
//    BILogger LOGGER = BILoggerFactory.getLogger(FineXmlBusinessRelationPathDAO.class);
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public boolean saveConfig(FineBusinessTableRelationPath path) throws FineSaveDAOException {
//        try {
//            List<FineBusinessTableRelationPath> paths = getAllConfig();
//            Map<String, FineBusinessTableRelationPath> pathMap = null;
//            if (paths == null || paths.isEmpty()) {
//                pathMap = new HashMap<String, FineBusinessTableRelationPath>();
//            } else {
//                pathMap = convertToMap(paths);
//            }
//            pathMap.put(path.getPathName(), path);
//            return savePath(pathMap);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean updateConfig(FineBusinessTableRelationPath relationPath) throws FineSaveDAOException, FineUpdateDAOException {
//        Map<String, FineBusinessTableRelationPath> pathMap = null;
//        try {
//            pathMap = convertToMap(getAllConfig());
//            if (pathMap.containsKey(relationPath.getPathName())) {
//                pathMap.put(relationPath.getPathName(), relationPath);
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean removeConfig(FineBusinessTableRelationPath relationPath) throws FineRemoveDAOException {
//        Map<String, FineBusinessTableRelationPath> pathMap = null;
//        try {
//            pathMap = convertToMap(getAllConfig());
//            if (pathMap.containsKey(relationPath.getPathName())) {
//                pathMap.remove(relationPath.getPathName());
//            }
//            savePath(pathMap);
//            return true;
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//            return false;
//        }
//    }
//
//    @Override
//    public boolean removeAllConfig() throws FineRemoveDAOException {
//        try {
//            Map<String, FineBusinessTableRelationPath> pathMap = new HashMap<String, FineBusinessTableRelationPath>();
//            return savePath(pathMap);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return false;
//    }
//
//    @Override
//    public List<FineBusinessTableRelationPath> getAllConfig() throws FineSelectDAOException {
//        try {
//            Map<String, FineBusinessTableRelationPath> map = objectMapper.readValue(makeSureXmlFileExist(), Map.class);
//            List<FineBusinessTableRelationPath> result = new ArrayList<FineBusinessTableRelationPath>();
//            Iterator<String> it = map.keySet().iterator();
//            while (it.hasNext()) {
//                String pathName = it.next();
//                result.add(map.get(pathName));
//            }
//            return result;
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return new ArrayList<FineBusinessTableRelationPath>();
//    }
//
//    @Override
//    public FineEngineType getEngineType() {
//        return FineEngineType.NONE;
//    }
//
//    private boolean savePath(Map<String, FineBusinessTableRelationPath> pathMap) throws Exception {
//        try {
//            objectMapper.writeValue(makeSureXmlFileExist(), pathMap);
//            return true;
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//            return false;
//        }
//    }
//
//    /**
//     * 判断文件是否存在，不存在的话，创建一个文件
//     */
//    private File makeSureXmlFileExist() {
//        String path = this.getClass().getResource("/").getPath();
//        File tableFile = new File(path + File.separator + XML_FILE_NAME);
//        if (!tableFile.exists()) {
//            FileUtils.createFile(tableFile);
//        }
//        return tableFile;
//    }
//
//    private Map<String, FineBusinessTableRelationPath> convertToMap(List<FineBusinessTableRelationPath> pathList) {
//        Map<String, FineBusinessTableRelationPath> paths = new HashMap<String, FineBusinessTableRelationPath>();
//        for (FineBusinessTableRelationPath path : pathList) {
//            paths.put(path.getPathName(), path);
//        }
//        return paths;
//    }
//
//    @Override
//    public boolean saveConfigs(List<FineBusinessTableRelationPath> businessConfigList) throws Exception {
//        return false;
//    }
//
//    @Override
//    public boolean updateConfigs(List<FineBusinessTableRelationPath> businessConfigList) throws Exception {
//        return false;
//    }
//
//    @Override
//    public boolean removeConfigs(List<FineBusinessTableRelationPath> businessConfigList) throws Exception {
//        return false;
//    }
//}
