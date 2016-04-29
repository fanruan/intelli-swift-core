ItemModulesView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ItemModulesView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-item-modules bi-mvc-layout"
        })
    },

    _init: function () {
        ItemModulesView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.real_data_checkbox"
            }]
        })
    }
});

ItemModulesModel = BI.inherit(BI.Model, {});