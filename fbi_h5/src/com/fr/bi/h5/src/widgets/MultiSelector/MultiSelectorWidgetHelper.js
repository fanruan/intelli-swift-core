import {clone} from 'core'
export default class MultiSelectorWidgetHelper {
    constructor(props) {
        this.items = props.items;
        this.value = props.value || [];
        this.selected = clone(this.value);
        this.type = props.type;
        this.sorted = this._sortItems();
    }

    _sortItems() {
        const front = this.value.map(val=> {
            return {
                value: val,
                selected: this.type !== 2
            }
        }), items = [];
        this.items.forEach((item)=> {
            items.push({
                ...item,
                selected: this.type === 2
            });
        });
        return front.concat(items);
    }

    _selectOneValue(val) {
        if (this.selected.indexOf(val) === -1) {
            this.selected.push(val);
            this.sorted = this._digest();
        }
    }

    _disSelectOneValue(val) {
        let idx;
        if ((idx = this.selected.indexOf(val)) >= -1) {
            this.selected.splice(idx, 1);
            this.sorted = this._digest();
        }
    }

    _digest() {
        const items = [];
        this.items.forEach((item)=> {
            items.push({
                ...item,
                selected: this.type === 2 ? (this.selected.indexOf(item.value) === -1) : (this.selected.indexOf(item.value) > -1)
            });
        });
        return items;
    }

    selectOneValue(val) {
        if (this.type === 2) {
            this._disSelectOneValue(val);
        } else {
            this._selectOneValue(val);
        }
    }

    disSelectOneValue(val) {
        if (this.type === 2) {
            this._selectOneValue(val);
        } else {
            this._disSelectOneValue(val);
        }
    }

    getSelectedValue() {
        return clone(this.selected);
    }

    getSortedItems() {
        return this.sorted;
    }

    getValue() {
        return clone(this.selected);
    }

    setType(type) {
        this.type = type;
        this.sorted = this.items.map((item)=> {
            return {
                ...item,
                selected: type === 2
            };
        });
    }
}