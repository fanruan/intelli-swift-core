package com.fr.swift.query.builder;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.IndexQuery;
import com.fr.swift.segment.Segment;

import java.net.URI;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface LocalDetailIndexQueryBuilder {

    Map<URI, IndexQuery<ImmutableBitMap>> buildLocalQuery(DetailQueryInfo info);

    IndexQuery<ImmutableBitMap> buildLocalQuery(DetailQueryInfo info, Segment segment);

}
