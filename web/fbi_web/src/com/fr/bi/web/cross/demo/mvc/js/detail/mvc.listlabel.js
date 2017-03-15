ListLabelView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ListLabelView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        ListLabelView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var listLabel = BI.createWidget({
            type: "bi.list_label",
            title: "区域",
            items: [{
                text: "鼓楼",
                value: "鼓楼"
            }, {
                text: "玄武",
                value: "玄武"
            }, {
                text: "雨花台",
                value: "雨花台"
            }]
        });
        listLabel.setValue(["雨花台"]);
        var button = BI.createWidget({
            type: 'bi.button',
            text: '取值',
            level: 'common',
            height: 30,
            width: 60,
            handler: function () {
                BI.Msg.alert('提示', "[" + listLabel.getValue().toString() + "]");
            }
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [listLabel, button]
        })
    }
});

ListLabelModel = BI.inherit(BI.Model, {});