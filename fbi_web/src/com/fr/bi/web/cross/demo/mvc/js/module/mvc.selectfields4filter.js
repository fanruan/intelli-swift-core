SelectFields4FilterView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectFields4FilterView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-fields-4-filter bi-mvc-layout"
        })
    },

    _init: function () {
        SelectFields4FilterView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var dids = BI.Utils.getAllDimensionIDs();
        BI.createWidget({
            type: "bi.left",
            cls: "layout-bg-gray",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.select_single_field",
                cls: "layout-bg-white",
                dId: dids[0],
                width: 210,
                height: 600
            }]
        })
    }
});

SelectFields4FilterModel = BI.inherit(BI.Model, {});