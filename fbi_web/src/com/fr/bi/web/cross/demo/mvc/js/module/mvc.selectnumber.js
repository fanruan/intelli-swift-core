SelectNumberView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectNumberView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-number bi-mvc-layout"
        })
    },

    _init: function () {
        SelectNumberView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.left",
            cls: "layout-bg-gray",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.select_number",
                cls: "layout-bg-white",
                width: 210,
                height: 600
            }]
        })
    }
});

SelectNumberModel = BI.inherit(BI.Model, {});