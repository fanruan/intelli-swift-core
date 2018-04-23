package com.fr.swift.query.adapter.dimension;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by Lyon on 2018/4/23.
 */
public interface DimensionInfo {

    List<Column> getDimensions();

    DetailFilter getDetailFilter();

    MatchFilter getMatchFilter();

    List<Sort> getSorts();
}
