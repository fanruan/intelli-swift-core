/**
 * Created by Fay on 2016/7/13.
 */
DataLabelView = FR.extend(BI.View,{
    _defaultConfig: function () {
        return BI.extend(DataLabelView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-layout"
        })
    },

    _init: function () {
        DataLabelView.superclass._init.apply(this, arguments);
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
                    type: "bi.data_label"
                })
            }]
        });
    }
});

DataLabelModel = BI.inherit(BI.Model, {});