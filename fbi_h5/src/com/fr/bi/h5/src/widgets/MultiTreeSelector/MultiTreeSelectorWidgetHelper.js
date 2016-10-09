import {Tree, each, some, isNil, isEmpty, find, clone, cloneDeep, isPlainObject, Promise} from 'core'
export default class MultiTreeSelectorWidgetHelper {
    constructor(state) {
        this.items = state.items;
        this.value = state.value || {};
        const format = this._initTree(this.items, this.value);
        this.sorted = this._expandTreeItems(format);
    }

    _createMap(items) {
        this.map = {};
        each(items, (node)=> {
            this.map[node.id] = node;
        });
    }

    _createSelectedMap(selected_values) {
        this.selMap = {};
        const track = (val, route)=> {
            each(val, (child, key)=> {
                if (isEmpty(child)) {
                    this.selMap[route + key] = 2;
                } else {
                    this.selMap[route + key] = 1;
                }
                track(child, route + key);
            })
        };
        track(selected_values, '')
    }

    _getKey(values) {
        return values.join('');
    }

    _getRouteKey(route) {
        return this._getKey(this._getRouteValues(route));
    }

    _getRouteValues(route) {
        const result = [];
        each(route, (key)=> {
            result.push(this.map[key].value || this.map[key].text);
        });
        return result;
    }

    _getTreeList(map) {
        const result = [];
        const track = (node, parent)=> {
            each(node, (value, key)=> {
                if (isPlainObject(value) && isEmpty(value)) {
                    result.push(parent.concat(key));
                } else {
                    track(value, parent.concat(key));
                }
            })
        };
        track(map, []);
        return result;
    }

    _getTree(map, values) {
        let cur = map;
        some(values, function (value) {
            if (cur[value] == null) {
                return true;
            }
            cur = cur[value];
        });
        return cur;
    }

    _addTreeNode(map, values, key, value) {
        let cur = map;
        each(values, function (value) {
            if (cur[value] == null) {
                cur[value] = {};
            }
            cur = cur[value];
        });
        cur[key] = value;
    }

    //构造树节点
    _buildTree(map, values) {
        let cur = map;
        each(values, function (value) {
            if (cur[value] == null) {
                cur[value] = {};
            }
            cur = cur[value];
        })
    }

    //获取半选框值
    _buildHalfSelectedValues(map, node, parentValues) {
        const {halfCheck, checked, isParent, value, text} = node.get('data');
        //将未选的去掉
        if (checked === false && halfCheck === false) {
            return;
        }
        const path = parentValues.concat(value || text);
        //如果节点已展开,并且是半选
        if (isParent === true && node.getChildrenLength() > 0 && halfCheck === true) {
            // each(node.getChildren(), (ch)=> {
            //     this._buildHalfSelectedValues(map, ch, path);
            // });
            return;
        }
        if (node.getChildrenLength() > 0 || halfCheck === false) {
            this._buildTree(map, path);
            return;
        }
        const treeNode = this._getTree(this.value, path);
        this._addTreeNode(map, parent, value || text, treeNode);
    }

    _initTree(items, selected_values = {}) {
        this._createMap(items);
        this._createSelectedMap(selected_values);
        const format = Tree.transformToTreeFormat(items);
        this.tree = new Tree();
        this.tree.initTree(format);
        this.tree.recursion((child, routes)=> {
            const key = this._getRouteKey(routes);
            if (this.selMap[key] === 1) {
                child.get('data').checked = true;
                child.get('data').halfCheck = true;
            } else if (this.selMap[key] === 2) {
                child.get('data').checked = true;
                child.get('data').halfCheck = false;
            }
        });
        return format;
    }

    _expandTreeItems(items) {
        const result = [];
        const track = (nodes, layer)=> {
            each(nodes, (node, i)=> {
                const {children, ...others} = node;
                const isLeaf = isNil(node.children) && !node.isParent;
                result.push({
                    layer: layer,
                    isLeaf,
                    ...others
                });
                if (node.expanded === true) {
                    track(children, layer + 1);
                }
            })
        };
        track(items, 0);
        return result;
    }

    _adjustUpTreeSelected(node) {
        if (this.tree.isRoot(node)) {
            return;
        }
        let isAllSelected = true, isHalSelected = false;
        each(node.getChildren(), (child)=> {
            const data = child.get('data');
            if (!data.checked || data.halfCheck) {
                isAllSelected = false;
            }
            if (data.checked) {
                isHalSelected = true;
            }
        });
        node.get('data').checked = (isAllSelected || isHalSelected);
        node.get('data').halfCheck = !isAllSelected && isHalSelected;
        this._adjustUpTreeSelected(node.getParent());
    }

    _adjustDownTreeSelected(node) {
        const checked = node.get('data').checked, halfCheck = node.get('data').halfCheck;
        each(node.getChildren(), (child)=> {
            const data = child.get('data');
            if (!checked || !halfCheck) {
                data.checked = checked;
                data.halfCheck = false;
                this._adjustDownTreeSelected(child);
            }
        });
    }

    _digestSelected() {
        const map = {};
        const mustDeleted = new Set();
        this.tree.recursion((child, routes)=> {
            const {checked, halfCheck, isParent} = child.get('data');
            mustDeleted.add(this._getRouteKey(routes));
            if (checked === true && halfCheck === true) {
                if (isParent && child.getChildrenLength() === 0) {
                    this._buildHalfSelectedValues(map, child, this._getRouteValues(routes).slice(0, routes.length - 1));
                    return true;
                }
            } else if (checked === true) {
                this._buildTree(map, this._getRouteValues(routes));
                return true;
            }
        });
        each(this.value, (value, key)=> {
            if (!map[key] && !mustDeleted.has(key)) {
                map[key] = value;
            }
        });
        this.value = map;
    }

    _selectOneNode(node) {
        const find = this.tree.search(node.id);
        if (find) {
            const data = find.get('data');
            data.checked = true;
            data.halfCheck = false;
            this._adjustUpTreeSelected(find.getParent());
            this._adjustDownTreeSelected(find);
            this._digestSelected();
        }
    }

    _disSelectOneNode(node) {
        const find = this.tree.search(node.id);
        if (find) {
            const data = find.get('data');
            data.checked = false;
            data.halfCheck = false;
            this._adjustUpTreeSelected(find.getParent());
            this._adjustDownTreeSelected(find);
            this._digestSelected();
        }
    }

    selectOneNode(node) {
        this._selectOneNode(node);
    }

    disSelectOneNode(node) {
        this._disSelectOneNode(node);
    }

    expandOneNode(node) {
        const find = this.tree.search(node.id);
        if (find) {
            const data = find.get('data');
            data.expanded = true;
        }
        return new Promise(function (resolve, reject) {
            resolve();
        });
    }

    collapseOneNode(node) {
        const find = this.tree.search(node.id);
        if (find) {
            const data = find.get('data');
            data.expanded = false;
        }
        return new Promise(function (resolve, reject) {
            resolve();
        });
    }

    getSelectedValue() {
        return this.value;
    }

    getItems() {
        return Tree.transformToArrayFormat(this.tree.toJSON());
    }

    getSortedItems() {
        return this.sorted;
    }

}