package com.fr.swift.adaptor.widget.target.exception;

import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.fr.swift.structure.graph.Digraph;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.List;

/**
 * Created by Lyon on 2018/5/1.
 */
public class TargetCircularDependencyException extends Exception {

    private String message;

    public TargetCircularDependencyException(Digraph<String> digraph, final AbstractTableWidget widget) {
        List<String> cycle = digraph.cycle();
        List<String> fieldNames = IteratorUtils.iterator2List(new MapperIterator<String, String>(cycle.iterator(), new Function<String, String>() {
            @Override
            public String apply(String p) {
                return widget.getFieldByFieldId(p).getName();
            }
        }));
        this.message = "there is Circular Dependency: " + fieldNames.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
