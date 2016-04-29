SelectStringView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectStringView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-string bi-mvc-layout"
        })
    },

    _init: function () {
        SelectStringView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.left",
            cls: "layout-bg-gray",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.select_string",
                cls: "layout-bg-white",
                width: 210,
                height: 600
            }]
        })
    }
});

SelectStringModel = BI.inherit(BI.Model, {});