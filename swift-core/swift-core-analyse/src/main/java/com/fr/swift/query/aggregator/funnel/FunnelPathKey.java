package com.fr.swift.query.aggregator.funnel;

import com.fr.swift.query.info.funnel.FunnelEventBean;

import java.util.List;

/**
 * @author yee
 * @date 2019-07-12
 */
public class FunnelPathKey {
    private List<FunnelEventBean> paths;
    private String path;

    public FunnelPathKey(List<FunnelEventBean> paths) {
        this.paths = paths;
        StringBuilder stringBuilder = new StringBuilder();
        for (FunnelEventBean path : paths) {
            stringBuilder.append(path.getName()).append("-");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        path = stringBuilder.toString();
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
}
