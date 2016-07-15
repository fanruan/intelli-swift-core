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

        this.conditions = BI.createWidget({
            type: "bi.data_label_condition_group"
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.right",
                items: [addButton],
                rgap: 8
            },this.conditions],
            width:500,
            height:500
        })
    },
    
    _createCondition: function () {
        return BI.createWidget({
            type: "bi.data_label_condition",
            width: 500,
            height: 40
        })
    }
});

BI.DataLabel.EVENT_CHANGE = "BI.DataBabel.EVENT_CHANGE";
$.shortcut("bi.data_label", BI.DataLabel);
