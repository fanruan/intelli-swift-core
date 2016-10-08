import {Tree, each, some, isNil, isEmpty, find, clone} from 'core'
import MultiTreeSelectorWidgetHelper from './MultiTreeSelectorWidgetHelper'

export default class MultiTreeSelectorWidgetAsnycHelper extends MultiTreeSelectorWidgetHelper {
    constructor(state, props) {
        super(state);
        this.floors = props.floors;
        this.itemsCreator = props.itemsCreator;
    }

    _digest() {
        const format = Tree.transformToTreeFormat(this.items);
        this.tree.initTree(format);
    }

    _getParentValues(node) {
        if (this.tree.isRoot(node)) {
            return [];
        }
        var ps = this._getParentValues(node.getParent());
        return ps.concat(node.get('data').value || node.get('data').text);
    }

    expandOneNode(node) {
        const find = this.tree.search(node.id);
        if (find) {
            const data = find.get('data');
            data.expanded = true;
            this.map[node.id].expanded = true;
            if (data.isParent && find.getChildrenLength() === 0) {
                return this.itemsCreator({
                    id: node.id,
                    times: -1,
                    floors: this.floors,
                    check_state: {
                        checked: data.checked,
                        half: data.halfCheck
                    },
                    parent_values: this._getParentValues(find),
                    selected_values: this.value
                }).then((data)=> {
                    each(data.items, (item)=> {
                        item.pId = node.id
                    });
                    this.items = this.items.concat(data.items);
                    this._digest();
                });
            }
        }
        return new Promise(function(resolve, reject) {
            resolve();
        });
    }
}