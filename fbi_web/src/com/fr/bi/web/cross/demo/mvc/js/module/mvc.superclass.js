SuperClassView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SuperClassView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-combos bi-mvc-layout"
        })
    },

    _init: function () {
        SuperClassView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: BI.createItems([{
                text: "BI.Pane"
            }, {
                text: "BI.Single"
            }, {
                text: "BI.Tip"
            }, {
                text: "BI.BasicButton"
            }, {
                text: "BI.NodeButton"
            }, {
                text: "BI.Trigger"
            }, {
                text: "BI.FloatSection"
            }, {
                text: "BI.PopoverSection"
            }, {
                text: "BI.BarFloatSection"
            }, {
                text: "BI.BarPopoverSection"
            }], {
                type: "bi.label",
                height: 50,
                cls: "mvc-color-blue mvc-font-big"
            })
        })
    }
});

SuperClassModel = BI.inherit(BI.Model, {});