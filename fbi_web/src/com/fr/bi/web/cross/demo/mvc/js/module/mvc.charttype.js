ChartTypeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ChartTypeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-chart-type bi-mvc-layout"
        })
    },

    _init: function () {
        ChartTypeView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.left",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.select_chart_group",
                width: 800
            }]
        })
    }
});

ChartTypeModel = BI.inherit(BI.Model, {});