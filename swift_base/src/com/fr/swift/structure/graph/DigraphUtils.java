package com.fr.swift.structure.graph;

import com.fr.swift.structure.iterator.IteratorUtils;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Created by Lyon on 2018/5/1.
 */
public class DigraphUtils {

    /**
     * 有向图的拓扑排序
     *
     * @param digraph 无环有向图
     * @param <T>
     * @return
     */
    public static <T> List<T> topologicalOrder(Digraph<T> digraph) {
        List<T> postOrder = new PostOrder<T>(digraph).postOrder();
        // 反转后续顺序就是无环有向图的拓扑排序
        Collections.reverse(postOrder);
        return postOrder;
    }

    private static class PostOrder<T> {
        // 被标记的顶点
        private Set<T> marked;
        // 用于保存顶点的队列
        private Queue<T> queue;

        public PostOrder(Digraph<T> digraph) {
            this.marked = new HashSet<T>();
            this.queue = new ArrayDeque<T>();
            init(digraph);
        }

        private void init(Digraph<T> digraph) {
            for (T v : digraph.vertices()) {
                if (!marked.contains(v)) {
                    deepFirstTraversal(digraph, v);
                }
            }
        }

        private void deepFirstTraversal(Digraph<T> digraph, T vertex) {
            marked.add(vertex);
            for (T v : digraph.adjacent(vertex)) {
                if (!marked.contains(v)) {
                    deepFirstTraversal(digraph, v);
                }
            }
            // 递归调用之后再把顶点扔进队列
            queue.add(vertex);
        }

        public List<T> postOrder() {
            return IteratorUtils.iterator2List(queue.iterator());
        }
    }
}
