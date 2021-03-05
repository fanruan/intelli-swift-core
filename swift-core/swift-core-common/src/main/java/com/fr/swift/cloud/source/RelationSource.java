package com.fr.swift.cloud.source;


import com.fr.swift.cloud.source.core.CoreService;

import java.util.List;

/**
 * Created by roy on 2017/7/9 .
 */
public interface RelationSource extends Source, CoreService {

    SourceKey getPrimarySource();

    SourceKey getForeignSource();

    List<String> getPrimaryFields();

    List<String> getForeignFields();

    RelationSourceType getRelationType();

}
