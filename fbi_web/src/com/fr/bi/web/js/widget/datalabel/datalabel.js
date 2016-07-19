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
        var self = this;
        var addButton = BI.createWidget({
            type: "bi.button",
            text: "添加条件",
            width: 80,
            handler: function () {
                self.conditions.addItem();
            }
        });
        this.tab = BI.createWidget({
            type: "bi.data_label_tab"
        });
        this.conditions = BI.createWidget({
            type: "bi.data_label_condition_group"
        });
        BI.createWidget({
            type: "bi.vertical",
            items: [{
                type: "bi.right",
                items: [addButton],
                rgap: 10
            },this.conditions,this.tab],
            width: 560,
            height: 400,
            element: this.element,
            scrollable: null,
            scrolly: false,
            scrollx: false
        });
    }
});

BI.DataLabel.EVENT_CHANGE = "BI.DataBabel.EVENT_CHANGE";
$.shortcut("bi.data_label", BI.DataLabel);
