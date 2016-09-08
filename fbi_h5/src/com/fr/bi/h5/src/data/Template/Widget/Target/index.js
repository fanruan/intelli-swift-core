class Target {
    constructor(target, id, widget) {
        this.target = target;
        this._parent = widget;
    }

    getName() {
        return this.target.name;
    }

    isUsed() {
        return this.target.used;
    }

    getWidgetBelongTo() {
        return this._parent;
    }
}
export default Target