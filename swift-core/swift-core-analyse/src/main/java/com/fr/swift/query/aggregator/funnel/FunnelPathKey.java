package com.fr.swift.query.aggregator.funnel;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.info.funnel.FunnelVirtualStep;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-12
 */
public class FunnelPathKey implements Serializable, Comparable<FunnelPathKey> {
    private static final long serialVersionUID = 5665160665150096552L;
    private List<FunnelVirtualStep> paths;
    private final String path;

    public FunnelPathKey(List<FunnelVirtualStep> paths) {
        this.paths = paths;
        this.path = init();
    }

    private String init() {
        StringBuilder stringBuilder = new StringBuilder();
        for (FunnelVirtualStep path : paths) {
            stringBuilder.append(path.getName()).append("->");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public int size() {
        return paths.size();
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunnelPathKey)) return false;

        FunnelPathKey key = (FunnelPathKey) o;

        return path != null ? path.equals(key.path) : key.path == null;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public int compareTo(FunnelPathKey o) {
        return Comparators.STRING_ASC.compare(path, o.path);
    }
}
