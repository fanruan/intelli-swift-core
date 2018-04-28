package com.fr.swift.query.filter.match;

import com.fr.swift.result.SwiftNode;

import java.util.List;

/**
 * Created by pony on 2018/4/17.
 */
public class GeneralOrMatchFilter extends AbstractGeneralMatchFilter {

    public GeneralOrMatchFilter(List<MatchFilter> children) {
        super(children);
    }

    @Override
    public boolean matches(SwiftNode node) {
        if (children != null) {
            for (MatchFilter filter : children) {
                if (filter.matches(node)) {
                    return true;
                }
            }
        }
        return children == null || children.isEmpty();
    }
}
