/**
 * 选择文本字段面板
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectStringPane
 * @extend BI.Widget
 */
BI.SelectStringPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectStringPane.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        })
    },

    _init: function () {
        BI.SelectStringPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.service = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            wId: o.wId,
            showRelativeTables: true,
            showExcelView: false,
            showDateGroup: false,
            tablesCreator: function (packageIdOrTableId, isRelation) {
                if (isRelation === true) {
                    var tIds = BI.Utils.getPrimaryRelationTablesByTableID(packageIdOrTableId);
                    return BI.map(tIds, function (i, id) {
                        return {
                            id: id
                        }
                    })
                }
                var ids = BI.Utils.getTableIDsOfPackageID(packageIdOrTableId);
                return BI.map(ids, function (i, id) {
                    return {
                        id: id
                    }
                })
            },
            fieldsCreator: function (tableId, isRelation) {
                var ids = BI.Utils.getStringFieldIDsOfTableID(tableId);
                var result = [];
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        result.push({
                            id: fid,
                            type: "bi.select_string_level0_item"
                        })
                    }
                });
                return result;
            }
        });
    }
});

$.shortcut("bi.select_string", BI.SelectStringPane);