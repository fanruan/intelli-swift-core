/**
 * 选择文本字段面板
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectNumberPane
 * @extend BI.Widget
 */
BI.SelectNumberPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectNumberPane.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        })
    },

    _init: function () {
        BI.SelectNumberPane.superclass._init.apply(this, arguments);
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
            fieldsCreator: function (tableId, opt) {
                opt = opt || {};
                var ids = BI.Utils.getNumberFieldIDsOfTableID(tableId);
                var result = [];
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        result.push({
                            id: fid,
                            type: "bi.select_number_level0_item"
                        })
                    }
                });
                return result;
            }
        });
    }
});

$.shortcut("bi.select_number", BI.SelectNumberPane);