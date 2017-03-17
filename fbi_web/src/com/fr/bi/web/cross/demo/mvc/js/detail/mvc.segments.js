SegmentsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(NodesView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-segments bi-mvc-layout"
        })
    },

    _init: function () {
        SegmentsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.label",
                height: 30,
                text: "默认风格"
            }, {
                type: "bi.segment",
                items: [{text: "tab1", value: 1, selected: true}, {text: "tab2", value: 2}, {
                    text: "tab3",
                    disabled: true,
                    value: 3
                }]
            }, {
                type: "bi.label",
                height: 30,
                text: "风格1"
            }, {
                type: "bi.line_segment",
                items: [{text: "tab1", value: 1, selected: true}, {text: "tab2", value: 2}]
            }],
            hgap: 50,
            vgap: 20
        })
    }
});

SegmentsModel = BI.inherit(BI.Model, {});