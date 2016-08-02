/**
 * ExcelViewSettingExpander
 *
 * Created by GUY on 2016/4/7.
 * @class BI.ExcelViewSettingExpander
 * @extends BI.Widget
 */
BI.ExcelViewSettingExpander = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSettingExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view-setting-expander",
            tableName: "",
            value: "",
            open: false,
            fields: []
        });
    },

    _init: function () {
        BI.ExcelViewSettingExpander.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = BI.createWidget({
            type: "bi.triangle_group_node",
            cls: "excel-view-setting-expander-node",
            height: 25,
            open: o.open,
            text: o.tableName,
            value: o.value,
            title: o.tableName
        });
        this.table = BI.createWidget({
            type: "bi.excel_view_setting_table",
            fields: o.fields,
            clearOneCell: function(fieldId){
                o.clearOneCell(fieldId);
            }
        });
        this.table.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.expander = BI.createWidget({
            type: "bi.expander",
            element: this.element,
            el: this.node,
            popup: {
                type: "bi.vertical",
                items: [this.table],
                hgap: 10
            }
        });
    },

    setValue: function (v) {
        this.table.setValue(v)
    },

    getValue: function () {
        return this.table.getValue();
    },

    getMarkedFields: function() {
        return this.table.getMarkedFields();
    },

    populate: function (items, keyword, context) {
        this.node.setValue(context.tableName);
        this.table.populate(context.fields);
    }
});
$.shortcut('bi.excel_view_setting_expander', BI.ExcelViewSettingExpander);