package com.fr.swift.core.rpc;


import com.fr.swift.basic.Destination;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRDestination implements Destination {

    private String destNodeId;

    public FRDestination(String destNodeId) {
        this.destNodeId = destNodeId;
    }

    @Override
    public String getId() {
        return destNodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FRDestination that = (FRDestination) o;

        return destNodeId != null ? destNodeId.equals(that.destNodeId) : that.destNodeId == null;
    }

    @Override
    public int hashCode() {
        return destNodeId != null ? destNodeId.hashCode() : 0;
    }
}
