package com.fr.swift.cloud.config.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.config.SwiftConfigConstants;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentSource;
import com.fr.swift.cloud.source.SourceKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yee
 * @date 2018/5/24
 */
@SuppressWarnings("all")
@JsonIgnoreProperties({"table", "order"})
@Entity
@Table(name = "fine_swift_segments")
public class SwiftSegmentEntity implements Serializable, SegmentKey {
    private static final long serialVersionUID = -3997047283578403498L;

    @JsonProperty("id")
    @Id
    private String id;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER)
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER)
    private String segmentOwner;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER)
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER)
    private int segmentOrder;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_URI)
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_URI)
    private String segmentUri;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE)
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE)
    @Enumerated(EnumType.STRING)
    private Types.StoreType storeType;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.COLUMN_SWIFT_SCHEMA)
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_SWIFT_SCHEMA)
    @Enumerated(EnumType.STRING)
    private SwiftDatabase swiftSchema;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.CREATE_TIME)
    @Column(name = SwiftConfigConstants.SegmentConfig.CREATE_TIME)
    private Date createTime;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.FINISH_TIME)
    @Column(name = SwiftConfigConstants.SegmentConfig.FINISH_TIME)
    private Date finishTime;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_SOURCE)
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_SOURCE)
    @Enumerated(EnumType.STRING)
    private SegmentSource segmentSource;

    @JsonProperty(SwiftConfigConstants.SegmentConfig.COLUMN_ROWS)
    @Column(name = SwiftConfigConstants.SegmentConfig.COLUMN_ROWS, columnDefinition = "INT default -1")
    private int rows;

    public SwiftSegmentEntity() {
    }

    public SwiftSegmentEntity(SegmentKey segKey) {
        this(segKey.getTable(), segKey.getOrder(), segKey.getStoreType(), segKey.getSwiftSchema(), segKey.segmentSource(), segKey.getSegmentUri());
    }

    public SwiftSegmentEntity(SourceKey segmentOwner, int segmentOrder, Types.StoreType storeType, SwiftDatabase swiftSchema) {
        this(segmentOwner, segmentOrder, storeType, swiftSchema, SegmentSource.CREATED, "0");
    }

    public SwiftSegmentEntity(SourceKey segmentOwner, int segmentOrder, Types.StoreType storeType, SwiftDatabase swiftSchema, String segmentUri) {
        this(segmentOwner, segmentOrder, storeType, swiftSchema, SegmentSource.CREATED, segmentUri);
    }

    public SwiftSegmentEntity(SourceKey segmentOwner, int segmentOrder, Types.StoreType storeType, SwiftDatabase swiftSchema, SegmentSource segmentSource, String segmentUri) {
        id = getId(segmentOwner, segmentOrder, storeType);
        this.segmentOwner = segmentOwner.getId();
        this.segmentOrder = segmentOrder;
        this.storeType = storeType;
        this.swiftSchema = swiftSchema;
        this.segmentUri = segmentUri;
        this.createTime = new Date();
        this.segmentSource = segmentSource;
    }

    public static String getHistoryId(String segmentOwner, int segmentOrder) {
        return getId(new SourceKey(segmentOwner), segmentOrder, Types.StoreType.FINE_IO);
    }

    public static String getId(SourceKey segmentOwner, int segmentOrder, Types.StoreType storeType) {
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
    public Types.StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(Types.StoreType storeType) {
        this.storeType = storeType;
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
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public Date getFinishTime() {
        return finishTime;
    }

    @Override
    public void markFinish(int rows) {
        this.finishTime = new Date();
        this.rows = rows;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public SegmentSource segmentSource() {
        return segmentSource;
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
