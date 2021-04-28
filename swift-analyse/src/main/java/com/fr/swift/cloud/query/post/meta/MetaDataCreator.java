package com.fr.swift.cloud.query.post.meta;

import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * Created by lyon on 2018/11/28.
 */
public interface MetaDataCreator<T extends QueryBean> {

    SwiftMetaData create(T queryBean) throws SwiftMetaDataException;
}
