FloatCenterView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FloatCenterView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-float-center bi-mvc-layout"
        })
    },

    _init: function () {
        FloatCenterView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.float_center",
            element: vessel,
            items: [{
                type: "bi.label",
                text: "floatCenter与center的不同在于，它可以控制最小宽度和最大宽度",
                cls: "layout-bg1",
                whiteSpace: "normal"
            }, {
                type: "bi.label",
                text: "floatCenter与center的不同在于，它可以控制最小宽度和最大宽度",
                cls: "layout-bg2",
                whiteSpace: "normal"
            }],
            hgap: 20,
            vgap: 20
        })
    }
});

FloatCenterModel = BI.inherit(BI.Model, {});