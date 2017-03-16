BubbleComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BubbleComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-bubble-combo bi-mvc-layout"
        })
    },

    _init: function () {
        BubbleComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        var combo1 = BI.createWidget({
            type: "bi.bubble_combo",
            el: {
                type: "bi.button",
                text: "测试",
                height: 25
            },
            popup: {
                el: {
                    type: "bi.button_group",
                    items: BI.makeArray(100, {
                        type: "bi.text_item",
                        height: 25,
                        text: "item"
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                },
                maxHeight: 200
            }
        })
        var combo2 = BI.createWidget({
            type: "bi.bubble_combo",
            el: {
                type: "bi.button",
                text: "测试",
                height: 25
            },
            popup: {
                type: "bi.bubble_bar_popup_view",
                el: {
                    type: "bi.button_group",
                    items: BI.makeArray(100, {
                        type: "bi.text_item",
                        height: 25,
                        text: "item"
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                },
                maxHeight: 200,
                minWidth: 600
            }
        })
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: combo1,
                left: 100,
                top: 100
            }, {
                el: combo2,
                left: 100,
                bottom: 100
            }]
        })
    }
});
BubbleComboModel = BI.inherit(BI.Model, {});