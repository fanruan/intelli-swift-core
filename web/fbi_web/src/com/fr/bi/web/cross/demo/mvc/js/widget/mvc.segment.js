SegmentView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SegmentView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-segment bi-mvc-layout"
        })
    },

    _init: function () {
        SegmentView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            vgap: 20,
            hgap: 30,
            items: [{
                type: "bi.segment",
                items: [{
                    text: "1",
                    value: 1
                }, {
                    text: "2",
                    value: 2
                }, {
                    text: "3",
                    value: 3
                }]
            }]
        })
    }
});

SegmentModel = BI.inherit(BI.Model, {});