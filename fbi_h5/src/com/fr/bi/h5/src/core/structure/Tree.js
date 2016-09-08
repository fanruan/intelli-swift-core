import UUID from './UUID'
import isNil from 'lodash/isNil'
import isUndefined from 'lodash/isUndefined'
import isArray from 'lodash/isArray'
import isObject from 'lodash/isObject'
import isEmpty from 'lodash/isEmpty'
import isEqual from 'lodash/isEqual'
import some from 'lodash/some'
import every from 'lodash/every'
import clone from 'lodash/clone'
import extend from 'lodash/extend'

import first from 'lodash/first'
import last from 'lodash/last'
import nth from 'lodash/nth'
import findIndex from 'lodash/findIndex'

class Tree {
    static Node = Node;

    constructor() {
        this.root = new Node(UUID());
    }

    addNode(node, newNode, index) {
        if (isNil(newNode)) {
            this.root.addChild(node, index);
        } else if (isNil(node)) {
            this.root.addChild(newNode, index);
        } else {
            node.addChild(newNode, index);
        }
    }

    isRoot(node) {
        return node === this.root || node.id === this.root.id;
    }

    getRoot() {
        return this.root;
    }

    clear() {
        this.root.clear();
    }

    initTree(nodes) {
        this.clear();
        var queue = [];
        nodes.forEach((node, i)=> {
            var n = new Node(node);
            n.set("data", node);
            this.addNode(n);
            queue.push(n);
        });
        while (queue.length > 0) {
            var parent = queue.shift();
            var node = parent.get("data");
            node.children && node.children.forEach((child, i)=> {
                var n = new Node(child);
                n.set("data", child);
                queue.push(n);
                self.addNode(parent, n);
            });
        }
    }

    _toJSON(node) {
        var children = [];
        if (node) {
            node.getChildren().forEach((child, i)=> {
                children.push(this._toJSON(child));
            })
        }
        if (children.length > 0) {
            return {
                id: node.id,
                ...node.getData(),
                children: children
            }
        }
        return {
            id: node.id,
            ...node.getData()
        }
    }

    toJSON(node) {
        var result = [];
        (node || this.root).getChildren().forEach((child, i)=> {
            result.push(this._toJSON(child));
        });
        return result;
    }

    _toJSONWithNode(node) {
        var children = [];
        if (node) {
            node.getChildren().forEach((child, i)=> {
                children.push(this._toJSONWithNode(child));
            })
        }
        if (children.length > 0) {
            return {
                id: node.id,
                node: node,
                ...node.getData(),
                children: children
            }
        }
        return {
            id: node.id,
            node: node,
            ...node.getData()
        }
    }

    toJSONWithNode(node) {
        var result = [];
        (node || this.root).getChildren().forEach((child, i)=> {
            result.push(this._toJSONWithNode(child));
        });
        return result;
    }

    search(root, target, param) {
        if (!(root instanceof Node)) {
            return arguments.callee.apply(this, [this.root, root, target]);
        }
        var next = null;

        if (isNil(target)) {
            return null;
        }
        if (isEqual(root[param || "id"], target)) {
            return root;
        }

        some(root.getChildren(), (child)=> {
            next = this.search(child, target, param);
            if (null !== next) {
                return true;
            }
        });
        return next;
    }

    _traverse(node, callback) {
        var queue = [];
        queue.push(node);
        while (!isEmpty(queue)) {
            var temp = queue.shift();
            var b = callback && callback(temp);
            if (b === false) {
                break;
            }
            if (b === true) {
                continue;
            }
            if (temp != null) {
                queue = queue.concat(temp.getChildren());
            }
        }
    }

    traverse(callback) {
        this._traverse(this.root, callback);
    }

    _recursion(node, route, callback) {
        var self = this;
        return every(node.getChildren(), function (child) {
            var next = clone(route);
            next.push(child.id);
            var b = callback && callback(child, next);
            if (b === false) {
                return false;
            }
            if (b === true) {
                return true;
            }
            return self._recursion(child, next, callback);
        });
    }

    recursion(callback) {
        this._recursion(this.root, [], callback);
    }

    inOrderTraverse(callback) {
        this._inOrderTraverse(this.root, callback);
    }

    //中序遍历(递归)
    _inOrderTraverse(node, callback) {
        if (node != null) {
            this._inOrderTraverse(node.getLeft());
            callback && callback(node);
            this._inOrderTraverse(node.getRight());
        }
    }

