/**
 * 数据标签
 * Created by Fay on 2016/7/13.
 */
BI.DataLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabel.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-data-label"
        });
    },

    _init: function () {
        BI.DataLabel.superclass._init.apply(this, arguments);
        var self = this;
        this.condition = BI.createWidget({
            type: "bi.data_label_condition"
        });
        BI.createWidget({
            type: "bi.vertical",
            items: [this.condition],
            element: this.element
        });
    }
});

BI.DataLabel.EVENT_CHANGE = "BI.DataBabel.EVENT_CHANGE";
$.shortcut("bi.data_label", BI.DataLabel);
