/**
 * 指标弹出明细表表格
 *
 * Created by GUY on 2016/5/18.
 * @class BI.DetailTablePopupDetailTableModel
 * @extends FR.OB
 */
BI.DetailTablePopupDetailTableModel = BI.inherit(FR.OB, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupDetailTableModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.DetailTablePopupDetailTableModel.superclass._init.apply(this, arguments);

    },

    getAllDimensionIDs: function () {
        var widget = Data.SharingPool.get(BI.DetailTablePopup.SHARING_POOL_KEY);
        return BI.keys(widget.dimensions);
    },

    isDimensionUsable: function (dId) {
        var widget = Data.SharingPool.get(BI.DetailTablePopup.SHARING_POOL_KEY);
        return widget.dimensions[dId].used;
    },

    getDimensionNameByID: function (dId) {
        var widget = Data.SharingPool.get(BI.DetailTablePopup.SHARING_POOL_KEY);
        return widget.dimensions[dId].name;
    },

    getView: function () {
        var widget = Data.SharingPool.get(BI.DetailTablePopup.SHARING_POOL_KEY);
        return widget.view;
    },

    getData: function (page, callback) {
        var widget = Data.SharingPool.get(BI.DetailTablePopup.SHARING_POOL_KEY);
        var dimensions = widget.dimensions;
        var tables = [], fields = [], map = {};
        BI.each(dimensions, function (dId, dimension) {
            var fId = dimension._src.field_id;
            var tId = BI.Utils.getTableIdByFieldID(fId);
            fields.push(fId);
            tables.push(tId);
            map[dId] = tId;
        });
        var commonIds = BI.Utils.getCommonForeignTablesByTableIDs(tables);
        if (commonIds.length > 0) {
            var commonId = commonIds[0];
            BI.each(dimensions, function (dId, dimension) {
                var relations = BI.Utils.getPathsFromTableAToTableB(map[dId], commonId);
                dimension.dimension_map = {};
                dimension.dimension_map[commonId] = {
                    target_relation: relations[0] || []
                }
            });

        }
        BI.Utils.getWidgetDataByWidgetInfo(dimensions, widget.view, callback, {
            type: BICst.WIDGET.DETAIL,
            page: page || BICst.TABLE_PAGE_OPERATOR.REFRESH
        })
    },

    populate: function () {
        var self = this;
    }
});