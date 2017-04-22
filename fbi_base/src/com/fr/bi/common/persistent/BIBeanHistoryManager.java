package com.fr.bi.common.persistent;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neil on 17-3-20.
 */
public class BIBeanHistoryManager {
    private Map<String, List<String>> beanClassMapping = new HashMap<String, List<String>>();
    private static final String DEFAULT_FILE_NAME = "bean_history_class.xml";
    private static final BILogger LOGGER = BILoggerFactory.getLogger(BIBeanHistoryManager.class);

    private BIBeanHistoryManager() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_FILE_NAME);

        if (inputStream == null) {
            LOGGER.error("Bean History xml File not found in path " + this.getClass().getClassLoader().getResource(DEFAULT_FILE_NAME).getPath());
        } else {
            BeanHistoryXMLReader beanHistoryXMLReader = new BeanHistoryXMLReader();
            Map<String, List<String>> beanMapping = beanHistoryXMLReader.loadBeanHistoryMap(inputStream);
            registerBeanHistoryManager(beanMapping);
        }
    }

    private static class BIBeanHistoryManagerSingleton {
        static final BIBeanHistoryManager INSTANCE = new BIBeanHistoryManager();
    }

    public static BIBeanHistoryManager getInstance() {
        return BIBeanHistoryManagerSingleton.INSTANCE;
    }

    public void registerBeanHistoryManager(Map<String, List<String>> beanClassMapping) {
        this.beanClassMapping.putAll(beanClassMapping);
    }

    public String getCurrentClassName(String oldClass) {
        if (beanClassMapping != null) {
            for (Map.Entry<String, List<String>> entry : beanClassMapping.entrySet()) {
                List<String> oldClasses = entry.getValue();
                if (oldClasses.contains(oldClass)) {
                    return entry.getKey();
                }
            }
        }
        return oldClass;
    }

}
