package com.fr.swift.beans.factory.init;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * This class created on 2019/8/12
 *
 * @author Krysta
 * @description
 * */
public class TreeUtils {

    private static List<SwiftBeanDefinition> path = new ArrayList<>();

    /*
     * tree表示整棵树的根节点，root是子树的根节点
     * 采用深度优先搜索tree的叶子，查看是否与root相符合。
     * 如果有循环依赖，叶子的 circle=true，并不会把该节点与叶子相融，此时这个叶子停止生长
     * */
    public static void buildTree(DependencyTreeNode tree, DependencyTreeNode root) {
        if (tree == null) return;

        for (DependencyTreeNode node : tree.next) {
            buildTree(node, root);
        }

        if (tree.getWrapperDefinition().equals(root.getWrapperDefinition())) {
            tree.replaceNode(root);
        }

    }


    /*
     * 判断循环依赖，采用深度优先搜索，判断一条路径中有没有重复的元素
     * */

    public static void isCircle(DependencyTreeNode root) {
        if (root == null) return;
        path.add(root.getWrapperDefinition().definition); //将节点的SwiftBeanDefinition信息加入到路径中

        for (DependencyTreeNode node : root.next) {
            isCircle(node);
        }
        if (!path.isEmpty()) { //因为有的bean中没有autowired，所以没有next，自然就需要判空
            Set<SwiftBeanDefinition> set = new HashSet<>();
            SwiftBeanDefinition definition = null;

            for (SwiftBeanDefinition swiftBeanDefinition : path) {
                if (set.contains(swiftBeanDefinition)) {
                    definition = swiftBeanDefinition;
                } else {
                    set.add(swiftBeanDefinition);
                }
            }

            if (definition != null) { //说明有重复的节点
                Crasher.crash(definition.getClazz().getName() + " has a dead circle dependency");
            }

            path.remove(path.size() - 1); //将最后的叶子节点删除，用于判断另一个调用链
        }
    }


}
