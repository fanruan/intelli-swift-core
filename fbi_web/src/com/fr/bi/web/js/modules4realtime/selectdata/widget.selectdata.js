/**
 * Created by GUY on 2016/4/26.
 *
 * @class BI.DetailSelectData4RealTime
 * @extend BI.Widget
 */
BI.DetailSelectData4RealTime = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectData4RealTime.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-select-data-4-realtime",
            wId: ""
        })
    },

    _init: function () {
        BI.DetailSelectData4RealTime.superclass._init.apply(this, arguments);
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
            fieldsCreator: function (tableId, isRelation) {
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId);
                ids = BI.Utils.getCountFieldIDsOfTableID(tableId).concat(ids);
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
$.shortcut("bi.detail_select_data_4_realtime", BI.DetailSelectData4RealTime);