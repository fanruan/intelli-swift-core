package com.fr.swift.config.conf.entity;

import com.fr.decision.base.entity.BaseEntity;
import com.fr.decision.webservice.utils.DecisionServiceConstants;
import com.fr.swift.config.conf.bean.Convert;
import com.fr.swift.config.conf.bean.SegmentBean;
import com.fr.swift.config.conf.convert.StoreTypeConverter;
import com.fr.swift.config.conf.convert.URIConverter;
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
public class SegmentEntity extends BaseEntity implements Convert<SegmentBean> {
    @Column(name = "sourceKey", nullable = false)
    private String sourceKey;
    @Column(name = "segmentName")
    private String name;
    @Column(name = "uri", nullable = false, unique = true, length = DecisionServiceConstants.LONG_TEXT_LENGTH)
    @com.fr.third.javax.persistence.Convert(
            converter = URIConverter.class
    )
    private URI uri;
    @Column(name = "order")
    private int order;
    @Column(name = "storeType", nullable = false)
    @com.fr.third.javax.persistence.Convert(
            converter = StoreTypeConverter.class
    )
    private Types.StoreType storeType;

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Types.StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(Types.StoreType storeType) {
        this.storeType = storeType;
    }

    @Override
    public SegmentBean convert() {
        return new SegmentBean(sourceKey, name, uri, order, storeType);
    }
}
