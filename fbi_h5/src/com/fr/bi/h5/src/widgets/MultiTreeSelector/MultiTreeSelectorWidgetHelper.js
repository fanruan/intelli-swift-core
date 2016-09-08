export default class MultiTreeSelectorWidgetHelper {
    constructor(props) {
        this.items = props.items;
        this.sorted = this.items;
        this.value = Array.from(props.value || []);
        this.type = props.type;
    }

    _selectOneValue(val) {
        if (this.value.indexOf(val) === -1) {
            this.value.push(val);
            this.sorted = this._sortItems();
        }
    }

    _disSelectOneValue(val) {
        let idx;
        if ((idx = this.value.indexOf(val)) >= -1) {
            this.value.splice(idx, 1);
            this.sorted = this._sortItems();
        }
    }

    _sortItems() {
        const front = [], items = [];
        this.items.forEach((item)=> {
            if (this.value.indexOf(item.value) > -1) {
                front.push({...item, selected: this.type !== 1});
            } else {
                items.push({...item, selected: this.type === 1})
            }
        });
        return front.concat(items);
    }

    selectOneValue(val) {
        if (this.type === 1) {
            this._disSelectOneValue(val);
        } else {
            this._selectOneValue(val);
        }
    }

    disSelectOneValue(val) {
        if (this.type === 1) {
            this._selectOneValue(val);
        } else {
            this._disSelectOneValue(val);
        }
    }

    getSelectedValue() {
        return Array.from(this.value);
    }

    getSortedItems() {
        return this.sorted;
    }

}