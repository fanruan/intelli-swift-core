ImageSetView = FR.extend(BI.View,{
    _defaultConfig: function () {
        return BI.extend(ImageSetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-layout"
        })
    },

    _init: function () {
        ImageSetView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var self = this, o = this.options;

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            hgap:100,
            vgap:60,
            items:[{
                el: BI.createWidget({
                    type: "bi.image_set"
                })
            }]
        });
    }
});

ImageSetModel = BI.inherit(BI.Model, {});