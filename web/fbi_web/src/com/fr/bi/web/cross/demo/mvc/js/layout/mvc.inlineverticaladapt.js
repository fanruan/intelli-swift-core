InlineVerticalAdaptView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(InlineVerticalAdaptView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-inline-vertical-adapt bi-mvc-layout"
        })
    },

    _init: function () {
        InlineVerticalAdaptView.superclass._init.apply(this, arguments);
    },

    _createLayout: function () {
        return BI.createWidget({
            type: "bi.inline_vertical_adapt",
            vgap: 10,
            items: [{
                type: "bi.label",
                text: "Inline Vertical Adapt上下自适应",
                cls: "layout-bg1",
                width: 300,
                height: 30
            }, {
                type: "bi.label",
                text: "Inline Vertical Adapt上下自适应",
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

InlineVerticalAdaptModel = BI.inherit(BI.Model, {});