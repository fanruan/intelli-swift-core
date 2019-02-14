package com.fr.swift.structure.graph;

import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * 顶点对象必须实现hashCode & equals！
 * <p>
 * Created by Lyon on 2018/4/30.
 */
public class DigraphImpl<Vertex> implements Digraph<Vertex> {

    // 有向图的邻接Map
    private Map<Vertex, List<Vertex>> adjMap = new HashMap<Vertex, List<Vertex>>();
    // 顶点入度的Map
    private Map<Vertex, Integer> inDegreeMap = new HashMap<Vertex, Integer>();
    // 有向边的个数
    private int edgesCount = 0;
    // 顶点的集合
    private Set<Vertex> vertices = new HashSet<Vertex>();

    // 有向环
    private List<Vertex> cycle = new ArrayList<Vertex>(0);

    @Override
    public int verticesCount() {
        return vertices.size();
    }

    @Override
    public int edgesCount() {
        return edgesCount;
    }

    @Override
    public void addEdge(Vertex v, Vertex w) {
        vertices.add(v);
        if (w != null) {
            vertices.add(w);
        }
        if (!adjMap.containsKey(v)) {
            adjMap.put(v, new ArrayList<Vertex>());
        }
        if (w != null && !inDegreeMap.containsKey(w)) {
            inDegreeMap.put(w, 0);
        }
        if (w != null) {
            adjMap.get(v).add(w);
            inDegreeMap.put(w, inDegreeMap.get(w) + 1);
            edgesCount += 1;
        }
    }

    @Override
    public List<Vertex> vertices() {
        return new ArrayList<Vertex>(vertices);
    }

    @Override
    public Iterable<Vertex> adjacent(Vertex v) {
        return adjMap.get(v) == null ? new ArrayList<Vertex>(0) : adjMap.get(v);
    }

    @Override
    public int inDegree(Vertex v) {
        if (!vertices.contains(v)) {
            throw new NoSuchElementException("can not found vertex: " + v.toString() + " in Digraph!");
        }
        return inDegreeMap.get(v) == null ? 0 : inDegreeMap.get(v);
    }

    @Override
    public int outDegree(Vertex v) {
        if (!vertices.contains(v)) {
            throw new NoSuchElementException("can not found vertex: " + v.toString() + " in Digraph!");
        }
        return adjMap.get(v) == null ? 0 : adjMap.get(v).size();
    }

    @Override
    public boolean hasCycle() {
        DirectedCycle<Vertex> directedCycle = new DirectedCycle<Vertex>(this);
        if (directedCycle.hasCycle()) {
            cycle = directedCycle.cycle();
        }
        return !cycle.isEmpty();
    }

    @Override
    public List<Vertex> cycle() {
        if (cycle.isEmpty()) {
            DirectedCycle<Vertex> directedCycle = new DirectedCycle<Vertex>(this);
            if (directedCycle.hasCycle()) {
                cycle = directedCycle.cycle();
            }
        }
        return cycle;
    }

    @Override
    public Digraph<Vertex> reverse() {
        Digraph<Vertex> reverse = new DigraphImpl<Vertex>();
        for (Vertex vertex : vertices) {
            if (!adjacent(vertex).iterator().hasNext()) {
                // 孤立顶点
                reverse.addEdge(vertex, null);
                continue;
            }
            for (Vertex v : adjacent(vertex)) {
                reverse.addEdge(v, vertex);
            }
        }
        return reverse;
    }

    private static class DirectedCycle<Vertex> {
        // 被标记的顶点
        private Set<Vertex> marked;
        // edge2Map.get(v) = 指向v的顶点
        private Map<Vertex, Vertex> edge2Map;
        // 顶点v是否在调用栈中
        private Set<Vertex> onStack;
        // 有向图中的有向环
        private LimitedStack<Vertex> cycle;

        public DirectedCycle(Digraph<Vertex> digraph) {
            marked = new HashSet<Vertex>();
            edge2Map = new HashMap<Vertex, Vertex>();
            onStack = new HashSet<Vertex>();
            cycle = new ArrayLimitedStack<Vertex>(digraph.verticesCount() + 1);
            findCycle(digraph);
        }

        private void findCycle(Digraph<Vertex> digraph) {
            for (Vertex vertex : digraph.vertices()) {
                if (!marked.contains(vertex) && cycle.isEmpty()) {
                    deepFirstTraversal(digraph, vertex);
                }
            }
        }

        private void deepFirstTraversal(Digraph<Vertex> digraph, Vertex vertex) {
            onStack.add(vertex);
            marked.add(vertex);
            for (Vertex v : digraph.adjacent(vertex)) {
                if (!cycle.isEmpty()) {
                    // 说明已经发现有向环
                    return;
                } else if (!marked.contains(v)) {
                    // 顶点v没有被标记。指向v的顶点是vertex(vertex -> v)
                    edge2Map.put(v, vertex);
                    // 深度优先递归
                    deepFirstTraversal(digraph, v);
                } else if (onStack.contains(v)) {
                    // 顶点v已经在调用栈中，说明检测到环了。通过回溯找到环
                    for (Vertex x = vertex; x != v; x = edge2Map.get(x)) {
                        cycle.push(x);
                    }
                    cycle.push(v);
                    cycle.push(vertex);
                }
            }
            // 顶点vertex出栈
            onStack.remove(vertex);
        }

        public boolean hasCycle() {
            return !cycle.isEmpty();
        }

        public List<Vertex> cycle() {
            List<Vertex> vertices = new ArrayList<Vertex>(cycle.size());
            while (!cycle.isEmpty()) {
                vertices.add(cycle.pop());
            }
            return vertices;
        }
    }
}
