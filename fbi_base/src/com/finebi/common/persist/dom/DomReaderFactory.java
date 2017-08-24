package com.finebi.common.persist.dom;
/**
 * This class created on 2017/7/6.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.common.resource.ResourcePool;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.HashMap;
import java.util.Map;

public class DomReaderFactory<Pool extends ResourcePool> {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(DomReaderFactory.class);
    private Map<String, DomSpecifiedReader<Pool>> xmlPatternVersionReaders = new HashMap<String, DomSpecifiedReader<Pool>>();

    DomSpecifiedReader<Pool> getDomReader(String version) {
        if (xmlPatternVersionReaders.containsKey(version)) {
            return xmlPatternVersionReaders.get(version);
        } else {
            throw BINonValueUtils.beyondControl("");
        }
    }

    public void register(String xmlPatternVersion, DomSpecifiedReader<Pool> reader) {
        xmlPatternVersionReaders.put(xmlPatternVersion, reader);
    }
}
