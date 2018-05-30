package com.fr.swift.config.entity;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.Convert;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.convert.StoreTypeConverter;
import com.fr.swift.config.convert.URIConverter;
import com.fr.swift.cube.io.Types;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/24
 */
@Entity
@Table(name = "FINE_SWIFT_SEGMENTS")
public class SwiftSegmentEntity implements Convert<SegmentKeyBean> {

    @Id
    private String id;

    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER)
    private String segmentOwner;
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_URI, unique = true, length = SwiftConfigConstants.LONG_TEXT_LENGTH)
    @com.fr.third.javax.persistence.Convert(
            converter = URIConverter.class
    )
    private URI segmentUri;
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER)
    private int segmentOrder;
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE)
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public SegmentKeyBean convert() {
        return new SegmentKeyBean(segmentOwner, segmentUri, segmentOrder, storeType);
    }
}
