export default class MultiTreeSelectorWidgetHelper {
    constructor(props) {
        this.itemsCreator = props.itemsCreator;
        this.sorted = this.items;
        this.value = Array.from(props.value || []);
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

    expandOneValue() {

    }

    collapseOneValue() {

    }

    getSelectedValue() {
        return Array.from(this.value);
    }

    getSortedItems() {
        return this.sorted;
    }

}