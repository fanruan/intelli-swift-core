SelectDateView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectDateView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-date bi-mvc-layout"
        })
    },

    _init: function () {
        SelectDateView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.left",
            cls: "layout-bg-gray",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.select_date",
                cls: "layout-bg-white",
                width: 210,
                height: 600
            }]
        })
    }
});

SelectDateModel = BI.inherit(BI.Model, {});