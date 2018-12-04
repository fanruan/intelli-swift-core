package com.fr.swift.segment.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fr.swift.query.Queryable;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.bean.impl.SegmentDestinationImpl;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/13
 */
@JsonDeserialize(as = SegmentDestinationImpl.class)
public interface SegmentDestinationBean extends SegmentDestination {
}
