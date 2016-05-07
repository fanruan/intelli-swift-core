/**
 * ExcelViewSettingTree
 *
 * Created by GUY on 2016/4/7.
 * @class BI.ExcelViewSettingTree
 * @extends BI.Widget
 */
BI.ExcelViewSettingTree = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSettingTree.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view-setting-tree",
            tables: []
        });
    },

    _init: function () {
        BI.ExcelViewSettingTree.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button_group = BI.createWidget({
            type: "bi.button_map",
            element: this.element,
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE,
            items: this._formatTable(o.tables),
            layouts: [{
                type: "bi.vertical"
            }]
        })
    },

    _formatTable: function (tables) {
        var self = this;
        return BI.map(tables, function (i, table) {
            return BI.extend({
                type: "bi.excel_view_setting_expander",
                clearOneCell: function(fieldId){
                    self.options.clearOneCell(fieldId);
                }
            }, table);
        });
    },

    setValue: function (v) {
        this.button_group.setValue(v);
    },

    getValue: function () {
        return this.button_group.getValue();
    },

    getMarkedFields: function() {
        var allButtons = this.button_group.getAllButtons();
        var markedFields = {};
        BI.each(allButtons, function(i, button) {
            BI.extend(markedFields, button.getMarkedFields());
        });
        return markedFields;
    },

    populate: function (tables) {
        this.button_group.populate(this._formatTable(tables));
    }
});
$.shortcut('bi.excel_view_setting_tree', BI.ExcelViewSettingTree);