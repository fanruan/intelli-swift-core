package com.fr.swift.query.post.meta;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.SwiftMetaData;

/**
 * Created by lyon on 2018/11/28.
 */
public interface MetaDataCreator<T extends QueryBean> {

    SwiftMetaData create(T queryBean) throws SwiftMetaDataException;
}
