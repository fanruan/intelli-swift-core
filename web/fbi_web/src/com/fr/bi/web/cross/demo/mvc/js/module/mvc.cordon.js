CordonView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(CordonView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-chart-type bi-mvc-layout"
        })
    },

    _init: function () {
        CordonView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            vgap: 20,
            items: [{
                type: "bi.cordon_item",
                cordon_name: "警戒线",
                cordon_value: "100",
                cordon_number_level: BICst.TARGET_STYLE.NUM_LEVEL.MILLION,
                cordon_color: "",
                width: 500,
                height: 40
            }, {
                type: "bi.cordon_pane"
            }]
        })
    }
});

CordonModel = BI.inherit(BI.Model, {});