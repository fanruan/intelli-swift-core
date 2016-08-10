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
        var currentTableId = BI.Utils.getTableIdByFieldID(o.field_id);
        var primaryRelationTableIds = BI.Utils.getPrimaryRelationTablesByTableID(currentTableId);
        primaryRelationTableIds = BI.without(primaryRelationTableIds, currentTableId);
        this.service = BI.createWidget({
            type: "bi.simple_select_data_service",
            element: this.element,
            isDefaultInit: true,
            tablesCreator: function () {
                var tablesStructure = [];
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
                var fieldStructure = [];
                var path = [];
                if (tableId !== currentTableId) {
                    path = BI.Utils.getPathsFromTableAToTableB(tableId, currentTableId);
                }
                BI.each(BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId), function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === false) {
                        return;
                    }
                    //不需要记录数
                    if (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.COUNTER) {
                        return;
                    }
                    var fieldName = BI.Utils.getFieldNameByID(fid);

                    if (path.length > 1) {
                        fieldStructure.push({
                            id: fid,
                            pId: tableId,
                            type: "bi.expander",
                            text: fieldName,
                            el: BI.extend({
                                wId: o.wId,
                                text: fieldName,
                                title: fieldName,
                                fieldType: BI.Utils.getFieldTypeByID(fid),
                                value: fid,
                                type: "bi.select_data_level1_date_node",
                                layer: 1,
                                isParent: true,
                                open: false
                            }),
                            popup: {
                                type: "bi.select_data_loader",
                                items: self._createMultiPathFields(fid, path)
                            }
                        });
                    } else {
                        fieldStructure.push({
                            id: fid,
                            pId: tableId,
                            type: "bi.select_data_level0_item",
                            fieldType: BI.Utils.getFieldTypeByID(fid),
                            text: fieldName,
                            title: fieldName
                        });
                    }
                });
                return fieldStructure;
            }
        });
        this.service.on(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.TargetFilterSelectField.EVENT_CLICK_ITEM, arguments);
        });
    },

    _createMultiPathFields: function (fieldId, path) {
        var self = this;
        var fields = [];
        var fieldName = BI.Utils.getFieldNameByID(fieldId);
        BI.each(path, function (i, relations) {
            fields.push({
                type: "bi.select_data_level1_item",
                text: self._createNameByRelations(relations),
                title: fieldName,
                value: {
                    field_id: fieldId,
                    target_relation: relations
                }
            })
        });
        return fields;
    },

    _createNameByRelations: function (relations) {
        var self = this, o = this.options;
        var name = "";
        BI.each(relations, function (i, relation) {
            var pId = BI.Utils.getPrimaryIdFromRelation(relation);
            var fId = BI.Utils.getForeignIdFromRelation(relation);
            if (i === 0) {
                name += BI.Utils.getFieldNameByID(pId);
            } else {
                name += ">" + BI.Utils.getFieldNameByID(pId);
            }
        });
        return name += ">" + BI.Utils.getFieldNameByID(o.field_id);
    }
});
BI.TargetFilterSelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.target_filter_select_field", BI.TargetFilterSelectField);