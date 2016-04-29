/**
 * Created by roy on 15/11/9.
 */
LazyLoaderView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(LazyLoaderView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-lazy-loader"
        })
    },
    _init: function () {
        LazyLoaderView.superclass._init.apply(this, arguments);

    },

    _render: function (vessel) {
        var self = this;
        BI.createWidget({
            type: "bi.lazy_loader",
            element: vessel,
            el: {
                layouts: [{
                    type: "bi.left",
                    hgap: 5
                }]
            },
            items: BI.createItems(ITEMS, {
                type: "bi.button"
            })
        })
    }

});

LazyLoaderModel = BI.inherit(BI.Model, {});