class Dimension {
    constructor(dimension, id, widget) {
        this.dimension = dimension;
        this._parent = widget;
    }

    getName() {
        return this.dimension.name;
    }

    isUsed() {
        return this.dimension.used;
    }

    getWidgetBelongTo() {
        return this._parent;
    }
}
export default Dimension