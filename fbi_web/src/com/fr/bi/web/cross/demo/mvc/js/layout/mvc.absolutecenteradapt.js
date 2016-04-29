AbsoluteCenterAdaptView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(AbsoluteCenterAdaptView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-absolute-center-adapt bi-mvc-layout"
        })
    },

    _init: function () {
        AbsoluteCenterAdaptView.superclass._init.apply(this, arguments);
    },

    _createNoWidth: function () {
        return BI.createWidget({
            type: "bi.absolute_center_adapt",
            hgap: 10,
            items: [{
                type: "bi.label",
                width: 100,
                height: 100,
                text: "AbsoluteCenterAdapt",
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

AbsoluteCenterAdaptModel = BI.inherit(BI.Model, {
    _init: function () {
        AbsoluteCenterAdaptModel.superclass._init.apply(this, arguments);
    }
});