export default class MultiSelectorWidgetHelper {
    constructor(props) {
        this.items = props.items;
        this.value = Array.from(props.value || []);
        this.type = props.type;
    }

    _selectOneValue(val) {
        if (this.value.indexOf(val) === -1) {
            this.value.push(val);
        }
    }

    _disSelectOneValue(val) {
        let idx;
        if ((idx = this.value.indexOf(val)) === -1) {
            this.value.splice(idx, 1);
        }
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

    getItems() {
        const front = [], items = [];
        this.items.forEach((item)=> {
            if (this.value.indexOf(item.value) > -1) {
                front.push({selected: this.type !== 1, ...item});
            } else {
                items.push({selected: this.type === 1, ...item})
            }
        });
        return front.concat(items);
    }

}