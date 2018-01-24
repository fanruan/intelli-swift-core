//package com.fr.swift.conf.business.relation;
//
//import com.finebi.base.constant.FineEngineType;
//import com.finebi.base.utils.data.io.FileUtils;
//import com.finebi.conf.exception.dao.FineRemoveDAOException;
//import com.finebi.conf.exception.dao.FineSaveDAOException;
//import com.finebi.conf.exception.dao.FineSelectDAOException;
//import com.finebi.conf.exception.dao.FineUpdateDAOException;
//import com.finebi.conf.service.dao.provider.BusinessConfigDAO;
//import com.finebi.conf.structure.relation.FineBusinessTableRelation;
//import com.finebi.log.BILogger;
//import com.finebi.log.BILoggerFactory;
//import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
//import com.fr.third.springframework.stereotype.Component;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
///**
// * This class created on 2018/1/11
// *
// * @author Each.Zhang
// */
//@Component("fineXmlBusinessRelationDAO")
//public class FineXmlBusinessRelationDAO implements BusinessConfigDAO<FineBusinessTableRelation> {
//    private final static String XML_FILE_NAME = "relation.xml";
//    BILogger LOGGER = BILoggerFactory.getLogger(FineXmlBusinessRelationDAO.class);
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public boolean saveConfig(FineBusinessTableRelation tableRelation) throws FineSaveDAOException {
//        List<FineBusinessTableRelation> relations = null;
//        try {
//            relations = getAllConfig();
//            Map<String, FineBusinessTableRelation> relationMap = null;
//            if (relations == null || relations.isEmpty()) {
//                relationMap = new HashMap<String, FineBusinessTableRelation>();
//            } else {
//                relationMap = convertToMap(relations);
//            }
//            relationMap.put(tableRelation.getRelationName(), tableRelation);
//            return saveRelations(relationMap);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean updateConfig(FineBusinessTableRelation tableRelation) throws FineSaveDAOException, FineUpdateDAOException {
//        Map<String, FineBusinessTableRelation> relationMap = null;
//        try {
//            relationMap = convertToMap(getAllConfig());
//            if (relationMap.containsKey(tableRelation.getRelationName())) {
//                relationMap.put(tableRelation.getRelationName(), tableRelation);
//                return saveRelations(relationMap);
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean removeConfig(FineBusinessTableRelation tableRelation) throws FineRemoveDAOException {
//        Map<String, FineBusinessTableRelation> relationMap = null;
//        try {
//            relationMap = convertToMap(getAllConfig());
//            if (relationMap.containsKey(tableRelation.getRelationName())) {
//                relationMap.remove(tableRelation.getRelationName());
//            }
//            saveRelations(relationMap);
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
//            saveRelations(new HashMap<String, FineBusinessTableRelation>());
//            return true;
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return false;
//    }
//
//    @Override
//    public List<FineBusinessTableRelation> getAllConfig() throws FineSelectDAOException {
//        try {
//            Map<String, FineBusinessTableRelation> map = objectMapper.readValue(makeSureXmlFileExist(), Map.class);
//            List<FineBusinessTableRelation> result = new ArrayList<FineBusinessTableRelation>();
//            Iterator<String> it = map.keySet().iterator();
//            while (it.hasNext()) {
//                String relationName = it.next();
//                result.add(map.get(relationName));
//            }
//            return result;
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return new ArrayList<FineBusinessTableRelation>();
//    }
//
//    @Override
//    public FineEngineType getEngineType() {
//        return FineEngineType.NONE;
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
//    private boolean saveRelations(Map<String, FineBusinessTableRelation> relationMap) throws Exception {
//        try {
//            objectMapper.writeValue(makeSureXmlFileExist(), relationMap);
//            return true;
//        } catch (IOException e) {
//            LOGGER.error(e.getMessage(), e);
//            return false;
//        }
//    }
//
//    private Map<String, FineBusinessTableRelation> convertToMap(List<FineBusinessTableRelation> relationList) {
//        Map<String, FineBusinessTableRelation> relations = new HashMap<String, FineBusinessTableRelation>();
//        for (FineBusinessTableRelation relation : relationList) {
//            relations.put(relation.getRelationName(), relation);
//        }
//        return relations;
//    }
//
//    @Override
//    public boolean saveConfigs(List<FineBusinessTableRelation> businessConfigList) throws Exception {
//        return false;
//    }
//
//    @Override
//    public boolean updateConfigs(List<FineBusinessTableRelation> businessConfigList) throws Exception {
//        return false;
//    }
//
//    @Override
//    public boolean removeConfigs(List<FineBusinessTableRelation> businessConfigList) throws Exception {
//        return false;
//    }
//}
