/**
 * @class BI.SelectDataWithMask
 * @extend BI.Widget
 * 带有蒙版的选择字段（设置表关联）
 */
BI.SelectDataWithMask = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.SelectDataWithMask.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-data-with-mask"
        })
    },

    _init: function(){
        BI.SelectDataWithMask.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.selectDataPane = BI.createWidget({
            type: "bi.select_single_relation_table_field",
            model: o.model,
            field_id: o.field_id
        });
        this.selectDataPane.on(BI.SelectSingleRelationTableField.EVENT_CLICK_ITEM, function(){
            self.fireEvent(BI.SelectDataWithMask.EVENT_CHANGE, arguments);
        });

        var wrapper = BI.createWidget({
            type: "bi.vtape",
            cls: "select-data-wrapper",
            items: [{
                el: this.selectDataPane,
                height: "fill"
            }, {
                el: this._createSelectDataBottom(),
                height: 50
            }],
            width: 240
        });
        this.mask = BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.default",
                    cls: "select-data-mask"
                },
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.mask,
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }, {
                el: wrapper,
                top: 10,
                bottom: 10,
                left: 10
            }]
        })
    },

    _createSelectDataBottom: function(){
        var self = this;
        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            width: 150,
            height: 30,
            text: BI.i18nText("BI-Cancel")
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.SelectDataWithMask.EVENT_VALUE_CANCEL);
        });
        return BI.createWidget({
            type: "bi.horizontal_auto",
            cls: "select-data-button-group",
            items: [cancelButton],
            vgap: 10
        })
    },

    destroy: function(){
        this.selectDataPane.destroy();
    }
});
BI.SelectDataWithMask.EVENT_CHANGE = "EVENT_CHANGE";
BI.SelectDataWithMask.EVENT_VALUE_CANCEL = "EVENT_VALUE_CANCEL";
$.shortcut("bi.select_data_with_mask", BI.SelectDataWithMask);