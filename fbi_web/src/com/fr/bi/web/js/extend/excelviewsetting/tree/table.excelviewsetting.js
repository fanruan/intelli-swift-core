/**
 * ExcelViewSettingTable
 *
 * Created by GUY on 2016/4/7.
 * @class BI.ExcelViewSettingTable
 * @extends BI.Widget
 */
BI.ExcelViewSettingTable = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSettingTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view-setting-table",
            fields: []
        });
    },

    _init: function () {
        BI.ExcelViewSettingTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var header = BI.createWidget({
            type: "bi.excel_view_setting_header"
        });
        this.button_group = BI.createWidget({
            type: "bi.button_map",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE,
            items: this._formatItems(o.fields),
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.button_group.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [header, this.button_group]
        });
    },

    _formatItems: function (items) {
        var self = this;
        return BI.map(items, function (i, item) {
            return BI.extend({
                type: "bi.excel_view_setting_item",
                clearOneCell: function(fieldId) {
                    self.options.clearOneCell(fieldId);
                }
            }, item);
        });
    },
    
    setValue: function (v) {
        this.button_group.setValue(v);
    },

    getValue: function () {
        return this.button_group.getValue();
    },

    getMarkedFields: function(){
        var allButtons = this.button_group.getAllButtons();
        var fields = {};
        BI.each(allButtons, function(i, button) {
            var position = button.getPosition();
            if(BI.isNotNull(position)) {
                fields[button.getValue()] = position;
            }
        });
        return fields;
    },

    populate: function (fields) {
        this.button_group.populate(this._formatItems(fields));
    }
});
$.shortcut('bi.excel_view_setting_table', BI.ExcelViewSettingTable);