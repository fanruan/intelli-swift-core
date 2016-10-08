import {Tree, each, some, isNil, isEmpty, find, clone, Promise} from 'core'
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

    _getRouteKey(route) {
        let result = '';
        each(route, (key)=> {
            result += this.map[key].value;
        });
        return result;
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

    _selectOneNode(node) {
        const find = this.tree.search(node.id);
        if (find) {
            const data = find.get('data');
            data.checked = true;
            data.halfCheck = false;
            this._adjustUpTreeSelected(find.getParent());
            this._adjustDownTreeSelected(find);
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
        return new Promise(function(resolve, reject) {
            resolve();
        });
    }

    collapseOneNode(node) {
        const find = this.tree.search(node.id);
        if (find) {
            const data = find.get('data');
            data.expanded = false;
        }
        return new Promise(function(resolve, reject) {
            resolve();
        });
    }

    getSelectedValue() {
        return clone(this.value);
    }

    getItems() {
        return Tree.transformToArrayFormat(this.tree.toJSON());
    }

    getSortedItems() {
        return this.sorted;
    }

}