SelectDataMaskView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectDataMaskView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-data-mask bi-mvc-layout"
        })
    },

    _init: function () {
        SelectDataMaskView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var data = BI.requestSync("fr_bi_configure","get_business_package_group","");
        Data.SharingPool.put("groups", data.groups);
        Data.SharingPool.put("packages", data.packages);
        BI.createWidget({
            type: "bi.left",
            cls: "layout-bg-gray",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [{
                type: "bi.select_data_with_mask",
                cls: "layout-bg-white",
                fieldType: BICst.COLUMN.STRING,
                height: 600
            }]
        })
    }
});

SelectDataMaskModel = BI.inherit(BI.Model, {});