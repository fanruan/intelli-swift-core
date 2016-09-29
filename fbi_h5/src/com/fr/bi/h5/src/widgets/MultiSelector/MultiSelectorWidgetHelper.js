export default class MultiSelectorWidgetHelper {
    constructor(props) {
        this.items = props.items;
        this.value = Array.from(props.value || []);
        this.type = props.type;
        this.sorted = this._sortItems();
    }

    _sortItems() {
        const front = [], items = [];
        this.items.forEach((item)=> {
            if (this.value.indexOf(item.value) > -1) {
                front.push({...item, selected: this.type !== 2});
            } else {
                items.push({
                    ...item,
                    selected: this.type === 2
                });
            }
        });
        return front.concat(items);
    }

    _selectOneValue(val) {
        if (this.value.indexOf(val) === -1) {
            this.value.push(val);
            this.sorted = this._digest();
        }
    }

    _disSelectOneValue(val) {
        let idx;
        if ((idx = this.value.indexOf(val)) >= -1) {
            this.value.splice(idx, 1);
            this.sorted = this._digest();
        }
    }

    _digest() {
        const items = [];
        this.items.forEach((item)=> {
            items.push({
                ...item,
                selected: this.type === 2 ? (this.value.indexOf(item.value) === -1) : (this.value.indexOf(item.value) > -1)
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
        return Array.from(this.value);
    }

    getSortedItems() {
        return this.sorted;
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