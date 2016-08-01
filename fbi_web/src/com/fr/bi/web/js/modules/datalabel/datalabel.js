/**
 * 数据标签
 * Created by Fay on 2016/7/13.
 */
BI.DataLabel = BI.inherit(BI.Widget, {
    _constant: {
        ADD_BUTTON_WIDTH: 70,
        ADD_BUTTON_HEIGHT: 26
    },

    _defaultConfig: function () {
        var conf = BI.DataLabel.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-data-label",
            dId: ""
        });
    },

    _init: function () {
        BI.DataLabel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var addButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_Condition"),
            cls: "button-ignore",
            width: this._constant.ADD_BUTTON_WIDTH,
            height: this._constant.ADD_BUTTON_HEIGHT,
            handler: function () {
                self.conditions.addItem();
            }
        });
        this.conditions = BI.createWidget({
            type: "bi.data_label_condition_group",
            dId: o.dId
        });
        BI.createWidget({
            type: "bi.vertical",
            items: [{
                type: "bi.right",
                items: [addButton],
                hgap: 4,
            }, this.conditions],
            element: this.element
        });
    },

    populate: function () {
        var o = this.options;
        this.conditions.populate();
    },
    
    getValue: function () {
        return this.conditions.getValue();
    }
});

BI.DataLabel.EVENT_CHANGE = "BI.DataBabel.EVENT_CHANGE";
$.shortcut("bi.data_label", BI.DataLabel);
