/**
 * Created by GUY on 2016/6/15.
 * @class BI.TreeSelectDataPane
 * @extends BI.Widget
 */
BI.TreeSelectDataPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TreeSelectDataPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-tree-select-data",
            wId: ""
        })
    },

    _init: function () {
        BI.TreeSelectDataPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.service = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            wId: o.wId,
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: true,
            tablesCreator: function (packageId, isRelation) {
                if (isRelation === true) {
                    var tIds = BI.Utils.getPrimaryRelationTablesByTableID(packageId);
                    return BI.map(tIds, function (i, id) {
                        return {
                            id: id,
                            type: "bi.tree_select_data_level1_node"
                        }
                    })
                }
                var ids = BI.Utils.getTableIDsOfPackageID(packageId);
                return BI.map(ids, function (i, id) {
                    return {
                        id: id,
                        type: "bi.tree_select_data_level0_node"
                    }
                })
            },
            fieldsCreator: function (tableId, isRelation) {
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId);
                var result = [];
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        result.push({
                            id: fid,
                            type: isRelation ? "bi.select_string_level1_item" : "bi.select_string_level0_item"
                        })
                    }
                });
                return result;
            }
        });
    }
});
$.shortcut("bi.tree_select_data", BI.TreeSelectDataPane);