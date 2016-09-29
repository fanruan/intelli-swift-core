class Target {
    constructor($$target, id, widget) {
        this.$$target = $$target;
        this._parent = widget;
    }

    getName() {
        return this.$$target.get('name');
    }

    isUsed() {
        return this.$$target.get('used');
    }

    getWidgetBelongTo() {
        return this._parent;
    }
}
export default Target