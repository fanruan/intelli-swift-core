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

    setUsed(b) {
        return this.$$target.set('used', !!b);
    }

    getWidgetBelongTo() {
        return this._parent;
    }
}
export default Target