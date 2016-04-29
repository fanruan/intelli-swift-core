HorizontalAutoView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(HorizontalAutoView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-horizontal-auto bi-mvc-layout"
        })
    },

    _init: function () {
        HorizontalAutoView.superclass._init.apply(this, arguments);
    },

    _createLayout: function () {
        return BI.createWidget({
            type: "bi.horizontal_auto",
            vgap: 10,
            items: [{
                type: "bi.label",
                text: "Horizontal Auto左右自适应",
                cls: "layout-bg1",
                width: 300,
                height: 30
            }, {
                type: "bi.label",
                text: "Horizontal Auto左右自适应",
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

HorizontalAutoModel = BI.inherit(BI.Model, {});