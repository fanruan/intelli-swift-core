package com.fr.bi.stable.structure.tree;

import com.fr.bi.stable.structure.Node;
import com.fr.bi.stable.structure.Root;
import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by GUY on 2015/4/22.
 */
public class Tree implements Release {
    public Node root = new Root(null);

    public Tree() {
    }

    public Node getRoot() {
        return root;
    }

    public <K extends Node<?>> K addNode(K node, K newNode) {
        if (null == node) {
            root.addChild(newNode);
        } else {
            node.addChild(newNode);
        }
        return newNode;
    }

    /**
     * 获取下一个节点
     *
     * @param node
     * @param checker 检查某个节点能否展开，若能展开则展开
     * @return
     */
    public <K extends Node<?>> K getNextNode(K node, BrokenTraversal<K> checker) {

        K next = node.getRight();
        K par = node.getParent();
        while (par != null && next == null) {
            next = par.getRight();
            par = par.getParent();
        }
        //先把下一节点展开到叶子节点
        Queue<K> queue = new LinkedBlockingQueue<K>();
        queue.offer(next);
        while (!queue.isEmpty()) {
            K t = queue.poll();
            if (checker.actionPerformed(t)) {
                //说明能展开就展开该节点，如果没有则创建
                queue.offer(t.<K>getFirstChild());
                next = t.getFirstChild();
            }
        }
        return next;
    }

    /**
     * 获取上一个节点
     *
     * @param node
     * @param checker 检查某个节点能否展开，若能展开则展开
     * @return
     */
    public <K extends Node<?>> K getLastNode(K node, BrokenTraversal<K> checker) {

        K last = node.getLeft();
        K par = node.getParent();
        while (par != null && last == null) {
            last = par.getLeft();
            par = par.getParent();
        }
        //先把上一节点展开到叶子节点
        Queue<K> queue = new ConcurrentLinkedQueue<K>();
        queue.offer(last);
        while (!queue.isEmpty()) {
            K t = queue.poll();
            if (checker.actionPerformed(t)) {
                //说明能展开就展开该节点，如果没有则创建
                queue.offer(t.<K>getLastChild());
                last = t.getLastChild();
            }
        }
        return last;
    }

    public <K extends Node<?>> K getNode(Object node) {
        return search(root, node);
    }


    /**
     * 查找node节点
     *
     * @param root
     * @param node
     * @return
     */
    public <K extends Node<?>> K search(Node root, Object node) {

        K next = null;

        if (ComparatorUtils.equals(root.getValue(), node)) {
            return (K) root;
        }

        Iterator it = root.getChildIterator();
        while (it.hasNext()) {
            next = search((K) it.next(), node);

            if (null != next) {
                break;
            }
        }
        return next;
    }

    public <K extends Node<?>> void showNode(K node) {
        if (null != node) {
            //循环遍历node的节点
            BILogger.getLogger().info(node.getValue().toString());
            Iterator it = node.getChildIterator();
            while (it.hasNext()) {
                showNode((Node) it.next());
            }
        }
    }

    @Override
    public void clear() {
        root.clear();
    }
}