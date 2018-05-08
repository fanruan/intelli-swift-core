package com.fr.swift.query.group.by2.row;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.GroupByController;
import com.fr.swift.query.group.by2.PopUpCallback;

import java.util.List;

/**
 * Created by Lyon on 2018/4/26.
 */
public class ExpandAllController implements GroupByController<GroupByEntry> {

    @Override
    public boolean isRow(List<GroupByEntry> entries, PopUpCallback callback) {
        return entries.size() == 0 || entries.get(entries.size() - 1) != null;
    }
}
