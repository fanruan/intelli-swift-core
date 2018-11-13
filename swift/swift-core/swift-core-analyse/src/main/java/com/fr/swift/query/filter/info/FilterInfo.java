package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.core.CoreService;

/**
 * Created by pony on 2017/12/11.
 * 过滤的配置
 * 可以包含多个过滤器
 */
public interface FilterInfo extends CoreService {
    /**
     * 是否是对结果的过滤
     *
     * @return
     */
    boolean isMatchFilter();

    DetailFilter createDetailFilter(Segment segment);

    MatchFilter createMatchFilter();
}
