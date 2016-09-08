import {Tree, each, isNil, find} from 'core'
export default class MultiTreeSelectorWidgetHelper {
    constructor(props) {
        this.items = props.items;
        this.sorted = this._expandTreeItems(this._formatItems(this.items));
        this.value = Array.from(props.value || []);
    }

    _formatItems(items) {
        return Tree.transformToTreeFormat(items);
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

    _selectOneValue(val) {
        if (this.value.indexOf(val) === -1) {
            this.value.push(val);
        }
    }

    _disSelectOneValue(val) {
        let idx;
        if ((idx = this.value.indexOf(val)) >= -1) {
            this.value.splice(idx, 1);
        }
    }

    selectOneValue(val) {
        this._selectOneValue(val);
    }

    disSelectOneValue(val) {
        this._disSelectOneValue(val);
    }

    expandOneValue(val) {
        const target = find(this.items, (item)=> {
            return item.value === val;
        });
        if (!isNil(target)) {
            target.expanded = true;
            this.sorted = this._expandTreeItems(this._formatItems(this.items));
        }
    }

    collapseOneValue(val) {
        const target = find(this.items, (item)=> {
            return item.value === val;
        });
        if (!isNil(target)) {
            target.expanded = false;
            this.sorted = this._expandTreeItems(this._formatItems(this.items));
        }
    }

    getSelectedValue() {
        return Array.from(this.value);
    }

    getSortedItems() {
        return this.sorted;
    }

}