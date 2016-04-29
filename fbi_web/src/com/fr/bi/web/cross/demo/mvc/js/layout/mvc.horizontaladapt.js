HorizontalAdaptView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(HorizontalAdaptView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-horizontal-adapt bi-mvc-layout"
        })
    },

    _init: function () {
        HorizontalAdaptView.superclass._init.apply(this, arguments);
    },

    _createLayout: function () {
        return BI.createWidget({
            type: "bi.horizontal_adapt",
            vgap: 10,
            items: [{
                type: "bi.label",
                text: "Horizontal Adapt左右自适应",
                cls: "layout-bg1",
                //width: 300,
                height: 30
            }, {
                type: "bi.label",
                text: "Horizontal Adapt左右自适应",
                cls: "layout-bg2",
                //width: 300,
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

HorizontalAdaptModel = BI.inherit(BI.Model, {});