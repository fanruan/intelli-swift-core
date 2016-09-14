TextLabelView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TextLabelView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        TextLabelView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.text_label",
                title: "区域",
                items: [{
                    text: "不限"
                }, {
                    text: "鼓楼",
                    value: 1
                }, {
                    text: "玄武",
                    value: 2
                }, {
                    text: "雨花台",
                    value: 3
                }]
            }]
        })
    }
});

TextLabelModel = BI.inherit(BI.Model, {});