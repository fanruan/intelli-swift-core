package com.fr.swift.conf.business;

import com.finebi.base.common.resource.FineResourceItem;
import com.finebi.base.constant.FineEngineType;
import com.finebi.base.utils.data.io.FileUtils;
import com.finebi.conf.service.dao.provider.BusinessConfigDAO;
import com.fr.general.ComparatorUtils;
import com.fr.swift.conf.business.container.ResourceContainer;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2018-1-23 15:42:55
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class SwiftBaseXmlDao<T extends FineResourceItem> implements BusinessConfigDAO<T> {

    private AbstractSwiftParseXml<T> xmlHandler;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftBaseXmlDao.class);
    private String xmlFileName;
    protected ResourceContainer<T> resourceContainer;

    private ISwiftXmlWriter<T> swiftXmlWriter;

    public SwiftBaseXmlDao(AbstractSwiftParseXml<T> xmlHandler, String xmlFileName, ISwiftXmlWriter<T> swiftXmlWriter) {
        this.xmlHandler = xmlHandler;
        this.xmlFileName = xmlFileName;
        this.swiftXmlWriter = swiftXmlWriter;
    }

    @Override
    public boolean saveConfig(T resource) {
        List<T> resourceList = new ArrayList<T>();
        resourceList.add(resource);
        return saveConfigs(resourceList);
    }

    @Override
    public boolean saveConfigs(List<T> resourceList) {
        try {
            List<T> resouceList = getAllConfig();
            resouceList.addAll(resourceList);
            saveResources(resouceList);
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateConfig(T resource) {
        List<T> resourceList = new ArrayList<T>();
        resourceList.add(resource);
        return updateConfigs(resourceList);
    }

    @Override
    public boolean updateConfigs(List<T> resourceList) {
        try {
            List<T> resources = getAllConfig();
            Iterator<T> it = resources.iterator();
            while (it.hasNext()) {
                T fineResource = it.next();
                for (T resource : resourceList) {
                    if (ComparatorUtils.equals(resource.getId(), fineResource.getId())) {
                        it.remove();
                        break;
                    }
                }
            }
            resources.addAll(resourceList);
            saveResources(resources);
            return true;
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean removeConfig(T resource) {
        List<T> resourceList = new ArrayList<T>();
        resourceList.add(resource);
        return removeConfigs(resourceList);
    }

    @Override
    public boolean removeConfigs(List<T> resourceList) {
        try {
            List<T> resources = getAllConfig();
            Iterator<T> it = resources.iterator();
            while (it.hasNext()) {
                T fineResource = it.next();
                for (T resource : resourceList) {
                    if (ComparatorUtils.equals(resource.getId(), fineResource.getId())) {
                        it.remove();
                        break;
                    }
                }
            }
            saveResources(resources);
            return true;
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean removeAllConfig() {

        try {
            List<T> resources = new ArrayList<T>();
            saveResources(resources);
        } catch (Exception e) {
            LOGGER.info("rm all get pack", e);
        }
        return false;
    }

    @Override
    public List<T> getAllConfig() {
//        SAXParser parser = null;
//        try {
//            parser = SAXParserFactory.newInstance().newSAXParser();
//            parser.parse(getInputStream(), xmlHandler);
//        } catch (Exception e) {
//            LOGGER.info("error get all pack", e);
//        }
//        List<T> ret = new ArrayList<T>();
//        try {
//            for (T pack : xmlHandler.getList()) {
//                ret.add(pack);
//            }
//        } catch (Exception e) {
//
//        }
//        return new ArrayList<T>(ret);
        return resourceContainer.getResources();
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    protected void saveResources(List<T> resources) throws Exception {
//        swiftXmlWriter.write(resources, getOutPutStream());
        resourceContainer.saveResources(resources);
    }

    protected OutputStream getOutPutStream() throws Exception {
        makeSureXmlFileExist();
        ClassPathResource resource = new ClassPathResource(xmlFileName);
        OutputStream out = new FileOutputStream(resource.getFile());
        return out;
    }

    protected File makeSureXmlFileExist() {
        String path = this.getClass().getResource("/").getPath();
        File tableFile = new File(path + File.separator + xmlFileName);
        if (!tableFile.exists()) {
            FileUtils.createFile(tableFile);
        }
        return tableFile;
    }

    protected InputStream getInputStream() throws Exception {
        makeSureXmlFileExist();
        ClassPathResource resource = new ClassPathResource(xmlFileName);
        return resource.getInputStream();
    }
}
