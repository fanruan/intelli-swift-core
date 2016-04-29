MultifileView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MultifileView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multifile bi-mvc-layout"
        })
    },

    _init: function () {
        MultifileView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.adaptive",
                    cls: "layout-bg1",
                    items: [{
                        type: "bi.multifile_editor",
                        width: 400,
                        height: 300
                    }],
                    width: 400,
                    height: 300
                },
                top: 50,
                left: 50
            }]
        })
    }
});

MultifileModel = BI.inherit(BI.Model, {});