/**
 * Created by roy on 16/3/29.
 */
BI.FieldRelationSettingPopupModel = BI.inherit(FR.OB, {
    _defaultConfig: function () {
        return BI.extend(BI.FieldRelationSettingPopupModel.superclass._defaultConfig.apply(this, arguments), {
            dimensionIds: []
        })
    },


    _init: function () {
        BI.FieldRelationSettingPopupModel.superclass._init.apply(this, arguments);
        this._initData();
    },

    _initData: function () {
        var o = this.options;
        this.wId = o.wId || "";
        this.dimensionIds = this._getRelationDimensions(BI.Utils.getWidgetViewByID(this.wId));
        this.selectedForeignTable = this._getSelectedCommonForeignTable();
        this.choosePathMap = this._initChoosePathMap();
    },

    _getAllDistinctTables: function () {
        var dimensions = this.dimensionIds;
        var distinctTables = [];
        BI.each(dimensions, function (i, did) {
            var fieldId = BI.Utils.getFieldIDByDimensionID(did);
            var tableId = BI.Utils.getTableIdByFieldID(fieldId);
            if (!BI.contains(distinctTables, tableId)) {
                distinctTables.push(tableId)
            }
        });
        return distinctTables;
    },


    getComboItems: function () {
        var tables = this._getAllDistinctTables();
        var commonTables = this._getCommonTables(tables);
        var result = [];
        BI.each(commonTables, function (i, commonTableId) {
            var item = {};
            item.text = BI.Utils.getTableNameByID(commonTableId);
            item.value = commonTableId;
            item.children = [];
            BI.each(tables, function (i, tableId) {
                var leafItem = {};
                leafItem.text = BI.Utils.getTableNameByID(tableId);
                leafItem.value = tableId;
                item.children.push(leafItem);
            });
            result.push(item);
        });
        return result;
    },


    getLabelItem: function () {
        var labelValue = "";
        BI.each(this.dimensionIds, function (i, did) {
            var fieldId = BI.Utils.getFieldIDByDimensionID(did);
            labelValue += "\"" + BI.Utils.getFieldNameByID(fieldId) + "(" + BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldId)) + ")\" "
        });
        return labelValue;
    },


    _initChoosePathMap: function () {
        var self = this;
        var choosePathMap = {};
        BI.each(this.dimensionIds, function (i, did) {
            choosePathMap[did] = BI.Utils.getTargetRelationByDimensionID(did);
        });
        return choosePathMap;
    },

    _getCommonTables: function (distinctTables) {
        return BI.Utils.getCommonForeignTablesByTableIDs(distinctTables);
    },

    _getSelectedCommonForeignTable: function () {
        var did = this.dimensionIds[0] || "";
        var dimension_map = BI.Utils.getDimensionMapByDimensionID(did) || {};
        return BI.keys(dimension_map)[0] || "";
    },

    _getRelationDimensions: function (view) {
        var result = [];
        var dimensions = BI.firstObject(view);
        BI.each(dimensions, function (i, dId) {
            if (BI.Utils.getDimensionTypeByID(dId) === BICst.TARGET_TYPE.DATE || BI.Utils.getDimensionTypeByID(dId) === BICst.TARGET_TYPE.STRING || BI.Utils.getDimensionTypeByID(dId) === BICst.TARGET_TYPE.NUMBER) {
                result.push(dId);
            }
        });
        return result;
    },

    getDimensions: function () {
        return BI.deepClone(this.dimensionIds);
    },

    getSelectedForeignTable: function () {
        return BI.deepClone(this.selectedForeignTable);
    },

    getChoosePathFromDid: function (did) {
        return BI.deepClone(this.choosePathMap[did]);
    },

    setSelectedForeignTable: function (tableId) {
        this.selectedForeignTable = tableId;
    },

    refreshChoosePathMap: function () {
        var self = this;
        BI.each(this.dimensionIds, function (i, did) {
            var primaryTable = BI.Utils.getTableIDByDimensionID(did);
            var paths = BI.Utils.getPathsFromTableAToTableB(primaryTable, self.selectedForeignTable);
            self.choosePathMap[did] = paths[0] || [];
        })
    },

    getValue: function () {
        var result = {};
        var self = this;
        var target_relation = {};
        BI.each(this.dimensionIds, function (i, did) {
            target_relation[did] = self.choosePathMap[did];
        });
        result.targetTableId = this.selectedForeignTable;
        result.target_relation = target_relation;
        return result;
    }


});