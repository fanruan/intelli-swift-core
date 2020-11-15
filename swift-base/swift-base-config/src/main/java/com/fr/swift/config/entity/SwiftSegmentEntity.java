package com.fr.swift.config.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

import static com.fr.swift.config.SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER;
import static com.fr.swift.config.SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER;
import static com.fr.swift.config.SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_SCHEMA;
import static com.fr.swift.config.SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_URI;
import static com.fr.swift.config.SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE;

/**
 * @author yee
 * @date 2018/5/24
 */
@JsonIgnoreProperties({"table", "order"})
@Entity
@Table(name = "fine_swift_segments")
public class SwiftSegmentEntity implements Serializable, SegmentKey {
    private static final long serialVersionUID = -3997047283578403498L;

    @JsonProperty("id")
    @Id
    private String id;

    @JsonProperty(COLUMN_SEGMENT_OWNER)
    @Column(name = COLUMN_SEGMENT_OWNER)
    private String segmentOwner;

    @JsonProperty(COLUMN_SEGMENT_ORDER)
    @Column(name = COLUMN_SEGMENT_ORDER)
    private int segmentOrder;

    @JsonProperty(COLUMN_SEGMENT_URI)
    @Column(name = COLUMN_SEGMENT_URI)
    private String segmentUri;

    @JsonProperty(COLUMN_STORE_TYPE)
    @Column(name = COLUMN_STORE_TYPE)
    @Enumerated(EnumType.STRING)
    private Types.StoreType storeType;

    @JsonProperty(COLUMN_SEGMENT_SCHEMA)
    @Column(name = COLUMN_SEGMENT_SCHEMA)
    @Enumerated(EnumType.STRING)
    private SwiftDatabase swiftSchema;

    public SwiftSegmentEntity() {
    }

    public SwiftSegmentEntity(SegmentKey segKey) {
        this(segKey.getTable(), segKey.getOrder(), segKey.getStoreType(), segKey.getSwiftSchema(), segKey.getSegmentUri());
    }

    public SwiftSegmentEntity(SourceKey segmentOwner, int segmentOrder, StoreType storeType, SwiftDatabase swiftSchema) {
        id = getId(segmentOwner, segmentOrder, storeType);
        this.segmentOwner = segmentOwner.getId();
        this.segmentOrder = segmentOrder;
        this.storeType = storeType;
        this.swiftSchema = swiftSchema;
    }

    public SwiftSegmentEntity(SourceKey segmentOwner, int segmentOrder, StoreType storeType, SwiftDatabase swiftSchema, String segmentUri) {
        id = getId(segmentOwner, segmentOrder, storeType);
        this.segmentOwner = segmentOwner.getId();
        this.segmentOrder = segmentOrder;
        this.storeType = storeType;
        this.swiftSchema = swiftSchema;
        this.segmentUri = segmentUri;
    }

    public static String getHistoryId(String segmentOwner, int segmentOrder) {
        return getId(new SourceKey(segmentOwner), segmentOrder, StoreType.FINE_IO);
    }

    private static String getId(SourceKey segmentOwner, int segmentOrder, StoreType storeType) {
        return String.format("%s@%s@%d", segmentOwner.getId(), storeType, segmentOrder);
    }

    public String getSegmentOwner() {
        return segmentOwner;
    }

    public void setSegmentOwner(String segmentOwner) {
        this.segmentOwner = segmentOwner;
    }

    public int getSegmentOrder() {
        return segmentOrder;
    }

    public void setSegmentOrder(int segmentOrder) {
        this.segmentOrder = segmentOrder;
    }

    @Override
    public SourceKey getTable() {
        return new SourceKey(segmentOwner);
    }

    @Override
    public Integer getOrder() {
        return segmentOrder;
    }

    @Override
    public String getSegmentUri() {
        return segmentUri;
    }

    @Override
    public StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    public SwiftSegmentEntity setSegmentUri(String segmentUri) {
        this.segmentUri = segmentUri;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public SwiftDatabase getSwiftSchema() {
        return swiftSchema;
    }

    public void setSwiftSchema(SwiftDatabase swiftSchema) {
        this.swiftSchema = swiftSchema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SwiftSegmentEntity that = (SwiftSegmentEntity) o;

        if (segmentOrder != that.segmentOrder) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (segmentOwner != null ? !segmentOwner.equals(that.segmentOwner) : that.segmentOwner != null) {
            return false;
        }
        if (storeType != that.storeType) {
            return false;
        }
        return swiftSchema == that.swiftSchema;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (segmentOwner != null ? segmentOwner.hashCode() : 0);
        result = 31 * result + segmentOrder;
        result = 31 * result + (storeType != null ? storeType.hashCode() : 0);
        result = 31 * result + (swiftSchema != null ? swiftSchema.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return id;
    }
}
