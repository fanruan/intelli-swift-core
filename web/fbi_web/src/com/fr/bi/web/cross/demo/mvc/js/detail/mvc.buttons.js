ButtonsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ButtonsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-buttons bi-mvc-layout"
        })
    },

    _init: function () {
        ButtonsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.label",
                height: 30,
                text: "树节点用到的十字形Checkbox"
            }, {
                type: "bi.tree_node_checkbox"
            }, {
                type: "bi.label",
                height: 30,
                text: "树节点用到的三角形Checkbox"
            }, {
                type: "bi.tree_group_node_checkbox"
            }, {
                type: "bi.label",
                height: 30,
                text: "树节点用到的箭头Checkbox"
            }, {
                type: "bi.arrow_tree_group_node_checkbox"
            }, {
                type: "bi.label",
                height: 30,
                text: "下拉框用到的trigger按钮"
            }, {
                type: "bi.trigger_icon_button"
            }, {
                type: "bi.label",
                height: 30,
                text: "预览按钮"
            }, {
                type: "bi.preview_icon_button"
            }],
            hgap: 50,
            vgap: 20
        })
    }
});

ButtonsModel = BI.inherit(BI.Model, {});