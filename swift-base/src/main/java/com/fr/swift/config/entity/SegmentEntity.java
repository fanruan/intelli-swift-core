package com.fr.swift.config.entity;

import com.fr.decision.webservice.utils.DecisionServiceConstants;
import com.fr.swift.config.bean.Convert;
import com.fr.swift.config.bean.SegmentBean;
import com.fr.swift.config.convert.StoreTypeConverter;
import com.fr.swift.config.convert.URIConverter;
import com.fr.swift.cube.io.Types;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/24
 */
@Entity
@Table(name = "swift_segments")
public class SegmentEntity extends com.fr.config.entity.Entity implements Convert<SegmentBean> {
    @Column(name = "segmentOwner")
    private String segmentOwner;
    @Column(name = "segmentName")
    private String segmentName;
    @Column(name = "segmentUri", unique = true, length = DecisionServiceConstants.LONG_TEXT_LENGTH)
    @com.fr.third.javax.persistence.Convert(
            converter = URIConverter.class
    )
    private URI segmentUri;
    @Column(name = "segmentOrder")
    private int segmentOrder;
    @Column(name = "storeType")
    @com.fr.third.javax.persistence.Convert(
            converter = StoreTypeConverter.class
    )
    private Types.StoreType storeType;

    public String getSegmentOwner() {
        return segmentOwner;
    }

    public void setSegmentOwner(String segmentOwner) {
        this.segmentOwner = segmentOwner;
    }

    public URI getSegmentUri() {
        return segmentUri;
    }

    public void setSegmentUri(URI segmentUri) {
        this.segmentUri = segmentUri;
    }

    public int getSegmentOrder() {
        return segmentOrder;
    }

    public void setSegmentOrder(int segmentOrder) {
        this.segmentOrder = segmentOrder;
    }

    public Types.StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(Types.StoreType storeType) {
        this.storeType = storeType;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    @Override
    public SegmentBean convert() {
        return new SegmentBean(segmentOwner, segmentName, segmentUri, segmentOrder, storeType);
    }
}
