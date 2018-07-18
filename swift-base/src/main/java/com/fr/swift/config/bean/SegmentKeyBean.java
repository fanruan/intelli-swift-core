package com.fr.swift.config.bean;

import com.fr.stable.StringUtils;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftPathService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Strings;

import java.io.Serializable;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SegmentKeyBean implements Serializable, Convert<SwiftSegmentEntity>, SegmentKey {
    private static final long serialVersionUID = 3202594634845509238L;
    private transient SwiftPathService service = SwiftContext.get().getBean(SwiftPathService.class);
    private transient SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
    /**
     * sourceKey@storeType@order
     */
    private String id;
    private String sourceKey;
    private URI absoluteUri;
    private URI uri;
    private Integer order;
    private SwiftDatabase.Schema swiftSchema;
    private Types.StoreType storeType;

    public SegmentKeyBean(String sourceKey, URI uri, int order, Types.StoreType storeType, SwiftDatabase.Schema schema) {
        this.sourceKey = sourceKey;
        this.uri = uri;
        this.order = order;
        this.storeType = storeType;
        this.id = toString();
        this.swiftSchema = schema;
        String path = service.getSwiftPath();
        initAbsoluteUri(path);
    }

    public SegmentKeyBean() {
    }

    private void initAbsoluteUri(String path) {
        path = Strings.trimSeparator(path, "\\", "/");
        path = "/" + path + "/";
        absoluteUri = URI.create(Strings.trimSeparator(path, "/")).resolve(uri);
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    @Override
    public SourceKey getTable() {
        if (sourceKey == null) {
            return null;
        }
        return new SourceKey(sourceKey);
    }

    @Override
    public URI getAbsoluteUri() {
        if (null == absoluteUri) {
            initAbsoluteUri(service.getSwiftPath());
        }
        return absoluteUri;
    }

    public void setAbsoluteUri(URI absoluteUri) {
        this.absoluteUri = absoluteUri;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public Integer getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public Types.StoreType getStoreType() {
        return storeType;
    }

    @Override
    public SwiftDatabase.Schema getSwiftSchema() {
        return null;
    }



    public void setStoreType(Types.StoreType storeType) {
        this.storeType = storeType;
    }

    public String getId() {
        if (StringUtils.isEmpty(id)) {
            id = toString();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public SwiftSegmentEntity convert() {
        SwiftSegmentEntity entity = new SwiftSegmentEntity();
        entity.setId(id);
        entity.setSegmentOwner(sourceKey);
        entity.setSegmentOrder(order);
        entity.setStoreType(storeType);
        entity.setSegmentUri(uri);
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SegmentKeyBean that = (SegmentKeyBean) o;

        if (sourceKey != null ? !sourceKey.equals(that.sourceKey) : that.sourceKey != null) {
            return false;
        }
        if (order != null ? !order.equals(that.order) : that.order != null) {
            return false;
        }
        return storeType == that.storeType;
    }

    @Override
    public int hashCode() {
        int result = sourceKey != null ? sourceKey.hashCode() : 0;
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (storeType != null ? storeType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return sourceKey + '@' + storeType + "@" + order;
    }
}
