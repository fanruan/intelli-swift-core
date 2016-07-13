/**
 * 数据标签
 * Created by Fay on 2016/7/13.
 */
BI.DataLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabel.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {

        });
    },

    _init: function () {
        BI.DataLabel.superclass._init.apply(this, arguments);
        var imageset = BI.createWidget({
            type: "bi.data_label_image_set"
        });
        var barchart = BI.createWidget({
            type: "bi.data_label_bar_chart"
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: imageset,
                left: 200,
                top: 200
            },{
                el: barchart,
                left: 0,
                top: 200
            }],
            width: 800,
            height: 400
        })
    }
});

BI.DataLabel.EVENT_CHANGE = "BI.DataBabel.EVENT_CHANGE";
$.shortcut("bi.data_label", BI.DataLabel);
