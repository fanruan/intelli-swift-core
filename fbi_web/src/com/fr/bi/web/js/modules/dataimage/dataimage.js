/**
 * Created by fay on 2016/9/1.
 */
BI.DataImage = BI.inherit(BI.Widget, {
    _constant: {
        ADD_BUTTON_WIDTH: 70,
        ADD_BUTTON_HEIGHT: 26
    },

    _defaultConfig: function () {
        var conf = BI.DataImage.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-data-image",
            dId: ""
        });
    },

    _init: function () {
        BI.DataImage.superclass._init.apply(this, arguments);
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
            type: "bi.data_image_condition_group",
            dId: o.dId
        });
        BI.createWidget({
            type: "bi.vertical",
            items: [{
                type: "bi.right",
                items: [addButton],
                hgap: 4
            }, {
                el: this.conditions,
                tgap: 10
            }],
            element: this.element
        });
    },

    populate: function () {
        this.conditions.populate();
    },

    getValue: function () {
        return this.conditions.getValue();
    }
});

BI.DataImage.EVENT_CHANGE = "BI.DataImage.EVENT_CHANGE";
$.shortcut("bi.data_image", BI.DataImage);
