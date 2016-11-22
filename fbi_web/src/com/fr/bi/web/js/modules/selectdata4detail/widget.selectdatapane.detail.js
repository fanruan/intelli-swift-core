/**
 * Created by roy on 16/4/1.
 */
BI.DetailDetailTableSelectDataPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailDetailTableSelectDataPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-detail-select-data",
            wId: ""
        })
    },

    _init: function () {
        BI.DetailDetailTableSelectDataPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.service = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            wId: o.wId,
            showRelativeTables: true,
            showExcelView: true,
            showDateGroup: true,
            showTime: true,
            tablesCreator: function (packageId, opt) {
                opt = opt || {};
                if (opt.isRelation === true) {
                    var tIds = BI.Utils.getPrimaryRelationTablesByTableID(packageId);
                    return BI.map(tIds, function (i, id) {
                        return {
                            id: id,
                            type: "bi.detail_detail_select_data_level1_node",
                            warningTitle: BI.i18nText("BI-Added_Data_Unavailable")
                        }
                    })
                }
                var ids = BI.Utils.getTableIDsOfPackageID(packageId);
                return BI.map(ids, function (i, id) {
                    return {
                        id: id,
                        type: "bi.detail_detail_select_data_level0_node",
                        warningTitle: BI.i18nText("BI-Added_Data_Unavailable")
                    }
                })
            },
            fieldsCreator: function (tableId, opt) {
                opt = opt || {};
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId);
                var result = [];
                var fieldNames = BI.map(ids, function(idx, fid){
                    return BI.Utils.getFieldNameByID(fid);
                });
                var matched = BI.Func.getSearchResult(fieldNames, opt.keyword).matched;
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        if(opt.isSearching === true && !self._isFieldValid(fid, o.wId)){
                            if(BI.contains(matched, BI.Utils.getFieldNameByID(fid))){
                                result.push({
                                    id: fid,
                                    type: "bi.detail_select_data_no_relation_match_search_item"
                                });
                            }
                        }else{
                            result.push({
                                id: fid
                            });
                        }
                    }
                });
                return result;
            }
        });
    },

    _isFieldValid: function(fieldId, wId){
        var tableId = BI.Utils.getTableIdByFieldID(fieldId)
        return BI.Utils.isTableUsableByWidgetID(tableId, wId);
    }
});
$.shortcut("bi.detail_detail_table_select_data", BI.DetailDetailTableSelectDataPane);