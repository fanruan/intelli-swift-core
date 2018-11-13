package com.fr.swift.query.filter.match;

import com.fr.swift.result.SwiftNode;

import java.util.List;

/**
 * Created by pony on 2018/4/17.
 */
public class GeneralAndMatchFilter extends AbstractGeneralMatchFilter {
    public GeneralAndMatchFilter(List<MatchFilter> children) {
        super(children);
    }

    @Override
    public boolean matches(SwiftNode node) {
        if (children != null) {
            for (MatchFilter filter : children) {
                if (!filter.matches(node)) {
                    return false;
                }
            }
        }
        return children != null || !children.isEmpty();
    }
}
