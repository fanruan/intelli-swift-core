package com.finebi.common.persist.dom;

import com.finebi.common.resource.ResourcePool;
import org.w3c.dom.Document;

/**
 * This class created on 2017/7/6.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

public interface DomSpecifiedReader<Pool extends ResourcePool> {
    /**
     * @return 目标代码的版本，默认当前的代码版本
     */
    String getTargetCodeVersion();

    /**
     * @return 存储的版本
     */
    String getXMLPatternVersion();

    Pool read(Document doc);
}
