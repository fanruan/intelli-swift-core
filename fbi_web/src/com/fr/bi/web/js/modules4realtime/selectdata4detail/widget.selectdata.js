/**
 * Created by GUY on 2016/4/26.
 *
 * @class BI.DetailDetailTableSelectData4RealTime
 * @extend BI.Widget
 */
BI.DetailDetailTableSelectData4RealTime = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailDetailTableSelectData4RealTime.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-detail-table-select-data-4-realtime",
            wId: ""
        })
    },

    _init: function () {
        BI.DetailDetailTableSelectData4RealTime.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.service = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            wId: o.wId,
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: true,
            tablesCreator: function (packageId, opt) {
                if (opt.isRelation === true) {
                    var tIds = BI.Utils.getPrimaryRelationTablesByTableID(packageId);
                    return BI.map(tIds, function (i, id) {
                        return {
                            id: id,
                            type: "bi.detail_select_data_level1_node_4_realtime"
                        }
                    })
                }
                var ids = BI.Utils.getTableIDsOfPackageID(packageId);
                return BI.map(ids, function (i, id) {
                    return {
                        id: id,
                        type: "bi.detail_select_data_level0_node_4_realtime"
                    }
                })
            },
            fieldsCreator: function (tableId, opt) {
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId);
                var result = [];
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        result.push({
                            id: fid
                        })
                    }
                });
                return result;
            }
        });
    }
});
$.shortcut("bi.detail_detail_table_select_data_4_realtime", BI.DetailDetailTableSelectData4RealTime);