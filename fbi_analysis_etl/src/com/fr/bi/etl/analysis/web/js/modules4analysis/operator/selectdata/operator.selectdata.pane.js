/**
 * Created by windy on 2017/4/6.
 */
/**
 * @class BI.AnalysisSelectDataOperatorPane
 * @extend BI.Widget
 * 选择字段
 */
BI.AnalysisSelectDataOperatorPane = BI.inherit(BI.Widget, {

    props: {},

    beforeCreate: function () {
        this.id = BI.UUID();
    },

    render: function () {
        var self = this;
        return {
            type: "bi.package_select_data_service",
            ref: function (_ref) {
                self.service = _ref;
            },
            isDefaultInit: false,
            showRelativeTables: true,
            showExcelView: false,
            showDateGroup: true,
            listeners: [{
                eventName: BI.PackageSelectDataService.EVENT_CLICK_ITEM,
                action: function () {
                    self.fireEvent(BI.AnalysisSelectDataOperatorPane.EVENT_CLICK_ITEM, arguments);
                }
            }],
            tablesCreator: function (packageId, opt) {
                opt = opt || {};
                if (opt.isRelation === true) {
                    var tIds = BI.Utils.getPrimaryRelationTablesByTableID(packageId);
                    return BI.map(tIds, function (i, id) {
                        return {
                            id: id,
                            type: "bi.analysis_etl_select_data_level_node",
                            layer: 1
                        }
                    })
                }
                var ids = BI.Utils.getTableIDsOfPackageID(packageId);
                return BI.map(ids, function (i, tid) {
                    return {
                        id: tid,
                        type: "bi.analysis_etl_select_data_level_node",
                        listener: function () {
                            self.registerEvents(this, tid)
                        }
                    }
                })
            },
            fieldsCreator: function (tableId, opt) {
                opt = opt || {};
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId);
                var result = [];
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        var field = {};
                        if (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.DATE) {
                            field = {
                                id: fid,
                                type: "bi.analysis_etl_detail_select_data_level_item",
                                layer: opt.isRelation ? 3 : 2
                            };
                        } else {
                            field = {
                                id: fid,
                                type: "bi.analysis_etl_detail_select_data_level_item",
                                layer: opt.isRelation ? 2 : 1
                            };
                        }
                        field.listener = function () {
                            self.registerEvents(this, tableId)
                        };
                        result.push(field);
                    }
                });
                return result;
            }
        }
    },

    setEnabledValue: function (v) {
        this.service.setEnabledValue(v)
    },

    setEnable: function (v) {
        BI.AnalysisSelectDataOperatorPane.superclass.setEnable.apply(this, arguments)
        this.service.setEnable(v)
    },

    setEnableTables: function (tables) {
        this.tables = tables;
        BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + this._getId());
    },

    _getId: function () {
        return this.id;
    },

    isEnable: function (tableId) {
        if (BI.isNotNull(Pool.current_edit_etl_used) && Pool.current_edit_etl_used.contains(tableId)) {
            return false;
        }
        return BI.isNull(this.tables) || BI.isEmptyArray(this.tables) || BI.deepContains(this.tables, tableId);
    },


    registerEvents: function (item, tableId) {
        var enable = this.isEnable(tableId);
        item.setEnable(enable);
        var self = this;
        BI.Broadcasts.on(BICst.BROADCAST.SRC_PREFIX + this._getId(), function () {
            var enable = self.isEnable(tableId);
            item.setEnable(enable);
            if (enable === false) {
                item.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.COLLAPSE)
            }
        })
    },

    populate: function () {
        var ids = BI.Utils.getAllPackageIDs();
        this.service.setPackage(ids[0]);
    }
});
BI.AnalysisSelectDataOperatorPane.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
BI.shortcut("bi.analysis_select_data_operator_pane", BI.AnalysisSelectDataOperatorPane);
