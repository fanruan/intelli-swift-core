HorizontalFloatView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(HorizontalFloatView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-horizontal-float bi-mvc-layout"
        })
    },

    _init: function () {
        HorizontalFloatView.superclass._init.apply(this, arguments);
    },

    _createLayout: function () {
        return BI.createWidget({
            type: "bi.horizontal_float",
            vgap: 10,
            items: [{
                type: "bi.label",
                text: "Horizontal Float左右自适应",
                cls: "layout-bg1"
            }]
        })
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 1,
            rows: 2,
            items: [{
                column: 0,
                row: 0,
                el: this._createLayout()
            }]
        })
    }
});

HorizontalFloatModel = BI.inherit(BI.Model, {});