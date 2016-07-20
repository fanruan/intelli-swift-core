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
        var header = BI.createWidget({
            type: "bi.center_adapt",
            cls: "image-set-header",
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: "数据标签样式",
                    cls: "header-title"
                }],
                hgap: 10
            }, {
                type: 'bi.right',
                items:[{
                    el:{
                        type: "bi.icon_button",
                        cls: "close-font header-icon"
                    }
                }],
                rgap: 2
            }],
            height: 50,
            cls: "data-label-header"
        });
        this.condition = BI.createWidget({
            type: "bi.data_label_condition"
        });
        BI.createWidget({
            type: "bi.vertical",
            items: [header,this.condition],
            width: 560,
            height: 420,
            element: this.element,
            scrollable: null,
            scrolly: false,
            scrollx: false
        });
    }
});

BI.DataLabel.EVENT_CHANGE = "BI.DataBabel.EVENT_CHANGE";
$.shortcut("bi.data_label", BI.DataLabel);
