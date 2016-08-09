//Combo2控件
Combo2View = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(Combo2View.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-combo bi-mvc-layout"
        })
    },

    _init: function () {
        Combo2View.superclass._init.apply(this, arguments);
    },

    _createEl: function () {
        return {
            type: "bi.button",
            height: 25,
            text: "点击"
        }
    },

    oneCombo: function () {
        return BI.createWidget({
            type: "bi.combo",
            adjustLength: 5,
            el: this._createEl(),
            popup: {
                el: {
                    type: "bi.layout",
                    height: 500
                },
                maxHeight: 400
            }
        });
    },

    twoCombo: function () {
        return BI.createWidget({
            type: "bi.combo",
            adjustXOffset: 25,
            adjustYOffset: 5,
            direction: "bottom,left",
            el: this._createEl(),
            popup: {
                el: {
                    type: "bi.layout",
                    height: 1200
                }
            }
        });
    },

    threeCombo: function () {
        return BI.createWidget({
            type: "bi.combo",
            adjustYOffset: 5,
            el: this._createEl(),
            isNeedAdjustHeight: false,
            popup: {
                el: {
                    type: "bi.layout",
                    height: 1200
                }
            }
        });
    },

    fourCombo: function () {
        return BI.createWidget({
            type: "bi.combo",
            adjustXOffset: 25,
            adjustYOffset: 5,
            direction: "left",
            el: this._createEl(),
            isNeedAdjustHeight: true,
            popup: {
                el: {
                    type: "bi.layout",
                    height: 1200
                }
            }
        })
    },

    fiveCombo: function () {
        return BI.createWidget({
            type: "bi.combo",
            adjustXOffset: 25,
            adjustYOffset: 5,
            direction: "left,top",
            el: this._createEl(),
            isNeedAdjustHeight: true,
            popup: {
                el: {
                    type: "bi.layout",
                    height: 1200
                },
                maxHeight: 2000
            }
        })
    },

    sixCombo: function () {
        return BI.createWidget({
            type: "bi.combo",
            adjustXOffset: 25,
            adjustYOffset: 5,
            direction: "top,left",
            el: this._createEl(),
            isNeedAdjustHeight: true,
            popup: {
                el: {
                    type: "bi.layout",
                    height: 1200
                }
            }
        })
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            hgap: 10,
            vgap: 5,
            items: [[this.oneCombo(), this.twoCombo(), this.threeCombo()],
                [this.fourCombo(), this.fiveCombo(), this.sixCombo()]]
        });
    }
});
Combo2Model = BI.inherit(BI.Model, {
    _init: function () {
        Combo2Model.superclass._init.apply(this, arguments);
    }
});