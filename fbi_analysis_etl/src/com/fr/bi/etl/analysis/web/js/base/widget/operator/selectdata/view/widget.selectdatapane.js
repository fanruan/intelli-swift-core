/**
 * @class BI.AnalysisETLSelectDataPane
 * @extend BI.Widget
 * 选择字段
 */
BI.AnalysisETLSelectDataPane = BI.inherit(BI.MVCWidget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLSelectDataPane.superclass._defaultConfig.apply(this, arguments), {})
    },

    _initController: function () {
        return BI.AnalysisETLSelectDataPaneController;
    },

    _initView: function () {
        var self = this;

        this.service = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            isDefaultInit: false,
            showRelativeTables: true,
            showExcelView: false,
            showDateGroup: true,
            tablesCreator: function (packageId, isRelation) {
                if (isRelation === true) {
                    var tIds = BI.Utils.getPrimaryRelationTablesByTableID(packageId);
                    return BI.map(tIds, function (i, id) {
                        return {
                            id: id,
                            type: "bi.analysis_etl_select_data_level1_node"
                        }
                    })
                }
                var ids = BI.Utils.getTableIDsOfPackageID(packageId);
                return BI.map(ids, function (i, tid) {
                    return {
                        id: tid,
                        type: "bi.analysis_etl_select_data_level0_node",
                        listener: function () {
                            self.controller.registerEvents(this, tid)
                        }
                    }
                })
            },
            fieldsCreator: function (tableId, isRelation) {
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId);
                var result = [];
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        var field = {};
                        if (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.DATE) {
                            field = {
                                id: fid,
                                type: isRelation ? "bi.analysis_etl_detail_select_data_level2_item" : "bi.analysis_etl_detail_select_data_level1_item"
                            };
                        } else {
                            field = {
                                id: fid,
                                type: isRelation ? "bi.analysis_etl_detail_select_data_level1_item" : "bi.analysis_etl_detail_select_data_level0_item"
                            };
                        }
                        field.listener = function () {
                            self.controller.registerEvents(this, tableId)
                        };
                        result.push(field);
                    }
                });
                return result;
            }
        });
        this.service.on(BI.PackageSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.AnalysisETLSelectDataPane.EVENT_CLICK_ITEM, arguments);
        });

    },

    setEnabledValue: function (v) {
        this.service.setEnabledValue(v)
    },

    setEnable: function (v) {
        BI.AnalysisETLSelectDataPane.superclass.setEnable.apply(this, arguments)
        this.service.setEnable(v)
    }
});
BI.AnalysisETLSelectDataPane.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.analysis_etl_select_data_pane", BI.AnalysisETLSelectDataPane);