VerticalAdaptView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(VerticalAdaptView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-vertical-adapt bi-mvc-layout"
        })
    },

    _init: function () {
        VerticalAdaptView.superclass._init.apply(this, arguments);
    },

    _createLayout: function () {
        return BI.createWidget({
            type: "bi.vertical_adapt",
            vgap: 10,
            items: [{
                type: "bi.label",
                text: "Vertical Adapt上下自适应",
                cls: "layout-bg1",
                width: 300,
                height: 30
            }, {
                type: "bi.label",
                text: "Vertical Adapt上下自适应",
                cls: "layout-bg2",
                width: 300,
                height: 30
            }]
        })
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 2,
            rows: 1,
            items: [{
                column: 0,
                row: 0,
                el: this._createLayout()
            }]
        })
    }
});

VerticalAdaptModel = BI.inherit(BI.Model, {});