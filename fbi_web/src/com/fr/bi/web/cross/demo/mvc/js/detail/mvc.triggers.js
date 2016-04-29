TriggersView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TriggersView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-triggers bi-mvc-layout"
        })
    },

    _init: function () {
        TriggersView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var multiTrigger = BI.createWidget({
            type: "bi.multi_select_trigger",
            adapter: new BI.Widget(),
            width: 200
        });
        multiTrigger.setValue({type: 1, value: [1, 2, 3]});

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.label",
                text: "只有一个图标的trigger"
            }, {
                type: "bi.icon_trigger",
                width: 30,
                height: 30
            }, {
                type: "bi.label",
                text: "文本加图标的trigger"
            }, {
                type: "bi.text_trigger",
                text: "这是一个简单的trigger",
                width: 200,
                height: 30
            }, {
                type: "bi.label",
                text: "输入框加图标的trigger"
            }, {
                type: "bi.editor_trigger",
                watermark: "这是水印",
                width: 200,
                height: 30
            }, {
                type: "bi.label",
                text: "可被选择的trigger"
            }, {
                type: "bi.select_text_trigger",
                text: "这是一个简单的trigger",
                width: 200,
                height: 30
            }, {
                type: "bi.label",
                text: "复选下拉框trigger"
            }, multiTrigger, {
                type: "bi.label",
                text: "日期trigger"
            }, {
                type: "bi.date_trigger"
            }, {
                type: "bi.label",
                text: "年份trigger"
            }, {
                type: "bi.year_trigger"
            }, {
                type: "bi.label",
                text: "季度trigger"
            }, {
                type: "bi.quarter_trigger"
            }, {
                type: "bi.label",
                text: "月份trigger"
            }, {
                type: "bi.month_trigger"
            }],
            hgap: 20,
            vgap: 20
        })
    }
});

TriggersModel = BI.inherit(BI.Model, {});