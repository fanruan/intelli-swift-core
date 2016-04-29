TipsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TipsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-tips bi-mvc-layout"
        })
    },

    _init: function () {
        TipsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.label",
                height: 30,
                text: "拖拽提示"
            }, {
                type: "bi.helper"
            }]
        })
    }
});

TipsModel = BI.inherit(BI.Model, {});