ExpanderView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ExpanderView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-expander bi-mvc-layout"
        })
    },

    _init: function () {
        ExpanderView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                type: "bi.expander",
                el: {
                    type: "bi.icon_text_node",
                    cls: "pull-right-ha-font mvc-border",
                    height: 25,
                    text: "Expander"
                },
                popup: {
                    cls: "mvc-border",
                    items: BI.createItems([{
                        text: "项目1",
                        value: 1
                    }, {
                        text: "项目2",
                        value: 2
                    }, {
                        text: "项目3",
                        value: 3
                    }, {
                        text: "项目4",
                        value: 4
                    }], {
                        type: "bi.single_select_item",
                        height: 25
                    })
                }
            }]
        })
    }
});

ExpanderModel = BI.inherit(BI.Model, {});