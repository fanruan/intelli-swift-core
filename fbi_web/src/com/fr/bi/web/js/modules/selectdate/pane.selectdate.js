/**
 * 选择文本字段面板
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectDatePane
 * @extend BI.Widget
 */
BI.SelectDatePane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDatePane.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        })
    },

    _init: function () {
        BI.SelectDatePane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.service = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            wId: o.wId,
            showRelativeTables: true,
            showExcelView: false,
            showDateGroup: false,
            tablesCreator: function (packageIdOrTableId, opt) {
                if (opt.isRelation === true) {
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
                var ids = BI.Utils.getDateFieldIDsOfTableID(tableId);
                var result = [];
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        result.push({
                            id: fid,
                            type: "bi.select_date_level0_item"
                        })
                    }
                });
                return result;
            }
        });
    }
});

$.shortcut("bi.select_date", BI.SelectDatePane);
