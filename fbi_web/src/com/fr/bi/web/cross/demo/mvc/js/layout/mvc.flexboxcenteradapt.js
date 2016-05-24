FlexboxCenterAdaptView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FlexboxCenterAdaptView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-flexbox-center-adapt bi-mvc-layout"
        })
    },

    _init: function () {
        FlexboxCenterAdaptView.superclass._init.apply(this, arguments);
    },

    _createNoWidth: function () {
        return BI.createWidget({
            type: "bi.flexbox_center_adapt",
            hgap: 10,
            items: [{
                type: "bi.label",
                text: "FlexboxCenterAdapt",
                cls: "layout-bg1"
            }]
        })
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 1,
            rows: 1,
            hgap: 10,
            vgap: 10,
            items: [[{
                el: this._createNoWidth()
            }]]
        })
    }
});

FlexboxCenterAdaptModel = BI.inherit(BI.Model, {
    _init: function () {
        FlexboxCenterAdaptModel.superclass._init.apply(this, arguments);
    }
});