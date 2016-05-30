/**
 * @class BI.ConfFilterSelectSingleField
 * @extend BI.Widget
 * 选择表中单个字段(提供搜索)
 */
BI.ConfFilterSelectSingleField = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ConfFilterSelectSingleField.superclass._defaultConfig.apply(this, arguments), {
            table: {}
        })
    },

    _init: function () {
        BI.ConfFilterSelectSingleField.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var fields = o.table.fields[0];
        this.service = BI.createWidget({
            type: "bi.simple_select_data_service",
            element: this.element,
            isDefaultInit: true,
            tablesCreator: function () {
                var tableName = o.table.table_name, tId = o.table.id;
                return [{
                    id: tId,
                    type: "bi.select_data_level0_node",
                    text: tableName,
                    value: tId,
                    open: true
                }];
            },
            fieldsCreator: function (tableId) {
                var result = [];
                BI.each(fields, function (i, field) {
                    var fId = BI.UUID();
                    result.push({
                        id: fId,
                        pId: tableId,
                        type: "bi.select_data_level0_item",
                        fieldType: field["field_type"],
                        text: field["field_name"],
                        value: {
                            field: field,
                            table: o.table
                        }
                    })
                });
                return result;
            }
        });
        this.service.on(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.ConfFilterSelectSingleField.EVENT_CLICK_ITEM, arguments);
        });
    },

    stopSearch: function () {
        this.service.stopSearch();
    }
});
BI.ConfFilterSelectSingleField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.conf_filter_select_single_field", BI.ConfFilterSelectSingleField);