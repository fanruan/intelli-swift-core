package com.fr.swift.config.entity;

import com.fr.swift.source.SourceKey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author lucifer
 * @date 2019-06-24
 * @description
 * @since advanced swift 1.0
 */
@Entity
@Table(name = "fine_swift_seg_bucket")
public class SwiftSegmentBucketElement implements Serializable {

    private static final long serialVersionUID = 7429912466423011006L;
    @Id
    private UnionKey unionKey;

    private SwiftSegmentBucketElement() {
    }

    public SwiftSegmentBucketElement(String sourceKey, int bucketIndex, String realSegmentKey) {
        unionKey = new UnionKey(sourceKey, bucketIndex, realSegmentKey);
    }

    public SwiftSegmentBucketElement(SourceKey sourceKey, int bucketIndex, String realSegmentKey) {
        this(sourceKey.getId(), bucketIndex, realSegmentKey);
    }

    public String getSourceKey() {
        return unionKey.getSourceKey();
    }

    public int getBucketIndex() {
        return unionKey.getBucketIndex();
    }

    public String getRealSegmentKey() {
        return unionKey.getRealSegmentKey();
    }

    @Override
    public String toString() {
        return unionKey.toString();
    }

    @Embeddable
    private static class UnionKey implements Serializable {
        private static final long serialVersionUID = -8244262801313790473L;
        @Column(name = "sourceKey")
        private String sourceKey;

        @Column(name = "bucketIndex")
        private int bucketIndex;

        @Column(name = "realSegmentKey")
        private String realSegmentKey;

        private UnionKey() {

        }

        public UnionKey(String sourceKey, int bucketIndex, String realSegmentKey) {
            this.sourceKey = sourceKey;
            this.bucketIndex = bucketIndex;
            this.realSegmentKey = realSegmentKey;
        }

        public String getSourceKey() {
            return sourceKey;
        }

        public int getBucketIndex() {
            return bucketIndex;
        }

        public String getRealSegmentKey() {
            return realSegmentKey;
        }

        @Override
        public String toString() {
            return "{" +
                    "sourceKey='" + sourceKey + '\'' +
                    ", bucketIndex=" + bucketIndex +
                    ", realSegmentKey='" + realSegmentKey + '\'' +
                    '}';
        }
    }
}