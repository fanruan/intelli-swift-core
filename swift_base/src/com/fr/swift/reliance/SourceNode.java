package com.fr.swift.reliance;

import com.fr.swift.increment.Increment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/4/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SourceNode {

    private SourceKey sourceKey;

    private DataSource node;

    private List<SourceNode> next;

    private boolean hasPrev = false;

    private boolean isIncrement = false;

    private Increment increment;

    public SourceNode(DataSource node) {
        this.node = node;
        this.sourceKey = node.getSourceKey();
        this.next = new ArrayList<SourceNode>();
    }

    public SourceNode(DataSource node, Increment increment) {
        this(node);
        this.increment = increment;
        if (this.increment != null) {
            this.isIncrement = true;
        }
    }

    public SourceKey getSourceKey() {
        return sourceKey;
    }

    public DataSource getNode() {
        return node;
    }

    public void addNext(SourceNode nextNode) {
        this.next.add(nextNode);
    }

    public List<SourceNode> next() {
        return next;
    }

    public boolean hasNext() {
        return next != null && !next.isEmpty();
    }

    public void setHasPrev() {
        this.hasPrev = true;
    }

    public boolean hasPrev() {
        return hasPrev;
    }

    public boolean isIncrement() {
        return isIncrement;
    }

    public Increment getIncrement() {
        return increment;
    }
}
