package com.fr.bi.stable.structure.tree;

import com.fr.bi.stable.structure.Node;
import com.fr.bi.stable.structure.Root;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 二叉树
 * Created by GUY on 2015/4/22.
 */
public class BinaryTree<T> {
    private Root<T> root;

    public BinaryTree() {
    }

    public BinaryTree(Root<T> root) {
        this.root = root;
    }

    //中序遍历(递归)
    public void inOrderTraverse() {
        inOrderTraverse(root);
    }

    public void inOrderTraverse(Node<T> node) {
        if (node != null) {
            inOrderTraverse(node.getLeft());
            System.out.println(node.getValue());
            inOrderTraverse(node.getRight());
        }
    }


    //中序遍历(非递归)
    public void nrInOrderTraverse() {

        Stack<Node<T>> stack = new Stack<Node<T>>();
        Node<T> node = root;
        while (node != null || !stack.isEmpty()) {
            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }
            node = stack.pop();
            System.out.println(node.getValue());
            node = node.getRight();

        }

    }

    //先序遍历(递归)
    public void preOrderTraverse() {
        preOrderTraverse(root);
    }

    public void preOrderTraverse(Node<T> node) {
        if (node != null) {
            System.out.println(node.getValue());
            preOrderTraverse(node.getLeft());
            preOrderTraverse(node.getRight());
        }
    }


    //先序遍历（非递归）
    public void nrPreOrderTraverse() {

        Stack<Node<T>> stack = new Stack<Node<T>>();
        Node<T> node = root;

        while (node != null || !stack.isEmpty()) {

            while (node != null) {
                System.out.println(node.getValue());
                stack.push(node);
                node = node.getLeft();
            }
            node = stack.pop();
            node = node.getRight();
        }

    }

    //后序遍历(递归)
    public void postOrderTraverse() {
        postOrderTraverse(root);
    }

    public void postOrderTraverse(Node<T> node) {
        if (node != null) {
            postOrderTraverse(node.getLeft());
            postOrderTraverse(node.getRight());
            System.out.println(node.getValue());
        }
    }

    //后续遍历(非递归)
    public void nrPostOrderTraverse() {

        Stack<Node<T>> stack = new Stack<Node<T>>();
        Node<T> node = root;
        Node<T> preNode = null;//表示最近一次访问的节点

        while (node != null || !stack.isEmpty()) {

            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }

            node = stack.peek();

            if (node.getRight() == null || node.getRight() == preNode) {
                System.out.println(node.getValue());
                node = stack.pop();
                preNode = node;
                node = null;
            } else {
                node = node.getRight();
            }

        }
    }

    //层序遍历
    public void levelTraverse() {
        levelTraverse(root);
    }

    public void levelTraverse(Root<T> node) {

        Queue<Node<T>> queue = new LinkedBlockingQueue<Node<T>>();
        queue.add(node);
        while (!queue.isEmpty()) {

            Node<T> temp = queue.poll();
            if (temp != null) {
                System.out.println(temp.getValue());
                queue.add(temp.getLeft());
                queue.add(temp.getRight());
            }
        }
    }
}