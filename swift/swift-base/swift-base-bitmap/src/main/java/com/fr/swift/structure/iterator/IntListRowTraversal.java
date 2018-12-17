package com.fr.swift.structure.iterator;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.structure.array.IntList;

public class IntListRowTraversal implements RowTraversal {
    private IntList list;

    public IntListRowTraversal(IntList list) {
        this.list = list;
    }

    @Override
    public void traversal(TraversalAction action) {
        for (int i = 0; i < list.size(); i++) {
            action.actionPerformed(list.get(i));
        }
    }

    @Override
    public boolean breakableTraversal(BreakTraversalAction action) {
        for (int i = 0; i < list.size(); i++) {
            if (action.actionPerformed(list.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCardinality() {
        return list.size();
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ImmutableBitMap toBitMap() {
        MutableBitMap bitMap = RoaringMutableBitMap.of();
        for (int i = 0; i < list.size(); i++) {
            bitMap.add(list.get(i));
        }
        return bitMap;
    }

    public IntList getList() {
        return list;
    }
}
