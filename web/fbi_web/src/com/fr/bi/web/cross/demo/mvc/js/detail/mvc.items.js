ItemsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ItemsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-items bi-mvc-layout"
        })
    },

    _init: function () {
        ItemsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.label",
                height: 30,
                text: "单选item"
            }, {
                type: "bi.single_select_item",
                text: "单选项"
            }, {
                type: "bi.label",
                height: 30,
                text: "复选item"
            }, {
                type: "bi.multi_select_item",
                text: "复选项"
            }, {
                type: "bi.label",
                height: 30,
                text: "字段item"
            }, {
                type: "bi.select_data_level0_item",
                ieldType: BICst.COLUMN.STRING,
                text: "字段项"
            }]
        })
    }
});

ItemsModel = BI.inherit(BI.Model, {});