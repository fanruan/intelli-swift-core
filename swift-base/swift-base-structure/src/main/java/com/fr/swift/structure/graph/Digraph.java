package com.fr.swift.structure.graph;

import java.util.List;

/**
 * 不带权重的有向图接口
 * <p>
 * Created by Lyon on 2018/4/30.
 */
public interface Digraph<Vertex> {

    /**
     * 当有向图的顶点数
     *
     * @return
     */
    int verticesCount();

    /**
     * 当有向图的边数
     *
     * @return
     */
    int edgesCount();

    /**
     * 添加有向边
     *
     * @param v 起点
     * @param w 终点
     */
    void addEdge(Vertex v, Vertex w);

    /**
     * 有向图的顶点的集合
     *
     * @return
     */
    List<Vertex> vertices();

    /**
     * 返回顶点v的邻接顶点（v -> *)
     *
     * @param v 顶点
     * @return
     */
    Iterable<Vertex> adjacent(Vertex v);

    /**
     * 顶点v的入度
     *
     * @param v
     * @return
     */
    int inDegree(Vertex v);

    /**
     * 顶点v的出度
     *
     * @param v
     * @return
     */
    int outDegree(Vertex v);

    /**
     * 有向图中是否存在环
     *
     * @return
     */
    boolean hasCycle();

    /**
     * 有向图中的一个环（检测到第一个环，可能有多个环）
     *
     * @return
     */
    List<Vertex> cycle();

    /**
     * 反转有向图
     *
     * @return
     */
    Digraph<Vertex> reverse();
}
