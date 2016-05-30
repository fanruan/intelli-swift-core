/**
 * @class BI.TargetFilterSelectField
 * @extend BI.Widget
 * 指标过滤选择字段——本表,所有主表字段,所有子表的数值类字段和记录数
 */
BI.TargetFilterSelectField = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.TargetFilterSelectField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter-select-field",
            field_id: ""
        })
    },

    _init: function () {
        BI.TargetFilterSelectField.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.service = BI.createWidget({
            type: "bi.simple_select_data_service",
            element: this.element,
            isDefaultInit: true,
            tablesCreator: function () {
                var tablesStructure = [];
                var currentTableId = BI.Utils.getTableIdByFieldID(o.field_id);
                var primaryRelationTableIds = BI.Utils.getPrimaryRelationTablesByTableID(currentTableId);
                tablesStructure.push({
                    id: currentTableId,
                    type: "bi.select_data_level0_node",
                    text: BI.Utils.getTableNameByID(currentTableId),
                    title: BI.Utils.getTableNameByID(currentTableId),
                    value: currentTableId,
                    isParent: true,
                    open: false
                });
                BI.each(primaryRelationTableIds, function (i, tId) {
                    tablesStructure.push({
                        id: tId,
                        type: "bi.select_data_level0_node",
                        text: BI.Utils.getTableNameByID(tId),
                        title: BI.Utils.getTableNameByID(tId),
                        value: tId,
                        isParent: true,
                        open: false
                    });
                });
                return tablesStructure;
            },
            fieldsCreator: function (tableId) {
                var currentTableId = BI.Utils.getTableIdByFieldID(o.field_id);
                var primaryRelationTableIds = BI.Utils.getPrimaryRelationTablesByTableID(currentTableId);
                var fieldStructure = [];
                BI.each(BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId), function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === false) {
                        return;
                    }
                    //主表-不需要记录数
                    if (BI.contains(primaryRelationTableIds, tableId) && (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.COUNTER)) {
                        return;
                    }
                    var fieldName = BI.Utils.getFieldNameByID(fid);
                    fieldStructure.push({
                        id: fid,
                        pId: tableId,
                        type: "bi.select_data_level0_item",
                        fieldType: BI.Utils.getFieldTypeByID(fid),
                        text: fieldName,
                        title: fieldName,
                        value: fid
                    })
                });
                return fieldStructure;
            }
        });
        this.service.on(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.TargetFilterSelectField.EVENT_CLICK_ITEM, arguments);
        });
    }
});
BI.TargetFilterSelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.target_filter_select_field", BI.TargetFilterSelectField);