    //中序遍历(非递归)
    nrInOrderTraverse(callback) {

        var stack = [];
        var node = this.root;
        while (node != null || !isEmpty(stack)) {
            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }
            node = stack.pop();
            callback && callback(node);
            node = node.getRight();
        }
    }

    preOrderTraverse(callback) {
        this._preOrderTraverse(this.root, callback);
    }

    //先序遍历(递归)
    _preOrderTraverse(node, callback) {
        if (node != null) {
            callback && callback(node);
            this._preOrderTraverse(node.getLeft());
            this._preOrderTraverse(node.getRight());
        }
    }

    //先序遍历（非递归）
    nrPreOrderTraverse(callback) {

        var stack = [];
        var node = this.root;

        while (node != null || !isEmpty(stack)) {

            while (node != null) {
                callback && callback(node);
                stack.push(node);
                node = node.getLeft();
            }
            node = stack.pop();
            node = node.getRight();
        }
    }

    postOrderTraverse(callback) {
        this._postOrderTraverse(this.root, callback);
    }

    //后序遍历(递归)
    _postOrderTraverse(node, callback) {
        if (node != null) {
            this._postOrderTraverse(node.getLeft());
            this._postOrderTraverse(node.getRight());
            callback && callback(node);
        }
    }

    //后续遍历(非递归)
    nrPostOrderTraverse(callback) {

        var stack = [];
        var node = this.root;
        var preNode = null;//表示最近一次访问的节点

        while (node != null || !isEmpty(stack)) {

            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }

            node = BI.last(stack);

            if (node.getRight() == null || node.getRight() == preNode) {
                callback && callback(node);
                node = stack.pop();
                preNode = node;
                node = null;
            } else {
                node = node.getRight();
            }
        }
    }

    static transformToArrayFormat(nodes) {
        if (!nodes) return [];
        var r = [];
        if (isArray(nodes)) {
            for (var i = 0, l = nodes.length; i < l; i++) {
                var node = clone(nodes[i]);
                delete node.children;
                r.push(node);
                if (nodes[i]["children"]) {
                    r = r.concat(Tree.transformToArrayFormat(nodes[i]["children"]));
                }
            }
        } else {
            var newNodes = clone(nodes);
            delete newNodes.children;
            r.push(newNodes);
            if (nodes["children"]) {
                r = r.concat(Tree.transformToArrayFormat(nodes["children"]));
            }
        }
        return r;
    }

    static transformToTreeFormat(sNodes) {
        var i, l;
        if (!sNodes) {
            return [];
        }

        if (isArray(sNodes)) {
            var r = [];
            var tmpMap = [];
            for (i = 0, l = sNodes.length; i < l; i++) {
                if (isNil(sNodes[i].id)) {
                    return sNodes;
                }
                tmpMap[sNodes[i].id] = clone(sNodes[i]);
            }
            for (i = 0, l = sNodes.length; i < l; i++) {
                if (tmpMap[sNodes[i].pId] && sNodes[i].id != sNodes[i].pId) {
                    if (!tmpMap[sNodes[i].pId].children) {
                        tmpMap[sNodes[i].pId].children = [];
                    }
                    tmpMap[sNodes[i].pId].children.push(tmpMap[sNodes[i].id]);
                } else {
                    r.push(tmpMap[sNodes[i].id]);
                }
                delete tmpMap[sNodes[i].id].pId;
            }
            return r;
        } else {
            return [sNodes];
        }
    }
}


class Node {
    constructor(id) {
        if (isObject(id)) {
            extend(this, id);
            return;
        }
        this.id = id;
    }

    set(key, value) {
        if (isObject(key)) {
            extend(this, key);
            return;
        }
        this[key] = value;
    }

    get(key) {
        return this[key];
    }

    isLeaf() {
        return isEmpty(this.children);
    }

    getChildren() {
        return this.children;
    }

    getChildrenLength() {
        return this.children.length;
    }

    getFirstChild() {
        return BI.first(this.children);
    }

    getLastChild() {
        return BI.last(this.children);
    }

    setLeft(left) {
        this.left = left;
    }

    getLeft() {
        return this.left;
    }

    setRight(right) {
        this.right = right;
    }

    getRight() {
        return this.right;
    }

    setParent(parent) {
        this.parent = parent;
    }

    getParent() {
        return this.parent;
    }

    getChild(index) {
        return this.children[index];
    }

    getChildIndex(id) {
        return findIndex(this.children, function (ch) {
            return ch.get("id") === id;
        });
    }

    removeChild(id) {
        this.removeChildByIndex(this.getChildIndex(id));
    }

    removeChildByIndex(index) {
        var before = this.getChild(index - 1);
        var behind = this.getChild(index + 1);
        if (before != null) {
            before.setRight(behind || null);
        }
        if (behind != null) {
            behind.setLeft(before || null);
        }
        this.children.splice(index, 1);
    }

    removeAllChilds() {
        this.children = [];
    }

    addChild(child, index) {
        var cur = null;
        if (isUndefined(index)) {
            cur = this.children.length - 1;
        } else {
            cur = index - 1;
        }
        child.setParent(this);
        if (cur >= 0) {
            this.getChild(cur).setRight(child);
            child.setLeft(this.getChild(cur));
        }
        if (isUndefined(index)) {
            this.children.push(child);
        } else {
            this.children.splice(index, 0, child);
        }
    }

    equals(obj) {
        return this === obj || this.id === obj.id;
    }

    clear() {
        this.parent = null;
        this.left = null;
        this.right = null;
        this.children = [];
    }
}
export default Tree