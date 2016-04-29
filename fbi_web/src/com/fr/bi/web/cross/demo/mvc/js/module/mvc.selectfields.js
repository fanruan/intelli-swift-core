SelectFieldsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectFieldsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-fields bi-mvc-layout"
        })
    },

    _init: function () {
        SelectFieldsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.left",
            cls: "layout-bg-gray",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.detail_select_data",
                width: 210,
                height: 600
            }]
        })
    }
});

SelectFieldsModel = BI.inherit(BI.Model, {});