//package com.fr.swift.result.node.iterator;
//
//import com.fr.swift.result.GroupNode;
//
//import java.util.Iterator;
//
///**
// * SwiftNode接口能直接拿到List了
// * Created by Lyon on 2018/4/4.
// */
//@Deprecated
//public class ChildIterator implements Iterator<GroupNode> {
//
//    private GroupNode next;
//
//    public ChildIterator(GroupNode root) {
//        next = root == null ? null : root.getChild(0);
//    }
//
//    @Override
//    public boolean hasNext() {
//        return next != null;
//    }
//
//    @Override
//    public GroupNode next() {
//        GroupNode old = next;
//        next = next.getSibling();
//        return old;
//    }
//
//    @Override
//    public void remove() {
//        throw new UnsupportedOperationException();
//    }
//}
