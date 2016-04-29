CenterVerticalAdaptView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(CenterVerticalAdaptView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-center-vertical-adapt bi-mvc-layout"
        })
    },

    _init: function () {
        CenterVerticalAdaptView.superclass._init.apply(this, arguments);
    },

    _createLayout: function () {
        return BI.createWidget({
            type: "bi.float_center_adapt",
            vgap: 10,
            items: [{
                type: "bi.label",
                text: "一种float布局的水平垂直居中",
                cls: "layout-bg1",
                width: 300,
                height: 30
            }]
        })
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 1,
            rows: 1,
            items: [{
                column: 0,
                row: 0,
                el: this._createLayout()
            }]
        })
    }
});

CenterVerticalAdaptModel = BI.inherit(BI.Model, {});