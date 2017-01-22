import {clone} from 'core'
export default class MultiSelectorWidgetHelper {
    constructor(state) {
        this.value = state.value || [];
        this.selected_values = state.selected_values;
        this.type = state.type;
        this.items = this.value.map(val=> {
            return {value: val}
        }).concat(state.items);
        this.sorted = this._digest();
    }

    _selectOneValue(val) {
        if (this.selected_values.indexOf(val) === -1) {
            this.selected_values.push(val);
            this.sorted = this._digest();
        }
    }

    _disSelectOneValue(val) {
        let idx;
        if ((idx = this.selected_values.indexOf(val)) > -1) {
            this.selected_values.splice(idx, 1);
            this.sorted = this._digest();
        }
    }

    _digest() {
        return this.items.map((item)=> {
            return {
                ...item,
                selected: this.type === 2 ? (this.selected_values.indexOf(item.value) === -1) : (this.selected_values.indexOf(item.value) > -1)
            };
        });
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
        return clone(this.selected_values);
    }

    getSortedItems() {
        return this.sorted;
    }
}