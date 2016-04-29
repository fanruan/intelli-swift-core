TabModulesView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TabModulesView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-tab-modules bi-mvc-layout"
        })
    },

    _init: function () {
        TabModulesView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.data_style_tab",
                height: 200
            }]
        })
    }
});

TabModulesModel = BI.inherit(BI.Model, {});