package com.fr.swift.cloud.source.load;

import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.result.SwiftResultSet;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
public interface CloudResultSet extends SwiftResultSet {

    CloudTableType getTableType();
}
