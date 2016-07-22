/**
 * Created by lfhli on 2016/7/15.
 */
BI.DataLabelConditionGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabelConditionGroup.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {

        });
    },

    _init: function () {
        BI.DataLabelConditionGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.buttonGroup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: o.items,
            layouts: [{
                type: "bi.vertical",
                hgap: 10,
                vgap: 2
            }]
        });

        this.buttons = this.buttonGroup.getAllButtons();
    },

    addItem: function () {
        var item = {
            type: "bi.target_no_type_field_filter_item"
        };
        this.buttonGroup.addItems([item]);
        this.buttons = this.buttonGroup.getAllButtons();
    }
});
BI.DataLabelConditionGroup.EVENT_CHANGE = "BI.DataLabelConditionGroup.EVENT_CHANGE";
$.shortcut("bi.data_label_condition_group", BI.DataLabelConditionGroup);