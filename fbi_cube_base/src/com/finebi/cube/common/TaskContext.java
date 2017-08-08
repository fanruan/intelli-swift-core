package com.finebi.cube.common;
/**
 * This class created on 2017/7/3.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TaskContext {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(TaskContext.class);
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public Object getParameterValue(String key) {
        return parameters.get(key);
    }

    void insertParameter(String key, Object value) {
        parameters.put(key, value);
    }
}
