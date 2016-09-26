import {Tree, each, some, isNil, find} from 'core'
export default class MultiTreeSelectorWidgetHelper {
    constructor(props) {
        this.items = props.items;
        const format = this._formatItems(this.items);
        this.tree = new Tree();
        this.tree.initTree(format);
        this.sorted = this._expandTreeItems(format);
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

    _adjustUpTreeSelected(node) {
        if (this.tree.isRoot(node)) {
            return;
        }
        let isAllSelected = true, isHalSelected = false;
        each(node.getChildren(), (child)=> {
            const data = child.get('data');
            if (data.selected < 2 || isNil(data.selected)) {
                isAllSelected = false;
            }
            if (data.selected > 0) {
                isHalSelected = true;
            }
        });
        node.get('data').selected = (isAllSelected ? 2 : (isHalSelected ? 1 : 0));
        this._adjustUpTreeSelected(node.getParent());
    }

    _adjustDownTreeSelected(node) {
        const selected = node.get('data').selected;
        each(node.getChildren(), (child)=> {
            const data = child.get('data');
            if (selected === 2 || selected === 0 || isNil(selected)) {
                data.selected = selected;
                this._adjustDownTreeSelected(child);
            }
        });
    }

    _selectOneValue(val) {
        const find = this.tree.search(val, 'value');
        if (find) {
            const data = find.get('data');
            data.selected = 2;
            this._adjustUpTreeSelected(find.getParent());
            this._adjustDownTreeSelected(find);
            this._digest();
        }
    }

    _disSelectOneValue(val) {
        const find = this.tree.search(val, 'value');

        if (find) {
            const data = find.get('data');
            data.selected = 0;
            this._adjustUpTreeSelected(find.getParent());
            this._adjustDownTreeSelected(find);
            this._digest();
        }
    }

    _digest() {
        this.sorted = this._expandTreeItems(this.tree.toJSON());
    }

    selectOneValue(val) {
        this._selectOneValue(val);
    }

    disSelectOneValue(val) {
        this._disSelectOneValue(val);
    }

    expandOneValue(val) {
        const find = this.tree.search(val, 'value');
        if (find) {
            const data = find.get('data');
            data.expanded = true;
            this._digest();
        }
    }

    collapseOneValue(val) {
        const find = this.tree.search(val, 'value');
        if (find) {
            const data = find.get('data');
            data.expanded = false;
            this._digest();
        }
    }

    getSelectedValue() {
        return Array.from(this.value);
    }

    getSortedItems() {
        return this.sorted;
    }

}