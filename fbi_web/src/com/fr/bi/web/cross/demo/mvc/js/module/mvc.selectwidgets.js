SelectWidgetsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectWidgetsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-widgets bi-mvc-layout"
        })
    },

    _init: function () {
        SelectWidgetsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.left",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.drag_icon_group"
            }]
        })
    }
});

SelectWidgetsModel = BI.inherit(BI.Model, {});