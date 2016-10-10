class Dimension {
    constructor($$dimension, id, widget) {
        this.$$dimension = $$dimension;
        this._parent = widget;
    }

    getName() {
        return this.$$dimension.get('name');
    }

    isUsed() {
        return this.$$dimension.get('used');
    }

    getWidgetBelongTo() {
        return this._parent;
    }
}
export default Dimension